package cc.devfun.pbrpc.gencode;

import com.google.protobuf.compiler.PluginProtos;

public interface CodeGenerator {
	public void generate(String srcDir, PluginProtos.CodeGeneratorRequest request) throws Exception;
}
