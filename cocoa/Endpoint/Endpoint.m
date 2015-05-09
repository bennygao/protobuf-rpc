//
//  Endpoint.m
//  cocoa
//
//  Created by Benny Gao on 15/5/9.
//  Copyright (c) 2015年 Benny Gao. All rights reserved.
//
#import <netinet/in.h>
#import <arpa/inet.h>
#import <sys/ioctl.h>
#import <sys/socket.h>
#import <fcntl.h>
#import <netdb.h>
#import "Endpoint.h"

#define GET_REASON(r)    getReason(__FILE__, __LINE__, (r))
static NSString* getReason(const char *file, const int line, const char *routine);

static NSString* getReason(const char *file, const int line, const char *routine) {
    return [NSString stringWithFormat:@"[%s:%d] %s = %d : %s",
            file, line, routine, errno, strerror(errno)];
}

static NSString* formatReason(const char *file, const int line, const char *format, ...) {
    va_list ap;
    va_start(ap, format);
    char tmpstr[256];
    bzero(tmpstr, sizeof(tmpstr));
    vsnprintf(tmpstr, sizeof(tmpstr) - 1, format, ap);
    va_end(ap);
    return [NSString stringWithCString:tmpstr encoding:NSASCIIStringEncoding];
}

static const NSUInteger DEFAULT_QUEUE_SIZE = 16;
static const NSUInteger PACKAGE_SIZE_FIELD_LENGTH = 4;

static const Byte NOTIFY_TO_SEND_MESSAGE_BEAN = 0;
static const Byte NOTIFY_TO_STOP = 0xff;

@implementation MessageBeanQueue
-(MessageBeanQueue *) initWithCapacity:(NSUInteger)capacity {
    self = [super init];
    if (self) {
        number = 0;
        array = [[NSMutableArray alloc] initWithCapacity:capacity];
        lock = [[NSCondition alloc] init];
    }
    
    return self;
}

-(void) dealloc {
#ifdef DEBUG
    NSLog(@"**** MessageBeanQueue dealloc");
#endif
    
    if (array != nil) {
        [array removeAllObjects];
        [array release];
        array = nil;
    }
    
    if (lock != nil) {
        [lock release];
        lock = nil;
    }
    
    [super dealloc];
}

-(MessageBeanQueue *) init {
    return [self initWithCapacity:DEFAULT_QUEUE_SIZE];
}

-(void) add:(MessageBean *)bean {
    if (bean == nil) {
        return;
    }
    
    [lock lock];
    @try {
        NSLog(@"bean.commandID :%d",bean.commandId);
        [array addObject:bean];
        ++number;
    } @finally {
        [lock unlock];
    }
}

-(MessageBean *) take {
    [lock lock];
    @try {
        if (number == 0) {
            return nil;
        }
        
        MessageBean *bean = [array objectAtIndex:0];
        if ([bean retainCount] >= 2)
        {
            [array removeObjectAtIndex:0];
        }
        --number;
        
        return bean;
    } @finally {
        [lock unlock];
    }
}

-(NSUInteger) size {
    return number;
}

-(void) clear {
    [lock lock];
    @try {
        [array removeAllObjects];
        number = 0;
    } @finally {
        [lock unlock];
    }
}
@end

@implementation SegmentHead

@synthesize commandId;
@synthesize serialNo;

- (void) getHeadInfo:(uint) type :(IOBuffer *) ibuffer {
    switch (type) {
        case 1:
            commandId = [ibuffer readShort];
            break;
            
        case 0:
        case 2:
            commandId = [ibuffer readShort];
            serialNo = [ibuffer readLong];
            break;
            
        default:
        {
            assert(@"SegmentHead not defined");
            commandId = [ibuffer readShort];
            break;
        }
            // @throw [NSException exceptionWithName:@"SegmentHead#getHeadInfo"
            //                                reason:formatReason(__FILE__, __LINE__, "Un-defined segment head type %d", type)
            //                              userInfo:nil];
    }
    
}

- (void) putHeadInfo:(uint) type :(IOBuffer *) obuffer {
    switch (type) {
        case 1:
            [obuffer writeShort:commandId];
            break;
            
        case 2:
            [obuffer writeShort:commandId];
            [obuffer writeLong:serialNo];
            break;
            
        default:
            @throw [NSException exceptionWithName:@"SegmentHead#putHeadInfo"
                                           reason:formatReason(__FILE__, __LINE__, "Un-defined segment head type %d", type)
                                         userInfo:nil];;
    }
}

@end

@implementation Segment

@synthesize totalRecvBytesNumber;

-(Segment *) init {
    self = [super init];
    if (self) {
        ibuffer = nil;
        head = nil;
        totalRecvBytesNumber = 0;
        
        [self reset];
    }
    
    return self;
}

-(void) reset {
    phase = read_segment_size;
    needBytes = PACKAGE_SIZE_FIELD_LENGTH;
    gotBytes = 0;
    segmentSize = 0;
    
    if (ibuffer == nil) {
        ibuffer = [[IOBuffer alloc] init];
    } else {
        [ibuffer clear];
    }
    
    if (head == nil) {
        head = [[SegmentHead alloc] init];
    }
}

-(void) dealloc {
#ifdef DEBUG
    NSLog(@"**** Segment dealloc");
#endif
    [ibuffer release];
    ibuffer = nil;
    
    [head release];
    head = nil;
    
    [super dealloc];
}


-(size_t) recv:(int) sock :(void *) buf :(size_t) len {
    size_t cnt = recv(sock, buf, len, 0);
    totalRecvBytesNumber += cnt;
    return cnt;
}

-(MessageBean *) action:(int) sock {
    size_t cnt;
    short commandId;
    long long serialNo;
    uint headType;
    
    if (phase == read_segment_size) {
        if (needBytes > 0) {
            cnt = [self recv:sock  :bytes + gotBytes :needBytes];
            needBytes -= cnt;
            gotBytes += cnt;
        }
        
        if (needBytes == 0) {
            int *ptr = (int *) bytes;
            segmentSize = [IOBuffer n2lInt:*ptr];
            needBytes = segmentSize;
            gotBytes = 0;
            phase = read_segment_content;
        }
        
        return nil;
    } else {
        size_t toread = MIN(IO_BUFFER_SIZE, needBytes);
        cnt = [self recv:sock  :bytes :toread];
        needBytes -= cnt;
        gotBytes += cnt;
        [ibuffer putBytes:bytes :cnt];
        
        if (needBytes == 0) {
            [ibuffer flip];
            
            // 读取一个字节的SegmentHead类型
            headType = [ibuffer readByte];
            
            // 根据SegmentHead类型读取完整的SegmentHead信息
            [head getHeadInfo:headType :ibuffer];
            commandId = head.commandId;
            serialNo = head.serialNo;
            
            // 反序列化MessageBean
            MessageBean *bean = [MessageBuffer unpack:commandId :ibuffer];
            
            [ibuffer clear];
            phase = read_segment_size;
            needBytes = PACKAGE_SIZE_FIELD_LENGTH;
            gotBytes = 0;
            
            return bean;
        } else {
            return nil;
        }
    }
}

@end

@implementation SocketEngine

@synthesize serverHost;
@synthesize serverPort;

-(NSString *) getServerIpAddress {
    if (serverHost == nil) {
        return nil;
    }
    
    struct hostent *host = gethostbyname([serverHost UTF8String]);
    if (host == NULL) {
        @throw [NSException exceptionWithName:@"SocketEngine#getServerIpAddress"
                                       reason:GET_REASON("gethostbyname()")
                                     userInfo:nil];
        
    }
    
    struct in_addr **list = (struct in_addr **) host->h_addr_list;
    return [NSString stringWithCString:inet_ntoa(*list[0])
                              encoding:NSASCIIStringEncoding];
}

-(void) setNonBlockSocket:(int) sock {
    int flags = fcntl(sock, F_GETFL, 0);
    if (fcntl(sock, F_SETFL, flags | O_NONBLOCK) == -1) {
        @throw [NSException exceptionWithName:@"SocketEngine#setNonBlock"
                                       reason:GET_REASON("fcntl()")
                                     userInfo:nil];
        
    }
}

-(void) initHostLink:(NSUInteger)timeout {
    hostLink = socket(AF_INET, SOCK_STREAM, 0);
    if (hostLink <= 0) {
        @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                       reason:GET_REASON("socket()")
                                     userInfo:nil];
    } else {
        [self setNonBlockSocket:hostLink];
    }
    
    struct hostent *host = gethostbyname([serverHost UTF8String]);
    if (host == NULL) {
        @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                       reason:GET_REASON("gethostbyname()")
                                     userInfo:nil];
    }
    
    fd_set rset, wset;
    struct timeval tval;
    struct sockaddr_in serverAddr;
    bzero(&serverAddr, sizeof(struct sockaddr_in));
    
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = inet_addr([[self getServerIpAddress] UTF8String]);
    serverAddr.sin_port = htons(serverPort);
    
    int err = connect(hostLink, (struct sockaddr *) &serverAddr, sizeof(serverAddr));
    if (err == 0) { // 连接立即建立了
        return;
    } else if (errno != EINPROGRESS) { // 连接没有建立，而且系统错误码不是EINPROGRESS
        @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                       reason:GET_REASON("connect()")
                                     userInfo:nil];
    }
    
    FD_ZERO(&rset);
    FD_SET(hostLink, &rset);
    wset = rset;
    tval.tv_sec = timeout;
    tval.tv_usec = 0;
    
    if (select(hostLink + 1, &rset, &wset, NULL, &tval) == 0) { // 超时了且没有连接上
        errno = ETIMEDOUT;
        @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                       reason:GET_REASON("select()")
                                     userInfo:nil];
    } else if (FD_ISSET(hostLink, &rset) || FD_ISSET(hostLink, &wset)) {
        uint len = sizeof(int);
        if (getsockopt(hostLink, SOL_SOCKET, SO_ERROR, &err, &len) < 0) {
            @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                           reason:GET_REASON("getsockopt()")
                                         userInfo:nil];
        } else if (err != 0) {
            errno = err;
            @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                           reason:GET_REASON("getsockopt()")
                                         userInfo:nil];
        } else {
            // 成功建立连接
            return;
        }
    } else {
        @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                       reason:GET_REASON("select()")
                                     userInfo:nil];
    }
}

-(void) initLocalSocketPair {
    if (socketpair(AF_LOCAL, SOCK_STREAM, 0, localSocketPair)) {
        @throw [NSException exceptionWithName:@"SocketEngine#initLocalSocketPair"
                                       reason:GET_REASON("socketpair()")
                                     userInfo:nil];
        
    }
    
    [self setNonBlockSocket:localSocketPair[0]];
}

-(SocketEngine *) init {
    self = [super init];
    if (self) {
        serverHost = nil;
        serverPort = 0;
        
        hostLink = -1;
        localSocketPair[0] = -1;
        localSocketPair[1] = -1;
        
        sendQueue = [[MessageBeanQueue alloc] init];
        recvQueue = [[MessageBeanQueue alloc] init];
        
        segment = [[Segment alloc] init];
        head = [[SegmentHead alloc] init];
        obuffer = [[IOBuffer alloc] init];
        
        threadLock = [[NSCondition alloc] init];
        totalSendBytesNumber = 0;
        
        isCheckingHeartbeat = false; // 是否正在主动检测心跳标识
        // 客户端主动检测服务器心跳的指令编号为1000，服务器返回-1000。
        cHeartbeatMessage = [[NullMessageBean alloc] initWithCommandId:1000];
        // 服务器主动检测客户端心跳的指令编号为-9999，客户端返回9999。
        sHeartbeatMessage = [[NullMessageBean alloc] initWithCommandId:9999];
    }
    
    return self;
}

-(BOOL) connectToHost:(NSString *)addr withPort:(ushort)port inSeconds:(NSUInteger)timeout {
    serverHost = addr;
    serverPort = port;
    @try {
        [self initHostLink:timeout];
        [self initLocalSocketPair];
        return true;
    }
    @catch (NSException *exception) {
        NSLog(@"%@", exception);
        return false;
    }
}

-(void) start {
    [NSThread detachNewThreadSelector:@selector(run) toTarget:self withObject:nil];
}

-(void) clear {
    if (hostLink > 0) {
        close(hostLink);
        hostLink = -1;
    }
    
    if (localSocketPair[0] > 0) {
        close(localSocketPair[0]);
        localSocketPair[0] = -1;
    }
    
    if (localSocketPair[1] > 0) {
        close(localSocketPair[1]);
        localSocketPair[1] = -1;
    }
    
    [recvQueue clear];
    [sendQueue clear];
    [segment reset];
    [obuffer clear];
    
    serverHost = nil;
    serverPort = 0;
    isCheckingHeartbeat = false;
}

-(void) stop {
    send(localSocketPair[1], &NOTIFY_TO_STOP, sizeof(Byte), 0);
    [threadLock lock]; // 等待网络读写线程完全退出
    [self clear];
    [threadLock unlock];
}

-(void) dealloc {
#ifdef DEBUG
    NSLog(@"**** SocketEngine dealloc");
#endif
    [self clear];
    
    if (sendQueue != nil) {
        [sendQueue release];
        sendQueue = nil;
    }
    
    if (recvQueue != nil) {
        [recvQueue release];
        recvQueue = nil;
    }
    
    if (segment != nil) {
        [segment release];
        segment = nil;
    }
    
    if (head != nil) {
        [head release];
        head = nil;
    }
    
    if (obuffer != nil) {
        [obuffer release];
        obuffer = nil;
    }
    
    if (cHeartbeatMessage != nil) {
        [cHeartbeatMessage release];
        cHeartbeatMessage = nil;
    }
    
    if (sHeartbeatMessage != nil) {
        [sHeartbeatMessage release];
        sHeartbeatMessage = nil;
    }
    
    [super dealloc];
}

-(void) sendMessageBean:(MessageBean *)message {
    [message retain];
    //NSLog(@"message.commandID: %d",message.commandId);
    [sendQueue add:message];
    send(localSocketPair[1], &NOTIFY_TO_SEND_MESSAGE_BEAN, sizeof(Byte), 0);
}

-(MessageBean *) recvMessageBean {
    return [recvQueue take];
}

-(void) doSend:(MessageBean *) bean {
    NSUInteger pkgSize = 0;
    short commandId = [bean commandId];
    [obuffer clear];
    
    // 预留一个整数（4个字节）的报文长度
    [obuffer writeInt:0];
    
    // 写入一个字节的SegmentHead类型信息
    // 当前客户端固定为 1
    [obuffer writeByte:1];
    
    // 写入完整SegmentHead信息
    head.commandId = commandId;
    [head putHeadInfo:1 :obuffer];
    
    // 写入MessageBean
    [MessageBuffer pack:commandId :bean :obuffer];
    
    // 回填报文长度
    pkgSize = [obuffer current] - sizeof(int);
    pkgSize = [IOBuffer l2nInt:(int) pkgSize];
    [obuffer replaceBytes:&pkgSize :(NSRange) {0, sizeof(int)}];
    
    [obuffer flip];
    
    NSUInteger total = [obuffer limit];
    NSUInteger count = total / IO_BUFFER_SIZE;
    NSUInteger rest = total % IO_BUFFER_SIZE;
    size_t cnt = 0;
    for (int i = 0; i < count; ++i) {
        [obuffer getBytes:bytes :IO_BUFFER_SIZE];
        cnt = send(hostLink, bytes, IO_BUFFER_SIZE, 0);
        totalSendBytesNumber += cnt;
    }
    
    [obuffer getBytes:bytes :rest];
    cnt = send(hostLink, bytes, rest, 0);
    totalSendBytesNumber += cnt;
}

- (void) requestHeartbeat {
#ifdef DEBUG
    NSLog(@"Client request heartbeat by message id 1000");
#endif
    [self doSend:cHeartbeatMessage];
}

- (void) responseServerHeartbeat {
#ifdef DEBUG
    NSLog(@"Client response server heartbeat by message id 9999");
#endif
    [self doSend:sHeartbeatMessage];
}

- (void) notityLinkLost {
    NullMessageBean *bean = [[NullMessageBean alloc] initWithCommandId:0];
    [recvQueue add:bean];
    [bean release];
}

-(void) run {
    [threadLock lock];
    fd_set rset;
    int maxfd = MAX(hostLink, localSocketPair[0]) + 1;
    int nread = 0;
    Byte notification = 0;
    MessageBean *bean;
    time_t lastRead = time(NULL);
    time_t current;
    struct timeval tval;
    
    bzero(&tval, sizeof(tval));
    while (TRUE) {
        bean = nil;
        FD_ZERO(&rset);
        FD_SET(hostLink, &rset);
        FD_SET(localSocketPair[0], &rset);
        
        // 设置空闲时间为60秒，超过60秒没有收到服务器任何消息的话，
        // 就主动向服务器发心跳检测信号。
        tval.tv_sec = isCheckingHeartbeat ? 30 : 60;
        select(maxfd, &rset, NULL, NULL, &tval);
        if (FD_ISSET(hostLink, &rset)) {
            lastRead = time(NULL);
            ioctl(hostLink, FIONREAD, &nread);
            if (nread == 0) { // 服务器端关闭了链接
#ifdef DEBUG
                NSLog(@"host link closed by server");
#endif
                [self notityLinkLost];
                // 结束线程循环，退出线程
                break;
            } else
            {
                bean = [[segment action:hostLink] retain];
                if (bean != nil)
                {
                    if (bean.commandId == -1000) { // 服务器返回心跳正常
                        isCheckingHeartbeat = false;
                        [bean release];
                    } else if (bean.commandId == -9999) { // 服务器主动检测心跳
                        [bean release];
                        // 返回消息给服务器
                        [self responseServerHeartbeat];
                    } else {
                        [recvQueue add:bean];
                        [bean release];
                    }
                }
            }
        }
        
        current = time(NULL);
        if (current - lastRead > tval.tv_sec) { // 超时时间里没有网络读写事件
            if (isCheckingHeartbeat) { // 又过了30秒还没有网络读写事件
#ifdef DEBUG
                NSLog(@"Client heartbeat timed-out");
#endif
                // 判断为与服务器断开连接
                [self notityLinkLost];
                // 结束线程循环，退出线程
                break;
            } else { // 检测心跳
                isCheckingHeartbeat = true;
                [self requestHeartbeat];
                continue;
            }
        }
        
        if (FD_ISSET(localSocketPair[0], &rset)) {
            recv(localSocketPair[0], &notification, sizeof(Byte), 0);
            if (notification == NOTIFY_TO_STOP) { // 结束线程运行
                break;
            } else {
                bean = [sendQueue take];
                [self doSend:bean];
                [bean release];
            }
        }
    }
    
    close(hostLink);
    hostLink = -1;
    [threadLock unlock];
}

-(NSUInteger) getTotalRecvBytesNumber {
    return segment.totalRecvBytesNumber;
}

-(NSUInteger) getTotalSendBytesNumber {
    return totalSendBytesNumber;
}

@end

