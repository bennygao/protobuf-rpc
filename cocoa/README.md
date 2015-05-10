# 前提
MacOSX中必须先安装了CocoaPods，如何安装CocoaPods请参阅[CocoaPods安装和使用教程](http://code4app.com/article/cocoapods-install-usage)。

# 运行pod自动获得依赖内容
首先确保Podfile文件中的内容如下：

    platform :osx , 10.8
	pod "ProtocolBuffers", "~> 1.9.7"·

运行pod install命令：

	$ pod install
	Analyzing dependencies

	CocoaPods 0.37.1 is available.
	To update use: `gem install cocoapods`

	For more information see http://blog.cocoapods.org
	and the CHANGELOG for this version http://git.io/BaH8pQ.

	Downloading dependencies
	Installing ProtocolBuffers (1.9.8)
	Generating Pods project
	Integrating client project

	[!] Please close any current Xcode sessions and use `cocoa.xcworkspace` for this project from now on.

下次用xcode打开cocoa.xcworkspace工程文件。

# protobuf的objective-c插件
［protobuf的objective-c插件](https://github.com/alexeyxo/protobuf-objc.git)
