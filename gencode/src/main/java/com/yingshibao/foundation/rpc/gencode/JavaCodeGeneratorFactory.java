package com.yingshibao.foundation.rpc.gencode;

import com.yingshibao.foundation.rpc.gencode.vm.JavaCodeGenerator;

public class JavaCodeGeneratorFactory implements CodeGeneratorFactory {
	@Override
	public CodeGenerator createCodeGenerator() throws Exception {
		return new JavaCodeGenerator();
	}
}
