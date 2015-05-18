# 依赖
需要JDK1.7或以上版本的java运行环境

# 安装

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
* __proto__ 存放示例工程的proto定义文件，yingshibao.proto
* __core__ protobuf-rpc的核心类，client和server都需要。
* __nio__ java nio实现的Endpoint, client需要。
* __mina__ apache mina-2.0.9 实现的Endpoint，server需要。
* __client__ java client示例代码。
* __server__ java server示例代码。
* __cocoa__ macosx以及iOS objective-c示例工程。

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