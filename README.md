# What is protobuf-rpc？
protobuf-rpc是一个基于[google ProtocolBuffer](https://developers.google.com/protocol-buffers/?hl=zh-cn)的远程方法调用(RPC)）实现，服务器端当前仅支持Java(JDK 1.7及以上)，客户端支持Objective-C(cocoa/iOS)和Java(J2SE/Android)。
# Why protobuf-rpc？
对于纯的基于Windows或者Linux的服务器来说，RPC的解决方案很多，WebService、Zero-ICE、Caucho Hessian等等，但是对于需要同时支持iOS、Android的移动App开发来说，可选的解决方案一下子变得很少。

## 适合移动App开发的RPC应该具备什么特征？
* **同时支持iOS、Android和服务器端**

毋庸置疑，对于移动端的平台来说，iOS和Android同样重要，iOS和Android的开发语言Objective-C和Android Java差别又是如此之大，仅能支持其中一种平台的方案不能满足要求，未来可能还需要能够支持WindowsPhone。

* **更小的网络通讯报文体积**

尤其在中国的运营商网络环境下，用户对于流量的消耗是比较敏感的，如果App能够更小的消耗用户网络流量，不仅通讯速度更快一些，对于用户综合体验也更加好。

* **客户端和服务器之间对等的相互调用能力**

传统意义上的Client/Server结构，客户端主动向服务器发送服务请求，服务器端处理后，返回响应给客户端。但是随着App功能的丰富，例如实现聊天室功能时，一个用户的聊天消息发送到服务器后，服务器要能够实时推送到其他在线用户的客户端，此时传统的Client/Server结构就很麻烦，因为从请求和服务的角度来说，服务器变成了请求方，客户端变成了服务提供方。

**protobuf-rpc是一个具备以上特征、特定面向移动App开发，提供iOS、Android和服务器端的RPC实现。**

protobuf-rpc同时支持iOS Objective-C、Android Java和服务器端Java。

protobuf-rpc在网络连接层面使用TCP长连接，减少了连接建立次数。应用层数据使用Google ProtocolBuffer来定义服务，ProtocolBuffer是由Google开发的一种数据序列化协议（类似于XML、JSON、hessian），ProtocolBuffer能够将数据进行序列化，并广泛应用在数据存储、通信协议等方面。相比XML、JSON，ProtocolBuffer序列化后的报文体积小很多。

protobuf-rpc能够让Client和Server对等地、灵活地相互调用对方提供的服务。

# 安装

## 依赖
需要JDK1.7或以上版本的java运行环境。

## 从源代码编译

	> git clone http://test.yingshibao.com:8889/bennygao/protobuf-rpc.git
	> cd protobuf-rpc
	> gradle build
	> gradle build

把protobuf-rpc/bin的路径增加到PATH环境变量中。

linux 和 macosx:
	
	> export PATH=$PATH:path-to-protobuf-rpc/bin

windows:

	> SET PATH=%PATH%;path-to-protobuf-rpc\bin

	
# 模块及目录说明
* __bin__ 存放执行脚本以及编译好的jar文件。
* __lib__ 存放项目依赖的外部jar文件。
* __proto__ 存放示例工程的proto定义文件，services.proto和messages.proto。
* __core__ protobuf-rpc的核心类，client和server都需要。
* __nio__ java nio实现的Endpoint, client需要。
* __mina__ apache mina-2.0.9 实现的Endpoint，server需要。
* __client__ java client示例代码。
* __server__ java server示例代码。
* __cocoa__ macosx以及iOS objective-c示例工程。
* __android__ Android示例工程。

# 编译proto文件生成代码
protobuf-rpc当前支持生成java和objective-c代码。
## 生成java代码
假设java项目工程的目录结构如下：

	[your-app-proj]
	|
	+---[src]
	    | 
	    +---[main]
	        |
	        +---[java]

linux 和 macosx

	> pbrpcc -g java -o path-to-your-app-proj/src/main/java -p your-proto.proto

windows

	> pbrpcc -g java -o path-to-your-app-proj\src\main\java -p your-proto.proto
	
命令执行完成后，会在`your-app-proj/src/main/java`目录下根据proto文件中`option java_package`选项设定的java package自动创建子目录。例如proto文件中设定`option java_package = "com.yingshibao.app.idl";`的话，java代码将被生成到`your-app-proj/src/main/java/com/yingshibao/app/idl`目录下。
## 生成objective-c代码
**protobuf-rpc当前仅支持在macosx环境下生成objective-c代码。**

	> pbrpcc -g objc -o path-to-your-app-proj -p your-proto.proto

命令运行完成后，会在`-o`选项指定的目录下生成两个.h头文件和两个.m实现文件，文件名根据proto文件中的`package`选项设定，例如proto文件中设定了`package yingshibao;`，则生成的4个源代码文件分别为`Yingshibao.pb.h` `Yingshibao.pb.m` `Yingshibao.rpc.h` `Yingshibao.rpc.m`。