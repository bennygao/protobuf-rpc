package com.yingshibao.foundation.rpc.gencode.vm;

import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;

public class Utils {
	public String getBaseName(String pathname) {
		int idx = pathname.lastIndexOf('.');
		return idx >= 0 ? pathname.substring(idx + 1) : pathname;
	}

	public String firstLetterLowerCase(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public String firstLetterUpperCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public String firstLetterLowerCaseBaseName(String pathname) {
		String baseName = getBaseName(pathname);
		return firstLetterLowerCase(baseName);
	}

	public String firstLetterUpperCaseBaseName(String pathname) {
		String baseName = getBaseName(pathname);
		return firstLetterUpperCase(baseName);
	}
	
	public int hashCode(MethodDescriptorProto method) {
		String prototype = String.format("%s %s(%s)", method.getOutputType(), method.getName(), method.getInputType());
		return prototype.hashCode();
	}
}
