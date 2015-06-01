//
//  main.m
//  cocoa
//
//  Created by Benny Gao on 15/5/9.
//  Copyright (c) 2015年 Benny Gao. All rights reserved.
//
#import <stdio.h>
#import <Foundation/Foundation.h>
#import <ProtocolBuffers/TextFormat.h>
#import "ProtobufRpc.h"
#import "Yingshibao.rpc.h"

@interface MyPushService : NSObject <PushService>

@end

@implementation MyPushService

- (None*) pushBarrage:(Barrage*) barrage {
    NSLog(@"客户端收到主动调用:%@", barrage);
    return nil;
}

@end

void test_unregistered_service(Endpoint *endpoint)
{
    NSCondition *condition = [[NSCondition alloc] init];
    CourseTypeBuilder *builder = [CourseType builder];
    builder.num = 10;
    builder.pageNum = 1;
    builder.courseType = 1;
    CourseType *courseType = [builder build];
    
    RpcSession *session = [[RpcSession alloc] initWithEndpoint:endpoint];
    CourseManagerClient *client = [[CourseManagerClient alloc] initWithRpcSession:session];
    
    // 同步调用
    @try {
        CourseList *courseList = [client getCourseList:courseType];
        NSLog(@"CourseManager#getCourseList 同步RPC调用 - 课程列表:%@", courseList);
    }  @catch (NSException *exception) {
        NSLog(@"CourseManager#getCourseList 同步RPC调用异常:%@", exception);
    }
    
    // 异步调用
    CallbackBlock callback = ^(PBGeneratedMessage* response, RpcState state) {
        if (state == service_not_exist) {
            NSLog(@"对方endpoint不提供getCourseList服务");
        } else if (state == rpc_canceled) {
            NSLog(@"RPC调用被取消");
        } else if (state == service_exception) {
            NSLog(@"对方endpoint处理请求时异常");
        } else {
            NSLog(@"异步RPC调用 - 课程列表:%@", response);
        }
        [condition lock];
        [condition signal];
        [condition unlock];
    };
    
    [condition lock];
    [client getCourseListAsync:courseType :callback];
    [condition wait];
    [condition unlock];
    
}

void test_registered_service(Endpoint *endpoint)
{
    NSCondition *condition = [[NSCondition alloc] init];
    UserInfoBuilder* builder = [UserInfo builder];
    builder.schoolName = @"北京大学";
    builder.nickName = @"马大哈";
    builder.phone = @"13810422191";
    builder.examType = 1;
    builder.channelName = @"360应用商店";
    UserInfo* userInfo = [builder build];
    
    RpcSession *session = [[RpcSession alloc] initWithEndpoint:endpoint];
    UserManagerClient *client = [[UserManagerClient alloc] initWithRpcSession:session];
    
    for (int i = 0; i < 10; ++i) {
        // 同步RPC调用
        RegisterResult *result = [client registerNewUser:userInfo];
        NSLog(@"[%d] 同步RPC调用 - 注册结果:%@", i, result);
        
        // 异步RPC调用
        CallbackBlock callback = ^(PBGeneratedMessage* response, RpcState state) {
            if (state == service_not_exist) {
                NSLog(@"对方endpoint不提供UserManager服务");
            } else if (state == rpc_canceled) {
                NSLog(@"RPC调用被取消");
            } else if (state == service_exception) {
                NSLog(@"对方endpoint处理请求时异常");
            } else {
                NSLog(@"[%d] 异步RPC调用 - 注册结果:%@", i, response);
            }
            
            [condition lock];
            [condition signal];
            [condition unlock];
        };
        [condition lock];
        [client registerNewUserAsync:userInfo :callback];
        [condition wait];
        [condition unlock];
    }
}

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        // 创建Endpint实例
        Endpoint *endpoint = [[Endpoint alloc] init];
        
        // 注册UserManager
        UserManager *um = [[UserManager alloc] init];
        [endpoint registerService:um];
        
        // 注册Push
        Push *push = [[Push alloc] initWithServiceImpl:[[MyPushService alloc] init]];
        [endpoint registerService:push];
        
        // 与服务器建立连接
        if ([endpoint connectToHost:@"localhost" withPort:10000 inSeconds:60]) {
            [endpoint start];
            test_unregistered_service(endpoint);
            [endpoint stop];
        } else {
            NSLog(@"与服务器建立连接失败");
        }
        
        // 与服务器建立连接
        if ([endpoint connectToHost:@"localhost" withPort:10000 inSeconds:60]) {
            [endpoint start];
            test_registered_service(endpoint);
            [endpoint stop];
        } else {
            NSLog(@"与服务器建立连接失败");
        }
    }
    
    return 0;
}
