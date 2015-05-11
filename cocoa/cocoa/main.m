//
//  main.m
//  cocoa
//
//  Created by Benny Gao on 15/5/9.
//  Copyright (c) 2015年 Benny Gao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ProtocolBuffers/TextFormat.h>
#import "Yingshibao.pb.h"

@interface AClass : NSObject
+ (NSArray*) serviceList;
@end

@implementation AClass

+ (NSArray*) serviceList {
    return [NSArray arrayWithObjects:[NSNumber numberWithInt:123], nil];
}

@end

void test_id(Class clazz)
{
//    SEL mysel = NSSelectorFromString(@"serviceList");
//    NSArray *ary = (NSArray*) [clazz performSelector:mysel];
    NSArray *ary = [clazz serviceList];
    NSLog(@"value=%@\n", ary);
}

@interface BlockingQueue : NSObject {
@private
    NSMutableArray *array;
    NSCondition *lock;
}

- (BlockingQueue*) init;
- (void) add:(NSNumber*)num;
- (NSNumber*) take;
@end

@implementation BlockingQueue

- (BlockingQueue*) init {
    array = [[NSMutableArray alloc] initWithCapacity:10];
    lock = [[NSCondition alloc] init];
    return self;
}

- (void) add:(NSNumber*) num {
    [lock lock];
    [array addObject:num];
    [lock signal];
    [lock unlock];
}

- (NSNumber*) take {
    [lock lock];
    while ([array count] == 0) {
        NSLog(@"wait for products");
        [lock wait];
    }
    
    NSNumber *data = [array objectAtIndex:0];
    [array removeObjectAtIndex:0];
    [lock unlock];
    return data;
}

@end


void test_lock(void)
{
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
    
    uint8_t buffer[size];
    NSOutputStream* ostream = [[NSOutputStream alloc] initToBuffer:buffer capacity:size];
    [ostream open];
    [userInfo writeToOutputStream:ostream];
    [ostream close];
    
    NSData *data = [NSData dataWithBytes: buffer length: size];
    NSInputStream* istream = [[NSInputStream alloc] initWithData:data];
    [istream open];
    UserInfo *another = [UserInfo parseFromData:data];
    NSLog(@"serializedSize=%d, %@", size, another);
}

void test_array(void)
{

    NSMutableData *buffer = [[NSMutableData alloc] initWithLength:10];
    NSLog(@"length=%lu", (unsigned long)[buffer length]);
    [buffer increaseLengthBy:10];
    NSLog(@"length=%lu", (unsigned long)[buffer length]);

}

int main(int argc, const char * argv[]) {
    @autoreleasepool {
        test_lock();
    }
    
    return 0;
}
