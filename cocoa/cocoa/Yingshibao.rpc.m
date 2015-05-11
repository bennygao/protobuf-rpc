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

- (UserManager*) initWithServiceImpl:(UserManagerService *)impl {
    serviceImpl = impl;
    return self;
}

- (NSArray*) getServiceList {
    return [NSArray arrayWithObjects:[NSNumber numberWithInt:-2051187760], nil];
}

- (PBGeneratedMessage*) invokeService:(int32_t)serviceId :(PBGeneratedMessage *)arg :(RpcSession *)session {
    switch (serviceId) {
        case -2051187760:
            return [serviceImpl registerNewUser:(UserInfo*) arg];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:@""
                                         userInfo:nil];
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForRequest:(int32_t)serviceId {
    switch (serviceId) {
        case -2051187760:
            return [UserInfo builder];
        default:
            return nil;
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForResponse:(int32_t)serviceId {
    switch (serviceId) {
        case -2051187760:
            return [RegisterResult builder];
        default:
            return nil;
    }
}

@end
