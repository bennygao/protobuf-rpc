package cc.devfun.pbrpc.gencode.vm;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.GeneratedMessage;
import cc.devfun.pbrpc.gencode.CodeGenerator;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class CommentedDescriptor {
    GeneratedMessage descriptor;
    String comments;
    List<CommentedDescriptor> children;

    static Map<DescriptorProtos.FieldDescriptorProto.Type, String> typeNameMap;

    static {
        typeNameMap = new HashMap<>();
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE, "double");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT, "float");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64, "int64");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT64, "uint64");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32, "int32");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED64, "fixed64");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_FIXED32, "fixed32");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL, "bool");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING, "string");
//        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_GROUP, "");
//        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE, "");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES, "bytes");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_UINT32, "uint32");
//        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM, "");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED32, "sfixed32");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SFIXED64, "sfixed64");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT32, "sint32");
        typeNameMap.put(DescriptorProtos.FieldDescriptorProto.Type.TYPE_SINT64, "sint64");
    }

    public CommentedDescriptor(GeneratedMessage descriptor, String comments) {
        this.descriptor = descriptor;
        this.comments = comments;
        this.children = new ArrayList<>();
    }

    public GeneratedMessage getDescriptor() {
        return this.descriptor;
    }

    public String getComments() {
        return this.comments;
    }

    public List<CommentedDescriptor> getChildren() {
        return this.children;
    }

    public void addChild(CommentedDescriptor child) {
        this.children.add(child);
    }

    @Override
    public String toString() {
        if (descriptor instanceof DescriptorProtos.ServiceDescriptorProto) {
            DescriptorProtos.ServiceDescriptorProto sdp = (DescriptorProtos.ServiceDescriptorProto) descriptor;
            return "service:" + sdp.getName() + " // " + comments;
        } else if (descriptor instanceof DescriptorProtos.DescriptorProto) {
            DescriptorProtos.DescriptorProto msg = (DescriptorProtos.DescriptorProto) descriptor;
            return "message:" + msg.getName() + " // " + comments;
        } else if (descriptor instanceof DescriptorProtos.FieldDescriptorProto) {
            DescriptorProtos.FieldDescriptorProto field = (DescriptorProtos.FieldDescriptorProto) descriptor;
            DescriptorProtos.FieldDescriptorProto.Label label = field.getLabel();
            if (label == DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED) {
                return "field: list<" + field.getTypeName() + "> " + field.getName() + " = " + field.getNumber() + " //" + comments;
            } else {
                String typeName = getTypeName(field);
                return "field:" + typeName + ' ' + field.getName() + " = " + field.getNumber() + " //" + comments;
            }
        } else {
            return "unknown";
        }
    }

    private String getTypeName(DescriptorProtos.FieldDescriptorProto field) {
        DescriptorProtos.FieldDescriptorProto.Type type = field.getType();
        return typeNameMap.get(type);
    }
}


public class HtmlGenerator extends VelocityCodeGeneratorTemplate implements CodeGenerator {
    // background: linear-gradient(rgba(70,91,116,1.0),rgba(37,45,56,1.0))
    public HtmlGenerator() throws Exception {
        super();
    }

    @Override
    public void generate(String srcDir, String srcPackage,
                         String outputEncoding, DescriptorProtos.FileDescriptorProto proto) throws Exception {
        Utils util = new Utils();
        VelocityContext vc = new VelocityContext();
        vc.put("package", srcPackage);
        vc.put("util", util);

        List<DescriptorProtos.ServiceDescriptorProto> services = proto.getServiceList();
        getServicesComments(proto);
        getMessagesComments(proto);
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
                String leadingComments = location.getLeadingComments();
                if (leadingComments != null) {
                    comments.append(leadingComments);
                }

                String trailingComments = location.getTrailingComments();
                if (trailingComments != null) {
                    comments.append(trailingComments);
                }
            }

            CommentedDescriptor cd = new CommentedDescriptor(message, comments.toString());
            messagesComments.add(cd);
            System.out.println(cd);

            int fieldCount = message.getFieldCount();
            for (int i = 0; i < fieldCount; ++i) {
                fieldComments.setLength(0);
                DescriptorProtos.FieldDescriptorProto field = message.getField(i);
                String fieldPath = messagePath + '.' + DescriptorProtos.FileDescriptorProto.PACKAGE_FIELD_NUMBER + '.' + i;
                location = searchLocation(locations, fieldPath);
                if (location != null) {
                    String leadingComments = location.getLeadingComments();
                    if (leadingComments != null) {
                        fieldComments.append(leadingComments);
                    }

                    String trailingComments = location.getTrailingComments();
                    if (trailingComments != null) {
                        fieldComments.append(trailingComments);
                    }
                }

                CommentedDescriptor cdField = new CommentedDescriptor(field, fieldComments.toString());
                cd.addChild(cdField);
                System.out.println(cdField);
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

        String comments;
        String servicePath;
        DescriptorProtos.SourceCodeInfo.Location location;
        int idx = 0;
        for (DescriptorProtos.ServiceDescriptorProto service : services) {
            comments = "";
            servicePath = String.format(servicePathFormat, idx);
            location = searchLocation(locations, servicePath);
            if (location != null) {
                String leadingComments = location.getLeadingComments();
                if (leadingComments != null) {
                    comments += leadingComments;
                }

                String trailingComments = location.getTrailingComments();
                if (trailingComments != null) {
                    comments += trailingComments;
                }
            }

            CommentedDescriptor cd = new CommentedDescriptor(service, comments);
            commentedServices.add(cd);
            System.out.println(cd);

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
