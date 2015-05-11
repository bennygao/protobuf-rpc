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
#import "ProtobufRpc.h"

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

///////////////////////////////////////////////////////////////////////////////
// Implementation of Message
///////////////////////////////////////////////////////////////////////////////
static const Byte STAGE_UNKNOWN = -1;
static const Byte STAGE_REQUEST = 0;
static const Byte STAGE_RESPONSE = 1;

@implementation Message

@synthesize command;
@synthesize serviceId;
@synthesize stamp;
@synthesize stage;
@synthesize argument;
@synthesize callback;

- (Message*) initWithCommand:(MESSAGE_COMMAND) cmd {
    self->command = cmd;
    self->serviceId = 0;
    self->stamp = 0;
    self->stage = STAGE_UNKNOWN;
    self->argument = nil;
    self->callback = nil;
    return self;
}

- (Message*) initwithServiceId:(int32_t) sid {
    return [self initwithServiceId:sid stamp:0 stage:0 argument:nil];
}

- (Message*) initwithServiceId:(int32_t)sid stamp:(int32_t)aStamp stage:(Byte)aStage argument:(PBGeneratedMessage *)arg {
    self->command = application;
    self->serviceId = sid;
    self->stamp = aStamp;
    self->stage = aStage;
    self->argument = arg;
    self->callback = nil;
    return self;
}

@end

///////////////////////////////////////////////////////////////////////////////
// Implementation of MessageQueue
///////////////////////////////////////////////////////////////////////////////
static const NSUInteger DEFAULT_QUEUE_SIZE = 16;
static const NSUInteger PACKAGE_SIZE_FIELD_LENGTH = 4;

static const Byte NOTIFY_TO_SEND_MESSAGE_BEAN = 0;
static const Byte NOTIFY_TO_STOP = 0xff;

@implementation BlockingQueue
- (BlockingQueue *) initWithCapacity:(NSUInteger)capacity {
    self = [super init];
    if (self) {
        array = [[NSMutableArray alloc] initWithCapacity:capacity];
        lock = [[NSCondition alloc] init];
    }
    
    return self;
}

- (void) dealloc {
    if (array != nil) {
        [array removeAllObjects];
        array = nil;
    }
    
    if (lock != nil) {
        lock = nil;
    }
}

- (BlockingQueue *) init {
    return [self initWithCapacity:DEFAULT_QUEUE_SIZE];
}

- (void) add:(id)object {
    if (object == nil) {
        return;
    }

    [lock lock];
    @try {
        [array addObject:object];
        [lock signal];
    } @finally {
        [lock unlock];
    }
}

- (id) take {
    [lock lock];
    @try {
        while ([array count] == 0) {
            [lock wait];
        }
        
        id object = [array objectAtIndex:0];
        [array removeObjectAtIndex:0];
        return object;
    } @finally {
        [lock unlock];
    }
}

- (id) poll {
    [lock lock];
    @try {
        if ([array count] == 0) {
            return nil;
        } else {
            id object = [array objectAtIndex:0];
            [array removeObjectAtIndex:0];
            return object;
        }
    } @finally {
        [lock unlock];
    }
}


- (NSUInteger) size {
    [lock lock];
    @try {
        return [array count];
    } @finally {
        [lock unlock];
    }
}

- (void) clear {
    [lock lock];
    @try {
        [array removeAllObjects];
    } @finally {
        [lock unlock];
    }
}
@end

///////////////////////////////////////////////////////////////////////////////
// Implementation of ClientStub
///////////////////////////////////////////////////////////////////////////////
static uint32_t STAMP = 0;

@implementation ClientStub

- (ClientStub*) initWithRpcSession:(RpcSession *)rs {
    session = rs;
    lock = [[NSCondition alloc] init];
    return self;
}

- (uint32_t) getStamp {
    [lock lock];
    @try {
        return ++STAMP;
    } @finally {
        [lock unlock];
    }
}

- (PBGeneratedMessage*) syncRpc:(uint32_t)serviceId :(PBGeneratedMessage *)arg {
    Message *request = [[Message alloc] initwithServiceId:serviceId stamp:[self getStamp] stage:STAGE_REQUEST argument:arg];
    BlockingQueue *queue = [[BlockingQueue alloc] initWithCapacity:1];
    request.callback = ^ (PBGeneratedMessage *response) {
        [queue add:response];
    };
    
    [session sendRequest:request];
    PBGeneratedMessage *response = [queue take];
    return response;
}

- (void) asyncRpc:(int32_t)serviceId :(PBGeneratedMessage *)arg :(CallbackBlock)callback {
    Message *request = [[Message alloc] initwithServiceId:serviceId stamp:[self getStamp] stage:STAGE_REQUEST argument:arg];
    request.callback = callback;
    [session sendRequest:request];
}

@end

///////////////////////////////////////////////////////////////////////////////
// Implementation of BytesOrderUtil
///////////////////////////////////////////////////////////////////////////////
@implementation BytesOrderUtil

+ (void) reverseBytesOrder:(void *)bytes :(int)size {
    Byte *p = (Byte *) bytes;
    int half = size / 2;
    Byte v;
    for (int i = 0, j = size - 1; i < half; ++i, --j) {
        v = p[i];
        p[i] = p[j];
        p[j] = v;
    }
}

+ (short) n2lShort:(short)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(short)];
#endif
    return v;
}

+ (int32_t) n2lInt32:(int32_t)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(int32_t)];
#endif
    return v;
}

+ (long long) n2lLong:(long long)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(long long)];
#endif
    return v;
}

+ (float) n2lFloat:(float)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(float)];
#endif
    return v;
}

+ (double) n2lDouble:(double)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(double)];
#endif
    return v;
}

+ (short) l2nShort:(short)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(short)];
#endif
    return v;
}

+ (int32_t) l2nInt32:(int32_t)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(int32_t)];
#endif
    return v;
}

+ (long long) l2nLong:(long long)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(long long)];
#endif
    return v;
}

+ (float) l2nFloat:(float)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(float)];
#endif
    return v;
}

+ (double) l2nDouble:(double)v {
#if BYTE_ORDER == LITTLE_ENDIAN
    [self reverseBytesOrder:&v :sizeof(double)];
#endif
    return v;
}

@end

///////////////////////////////////////////////////////////////////////////////
// Implementation of Segment
///////////////////////////////////////////////////////////////////////////////
@implementation Segment

@synthesize totalRecvBytesNumber;

-(Segment *) init {
    self = [super init];
    if (self) {
        buffer = [[NSMutableData alloc] initWithLength:DEFAULT_BUFFER_CHUNK_SIZE];
        totalRecvBytesNumber = 0;
        
        [self reset];
    }
    
    return self;
}

-(void) reset {
    phase = read_segment_size;
    needBytes = PACKAGE_SIZE_FIELD_LENGTH;
    gotBytes = 0;
    messageSize = 0;
}

- (NSUInteger) extendBuffer:(NSUInteger)expectedCapacity {
    NSInteger gap = expectedCapacity - [buffer length];
    if (gap > 0) {
        NSInteger cnt = gap / DEFAULT_BUFFER_CHUNK_SIZE + 1;
        for (int i = 0; i < cnt; ++i) {
            [buffer increaseLengthBy:DEFAULT_BUFFER_CHUNK_SIZE];
        }
    }
    
    return [buffer length];
}

-(size_t) recv:(int) sock :(void *) buf :(size_t) len {
    size_t cnt = recv(sock, buf, len, 0);
    totalRecvBytesNumber += cnt;
    return cnt;
}

-(Message *) action:(int) sock {
    size_t cnt;
    uint8_t *ptr = (uint8_t*) [buffer mutableBytes];
    
    if (phase == read_segment_size) {
        if (needBytes > 0) {
            cnt = [self recv:sock :ptr + gotBytes :needBytes];
            needBytes -= cnt;
            gotBytes += cnt;
        }
        
        if (needBytes == 0) {
            messageSize = [BytesOrderUtil n2lInt32:*((int32_t*) ptr)];
            needBytes = messageSize;
            gotBytes = 0;
            phase = read_segment_content;
            [self extendBuffer:messageSize];
        }
        
        return nil;
    } else {
        cnt = [self recv:sock  :ptr + gotBytes :needBytes];
        needBytes -= cnt;
        gotBytes += cnt;
        
        if (needBytes == 0) {
            // stamp int32_t (4 bytes)
            int32_t stamp = [BytesOrderUtil n2lInt32:*((int32_t*) (ptr))];
            ptr += sizeof(int32_t);
            
            // serviceId int32_t (4 bytes)
            int32_t serviceId = [BytesOrderUtil n2lInt32:*((int32_t*) (ptr))];
            ptr += sizeof(int32_t);
            
            // stage byte (1 bytes)
            Byte stage = *ptr;
            ptr += sizeof(Byte);
            
            // 反序列化Protobuf

            Message *bean;
            
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

@implementation Endpoint

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

-(Endpoint *) init {
    self = [super init];
    if (self) {
        serverHost = nil;
        serverPort = 0;
        
        hostLink = -1;
        localSocketPair[0] = -1;
        localSocketPair[1] = -1;
        
        sendQueue = [[BlockingQueue alloc] init];
        recvQueue = [[BlockingQueue alloc] init];
        
        segment = [[Segment alloc] init];
        
        threadLock = [[NSCondition alloc] init];
        totalSendBytesNumber = 0;
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
    
    serverHost = nil;
    serverPort = 0;
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
}

-(void) sendMessage:(Message *)message {
    //NSLog(@"message.commandID: %d",message.commandId);
    [sendQueue add:message];
    send(localSocketPair[1], &NOTIFY_TO_SEND_MESSAGE_BEAN, sizeof(Byte), 0);
}

-(Message *) recvMessage {
    return [recvQueue poll];
}

-(void) doSend:(Message *) bean {
//    NSUInteger pkgSize = 0;
//    
//    // 预留一个整数（4个字节）的报文长度
//    [obuffer writeInt32:0];
//    
//    // 写入一个字节的SegmentHead类型信息
//    // 当前客户端固定为 1
//    [obuffer writeByte:1];
//    
//    // 写入完整SegmentHead信息
//
//    
//    // 写入MessageBean
//    //    [MessageBuffer pack:commandId :bean :obuffer];
//    
//    // 回填报文长度
//    pkgSize = [obuffer current] - sizeof(int);
//    pkgSize = [IOBuffer l2nInt32:(int) pkgSize];
//    [obuffer replaceBytes:&pkgSize :(NSRange) {0, sizeof(int)}];
//    
//    [obuffer flip];
//    
//    NSUInteger total = [obuffer limit];
//    NSUInteger count = total / DEFAULT_BUFFER_CHUNK_SIZE;
//    NSUInteger rest = total % DEFAULT_BUFFER_CHUNK_SIZE;
//    size_t cnt = 0;
//    for (int i = 0; i < count; ++i) {
//        [obuffer getBytes:bytes withLength:DEFAULT_BUFFER_CHUNK_SIZE];
//        cnt = send(hostLink, bytes, DEFAULT_BUFFER_CHUNK_SIZE, 0);
//        totalSendBytesNumber += cnt;
//    }
//    
//    [obuffer getBytes:bytes withLength:rest];
//    cnt = send(hostLink, bytes, rest, 0);
//    totalSendBytesNumber += cnt;
}

- (void) notityLinkLost {
    
}

-(void) run {
    [threadLock lock];
    fd_set rset;
    int maxfd = MAX(hostLink, localSocketPair[0]) + 1;
    int nread = 0;
    Byte notification = 0;
    Message *message;
    time_t lastRead = time(NULL);
    time_t current;
    struct timeval tval;
    
    bzero(&tval, sizeof(tval));
    while (TRUE) {
        message = nil;
        FD_ZERO(&rset);
        FD_SET(hostLink, &rset);
        FD_SET(localSocketPair[0], &rset);
        
        select(maxfd, &rset, NULL, NULL, NULL);
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
            } else {
                if (message != nil) {
                    [recvQueue add:message];
                }
            }
        }
        
        current = time(NULL);
        
        if (FD_ISSET(localSocketPair[0], &rset)) {
            recv(localSocketPair[0], &notification, sizeof(Byte), 0);
            if (notification == NOTIFY_TO_STOP) { // 结束线程运行
                break;
            } else {
                message = [sendQueue poll];
                [self doSend:message];
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

///////////////////////////////////////////////////////////////////////////////
// Implementation of RpcSession
///////////////////////////////////////////////////////////////////////////////
@implementation RpcSession
- (RpcSession*) initWithEndpoint:(Endpoint *)ep {
    endpoint = ep;
    return self;
}

- (void) sendRequest:(Message *)message {
    [endpoint sendMessage:message];
}

@end

