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

@interface AClass : NSObject
- (NSArray*) serviceList;
@end

@implementation AClass

- (NSArray*) serviceList {
    NSLog(@"%@", [[self class] description]);
    NSString *a = [NSString stringWithFormat:@"%d", 123];
    return [NSArray arrayWithObjects:[NSNumber numberWithInt:123], nil];
}
@end

void test_dict(void) {
    NSArray *array = [NSArray arrayWithObjects:[NSNumber numberWithInt:-2051187760], nil];
    NSLog(@"array count=%ld\n", [array count]);
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] init];
    
    for (int i = 0; i < [array count]; ++i) {
        [dict setObject:@"hello" forKey:[array objectAtIndex:i]];
    }
    
    NSString *val = [dict objectForKey:[NSNumber numberWithInt:-2051187760]];
    NSLog(@"val=%@", val);
}

void test_id(Class clazz)
{
//    NSArray *ary = [clazz serviceList];
//    NSLog(@"value=%@\n", ary);
}


void test_lock(void)
{
    AClass *ac = [[AClass alloc] init];
    [ac serviceList];
    BlockingQueue *bq = [[BlockingQueue alloc] init];
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    NSBlockOperation *operation = [NSBlockOperation blockOperationWithBlock:^(){
        for (int i = 0; i < 10; ++i) {
            NSLog(@"[%d] begin take data in thread...", i);
            NSNumber *num = [bq take];
            NSLog(@"[%d] thread take data %@", i, num);
        }
    }];
    
    [queue addOperation:operation];
    for (int i = 0; i < 10; ++i) {
        NSLog(@"main thread sleep ...");
        sleep(2);
        NSLog(@"main thread unlock ...");
        [bq add:[NSNumber numberWithInt:123]];
     }
    
    [queue waitUntilAllOperationsAreFinished];
}

void test_pb(void)
{
    UserInfoBuilder* builder = [UserInfo builder];
    builder.schoolName = @"北京大学";
    builder.nickName = @"马大哈";
    builder.phone = @"13810422191";
    builder.examType = 1;
    builder.channelName = @"360应用商店";
    UserInfo* userInfo = [builder build];
    int size = [userInfo serializedSize];
    NSLog(@"serializedSize=%d, %@", size, userInfo);
    
    int serializedSize = [userInfo serializedSize];
    int messageSize = 9 + serializedSize;
    
    IOBuffer *buffer = [[IOBuffer alloc] init];
    
    // 报文长度, int32(4bytes)
    [buffer writeInt32:messageSize];
    
    // stamp
    [buffer writeInt32:123];
    
    // serviceId
    [buffer writeInt32:-456];
    
    // stage
    [buffer writeByte:127];
    
    // protobuf
    NSOutputStream* ostream = [[NSOutputStream alloc] initToBuffer:[buffer currentPtr] capacity:serializedSize];
    [ostream open];
    [userInfo writeToOutputStream:ostream];
    [ostream close];
    
    [buffer increaseCurrent:serializedSize];
    
    // 回填报文长度
    [buffer flip];
    
    int fd = open("/tmp/data", O_CREAT|O_TRUNC|O_WRONLY|0644);
    write(fd, [buffer currentPtr], [buffer limit]);
    close(fd);
    
    messageSize = [buffer readInt32];
    int stamp = [buffer readInt32];
    int serviceId = [buffer readInt32];
    
    Byte stage = [buffer readByte];
    NSData *idata = [NSData dataWithBytes:[buffer currentPtr] length:size];
    UserInfo *another = [UserInfo parseFromData:idata];
    NSLog(@"%d %d %d %@", stamp, serviceId, stage, another);

}

void test_array(void)
{

    NSMutableData *buffer = [[NSMutableData alloc] initWithLength:10];
    NSLog(@"length=%lu", (unsigned long)[buffer length]);
    [buffer increaseLengthBy:10];
    NSLog(@"length=%lu", (unsigned long)[buffer length]);

}

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
    
    for (int i = 0; i < 5; ++i) {
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
