package cc.devfun.pbrpc.gencode.vm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

import cc.devfun.pbrpc.gencode.LineEndFilterWriter;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.compiler.PluginProtos;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;


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

    protected String getJavaPackage(PluginProtos.CodeGeneratorRequest request) {
        for (DescriptorProtos.FileDescriptorProto proto : request.getProtoFileList()) {
            return proto.getOptions().getJavaPackage();
        }

        return "unknown";
    }

    protected String getProtoPackage(PluginProtos.CodeGeneratorRequest request) {
        for (DescriptorProtos.FileDescriptorProto proto : request.getProtoFileList()) {
            return proto.getPackage();
        }

        return "unknown";
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

    protected List<CommentedDescriptor> getAllServices(PluginProtos.CodeGeneratorRequest request) {
        List<CommentedDescriptor> allServices = new ArrayList<>();
        List<DescriptorProtos.FileDescriptorProto> protoList = request.getProtoFileList();
        for (DescriptorProtos.FileDescriptorProto proto : protoList) {
            List<CommentedDescriptor> services = getServicesComments(proto);
            allServices.addAll(services);
        }

        Collections.sort(allServices, new Comparator<CommentedDescriptor>() {
            @Override
            public int compare(CommentedDescriptor o1, CommentedDescriptor o2) {
                DescriptorProtos.ServiceDescriptorProto sd1 = (DescriptorProtos.ServiceDescriptorProto) o1.getDescriptor();
                DescriptorProtos.ServiceDescriptorProto sd2 = (DescriptorProtos.ServiceDescriptorProto) o2.getDescriptor();
                return sd1.getName().compareTo(sd2.getName());
            }
        });

        return allServices;
    }

    protected  List<CommentedDescriptor> getAllMessages(PluginProtos.CodeGeneratorRequest request) {
        List<CommentedDescriptor> allMessages = new ArrayList<>();
        List<DescriptorProtos.FileDescriptorProto> protoList = request.getProtoFileList();
        for (DescriptorProtos.FileDescriptorProto proto : protoList) {
            List<CommentedDescriptor> messages = getMessagesComments(proto);
            allMessages.addAll(messages);
        }

        Collections.sort(allMessages, new Comparator<CommentedDescriptor>() {
            @Override
            public int compare(CommentedDescriptor o1, CommentedDescriptor o2) {
                DescriptorProtos.DescriptorProto cd1 = (DescriptorProtos.DescriptorProto) o1.getDescriptor();
                DescriptorProtos.DescriptorProto cd2 = (DescriptorProtos.DescriptorProto) o2.getDescriptor();
                return cd1.getName().compareTo(cd2.getName());
            }
        });

        return allMessages;
    }
}
