// Yingshibao.rpc.h
// Created by protobuf-rpc-gencode.
// Tue May 12 20:31:56 CST 2015
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

#endif
