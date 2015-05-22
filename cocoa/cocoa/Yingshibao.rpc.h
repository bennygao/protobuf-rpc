// Yingshibao.rpc.h
// Created by protobuf-rpc-gencode.
// Fri May 22 13:08:58 CST 2015
// DO NOT EDIT!

#ifndef __Yingshibao_rpc_H__
#define __Yingshibao_rpc_H__

#import "ProtobufRpc.h"
#import "Yingshibao.pb.h"

@protocol UserManagerService <NSObject>
@required
- (RegisterResult*) registerNewUser:(UserInfo*) userInfo;
@end

@interface UserManagerClient: ClientStub
- (UserManagerClient*) initWithRpcSession:(RpcSession*) session;
- (RegisterResult*) registerNewUser:(UserInfo*) userInfo;
- (void) registerNewUserAsync:(UserInfo*) userInfo :(CallbackBlock) callback;
@end

@interface UserManager : NSObject <RpcServiceRegistry> {
@private
    id<UserManagerService> serviceImpl;
}

- (UserManager*) init;
- (UserManager*) initWithServiceImpl:(id<UserManagerService>) impl;
@end

@protocol PushService <NSObject>
@required
- (None*) pushBarrage:(Barrage*) barrage;
@end

@interface PushClient: ClientStub
- (PushClient*) initWithRpcSession:(RpcSession*) session;
- (None*) pushBarrage:(Barrage*) barrage;
- (void) pushBarrageAsync:(Barrage*) barrage :(CallbackBlock) callback;
@end

@interface Push : NSObject <RpcServiceRegistry> {
@private
    id<PushService> serviceImpl;
}

- (Push*) init;
- (Push*) initWithServiceImpl:(id<PushService>) impl;
@end

@protocol CourseManagerService <NSObject>
@required
- (CourseList*) getCourseList:(CourseType*) courseType;
@end

@interface CourseManagerClient: ClientStub
- (CourseManagerClient*) initWithRpcSession:(RpcSession*) session;
- (CourseList*) getCourseList:(CourseType*) courseType;
- (void) getCourseListAsync:(CourseType*) courseType :(CallbackBlock) callback;
@end

@interface CourseManager : NSObject <RpcServiceRegistry> {
@private
    id<CourseManagerService> serviceImpl;
}

- (CourseManager*) init;
- (CourseManager*) initWithServiceImpl:(id<CourseManagerService>) impl;
@end

#endif
