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

@interface UserManagerService : NSObject
- (RegisterResult*) registerNewUser:(UserInfo*) userInfo;
@end

@interface UserManagerClient : NSObject
- (RegisterResult*) registerNewUser:(UserInfo*) userInfo;
- (void) registerNewUserAsync:(UserInfo*) userInfo :(CallbackBlock) callback;
@end

@interface UserManager : RpcServiceRegistry {
@private
    UserManagerService *serviceImpl;
}

- (UserManager*) init;
- (UserManager*) initWithServiceImpl:(UserManagerService*) impl;
@end

#endif
