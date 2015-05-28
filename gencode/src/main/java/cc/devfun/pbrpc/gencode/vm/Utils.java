package cc.devfun.pbrpc.gencode.vm;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;

import java.util.HashMap;
import java.util.Map;

public class Utils {
	static class SingletonHolder {
		static Utils singleton = new Utils();
	}

	public static Utils getInstance() {
		return SingletonHolder.singleton;
	}

	private Map<DescriptorProtos.FieldDescriptorProto.Type, String> typeNameMap;

	private Utils() {
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

	public String getTypeName(DescriptorProtos.FieldDescriptorProto field) {
		DescriptorProtos.FieldDescriptorProto.Type type = field.getType();
		return typeNameMap.get(type);
	}

	public boolean isBasicType(DescriptorProtos.FieldDescriptorProto field) {
		return typeNameMap.containsKey(field.getType());
	}

	public boolean isBasicType(String typeName) {
		String baseName = getBaseName(typeName);
		for (String name : typeNameMap.values()) {
			if (name.equalsIgnoreCase(typeName)) {
				return true;
			}
		}

		return false;
	}

	public String getBaseName(String pathname) {
		int idx = pathname.lastIndexOf('.');
		return idx >= 0 ? pathname.substring(idx + 1) : pathname;
	}

	public String getFirstName(String name) {
		int idx = name.lastIndexOf('.');
		return idx >= 0 ? name.substring(0, idx) : name;
	}

	public String firstLetterUpperCaseFirstName(String name) {
        return firstLetterUpperCase(getFirstName(name));
	}

	public String firstLetterLowerCase(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public String firstLetterUpperCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	public String firstLetterLowerCaseBaseName(String pathname) {
		String baseName = getBaseName(pathname);
		return firstLetterLowerCase(baseName);
	}

	public String firstLetterUpperCaseBaseName(String pathname) {
		String baseName = getBaseName(pathname);
		return firstLetterUpperCase(baseName);
	}

	public String getMethodPrototype(MethodDescriptorProto method) {
		return String.format("%s %s(%s)", method.getOutputType(), method.getName(), method.getInputType());
	}

	public String getSimpleMethodPrototype(MethodDescriptorProto method) {
		return String.format("%s %s(%s)", getBaseName(method.getOutputType()),
				getBaseName(method.getName()), getBaseName(method.getInputType()));
	}
	
	public int hashCode(MethodDescriptorProto method) {
		return getMethodPrototype(method).hashCode();
	}
}
