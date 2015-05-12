//
//  Yingshibao.rpc.h
//  cocoa
//
//  Created by Benny Gao on 15/5/11.
//  Copyright (c) 2015年 Benny Gao. All rights reserved.
//

#ifndef cocoa_Yingshibao_rpc_h
#define cocoa_Yingshibao_rpc_h

#import "ProtobufRpc.h"
#import "Yingshibao.pb.h"

@protocol UserManagerService <NSObject>
@required
- (RegisterResult*) registerNewUser:(UserInfo*) userInfo;
@end

@interface UserManagerClient : ClientStub
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

#endif
