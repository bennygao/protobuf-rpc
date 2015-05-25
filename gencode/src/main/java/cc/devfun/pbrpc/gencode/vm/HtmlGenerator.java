package cc.devfun.pbrpc.gencode.vm;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.GeneratedMessage;
import cc.devfun.pbrpc.gencode.CodeGenerator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.Writer;
import java.util.Date;
import java.util.List;

public class HtmlGenerator extends VelocityCodeGeneratorTemplate implements CodeGenerator {
    private boolean generated;

    public HtmlGenerator() throws Exception {
        super();
        generated = false;
    }

    @Override
    public void generate(String srcDir, String srcPackage,
                         String outputEncoding, DescriptorProtos.FileDescriptorProto proto,
                         List<CommentedDescriptor> allServices, List<CommentedDescriptor> allMessages) throws Exception {
        if (generated) {
            return;
        }

        Utils util = Utils.getInstance();
        VelocityContext vc = new VelocityContext();
        vc.put("createTime", new Date());
        vc.put("package", srcPackage);
        vc.put("util", util);

        File outDir = new File(srcDir);
        if (! outDir.exists()) {
            outDir.mkdirs();
        }

        vc.put("services", allServices);
        vc.put("messages", allMessages);

        File targetFile;
        Writer writer;
        Template template;
        // 生成stylesheet.css
        template = Velocity.getTemplate("vm/html/stylesheet.css");
        targetFile = new File(outDir, "stylesheet.css");
        writer = getSourceWriter(targetFile, outputEncoding);
        template.merge(vc, writer);
        writer.close();

        // 生成index.html
        template = Velocity.getTemplate("vm/html/index.html.vm");
        targetFile = new File(outDir, "index.html");
        writer = getSourceWriter(targetFile, outputEncoding);
        template.merge(vc, writer);
        writer.close();

        // 生成services-index.html
        template = Velocity.getTemplate("vm/html/services-index.html.vm");
        targetFile = new File(outDir, "services-index.html");
        writer = getSourceWriter(targetFile, outputEncoding);
        template.merge(vc, writer);
        writer.close();

        // 生成messages-index.html
        template = Velocity.getTemplate("vm/html/messages-index.html.vm");
        targetFile = new File(outDir, "messages-index.html");
        writer = getSourceWriter(targetFile, outputEncoding);
        template.merge(vc, writer);
        writer.close();

        // 生成services-description.html
        template = Velocity.getTemplate("vm/html/services-description.html.vm");
        targetFile = new File(outDir, "services-description.html");
        writer = getSourceWriter(targetFile, outputEncoding);
        template.merge(vc, writer);
        writer.close();

        // 生成messages-description.html
        template = Velocity.getTemplate("vm/html/messages-description.html.vm");
        targetFile = new File(outDir, "messages-description.html");
        writer = getSourceWriter(targetFile, outputEncoding);
        template.merge(vc, writer);
        writer.close();

        generated = true;
    }
}
