// Yingshibao.rpc.m
// Created by protobuf-rpc-gencode.
// Thu May 28 22:55:33 CST 2015
// DO NOT EDIT!

#import <Foundation/Foundation.h>
#import "Yingshibao.rpc.h"

@implementation CourseManager
- (CourseManager*) init {
    return [self initWithServiceImpl:nil];
}

- (CourseManager*) initWithServiceImpl:(id<CourseManagerService>)impl {
    serviceImpl = impl;
    return self;
}

- (NSArray*) getServiceList {
    return [NSArray arrayWithObjects:
            [NSNumber numberWithInt:-1712929388],
            nil];
}

- (PBGeneratedMessage*) invokeService:(int32_t)serviceId :(PBGeneratedMessage *)arg {
    switch (serviceId) {
        case -1712929388:
            return [serviceImpl getCourseList:(CourseType*) arg];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForRequest:(int32_t)serviceId {
    switch (serviceId) {
        case -1712929388:
            return [CourseType builder];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForResponse:(int32_t)serviceId {
    switch (serviceId) {
        case -1712929388:
            return [CourseList builder];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}
@end

@implementation CourseManagerClient
- (CourseManagerClient*) initWithRpcSession:(RpcSession*)session {
    self = [super initWithRpcSession:session];
    return self;
}

- (CourseList*) getCourseList:(CourseType*)courseType {
    return (CourseList*) [self syncRpc:-1712929388 :courseType];
}

- (void) getCourseListAsync:(CourseType*)courseType :(CallbackBlock)callback {
    [self asyncRpc:-1712929388 :courseType :callback];
}
@end

@implementation Push
- (Push*) init {
    return [self initWithServiceImpl:nil];
}

- (Push*) initWithServiceImpl:(id<PushService>)impl {
    serviceImpl = impl;
    return self;
}

- (NSArray*) getServiceList {
    return [NSArray arrayWithObjects:
            [NSNumber numberWithInt:1816601235],
            nil];
}

- (PBGeneratedMessage*) invokeService:(int32_t)serviceId :(PBGeneratedMessage *)arg {
    switch (serviceId) {
        case 1816601235:
            return [serviceImpl pushBarrage:(Barrage*) arg];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForRequest:(int32_t)serviceId {
    switch (serviceId) {
        case 1816601235:
            return [Barrage builder];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}

- (PBGeneratedMessageBuilder*) getBuilderForResponse:(int32_t)serviceId {
    switch (serviceId) {
        case 1816601235:
            return [None builder];
        default:
            @throw [NSException exceptionWithName:@"NonexistentServiceIdException"
                                           reason:[NSString stringWithFormat:@"ServiceClass:%@ ServiceId:%d", [[self class] description], serviceId]
                                         userInfo:nil];
    }
}
@end

@implementation PushClient
- (PushClient*) initWithRpcSession:(RpcSession*)session {
    self = [super initWithRpcSession:session];
    return self;
}

- (None*) pushBarrage:(Barrage*)barrage {
    return (None*) [self syncRpc:1816601235 :barrage];
}

- (void) pushBarrageAsync:(Barrage*)barrage :(CallbackBlock)callback {
    [self asyncRpc:1816601235 :barrage :callback];
}
@end

@implementation UserManager
- (UserManager*) init {
    return [self initWithServiceImpl:nil];
}

- (UserManager*) initWithServiceImpl:(id<UserManagerService>)impl {
    serviceImpl = impl;
    return self;
}

- (NSArray*) getServiceList {
    return [NSArray arrayWithObjects:
            [NSNumber numberWithInt:-2051187760],
            nil];
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
- (UserManagerClient*) initWithRpcSession:(RpcSession*)session {
    self = [super initWithRpcSession:session];
    return self;
}

- (RegisterResult*) registerNewUser:(UserInfo*)userInfo {
    return (RegisterResult*) [self syncRpc:-2051187760 :userInfo];
}

- (void) registerNewUserAsync:(UserInfo*)userInfo :(CallbackBlock)callback {
    [self asyncRpc:-2051187760 :userInfo :callback];
}
@end

