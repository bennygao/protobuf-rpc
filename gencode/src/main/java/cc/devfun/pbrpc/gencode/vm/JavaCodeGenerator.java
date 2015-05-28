package cc.devfun.pbrpc.gencode.vm;

import java.io.File;
import java.io.Writer;
import java.util.List;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.compiler.PluginProtos;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import cc.devfun.pbrpc.gencode.CodeGenerator;

public class JavaCodeGenerator extends VelocityCodeGeneratorTemplate implements CodeGenerator {
    public JavaCodeGenerator() throws Exception {
        super();
    }

    @Override
    public void generate(String srcDir, PluginProtos.CodeGeneratorRequest request) throws Exception {
        VelocityContext vc = new VelocityContext();

        String javaPackage = getJavaPackage(request);
        vc.put("package", javaPackage);
        vc.put("util", Utils.getInstance());

        String path = getSourcePath(srcDir, javaPackage);
        new File(path).mkdirs();

        Template serviceTemplate = Velocity.getTemplate("vm/java/service.vm");
        List<CommentedDescriptor> allServices = getAllServices(request);
        for (CommentedDescriptor service : allServices) {
            DescriptorProtos.ServiceDescriptorProto sdp = (DescriptorProtos.ServiceDescriptorProto) service.getDescriptor();
            String serviceName = sdp.getName();
            String javaFile = path + File.separatorChar + serviceName + ".java";
            Writer writer = getSourceWriter(javaFile, "utf-8");
            vc.put("serviceName", serviceName);
            vc.put("methods", sdp.getMethodList());
            serviceTemplate.merge(vc, writer);
            writer.close();
        }
    }
}
