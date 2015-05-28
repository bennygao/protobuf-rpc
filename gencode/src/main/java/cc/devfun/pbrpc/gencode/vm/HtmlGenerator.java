package cc.devfun.pbrpc.gencode.vm;

import cc.devfun.pbrpc.gencode.CodeGenerator;
import com.google.protobuf.compiler.PluginProtos;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.Writer;
import java.util.Date;

public class HtmlGenerator extends VelocityCodeGeneratorTemplate implements CodeGenerator {
    public HtmlGenerator() throws Exception {
        super();
    }

    @Override
    public void generate(String srcDir, PluginProtos.CodeGeneratorRequest request) throws Exception {
        Utils util = Utils.getInstance();
        VelocityContext vc = new VelocityContext();
        vc.put("createTime", new Date());
        vc.put("util", util);

        File outDir = new File(srcDir);
        if (! outDir.exists()) {
            outDir.mkdirs();
        }

        vc.put("services", getAllServices(request));
        vc.put("messages", getAllMessages(request));

        File targetFile;
        Writer writer;
        Template template;
        // 生成stylesheet.css
        template = Velocity.getTemplate("vm/html/stylesheet.css");
        targetFile = new File(outDir, "stylesheet.css");
        writer = getSourceWriter(targetFile, "utf-8");
        template.merge(vc, writer);
        writer.close();

        // 生成index.html
        template = Velocity.getTemplate("vm/html/index.html.vm");
        targetFile = new File(outDir, "index.html");
        writer = getSourceWriter(targetFile, "utf-8");
        template.merge(vc, writer);
        writer.close();

        // 生成services-index.html
        template = Velocity.getTemplate("vm/html/services-index.html.vm");
        targetFile = new File(outDir, "services-index.html");
        writer = getSourceWriter(targetFile, "utf-8");
        template.merge(vc, writer);
        writer.close();

        // 生成messages-index.html
        template = Velocity.getTemplate("vm/html/messages-index.html.vm");
        targetFile = new File(outDir, "messages-index.html");
        writer = getSourceWriter(targetFile, "utf-8");
        template.merge(vc, writer);
        writer.close();

        // 生成services-description.html
        template = Velocity.getTemplate("vm/html/services-description.html.vm");
        targetFile = new File(outDir, "services-description.html");
        writer = getSourceWriter(targetFile, "utf-8");
        template.merge(vc, writer);
        writer.close();

        // 生成messages-description.html
        template = Velocity.getTemplate("vm/html/messages-description.html.vm");
        targetFile = new File(outDir, "messages-description.html");
        writer = getSourceWriter(targetFile, "utf-8");
        template.merge(vc, writer);
        writer.close();
    }
}
