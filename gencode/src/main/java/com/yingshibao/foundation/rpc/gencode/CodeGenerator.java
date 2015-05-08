package com.yingshibao.foundation.rpc.gencode;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

public interface CodeGenerator {
	public void generate(String srcDir, String srcPackage,
			String outputEncoding, FileDescriptorProto proto) throws Exception;
}
