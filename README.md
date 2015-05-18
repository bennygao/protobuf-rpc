# 依赖
需要JDK1.7或以上版本的java运行环境

# 安装

	> git clone http://test.yingshibao.com:8889/bennygao/protobuf-rpc.git
	> cd protobuf-rpc
	> gradle build
	> gradle build
	
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