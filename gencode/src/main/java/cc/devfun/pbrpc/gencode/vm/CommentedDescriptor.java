package cc.devfun.pbrpc.gencode.vm;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.GeneratedMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentedDescriptor {
    GeneratedMessage descriptor;
    String comments;
    List<CommentedDescriptor> children;

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
                String typeName = Utils.getInstance().getTypeName(field);
                return "field:" + typeName + ' ' + field.getName() + " = " + field.getNumber() + " //" + comments;
            }
        } else if (descriptor instanceof DescriptorProtos.MethodDescriptorProto) {
            DescriptorProtos.MethodDescriptorProto mdp = (DescriptorProtos.MethodDescriptorProto) descriptor;
            return "method:" + Utils.getInstance().getSimpleMethodPrototype(mdp) + " //" + comments;
        } else {
            return "unknown";
        }
    }
}
