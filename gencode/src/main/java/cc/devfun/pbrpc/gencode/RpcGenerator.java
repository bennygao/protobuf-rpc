package cc.devfun.pbrpc.gencode;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.devfun.pbrpc.gencode.vm.CommentedDescriptor;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileOptions;
import com.google.protobuf.TextFormat;
import com.google.protobuf.compiler.PluginProtos;

public class RpcGenerator {
	private final static String GENRPC_ARGS = "GENRPC_ARGS";

	public static void main(String[] args) throws Exception {
//		saveBinary();
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
		if (params[0].equalsIgnoreCase("javasvc")) {
			factory = new JavaCodeGeneratorFactory();
		} else if (params[0].equalsIgnoreCase("objc")) {
			factory = new ObjcCodeGeneratorFactory();
		} else if (params[0].equalsIgnoreCase("html")) {
			factory = new HtmlGeneratorFactory();
		} else {
			factory = null;
		}

		RpcGenerator gen = new RpcGenerator(System.in, factory, params[1]);
		gen.generate();

		System.exit(0);
	}

	private static void saveBinary() throws Exception {
		int cnt;
		byte[] buffer = new byte[1024];
		FileOutputStream fos = new FileOutputStream("proto.bin");

		while ((cnt = System.in.read(buffer)) > 0) {
			fos.write(buffer, 0, cnt);
		}

		fos.close();
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

    private void appendLog(PluginProtos.CodeGeneratorRequest request) throws Exception {
        List<FileDescriptorProto> protos = request.getProtoFileList();
        PrintStream ps = new PrintStream(new FileOutputStream("/tmp/gencode.log", true));
        ps.println("=================");
        for (FileDescriptorProto proto : protos) {
            ps.println(proto.getName());
        }
        ps.println("-----------------");
        ps.println(TextFormat.printToUnicodeString(request));
        ps.close();
    }
	
	public void generate() throws Exception {
		com.google.protobuf.Parser<? extends GeneratedMessage> parser = PluginProtos.CodeGeneratorRequest.PARSER;
		PluginProtos.CodeGeneratorRequest request = (PluginProtos.CodeGeneratorRequest) parser.parseFrom(input);
//        appendLog(request);
        CodeGenerator cg = factory.createCodeGenerator();
        cg.generate(outdir, request);
	}
}
