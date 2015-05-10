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

int main(int argc, const char * argv[]) {
    @autoreleasepool {
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
    return 0;
}
