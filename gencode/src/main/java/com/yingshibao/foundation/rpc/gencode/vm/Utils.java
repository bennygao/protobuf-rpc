package com.yingshibao.foundation.rpc.gencode.vm;

import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;

public class Utils {
	public String getBaseName(String pathname) {
		int idx = pathname.lastIndexOf('.');
		return idx >= 0 ? pathname.substring(idx + 1) : pathname;
	}
	
	public String firstLetterLowerCaseBaseName(String pathname) {
		String baseName = getBaseName(pathname);
		return baseName.substring(0, 1).toLowerCase() + baseName.substring(1);
	}
	
	public int hashCode(MethodDescriptorProto method) {
		String prototype = String.format("%s %s(%s)", method.getOutputType(), method.getName(), method.getInputType());
		return prototype.hashCode();
	}
}
