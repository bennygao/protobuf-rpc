package com.yingshibao.foundation.rpc.gencode.vm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;

import com.yingshibao.foundation.rpc.gencode.LineEndFilterWriter;


public class VelocityCodeGeneratorTemplate {
    public VelocityCodeGeneratorTemplate() throws Exception {
        Properties props = new Properties();
        // 模板文件是UTF-8编码
        props.setProperty("input.encoding", "UTF8");

        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.description",
                "Velocity Classpath Resource Loader");
        props.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");

//			props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
//					"org.apache.velocity.runtime.log.NullLogSystem");

        Velocity.init(props);
    }

    protected String getSourcePath(String path, String pkg) {
        if (pkg == null) {
            return path;
        } else {
            return new StringBuffer(path).append(File.separatorChar)
                    .append(pkg.replace('.', File.separatorChar)).toString();

        }
    }

    protected Writer getSourceWriter(File file, String encoding)
            throws Exception {
        Writer writer;
        if (encoding != null) {
            writer = new OutputStreamWriter(new FileOutputStream(file),
                    encoding);
        } else {
            writer = new FileWriter(file);
        }

        return new LineEndFilterWriter(writer);
    }

    protected Writer getSourceWriter(String pathname, String encoding)
            throws Exception {
        return getSourceWriter(new File(pathname), encoding);
    }
}
