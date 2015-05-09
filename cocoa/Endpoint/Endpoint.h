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

enum MessageCommand {
    application, // 应用消息
    stop, // 停止endpoint运行
    cancel // 取消正在等待响应的处理
};

enum RpcStrategy {
    sync_rpc, // 同步方法调用
    async_rpc // 异步方法调用
};



@interface ResponseHandle : NSObject {
@private
    enum RpcStrategy strategy;
    
}
@end

@interface Message : NSObject {
@private
    enum MessageCommand command;
    int32_t serviceId;
    uint32_t stamp;
    Byte stage;
    PBGeneratedMessage* argument;
}

- (Message *) initWithCommand:(enum MessageCommand) command;
- (Message *) initwithServiceId:(int32_t) serviceId;
@end

@interface MessageQueue : NSObject {
@private
    NSUInteger number;
    NSMutableArray *array;
    NSCondition *lock;
}

-(MessageQueue *) init;
-(MessageQueue *) initWithCapacity:(NSUInteger) capacity;

-(void) add:(Message *) bean;
-(Message *) take;
-(NSUInteger) size;
-(void) clear;
@end

typedef enum  {
    read_segment_size = 1,
    read_segment_content = 2
} SEGMENT_PHASE;

#define IO_BUFFER_SIZE    ((size_t) 1024)

@interface SegmentHead : NSObject {
@private
    short commandId;
    long long serialNo;
}

@property short commandId;
@property long long serialNo;

- (void) getHeadInfo:(uint) type :(IOBuffer *) ibuffer;
- (void) putHeadInfo:(uint) type :(IOBuffer *) obuffer;
@end

@interface Segment : NSObject {
@private
    SEGMENT_PHASE phase;
    NSUInteger needBytes;
    NSUInteger gotBytes;
    NSUInteger segmentSize;
    Byte bytes[IO_BUFFER_SIZE];
    IOBuffer *ibuffer;
    SegmentHead *head;
    
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
    
    MessageBeanQueue *sendQueue;
    MessageBeanQueue *recvQueue;
    
    Byte bytes[IO_BUFFER_SIZE];
    NSUInteger totalSendBytesNumber;
    
    Segment *segment;
    SegmentHead *head;
    IOBuffer *obuffer;
    NSCondition *threadLock;
    
    BOOL isCheckingHeartbeat; // 是否正在主动检测心跳标志
    NullMessageBean *cHeartbeatMessage; // 客户端主动检测心跳消息
    NullMessageBean *sHeartbeatMessage; // 响应服务器心跳检测消息
}

@property (readonly, weak) NSString *serverHost;
@property (readonly) ushort serverPort;

-(SocketEngine *) init;
-(NSString *) getServerIpAddress;

-(BOOL) connectToHost:(NSString *)addr withPort:(ushort)port inSeconds:(NSUInteger)timeout;
-(void) start;
-(void) stop;
-(void) run;

-(void) sendMessageBean:(MessageBean *) bean;
-(MessageBean *) recvMessageBean;

-(NSUInteger) getTotalRecvBytesNumber;
-(NSUInteger) getTotalSendBytesNumber;
@end


#endif
