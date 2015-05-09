//
//  Endpoint.h
//  cocoa
//
//  Created by Benny Gao on 15/5/9.
//  Copyright (c) 2015年 Benny Gao. All rights reserved.
//

#ifndef __Endpoint_H__
#define __Endpoint_H__

#import <Foundation/Foundation.h>
#import <ProtocolBuffers/GeneratedMessage.h>
#include <ProtocolBuffers/ProtocolBuffers.h>

#define DEFAULT_BUFFER_CHUNK_SIZE   1024

typedef enum {
    application = 0, // 应用消息
    stop = 1, // 停止endpoint运行
    cancel = 2 // 取消正在等待响应的处理
} MESSAGE_COMMAND;

typedef enum {
    sync_rpc = 1, // 同步方法调用
    async_rpc = 2 // 异步方法调用
} RPC_STRATEGY;

@class Message;

typedef void (^ CallbackBlock)(Message*);


@interface Message : NSObject {
@private
    MESSAGE_COMMAND command;
    int32_t serviceId;
    uint32_t stamp;
    Byte stage;
    PBGeneratedMessage* argument;
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
- (Message*) initwithServiceId:(int32_t)sid stamp:(int32_t)stamp stage:(Byte) stage argument:(PBGeneratedMessage*) arg;
@end

@interface MessageQueue : NSObject {
@private
    NSUInteger number;
    NSMutableArray *array;
    NSCondition *lock;
}

-(MessageQueue *) init;
-(MessageQueue *) initWithCapacity:(NSUInteger) capacity;

-(void) add:(Message *) message;
-(Message *) take;
-(NSUInteger) size;
-(void) clear;
@end

typedef enum  {
    read_segment_size = 1,
    read_segment_content = 2
} SEGMENT_PHASE;

#define IO_BUFFER_SIZE    ((size_t) 1024)

@interface IOBuffer : NSObject {
    NSMutableData *dataBuffer;
    NSUInteger limit;
    NSUInteger current;
}

@property (readonly) NSMutableData *dataBuffer;
@property (readonly) NSUInteger limit;
@property (readonly) NSUInteger current;

- (IOBuffer *) init;
- (IOBuffer *) initWithCapacity:(NSUInteger) capacity;
- (void) rewind;
- (void) flip;
- (void) clear;
- (NSUInteger) inputRemaining;
- (NSUInteger) outputRemaining;
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
    Byte bytes[IO_BUFFER_SIZE];
    IOBuffer *ibuffer;
    
    NSUInteger totalRecvBytesNumber;
}

@property (readonly) NSUInteger totalRecvBytesNumber;

-(Segment *) init;
-(void) reset;
@end

@interface SocketEngine : NSObject {
@private
    NSString *serverHost;
    ushort serverPort;
    
    int hostLink;
    int localSocketPair[2];
    
    MessageQueue *sendQueue;
    MessageQueue *recvQueue;
    
    Byte bytes[IO_BUFFER_SIZE];
    NSUInteger totalSendBytesNumber;
    
    Segment *segment;
    IOBuffer *obuffer;
    NSCondition *threadLock;
}

@property (readonly) NSString *serverHost;
@property (readonly) ushort serverPort;

-(SocketEngine *) init;
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
