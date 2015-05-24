package cc.devfun.pbrpc.gencode;

import cc.devfun.pbrpc.gencode.vm.JavaCodeGenerator;

public class JavaCodeGeneratorFactory implements CodeGeneratorFactory {
	@Override
	public CodeGenerator createCodeGenerator() throws Exception {
		return new JavaCodeGenerator();
	}
}
