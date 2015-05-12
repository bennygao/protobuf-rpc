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
#import "Yingshibao.pb.h"
#import "ProtobufRpc.h"
#import "Yingshibao.rpc.h"


void test_rpc(Endpoint *endpoint)
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
    
    for (int i = 0; i < 5000; ++i) {
        // 同步RPC调用
        RegisterResult *result = [client registerNewUser:userInfo];
        NSLog(@"[%d] 同步RPC调用 - 注册结果:%@", i, result);
        
        // 异步RPC调用
        CallbackBlock callback = ^(PBGeneratedMessage* response) {
            NSLog(@"[%d] 异步RPC调用 - 注册结果:%@", i, response);
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
        Endpoint *endpoint = [[Endpoint alloc] init];
        UserManager *um = [[UserManager alloc] init];
        [endpoint registerService:um];
        
        [endpoint connectToHost:@"localhost" withPort:10000 inSeconds:60];
        [endpoint start];
        test_rpc(endpoint);
        [endpoint stop];
        
        [endpoint connectToHost:@"localhost" withPort:10000 inSeconds:60];
        [endpoint start];
        test_rpc(endpoint);
        [endpoint stop];
    }
    
    return 0;
}
