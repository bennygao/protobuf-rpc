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

//static NSString* formatReason(const char *file, const int line, const char *format, ...) {
//    va_list ap;
//    va_start(ap, format);
//    char tmpstr[256];
//    bzero(tmpstr, sizeof(tmpstr));
//    vsnprintf(tmpstr, sizeof(tmpstr) - 1, format, ap);
//    va_end(ap);
//    return [NSString stringWithCString:tmpstr encoding:NSASCIIStringEncoding];
//}

///////////////////////////////////////////////////////////////////////////////
// Implementation of Message
///////////////////////////////////////////////////////////////////////////////
static const Byte MASK_RESPONSE = 0x01;
static const Byte MASK_SERVICE_NOT_EXIST = 0x02;
static const Byte MASK_RPC_CANCELED = 0x04;
static const Byte MASK_SERVICE_EXCEPTION = 0x08;

@implementation Message

@synthesize serviceId;
@synthesize stamp;
@synthesize feature;
@synthesize argument;
@synthesize responseHandle;

- (Message*) initwithServiceId:(int32_t)sid stamp:(int32_t)aStamp argument:(PBGeneratedMessage *)arg {
    serviceId = sid;
    stamp = aStamp;
    feature = 0;
    argument = arg;
    responseHandle = nil;
    return self;
}

- (Message*) createResponse:(PBGeneratedMessage *)argument {
    return nil;
}

- (void) setToRequest {
    feature &= ~MASK_RESPONSE;
}

- (void) setToResponse {
    feature |= MASK_RESPONSE;
}

- (BOOL) isRequest {
    return false;
}

- (BOOL) isResponse {
    return false;
}

- (void) setServiceNotExist {
    feature |= MASK_SERVICE_NOT_EXIST;
}

- (BOOL) isServiceNotExist {
    return (feature & MASK_SERVICE_NOT_EXIST) != 0;
}

- (void) setRpcCanceled {
    feature |= MASK_RPC_CANCELED;
}

- (BOOL) isRpcCanceled {
    return (feature & MASK_RPC_CANCELED) != 0;
}

- (void) setServiceException {
    feature |= MASK_SERVICE_EXCEPTION;
}

- (BOOL) isServiceException {
    return (feature & MASK_SERVICE_EXCEPTION) != 0;
}

+ (BOOL) isRequest:(Byte)feature {
    return (feature & MASK_RESPONSE) == 0;
}

+ (BOOL) isResponse:(Byte)feature {
    return (feature & MASK_RESPONSE) != 0;
}

+ (Message*) createMessageWithServiceId:(int32_t)sid stamp:(int32_t)stamp feature:(Byte)feature argument:(PBGeneratedMessage*)arg {
    Message *message = nil;
    if ([Message isRequest:feature]) {
        message = [[RequestMessage alloc] initwithServiceId:sid stamp:stamp argument:arg];
    } else {
        message = [[ResponseMessage alloc] initwithServiceId:sid stamp:stamp argument:arg];
    }
    
    message.feature = feature;
    return message;
}

@end

@implementation RequestMessage

- (RequestMessage*) initwithServiceId:(int32_t)sid stamp:(int32_t)stamp argument:(PBGeneratedMessage*)arg {
    [super initwithServiceId:sid stamp:stamp argument:arg];
    [self setToRequest];
    return self;
}

- (BOOL) isRequest {
    return true;
}

- (BOOL) isResponse {
    return false;
}

- (Message*) createResponse:(PBGeneratedMessage *)argument {
    return [[ResponseMessage alloc] initwithServiceId:self.serviceId stamp:self.stamp argument:argument];
}

@end

@implementation ResponseMessage

- (ResponseMessage*) initwithServiceId:(int32_t)sid stamp:(int32_t)stamp argument:(PBGeneratedMessage*)arg {
    [super initwithServiceId:sid stamp:stamp argument:arg];
    [self setToResponse];
    return self;
}

- (BOOL) isRequest {
    return false;
}

- (BOOL) isResponse {
    return true;
}

- (Message*) createResponse:(PBGeneratedMessage *)argument {
    return nil;
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

- (BlockingQueue *) init {
    return [self initWithCapacity:DEFAULT_QUEUE_SIZE];
}

- (BlockingQueue *) initWithCapacity:(NSUInteger)capacity {
    array = [[NSMutableArray alloc] initWithCapacity:capacity];
    lock = [[NSCondition alloc] init];
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
- (ClientStub*) init {
     @throw [NSException exceptionWithName:@"UnsupportedOperationException" reason:@"must initWithRpcSession" userInfo:nil];
}

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

- (PBGeneratedMessage*) syncRpc:(int32_t)serviceId :(PBGeneratedMessage *)arg {
    Message *request = [[RequestMessage alloc] initwithServiceId:serviceId stamp:[self getStamp] argument:arg];
    BlockingQueue *queue = [[BlockingQueue alloc] initWithCapacity:1];
    request.responseHandle = ^(Message *message) {
        [queue add:message];
    };
    
    [session sendRequest:request];
    Message *response = [queue take];
    if ([response isServiceNotExist]) {
        @throw [NSException exceptionWithName:@"UnregisteredService"
                                       reason:@"remote endpoint doesn't register invoked service."
                                     userInfo:nil];
    } else if ([response isRpcCanceled]) {
        @throw [NSException exceptionWithName:@"RpcCanceled"
                                       reason:@"synchronized rpc be canceled."
                                     userInfo:nil];
    } else if ([response isServiceException]) {
        @throw [NSException exceptionWithName:@"ServiceProcessException"
                                       reason:@"remote endpoint process service occured exception."
                                     userInfo:nil];
    } else {
        return response.argument;
    }
}

- (void) asyncRpc:(int32_t)serviceId :(PBGeneratedMessage *)arg :(CallbackBlock)callback {
    Message *request = [[RequestMessage alloc] initwithServiceId:serviceId stamp:[self getStamp] argument:arg];
    request.responseHandle = ^(Message *message) {
        if ([message isServiceNotExist]) {
            callback(nil, service_not_exist);
        } else if ([message isRpcCanceled]) {
            callback(nil, rpc_canceled);
        } else if ([message isServiceException]) {
            callback(nil, service_exception);
        }else {
            callback(message.argument, success);
        }
    };
    [session sendRequest:request];
}

@end

///////////////////////////////////////////////////////////////////////////////
// Implementation of IOBuffer
///////////////////////////////////////////////////////////////////////////////
@implementation IOBuffer

@synthesize dataBuffer;
@synthesize limit;
@synthesize current;

- (IOBuffer *) init {
    return [self initWithCapacity:DEFAULT_BUFFER_CHUNK_SIZE];
}

- (IOBuffer *) initWithCapacity:(NSUInteger)capacity {
    self = [super init];
    if (self) {
        dataBuffer = [[NSMutableData alloc] initWithLength:capacity];
        limit = capacity;
        current = 0;
    }
    
    return self;
}

- (void) rewind {
    current = 0;
}

- (void) flip {
    limit = current;
    current = 0;
}

- (void) clear {
    current = 0;
    limit = [dataBuffer length];
}

- (NSUInteger) remaining {
    return limit - current;
}

- (NSUInteger) moveCurrent:(NSInteger)adjust {
    current += adjust;
    return current;
}

- (Byte*) currentPtr {
    Byte *ptr = (Byte*) [dataBuffer mutableBytes];
    return ptr + current;
}

- (void) increaseCurrent:(NSUInteger) length {
    current += length;
}

- (NSUInteger) copyData:(void *) dest {
    return [self copyData:dest :(NSRange) {0, limit}];
}

- (NSUInteger) copyData:(void *) dest :(NSRange) range {
    [dataBuffer getBytes:dest range:range];
    return range.length - range.location;
}

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

- (void) getBytes:(void*)dest withLength:(NSUInteger)length {
    if ([self remaining] < length) {
        NSNumber *ln = [NSNumber numberWithInt:__LINE__];
        NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:
                              @"File:", [NSString stringWithUTF8String:__FILE__],
                              @"Line:", [ln stringValue],
                              @"Cause:", @"N/A",
                              @"Stack:", @"N/A",
                              nil];
        @throw [NSException exceptionWithName:@"IOBuffer#getBytes" reason:@"buffer overflow" userInfo:dict];
    } else {
        [dataBuffer getBytes:dest range:(NSRange) {current, length}];
        [self increaseCurrent:length];
    }
}

- (Byte) readByte {
    Byte v;
    [self getBytes:&v withLength:sizeof(Byte)];
    return v;
}

- (short) readShort {
    short v = 0;
    [self getBytes:&v withLength:sizeof(short)];
    return [IOBuffer n2lShort:v];
}

- (int32_t) readInt32 {
    int v = 0;
    [self getBytes:&v withLength:sizeof(int32_t)];
    return [IOBuffer n2lInt32:v];
}

- (long long) readLong {
    long long v = 0;
    [self getBytes:&v withLength:sizeof(long long)];
    return [IOBuffer n2lLong:v];
}

- (float) readFloat {
    float v = 0.00;
    [self getBytes:(&v) withLength:sizeof(float)];
    return [IOBuffer n2lFloat:v];
}

- (double) readDouble {
    double v = 0.00;
    [self getBytes:&v withLength:sizeof(double)];
    return [IOBuffer n2lDouble:v];
}

- (NSString*) readUTF {
    NSUInteger len = [self readShort];
    char cstr[len];
    [self getBytes:cstr withLength:len];
    return [[NSString alloc] initWithBytes:cstr length:len encoding:NSUTF8StringEncoding];
}

- (void) extendBufferToContain:(NSUInteger) length {
    NSInteger gap = [dataBuffer length] - current - length;
    if (gap < 0) {
        NSInteger num = (- gap) / DEFAULT_BUFFER_CHUNK_SIZE + 1;
        NSInteger increase = num * DEFAULT_BUFFER_CHUNK_SIZE;
        [dataBuffer increaseLengthBy:increase];
        limit = [dataBuffer length];
    }
}

- (void) replaceBytes:(const void *)src :(NSRange)range {
    [self->dataBuffer replaceBytesInRange:range withBytes:src];
}

- (void) putBytes:(const void *)src :(NSUInteger)length {
    [self extendBufferToContain:length];
    NSRange range = (NSRange) {current, length};
    [self->dataBuffer replaceBytesInRange:range withBytes:src];
    [self increaseCurrent:length];
}

- (void) putData:(NSData *) data {
    [self putBytes:[data bytes] :[data length]];
}

- (void) writeByte:(Byte)v {
    [self putBytes:&v :sizeof(Byte)];
}

- (void) writeShort:(short)v {
    v = [IOBuffer l2nShort:v];
    [self putBytes:&v :sizeof(short)];
}

- (void) writeInt32:(int32_t)v {
    v = [IOBuffer l2nInt32:v];
    [self putBytes:&v :sizeof(int32_t)];
}

- (void) writeLong:(long long)v {
    v = [IOBuffer l2nLong:v];
    [self putBytes:&v :sizeof(long long)];
}

- (void) writeFloat:(float)v {
    v = [IOBuffer l2nFloat:v];
    [self putBytes:&v :sizeof(float)];
}

- (void) writeDouble:(double)v {
    v = [IOBuffer l2nDouble:v];
    [self putBytes:&v :sizeof(double)];
}

- (void) writeUTF:(NSString *)v {
    short len = [v lengthOfBytesUsingEncoding:NSUTF8StringEncoding];
    short nlen = [IOBuffer l2nShort:len];
    [self putBytes:&nlen :sizeof(short)];
    [self putBytes:(void*) [v cStringUsingEncoding:NSUTF8StringEncoding] :len];
}

@end


///////////////////////////////////////////////////////////////////////////////
// Implementation of Segment
///////////////////////////////////////////////////////////////////////////////
@implementation Segment

@synthesize totalRecvBytesNumber;

- (Segment *) initWithEndpoint:(Endpoint*) ep {
    buffer = [[IOBuffer alloc] init];
    endpoint = ep;
    totalRecvBytesNumber = 0;
    [self reset];
    return self;
}

- (void) reset {
    phase = read_segment_size;
    needBytes = PACKAGE_SIZE_FIELD_LENGTH;
    gotBytes = 0;
    messageSize = 0;
    [buffer clear];
}

- (size_t) recv:(int) sock :(void *) buf :(size_t) len {
    size_t cnt = recv(sock, buf, len, 0);
    totalRecvBytesNumber += cnt;
    return cnt;
}

- (Message *) action:(int) sock {
    size_t cnt;
    Byte *ptr = (Byte*) [buffer currentPtr];
    
    if (phase == read_segment_size) {
        if (needBytes > 0) {
            cnt = [self recv:sock :ptr + gotBytes :needBytes];
            needBytes -= cnt;
            gotBytes += cnt;
        }
        
        if (needBytes == 0) {
            [buffer moveCurrent:PACKAGE_SIZE_FIELD_LENGTH];
            [buffer flip];
            messageSize = [buffer readInt32];
            
            needBytes = messageSize;
            gotBytes = 0;
            phase = read_segment_content;
            
            [buffer clear];
            [buffer extendBufferToContain:messageSize];
        }
        
        return nil;
    } else {
        cnt = [self recv:sock  :ptr + gotBytes :needBytes];
        needBytes -= cnt;
        gotBytes += cnt;
        Message *message = nil;
        
        if (needBytes == 0) {
            [buffer moveCurrent:messageSize];
            [buffer flip];
            
            // stamp int32_t (4 bytes)
            int32_t stamp = [buffer readInt32];
            
            // serviceId int32_t (4 bytes)
            int32_t serviceId = [buffer readInt32];
            
            // stage byte (1 bytes)
            Byte feature = [buffer readByte];
            
            // 反序列化Protobuf
            id<RpcServiceRegistry> service = [endpoint getService:serviceId];
            PBGeneratedMessage *protobuf = nil;
            
            if (service == nil) { // service不存在
                message = [Message createMessageWithServiceId:serviceId stamp:stamp feature:feature argument:nil];
                [message setServiceNotExist];
            } else {
                PBGeneratedMessageBuilder *builder;
                if ([Message isRequest:feature]) {
                    builder = [service getBuilderForRequest:serviceId];
                } else {
                    builder = [service getBuilderForResponse:serviceId];
                }
                
                NSData *idata = [NSData dataWithBytes:[buffer currentPtr] length:messageSize - 9];
                protobuf = [[builder mergeFromData:idata] build];
                message = [Message createMessageWithServiceId:serviceId stamp:stamp feature:feature argument:protobuf];
            }
            
            // 调整reading的阶段和buffer状态
            phase = read_segment_size;
            needBytes = PACKAGE_SIZE_FIELD_LENGTH;
            gotBytes = 0;
            [buffer clear];
        
            
        }
        
        return message;
    }
}

@end


///////////////////////////////////////////////////////////////////////////////
// Implementation of Segment
///////////////////////////////////////////////////////////////////////////////
const static int DEFAULT_CHECKING_HEARTBEAT_INTERVAL = 60;

@implementation Endpoint

@synthesize serverHost;
@synthesize serverPort;

- (NSString *) getServerIpAddress {
    if (serverHost == nil) {
        return nil;
    }
    
    struct hostent *host = gethostbyname([serverHost UTF8String]);
    if (host == NULL) {
        return nil;
    }

    struct in_addr **list = (struct in_addr **) host->h_addr_list;
    return [NSString stringWithCString:inet_ntoa(*list[0])
                              encoding:NSASCIIStringEncoding];
}

- (void) setNonBlockSocket:(int) sock {
    int flags = fcntl(sock, F_GETFL, 0);
    if (fcntl(sock, F_SETFL, flags | O_NONBLOCK) == -1) {
        @throw [NSException exceptionWithName:@"SocketEngine#setNonBlock"
                                       reason:GET_REASON("fcntl()")
                                     userInfo:nil];
        
    }
}

- (void) initHostLink:(NSUInteger)timeout {
    hostLink = socket(AF_INET, SOCK_STREAM, 0);
    if (hostLink <= 0) {
        @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                       reason:GET_REASON("socket()")
                                     userInfo:nil];
    } else {
        [self setNonBlockSocket:hostLink];
    }
    
    NSString *serverIpAddress = [self getServerIpAddress];
    if (serverIpAddress == nil) {
        @throw [NSException exceptionWithName:@"SocketEngine#initHostLink"
                                       reason:GET_REASON("gethostbyname()")
                                     userInfo:nil];
    }
    
    fd_set rset, wset;
    struct timeval tval;
    struct sockaddr_in serverAddr;
    bzero(&serverAddr, sizeof(struct sockaddr_in));
    
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_addr.s_addr = inet_addr([serverIpAddress UTF8String]);
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

- (void) initLocalSocketPair {
    if (socketpair(AF_LOCAL, SOCK_STREAM, 0, localSocketPair)) {
        @throw [NSException exceptionWithName:@"SocketEngine#initLocalSocketPair"
                                       reason:GET_REASON("socketpair()")
                                     userInfo:nil];
        
    }
    
    [self setNonBlockSocket:localSocketPair[0]];
}

- (Endpoint*) init {
    self = [self initWithHeartbeatInterval:DEFAULT_CHECKING_HEARTBEAT_INTERVAL];
    return self;
}

- (Endpoint *) initWithHeartbeatInterval:(int)hbi {
    serverHost = nil;
    serverPort = 0;
    
    hostLink = -1;
    localSocketPair[0] = -1;
    localSocketPair[1] = -1;
    
    sendQueue = [[BlockingQueue alloc] init];
    
    segment = [[Segment alloc] initWithEndpoint:self];
    serviceRegistry = [[NSMutableDictionary alloc] init];
    buffer = [[IOBuffer alloc] init];
    stampsMap = [[NSMutableDictionary alloc] init];
    operationQueue = [[NSOperationQueue alloc] init];
    
    threadLock = [[NSLock alloc] init];
    totalSendBytesNumber = 0;
    
    heartbeatInterval = hbi;
    heartbeatMessage = [[RequestMessage alloc] initwithServiceId:0 stamp:0 argument:nil];
    isCheckingHeartbeat = false;
    
    return self;
}

- (void) registerService:(id<RpcServiceRegistry>)service {
    NSArray *sidList = [service getServiceList];
    for (int i = 0; i < [sidList count]; ++i) {
        [serviceRegistry setObject:service forKey:[sidList objectAtIndex:i]];
    }
}

- (void) unregisterService:(id<RpcServiceRegistry>)service {
    NSArray *sidList = [service getServiceList];
    for (int i = 0; i < [sidList count]; ++i) {
        [serviceRegistry removeObjectForKey:[sidList objectAtIndex:i]];
    }
}

- (id<RpcServiceRegistry>) getService:(int32_t)serviceId {
    NSNumber *key = [NSNumber numberWithInt:serviceId];
    return [serviceRegistry objectForKey:key];
}

- (BOOL) connectToHost:(NSString *)addr withPort:(ushort)port inSeconds:(NSUInteger)timeout {
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
    while ([threadLock tryLock]) {
        [threadLock unlock];
        [NSThread sleepForTimeInterval:0.5f];
    }
}

-(void) clear {
    serverHost = nil;
    serverPort = 0;
    
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
    
    isCheckingHeartbeat = false;
    [sendQueue clear];
    [segment reset];
    [buffer clear];
    [stampsMap removeAllObjects];
    [operationQueue cancelAllOperations];
}

- (void) cancelAllWaitingCall {
    Message *cancelMessage = [[ResponseMessage alloc] initwithServiceId:0 stamp:0 argument:nil];
    [cancelMessage setRpcCanceled];
    
    NSArray *handles = [stampsMap allValues];
    for (int i = 0; i < [handles count]; ++i) {
        ResponseHandle handle = [handles objectAtIndex:i];
        handle(cancelMessage);
    }
    [stampsMap removeAllObjects];
}

-(void) stop {
    if (hostLink <= 0) {
        return;
    }
    
    send(localSocketPair[1], &NOTIFY_TO_STOP, sizeof(Byte), 0);
    
    [threadLock lock]; // 等待网络读写线程完全退出
    [self cancelAllWaitingCall];
    [self clear];
    [threadLock unlock];
}

-(void) sendMessage:(Message *)message {
    if ([threadLock tryLock]) {
        [threadLock unlock];
        @throw [NSException exceptionWithName:@"Endpoint#sendMessage"
                                       reason:@"Endpoint has already stopped or hasn't been started."
                                     userInfo:nil];
    }
    [sendQueue add:message];
    send(localSocketPair[1], &NOTIFY_TO_SEND_MESSAGE_BEAN, sizeof(Byte), 0);
}

-(void) doSend:(Message *) message {
    uint32_t messageSize = 9;
    SInt32 serializedSize = 0;
    if (message.argument != nil) {
        serializedSize = [message.argument serializedSize];
        
    }
    messageSize += serializedSize;

    [buffer clear];
    [buffer extendBufferToContain:messageSize + 4];
    
    // 报文长度, int32(4bytes)
    [buffer writeInt32:messageSize];
    
    // stamp, int32(4bytes)
    [buffer writeInt32:message.stamp];
    
    // serviceId, int32(4bytes)
    [buffer writeInt32:message.serviceId];
    
    // stage, 1byte
    [buffer writeByte:message.feature];

    // protobuf
    if (message.argument != nil) {
        NSOutputStream* ostream = [[NSOutputStream alloc] initToBuffer:[buffer currentPtr] capacity:serializedSize];
        [ostream open];
        [message.argument writeToOutputStream:ostream];
        [ostream close];
        [buffer increaseCurrent:serializedSize];
    }
    
    // 向remote endpoint发送数据
    [buffer flip];
    Byte *ptr = [buffer currentPtr];
    NSUInteger total = [buffer limit];
    NSUInteger sentBytes = 0;
    NSUInteger remaining = total;
    
    size_t cnt = 0;
    while (remaining > 0) {
        cnt = send(hostLink, ptr + sentBytes, remaining, 0);
        sentBytes += cnt;
        remaining -= cnt;
        totalSendBytesNumber += cnt;
    }
    
    // 如果是请求
    if ([message isRequest]) {
        int32_t stamp = message.stamp;
        [stampsMap setObject:message.responseHandle forKey:[NSNumber numberWithInt:stamp]];
    }
}

- (void) handleMessage:(Message*)message {
    int32_t serviceId = message.serviceId;
    int32_t stamp = message.stamp;
    NSBlockOperation *operation = nil;
    
    if (serviceId == 0) { // heartbeat消息
        if ([message isRequest]) {
            [self sendMessage:[message createResponse:nil]];
        } else {
            isCheckingHeartbeat = false;
        }
    } else if ([message isRequest]) { // 收到请求
        id<RpcServiceRegistry> registry = [self getService:serviceId];
        if (registry == nil) {
            NSLog(@"%@", [NSString stringWithFormat:@"Unregistered handle for serviceId:%d", serviceId]);
            Message *response = [[ResponseMessage alloc] initwithServiceId:serviceId stamp:stamp argument:nil];
            [response setServiceNotExist];
            [self sendMessage:response];
        } else {
            operation = [NSBlockOperation blockOperationWithBlock:^() {
                int32_t stamp = message.stamp;
                PBGeneratedMessage *result = [registry invokeService:serviceId :message.argument];
                Message *response = [[ResponseMessage alloc] initwithServiceId:serviceId stamp:stamp argument:result];
                [self sendMessage:response];
            }];
        }
    } else { // 收到响应
        NSNumber *key = [NSNumber numberWithInt:stamp];
        ResponseHandle handle = [stampsMap objectForKey:key];
        [stampsMap removeObjectForKey:key];
        
        if (handle == nil) {
            NSString *info =[NSString stringWithFormat:@"Unregistered handle for STAMP:%d", stamp];
            NSLog(@"%@", info);
        } else {
            operation = [NSBlockOperation blockOperationWithBlock:^() {
                handle(message);
            }];
        }
    }
    
    if (operation != nil) {
        [operationQueue addOperation:operation];
    }
}

-(void) run {
    [threadLock lock];
    fd_set rset;
    int maxfd = MAX(hostLink, localSocketPair[0]) + 1;
    int nread = 0;
    Byte notification = 0;
    Message *message;
    struct timeval tval;
    
    bzero(&tval, sizeof(tval));
    tval.tv_sec = heartbeatInterval;
    while (TRUE) {
        message = nil;
        FD_ZERO(&rset);
        FD_SET(hostLink, &rset);
        FD_SET(localSocketPair[0], &rset);
        
        if (heartbeatInterval > 0) {
            nread = select(maxfd, &rset, NULL, NULL, &tval);
        } else {
            nread = select(maxfd, &rset, NULL, NULL, NULL);
        }
        
        if (nread < 0) { // socket错误
            break;
        } else if (nread == 0) { // 空闲
            if (!isCheckingHeartbeat) {
                [self sendMessage:heartbeatMessage];
                isCheckingHeartbeat = true;
            } else { // heartbeat检测失败，判定为连接已经丢失。
                break;
            }
        }
        
        if (FD_ISSET(hostLink, &rset)) {
            ioctl(hostLink, FIONREAD, &nread);
            if (nread == 0) { // 服务器端关闭了链接
#ifdef DEBUG
                NSLog(@"RPC session link closed by remote endpoint.");
#endif
                // 结束线程循环，退出线程
                break;
            } else {
                Message *message = [segment action:hostLink];
                if (message != nil) {
                    [self handleMessage:message];
                }
            }
        }
        
        
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
    
    [threadLock unlock];
}

- (NSUInteger) getTotalRecvBytesNumber {
    return segment.totalRecvBytesNumber;
}

- (NSUInteger) getTotalSendBytesNumber {
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

