package cc.devfun.pbrpc.gencode.vm;

import com.google.protobuf.DescriptorProtos;
import cc.devfun.pbrpc.gencode.CodeGenerator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.Writer;
import java.util.Date;

/**
 * Created by gaobo on 15/5/12.
 */
public class ObjcCodeGenerator extends VelocityCodeGeneratorTemplate implements CodeGenerator {
    public ObjcCodeGenerator() throws Exception {
        super();
    }

    @Override
    public void generate(String srcDir, String srcPackage,
                         String outputEncoding, DescriptorProtos.FileDescriptorProto proto) throws Exception {
        Utils util = new Utils();
        VelocityContext vc = new VelocityContext();
        vc.put("package", srcPackage);
        vc.put("util", util);
        vc.put("createTime", new Date());

        File outDir = new File(srcDir);
        if (! outDir.exists()) {
            outDir.mkdirs();
        }

        Template headerTemplate = Velocity.getTemplate("vm/objc/header.vm");
        String protoName = util.firstLetterUpperCase(proto.getPackage());
        String fileName =  protoName + ".rpc.h";
        File headerFile = new File(outDir, fileName);
        Writer writer = getSourceWriter(headerFile, outputEncoding);
        vc.put("protoName", protoName);
        vc.put("fileName", fileName);
        vc.put("services", proto.getServiceList());
        headerTemplate.merge(vc, writer);
        writer.close();


        Template codeTemplate = Velocity.getTemplate("vm/objc/code.vm");
        fileName = protoName + ".rpc.m";
        File codeFile = new File(outDir, fileName);
        writer = getSourceWriter(codeFile, outputEncoding);
        vc.put("fileName", fileName);
        codeTemplate.merge(vc, writer);
        writer.close();
    }
}
