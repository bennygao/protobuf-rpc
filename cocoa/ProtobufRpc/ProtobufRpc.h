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

typedef void (^ CallbackBlock)(PBGeneratedMessage*);

typedef enum {
    application, // 应用消息
    cancel // 取消
} MESSAGE_COMMAND;


@interface Message : NSObject {
@private
    MESSAGE_COMMAND command;
    int32_t serviceId;
    uint32_t stamp;
    Byte stage;
    PBGeneratedMessage *argument;
    CallbackBlock callback;
}

@property (readonly) MESSAGE_COMMAND command;
@property (readonly) int32_t serviceId;
@property (readonly) uint32_t stamp;
@property (readonly) Byte stage;
@property (readonly, strong) PBGeneratedMessage* argument;
@property (strong) CallbackBlock callback;

- (Message*) initWithCommand:(MESSAGE_COMMAND)cmd;
- (Message*) initwithServiceId:(int32_t)sid;
- (Message*) initwithServiceId:(int32_t)sid stamp:(int32_t)stamp stage:(Byte)stage argument:(PBGeneratedMessage*)arg;

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
- (NSArray*) getServiceList;
- (PBGeneratedMessageBuilder*) getBuilderForRequest:(int32_t)serviceId;
- (PBGeneratedMessageBuilder*) getBuilderForResponse:(int32_t)serviceId;
- (PBGeneratedMessage*) invokeService:(int32_t)serviceId :(PBGeneratedMessage*)arg :(RpcSession*)session;
@end


@interface ClientStub : NSObject
- (PBGeneratedMessage*) syncRpc:(uint32_t) serviceId :(PBGeneratedMessage*) arg;
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

@interface BytesOrderUtil : NSObject
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
@end

@interface Segment : NSObject {
@private
    SEGMENT_PHASE phase;
    NSUInteger needBytes;
    NSUInteger gotBytes;
    NSUInteger messageSize;
    NSMutableData *buffer;
    
    NSUInteger totalRecvBytesNumber;
}

@property (readonly) NSUInteger totalRecvBytesNumber;

- (Segment *) init;
- (void) reset;
- (NSUInteger) extendBuffer:(NSUInteger) expectedCapacity;
@end

@interface Endpoint : NSObject {
@private
    NSString *serverHost;
    ushort serverPort;
    
    int hostLink;
    int localSocketPair[2];
    
    BlockingQueue *sendQueue;
    BlockingQueue *recvQueue;
    
    Byte bytes[DEFAULT_BUFFER_CHUNK_SIZE];
    NSUInteger totalSendBytesNumber;
    
    Segment *segment;
    NSCondition *threadLock;
}

@property (readonly) NSString *serverHost;
@property (readonly) ushort serverPort;

-(Endpoint *) init;
-(NSString *) getServerIpAddress;

-(BOOL) connectToHost:(NSString *)addr withPort:(ushort)port inSeconds:(NSUInteger)timeout;
-(void) start;
-(void) stop;
-(void) run;

-(void) sendMessage:(Message *) bean;
-(Message *) recvMessageBean;

-(NSUInteger) getTotalRecvBytesNumber;
-(NSUInteger) getTotalSendBytesNumber;
@end


#endif
