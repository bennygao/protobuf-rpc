//
//  Endpoint.h
//  cocoa
//
//  Created by Benny Gao on 15/5/9.
//  Copyright (c) 2015年 Benny Gao. All rights reserved.
//

#ifndef __PROTOBUF_RPC_H__
#define __PROTOBUF_RPC_H__

#import <Foundation/Foundation.h>
#import <ProtocolBuffers/GeneratedMessage.h>
#include <ProtocolBuffers/ProtocolBuffers.h>

#define DEFAULT_BUFFER_CHUNK_SIZE   1024

typedef enum {
    success,
    service_not_exist,
    rpc_canceled,
    service_exception
} RpcState;

@class Message;
typedef void (^ ResponseHandle)(Message*);
typedef void (^ CallbackBlock)(PBGeneratedMessage*, RpcState);


@interface Message : NSObject {
@private
    int32_t serviceId;
    uint32_t stamp;
    Byte feature;
    PBGeneratedMessage *argument;
    ResponseHandle responseHandle;
}

@property (readonly) int32_t serviceId;
@property (readonly) uint32_t stamp;
@property (readwrite) Byte feature;
@property (readonly, strong) PBGeneratedMessage* argument;
@property (strong) ResponseHandle responseHandle;

- (Message*) initwithServiceId:(int32_t)sid stamp:(int32_t)stamp argument:(PBGeneratedMessage*)arg;

- (void) setToRequest;
- (void) setToResponse;
- (BOOL) isRequest;
- (BOOL) isResponse;

- (void) setServiceNotExist;
- (BOOL) isServiceNotExist;

- (void) setRpcCanceled;
- (BOOL) isRpcCanceled;

- (void) setServiceException;
- (BOOL) isServiceException;
- (Message*) createResponse:(PBGeneratedMessage*) argument;

+ (BOOL) isRequest:(Byte)feature;
+ (BOOL) isResponse:(Byte)feature;
+ (Message*) createMessageWithServiceId:(int32_t)sid stamp:(int32_t)stamp feature:(Byte)feature argument:(PBGeneratedMessage*)arg;
@end

@interface RequestMessage : Message
- (RequestMessage*) initwithServiceId:(int32_t)sid stamp:(int32_t)stamp argument:(PBGeneratedMessage*)arg;
- (BOOL) isRequest;
- (BOOL) isResponse;
@end

@interface ResponseMessage : Message
- (ResponseMessage*) initwithServiceId:(int32_t)sid stamp:(int32_t)stamp argument:(PBGeneratedMessage*)arg;
- (BOOL) isRequest;
- (BOOL) isResponse;
@end

@class Endpoint;


@interface RpcSession : NSObject {
@private
    Endpoint *endpoint;
}

- (RpcSession*) initWithEndpoint:(Endpoint*)endpoint;
- (void) sendRequest:(Message*) message;
@end

@protocol RpcServiceRegistry <NSObject>
@required
- (NSArray*) getServiceList;
- (PBGeneratedMessageBuilder*) getBuilderForRequest:(int32_t)serviceId;
- (PBGeneratedMessageBuilder*) getBuilderForResponse:(int32_t)serviceId;
- (PBGeneratedMessage*) invokeService:(int32_t)serviceId :(PBGeneratedMessage*)arg;
@end

@interface ClientStub : NSObject {
@private
    RpcSession *session;
    NSCondition *lock;
}

- (ClientStub*) initWithRpcSession:(RpcSession*) rs;
- (uint32_t) getStamp;
- (PBGeneratedMessage*) syncRpc:(int32_t) serviceId :(PBGeneratedMessage*) arg;
- (void) asyncRpc:(int32_t) serviceId :(PBGeneratedMessage*) arg :(CallbackBlock) callback;
@end


@interface BlockingQueue : NSObject {
@private
    NSMutableArray *array;
    NSCondition *lock;
}

- (BlockingQueue *) init;
- (BlockingQueue *) initWithCapacity:(NSUInteger) capacity;

- (void) add:(id) object;
- (id) take;
- (id) poll;
- (NSUInteger) size;
- (void) clear;
@end

typedef enum  {
    read_segment_size = 1,
    read_segment_content = 2
} SEGMENT_PHASE;

@interface IOBuffer : NSObject {
    NSMutableData *dataBuffer;
    NSUInteger limit;
    NSUInteger current;
}

@property (readonly) NSMutableData *dataBuffer;
@property (readwrite) NSUInteger limit;
@property (readonly) NSUInteger current;

- (IOBuffer *) init;
- (IOBuffer *) initWithCapacity:(NSUInteger) capacity;

- (void) rewind;
- (void) flip;
- (void) clear;
- (NSUInteger) moveCurrent:(NSInteger) adjust;
- (NSUInteger) remaining;
- (Byte*) currentPtr;

- (void) increaseCurrent:(NSUInteger) length;
- (void) extendBufferToContain:(NSUInteger) length;
- (NSUInteger) copyData:(void *) dest;
- (NSUInteger) copyData:(void *) dest :(NSRange) range;

+ (void) reverseBytesOrder:(void*) bytes :(int) size;

+ (short) n2lShort:(short) v;
+ (int32_t) n2lInt32:(int32_t) v;
+ (long long) n2lLong:(long long) v;
+ (float) n2lFloat:(float) v;
+ (double) n2lDouble:(double) v;

+ (short) l2nShort:(short) v;
+ (int32_t) l2nInt32:(int32_t) v;
+ (long long)l2nLong:(long long) v;
+ (float) l2nFloat:(float) v;
+ (double) l2nDouble:(double) v;

- (void) getBytes:(void*)dest withLength:(NSUInteger)length;
- (Byte) readByte;
- (short) readShort;
- (int32_t) readInt32;
- (long long) readLong;
- (float) readFloat;
- (double) readDouble;
- (NSString*) readUTF;

- (void) replaceBytes:(const void *) src :(NSRange) range;
- (void) putBytes:(const void *) src :(NSUInteger) length;
- (void) putData:(NSData *) data;
- (void) writeByte:(Byte) v;
- (void) writeShort:(short) v;
- (void) writeInt32:(int32_t) v;
- (void) writeLong:(long long) v;
- (void) writeFloat:(float) v;
- (void) writeDouble:(double) v;
- (void) writeUTF:(NSString*) v;

@end

@interface Segment : NSObject {
@private
    SEGMENT_PHASE phase;
    NSUInteger needBytes;
    NSUInteger gotBytes;
    NSUInteger messageSize;
    IOBuffer *buffer;
    Endpoint *endpoint;
    
    NSUInteger totalRecvBytesNumber;
}

@property (readonly) NSUInteger totalRecvBytesNumber;

- (Segment *) initWithEndpoint:(Endpoint*) ep;
- (void) reset;
- (Message *) action:(int)sock;
@end

@interface Endpoint : NSObject {
@private
    NSString *serverHost;
    ushort serverPort;
    
    int hostLink;
    int localSocketPair[2];
    
    BlockingQueue *sendQueue;
    
    IOBuffer *buffer;
    NSUInteger totalSendBytesNumber;
    
    NSMutableDictionary *serviceRegistry;
    Segment *segment;
    NSLock *threadLock;
    NSMutableDictionary *stampsMap;
    NSOperationQueue *operationQueue;
    
    int heartbeatInterval;
    Message* heartbeatMessage;
    BOOL isCheckingHeartbeat;
}

@property (readonly) NSString *serverHost;
@property (readonly) ushort serverPort;

- (Endpoint *) initWithHeartbeatInterval:(int) hbi;
- (NSString *) getServerIpAddress;

- (BOOL) connectToHost:(NSString *)addr withPort:(ushort)port inSeconds:(NSUInteger)timeout;
- (void) start;
- (void) stop;
- (void) run;

- (void) registerService:(id<RpcServiceRegistry>)service;
- (void) unregisterService:(id<RpcServiceRegistry>)service;
- (id<RpcServiceRegistry>) getService:(int32_t)serviceId;

- (void) sendMessage:(Message *) message;

- (NSUInteger) getTotalRecvBytesNumber;
- (NSUInteger) getTotalSendBytesNumber;
@end


#endif
