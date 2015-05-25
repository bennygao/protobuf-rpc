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
		if (params[0].equalsIgnoreCase("java")) {
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
	
	public void generate() throws Exception {
		com.google.protobuf.Parser<? extends GeneratedMessage> parser = PluginProtos.CodeGeneratorRequest.PARSER;
		PluginProtos.CodeGeneratorRequest request = (PluginProtos.CodeGeneratorRequest) parser.parseFrom(input);
		List<FileDescriptorProto> protoList = request.getProtoFileList();

		PrintWriter pw = new PrintWriter(new FileWriter("genrpc.stub"));
		String text = TextFormat.printToUnicodeString(request);
		text.replace("\\n", "\n");
		pw.print(text);
		pw.close();

		List<CommentedDescriptor> allServices = new ArrayList<>();
		List<CommentedDescriptor> allMessages = new ArrayList<>();
		for (FileDescriptorProto proto : protoList) {
			List<CommentedDescriptor> services = getServicesComments(proto);
			allServices.addAll(services);

			List<CommentedDescriptor> messages = getMessagesComments(proto);
			allMessages.addAll(messages);
		}

        Collections.sort(allServices, new Comparator<CommentedDescriptor>() {
            @Override
            public int compare(CommentedDescriptor o1, CommentedDescriptor o2) {
                DescriptorProtos.ServiceDescriptorProto sd1 = (DescriptorProtos.ServiceDescriptorProto) o1.getDescriptor();
                DescriptorProtos.ServiceDescriptorProto sd2 = (DescriptorProtos.ServiceDescriptorProto) o2.getDescriptor();
                return sd1.getName().compareTo(sd2.getName());
            }
        });

        Collections.sort(allMessages, new Comparator<CommentedDescriptor>() {
            @Override
            public int compare(CommentedDescriptor o1, CommentedDescriptor o2) {
                DescriptorProtos.DescriptorProto cd1 = (DescriptorProtos.DescriptorProto) o1.getDescriptor();
                DescriptorProtos.DescriptorProto cd2 = (DescriptorProtos.DescriptorProto) o2.getDescriptor();
                return cd1.getName().compareTo(cd2.getName());
            }
        });

        for (FileDescriptorProto proto : protoList) {
			FileOptions options = proto.getOptions();
			String javaPackage = options.getJavaPackage();
			CodeGenerator cg = factory.createCodeGenerator();
			cg.generate(outdir, javaPackage, "utf-8", proto, allServices, allMessages);
		}
	}

    private void trimComments(StringBuilder sb, String comments) {
        if (comments == null) {
            return;
        }

        String[] sections = comments.split("\n");
        for (String section : sections) {
            String text = section;
            while (true) {
                text = text.trim();
                if (text.length() == 0) {
                    break;
                } else if (text.charAt(0) == '*') {
                    text = text.substring(1);
                } else {
                    break;
                }
            }

            if (text.length() > 0) {
                sb.append(text).append("<br />");
            }
        }
    }

	private List<CommentedDescriptor> getMessagesComments(DescriptorProtos.FileDescriptorProto proto) {
		List<CommentedDescriptor> messagesComments = new ArrayList<>();
		List<DescriptorProtos.DescriptorProto> messages = proto.getMessageTypeList();
		List<DescriptorProtos.SourceCodeInfo.Location> locations = proto.getSourceCodeInfo().getLocationList();

		String messagePathFormat = "" + DescriptorProtos.FileDescriptorProto.MESSAGE_TYPE_FIELD_NUMBER + ".%d";
		StringBuilder comments = new StringBuilder();
		StringBuilder fieldComments = new StringBuilder();
		String messagePath;
		DescriptorProtos.SourceCodeInfo.Location location;
		int idx = 0;
		for (DescriptorProtos.DescriptorProto message : messages) {
			comments.setLength(0);
			messagePath = String.format(messagePathFormat, idx);
			location = searchLocation(locations, messagePath);
			if (location != null) {
                trimComments(comments, location.getLeadingComments());
                trimComments(comments, location.getTrailingComments());
			}

			CommentedDescriptor cd = new CommentedDescriptor(message, comments.toString());
			messagesComments.add(cd);

			int fieldCount = message.getFieldCount();
			for (int i = 0; i < fieldCount; ++i) {
				fieldComments.setLength(0);
				DescriptorProtos.FieldDescriptorProto field = message.getField(i);
				String fieldPath = messagePath + '.' + DescriptorProtos.FileDescriptorProto.PACKAGE_FIELD_NUMBER + '.' + i;
				location = searchLocation(locations, fieldPath);
				if (location != null) {
                    trimComments(fieldComments, location.getLeadingComments());
                    trimComments(fieldComments, location.getTrailingComments());
				}

				CommentedDescriptor cdField = new CommentedDescriptor(field, fieldComments.toString());
				cd.addChild(cdField);
			}

			++idx;
		}

		return messagesComments;
	}

	private List<CommentedDescriptor> getServicesComments(DescriptorProtos.FileDescriptorProto proto) {
		List<DescriptorProtos.ServiceDescriptorProto> services = proto.getServiceList();
		List<DescriptorProtos.SourceCodeInfo.Location> locations = proto.getSourceCodeInfo().getLocationList();
		String servicePathFormat = "" + DescriptorProtos.FileDescriptorProto.SERVICE_FIELD_NUMBER + ".%d";
		List<CommentedDescriptor> commentedServices = new ArrayList<>();

		StringBuilder comments = new StringBuilder();
		String servicePath;
		DescriptorProtos.SourceCodeInfo.Location location;
		int idx = 0;
		for (DescriptorProtos.ServiceDescriptorProto service : services) {
			comments.setLength(0);
			servicePath = String.format(servicePathFormat, idx);
			location = searchLocation(locations, servicePath);
			if (location != null) {
                trimComments(comments, location.getLeadingComments());
                trimComments(comments, location.getTrailingComments());
			}

			CommentedDescriptor cd = new CommentedDescriptor(service, comments.toString());
			commentedServices.add(cd);

			int methodCount = service.getMethodCount();
			for (int i = 0; i < methodCount; ++i) {
				comments.setLength(0);
				DescriptorProtos.MethodDescriptorProto method = service.getMethod(i);
				String fieldPath = servicePath + '.' + DescriptorProtos.FileDescriptorProto.PACKAGE_FIELD_NUMBER + '.' + i;
				location = searchLocation(locations, fieldPath);
				if (location != null) {
                    trimComments(comments, location.getLeadingComments());
                    trimComments(comments, location.getTrailingComments());
				}

				CommentedDescriptor cdMethod = new CommentedDescriptor(method, comments.toString());
				cd.addChild(cdMethod);
			}

			++idx;
		}

		return commentedServices;
	}

	private String getPath(DescriptorProtos.SourceCodeInfo.Location location) {
		StringBuilder path = new StringBuilder();
		List<Integer> pathList = location.getPathList();
		int size = pathList.size();
		for (int i = 0; i < size; ++i) {
			if (i > 0) {
				path.append('.');
			}

			path.append(pathList.get(i));
		}

		return path.toString();
	}

	private DescriptorProtos.SourceCodeInfo.Location searchLocation(List<DescriptorProtos.SourceCodeInfo.Location> locations,
																	String path) {
		for (DescriptorProtos.SourceCodeInfo.Location location : locations) {
			if (getPath(location).equalsIgnoreCase(path)) {
				return location;
			}
		}

		return null;
	}
}
