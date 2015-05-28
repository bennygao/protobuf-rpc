package cc.devfun.pbrpc.gencode.vm;

import com.google.protobuf.DescriptorProtos;
import cc.devfun.pbrpc.gencode.CodeGenerator;
import com.google.protobuf.compiler.PluginProtos;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.Writer;
import java.util.Date;
import java.util.List;

public class ObjcCodeGenerator extends VelocityCodeGeneratorTemplate implements CodeGenerator {
    public ObjcCodeGenerator() throws Exception {
        super();
    }

    @Override
    public void generate(String srcDir, PluginProtos.CodeGeneratorRequest request) throws Exception {
        Utils utils = Utils.getInstance();
        VelocityContext vc = new VelocityContext();
        vc.put("util", utils);
        vc.put("createTime", new Date());
        vc.put("protoList", request.getProtoFileList());

        File outDir = new File(srcDir);
        if (! outDir.exists()) {
            outDir.mkdirs();
        }

        Template headerTemplate = Velocity.getTemplate("vm/objc/header.vm");
        String protoName = utils.firstLetterUpperCase(getProtoPackage(request));
        String fileName =  protoName + ".rpc.h";
        File headerFile = new File(outDir, fileName);
        Writer writer = getSourceWriter(headerFile, "utf-8");
        vc.put("protoName", protoName);
        vc.put("fileName", fileName);
        vc.put("services", getAllServices(request));
        headerTemplate.merge(vc, writer);
        writer.close();


        Template codeTemplate = Velocity.getTemplate("vm/objc/code.vm");
        fileName = protoName + ".rpc.m";
        File codeFile = new File(outDir, fileName);
        writer = getSourceWriter(codeFile, "utf-8");
        vc.put("fileName", fileName);
        codeTemplate.merge(vc, writer);
        writer.close();
    }
}
