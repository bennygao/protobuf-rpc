//
//  Yingshibao.rpc.m
//  cocoa
//
//  Created by 高波 on 15/5/11.
//  Copyright (c) 2015年 Benny Gao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Yingshibao.rpc.h"

@implementation UserManager

- (UserManager*) init {
    return [self initWithServiceImpl:nil];
}

- (UserManager*) initWithServiceImpl:(id<UserManagerService>)impl {
    serviceImpl = impl;
    return self;
}

- (NSArray*) getServiceList {
    return [NSArray arrayWithObjects:[NSNumber numberWithInt:-2051187760], nil];
}

- (PBGeneratedMessage*) invokeService:(int32_t)serviceId :(PBGeneratedMessage *)arg {
    switch (serviceId) {
        case -2051187760:
            return [serviceImpl registerNewUser:(UserInfo*) arg];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForRequest:(int32_t)serviceId {
    switch (serviceId) {
        case -2051187760:
            return [UserInfo builder];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForResponse:(int32_t)serviceId {
    switch (serviceId) {
        case -2051187760:
            return [RegisterResult builder];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}

@end

@implementation UserManagerClient

- (UserManagerClient*) initWithRpcSession:(RpcSession *)session {
    self = [super initWithRpcSession:session];
    return self;
}

- (RegisterResult*) registerNewUser:(UserInfo *)userInfo {
    return (RegisterResult*) [self syncRpc:-2051187760 :userInfo];
}

- (void) registerNewUserAsync:(UserInfo *)userInfo :(CallbackBlock)callback {
    [self asyncRpc:-2051187760 :userInfo :callback];
}

@end
