package com.yingshibao.foundation.rpc.gencode;

import java.io.*;
import java.util.List;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileOptions;
import com.google.protobuf.TextFormat;
import com.google.protobuf.compiler.PluginProtos;

public class RpcGenerator {
	private final static String GENRPC_ARGS = "GENRPC_ARGS";

	public static void main(String[] args) throws Exception {
		String genrpcArgs = System.getenv(GENRPC_ARGS);
		if (genrpcArgs == null) {
			System.err.println("ERROR: environment GENRPC_ARGS not set.");
			System.exit(1);
		}

		String[] params = genrpcArgs.split(" ");
		if (params.length != 2) {
			System.err.println("ERROR: error GENRPC_ARGS " + genrpcArgs);
			System.exit(2);
		}

		CodeGeneratorFactory factory;
		if (params[0].equalsIgnoreCase("java")) {
			factory = new JavaCodeGeneratorFactory();
		} else if (params[0].equalsIgnoreCase("objc")) {
			factory = null;
		} else {
			factory = null;
		}

		RpcGenerator gen = new RpcGenerator(System.in, factory, params[1]);
		gen.generate();

		System.exit(0);
	}
	
	private InputStream input;
	private CodeGeneratorFactory factory;
	private String outdir;
	
	public RpcGenerator(InputStream input, CodeGeneratorFactory factory, String outdir) {
		this.input = input;
		this.factory = factory;
		this.outdir = outdir;
	}
	
	public void generate() throws Exception {
		com.google.protobuf.Parser<? extends GeneratedMessage> parser = PluginProtos.CodeGeneratorRequest.PARSER;
		PluginProtos.CodeGeneratorRequest request = (PluginProtos.CodeGeneratorRequest) parser.parseFrom(input);
		List<FileDescriptorProto> protoList = request.getProtoFileList();

		PrintWriter pw = new PrintWriter(new FileWriter("genrpc.stub"));
		String text = TextFormat.printToUnicodeString(request);
		text.replace("\\n", "\n");
		pw.print(text);
		pw.close();
		
		for (FileDescriptorProto proto : protoList) {
			FileOptions options = proto.getOptions();
			String javaPackage = options.getJavaPackage();
			CodeGenerator cg = factory.createCodeGenerator();
			cg.generate(outdir, javaPackage, "utf-8", proto);
		}
	}
}
