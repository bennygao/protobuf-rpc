package com.yingshibao.foundation.rpc.gencode.vm;

import java.io.File;
import java.io.Writer;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.ServiceDescriptorProto;
import com.yingshibao.foundation.rpc.gencode.CodeGenerator;

public class JavaCodeGenerator extends VelocityCodeGeneratorTemplate implements CodeGenerator {
	public JavaCodeGenerator() throws Exception {
		super();
	}
	
	@Override
	public void generate(String srcDir, String srcPackage,
			String outputEncoding, FileDescriptorProto proto) throws Exception {
		Utils util = new Utils();
		VelocityContext vc = new VelocityContext();
		vc.put("package", srcPackage);
		vc.put("util", util);
		
		String path = getSourcePath(srcDir, srcPackage);
		new File(path).mkdirs();
		
		Template serviceTemplate = Velocity.getTemplate("vm/java/service.vm");
		
		
		List<ServiceDescriptorProto> services = proto.getServiceList();
		for (ServiceDescriptorProto service : services) {
			String serviceName = service.getName();
			String javaFile = path + File.separatorChar + serviceName + ".java";
			Writer writer = getSourceWriter(javaFile, outputEncoding);
			vc.put("serviceName", serviceName);
			vc.put("methods", service.getMethodList());
			serviceTemplate.merge(vc, writer);
			writer.close();
		}
		
	}

}
