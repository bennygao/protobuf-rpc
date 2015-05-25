package cc.devfun.pbrpc.gencode;

import cc.devfun.pbrpc.gencode.vm.CommentedDescriptor;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

import java.util.List;

public interface CodeGenerator {
	public void generate(String srcDir, String srcPackage,
						 String outputEncoding, FileDescriptorProto proto,
						 List<CommentedDescriptor> allServices, List<CommentedDescriptor> allMessages) throws Exception;
}
