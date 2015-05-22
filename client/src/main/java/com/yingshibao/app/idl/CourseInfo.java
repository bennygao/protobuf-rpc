// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: module.proto

package com.yingshibao.app.idl;

/**
 * Protobuf type {@code yingshibao.CourseInfo}
 *
 * <pre>
 * 课程信息
 * </pre>
 */
public final class CourseInfo extends
    com.google.protobuf.GeneratedMessage implements
    // @@protoc_insertion_point(message_implements:yingshibao.CourseInfo)
    CourseInfoOrBuilder {
  // Use CourseInfo.newBuilder() to construct.
  private CourseInfo(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
    super(builder);
    this.unknownFields = builder.getUnknownFields();
  }
  private CourseInfo(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

  private static final CourseInfo defaultInstance;
  public static CourseInfo getDefaultInstance() {
    return defaultInstance;
  }

  public CourseInfo getDefaultInstanceForType() {
    return defaultInstance;
  }

  private final com.google.protobuf.UnknownFieldSet unknownFields;
  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
      getUnknownFields() {
    return this.unknownFields;
  }
  private CourseInfo(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    initFields();
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownField(input, unknownFields,
                                   extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 8: {
            bitField0_ |= 0x00000001;
            id_ = input.readInt32();
            break;
          }
          case 18: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000002;
            name_ = bs;
            break;
          }
          case 26: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000004;
            teacherName_ = bs;
            break;
          }
          case 32: {
            bitField0_ |= 0x00000008;
            price_ = input.readInt32();
            break;
          }
          case 42: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000010;
            descritpion_ = bs;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e.getMessage()).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.yingshibao.app.idl.Module.internal_static_yingshibao_CourseInfo_descriptor;
  }

  protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.yingshibao.app.idl.Module.internal_static_yingshibao_CourseInfo_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.yingshibao.app.idl.CourseInfo.class, com.yingshibao.app.idl.CourseInfo.Builder.class);
  }

  public static com.google.protobuf.Parser<CourseInfo> PARSER =
      new com.google.protobuf.AbstractParser<CourseInfo>() {
    public CourseInfo parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new CourseInfo(input, extensionRegistry);
    }
  };

  @java.lang.Override
  public com.google.protobuf.Parser<CourseInfo> getParserForType() {
    return PARSER;
  }

  private int bitField0_;
  public static final int ID_FIELD_NUMBER = 1;
  private int id_;
  /**
   * <code>required int32 id = 1;</code>
   *
   * <pre>
   * 课程ID
   * </pre>
   */
  public boolean hasId() {
    return ((bitField0_ & 0x00000001) == 0x00000001);
  }
  /**
   * <code>required int32 id = 1;</code>
   *
   * <pre>
   * 课程ID
   * </pre>
   */
  public int getId() {
    return id_;
  }

  public static final int NAME_FIELD_NUMBER = 2;
  private java.lang.Object name_;
  /**
   * <code>required string name = 2;</code>
   *
   * <pre>
   * 课程名称
   * </pre>
   */
  public boolean hasName() {
    return ((bitField0_ & 0x00000002) == 0x00000002);
  }
  /**
   * <code>required string name = 2;</code>
   *
   * <pre>
   * 课程名称
   * </pre>
   */
  public java.lang.String getName() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        name_ = s;
      }
      return s;
    }
  }
  /**
   * <code>required string name = 2;</code>
   *
   * <pre>
   * 课程名称
   * </pre>
   */
  public com.google.protobuf.ByteString
      getNameBytes() {
    java.lang.Object ref = name_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      name_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int TEACHERNAME_FIELD_NUMBER = 3;
  private java.lang.Object teacherName_;
  /**
   * <code>required string teacherName = 3;</code>
   *
   * <pre>
   * 老师姓名
   * </pre>
   */
  public boolean hasTeacherName() {
    return ((bitField0_ & 0x00000004) == 0x00000004);
  }
  /**
   * <code>required string teacherName = 3;</code>
   *
   * <pre>
   * 老师姓名
   * </pre>
   */
  public java.lang.String getTeacherName() {
    java.lang.Object ref = teacherName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        teacherName_ = s;
      }
      return s;
    }
  }
  /**
   * <code>required string teacherName = 3;</code>
   *
   * <pre>
   * 老师姓名
   * </pre>
   */
  public com.google.protobuf.ByteString
      getTeacherNameBytes() {
    java.lang.Object ref = teacherName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      teacherName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int PRICE_FIELD_NUMBER = 4;
  private int price_;
  /**
   * <code>required int32 price = 4;</code>
   *
   * <pre>
   * 课程价格, 0:免费; 其他金额为课程价格，以分为单位。
   * </pre>
   */
  public boolean hasPrice() {
    return ((bitField0_ & 0x00000008) == 0x00000008);
  }
  /**
   * <code>required int32 price = 4;</code>
   *
   * <pre>
   * 课程价格, 0:免费; 其他金额为课程价格，以分为单位。
   * </pre>
   */
  public int getPrice() {
    return price_;
  }

  public static final int DESCRITPION_FIELD_NUMBER = 5;
  private java.lang.Object descritpion_;
  /**
   * <code>required string descritpion = 5;</code>
   *
   * <pre>
   * 课程简介
   * </pre>
   */
  public boolean hasDescritpion() {
    return ((bitField0_ & 0x00000010) == 0x00000010);
  }
  /**
   * <code>required string descritpion = 5;</code>
   *
   * <pre>
   * 课程简介
   * </pre>
   */
  public java.lang.String getDescritpion() {
    java.lang.Object ref = descritpion_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        descritpion_ = s;
      }
      return s;
    }
  }
  /**
   * <code>required string descritpion = 5;</code>
   *
   * <pre>
   * 课程简介
   * </pre>
   */
  public com.google.protobuf.ByteString
      getDescritpionBytes() {
    java.lang.Object ref = descritpion_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      descritpion_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private void initFields() {
    id_ = 0;
    name_ = "";
    teacherName_ = "";
    price_ = 0;
    descritpion_ = "";
  }
  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasId()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasName()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasTeacherName()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasPrice()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasDescritpion()) {
      memoizedIsInitialized = 0;
      return false;
    }
    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    getSerializedSize();
    if (((bitField0_ & 0x00000001) == 0x00000001)) {
      output.writeInt32(1, id_);
    }
    if (((bitField0_ & 0x00000002) == 0x00000002)) {
      output.writeBytes(2, getNameBytes());
    }
    if (((bitField0_ & 0x00000004) == 0x00000004)) {
      output.writeBytes(3, getTeacherNameBytes());
    }
    if (((bitField0_ & 0x00000008) == 0x00000008)) {
      output.writeInt32(4, price_);
    }
    if (((bitField0_ & 0x00000010) == 0x00000010)) {
      output.writeBytes(5, getDescritpionBytes());
    }
    getUnknownFields().writeTo(output);
  }

  private int memoizedSerializedSize = -1;
  public int getSerializedSize() {
    int size = memoizedSerializedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) == 0x00000001)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, id_);
    }
    if (((bitField0_ & 0x00000002) == 0x00000002)) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, getNameBytes());
    }
    if (((bitField0_ & 0x00000004) == 0x00000004)) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(3, getTeacherNameBytes());
    }
    if (((bitField0_ & 0x00000008) == 0x00000008)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, price_);
    }
    if (((bitField0_ & 0x00000010) == 0x00000010)) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(5, getDescritpionBytes());
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSerializedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  protected java.lang.Object writeReplace()
      throws java.io.ObjectStreamException {
    return super.writeReplace();
  }

  public static com.yingshibao.app.idl.CourseInfo parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.yingshibao.app.idl.CourseInfo parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.yingshibao.app.idl.CourseInfo parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.yingshibao.app.idl.CourseInfo parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.yingshibao.app.idl.CourseInfo parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.yingshibao.app.idl.CourseInfo parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static com.yingshibao.app.idl.CourseInfo parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static com.yingshibao.app.idl.CourseInfo parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static com.yingshibao.app.idl.CourseInfo parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static com.yingshibao.app.idl.CourseInfo parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public static Builder newBuilder() { return Builder.create(); }
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder(com.yingshibao.app.idl.CourseInfo prototype) {
    return newBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() { return newBuilder(this); }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessage.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code yingshibao.CourseInfo}
   *
   * <pre>
   * 课程信息
   * </pre>
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessage.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:yingshibao.CourseInfo)
      com.yingshibao.app.idl.CourseInfoOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.yingshibao.app.idl.Module.internal_static_yingshibao_CourseInfo_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.yingshibao.app.idl.Module.internal_static_yingshibao_CourseInfo_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.yingshibao.app.idl.CourseInfo.class, com.yingshibao.app.idl.CourseInfo.Builder.class);
    }

    // Construct using com.yingshibao.app.idl.CourseInfo.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
      }
    }
    private static Builder create() {
      return new Builder();
    }

    public Builder clear() {
      super.clear();
      id_ = 0;
      bitField0_ = (bitField0_ & ~0x00000001);
      name_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      teacherName_ = "";
      bitField0_ = (bitField0_ & ~0x00000004);
      price_ = 0;
      bitField0_ = (bitField0_ & ~0x00000008);
      descritpion_ = "";
      bitField0_ = (bitField0_ & ~0x00000010);
      return this;
    }

    public Builder clone() {
      return create().mergeFrom(buildPartial());
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.yingshibao.app.idl.Module.internal_static_yingshibao_CourseInfo_descriptor;
    }

    public com.yingshibao.app.idl.CourseInfo getDefaultInstanceForType() {
      return com.yingshibao.app.idl.CourseInfo.getDefaultInstance();
    }

    public com.yingshibao.app.idl.CourseInfo build() {
      com.yingshibao.app.idl.CourseInfo result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.yingshibao.app.idl.CourseInfo buildPartial() {
      com.yingshibao.app.idl.CourseInfo result = new com.yingshibao.app.idl.CourseInfo(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
        to_bitField0_ |= 0x00000001;
      }
      result.id_ = id_;
      if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
        to_bitField0_ |= 0x00000002;
      }
      result.name_ = name_;
      if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
        to_bitField0_ |= 0x00000004;
      }
      result.teacherName_ = teacherName_;
      if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
        to_bitField0_ |= 0x00000008;
      }
      result.price_ = price_;
      if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
        to_bitField0_ |= 0x00000010;
      }
      result.descritpion_ = descritpion_;
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.yingshibao.app.idl.CourseInfo) {
        return mergeFrom((com.yingshibao.app.idl.CourseInfo)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.yingshibao.app.idl.CourseInfo other) {
      if (other == com.yingshibao.app.idl.CourseInfo.getDefaultInstance()) return this;
      if (other.hasId()) {
        setId(other.getId());
      }
      if (other.hasName()) {
        bitField0_ |= 0x00000002;
        name_ = other.name_;
        onChanged();
      }
      if (other.hasTeacherName()) {
        bitField0_ |= 0x00000004;
        teacherName_ = other.teacherName_;
        onChanged();
      }
      if (other.hasPrice()) {
        setPrice(other.getPrice());
      }
      if (other.hasDescritpion()) {
        bitField0_ |= 0x00000010;
        descritpion_ = other.descritpion_;
        onChanged();
      }
      this.mergeUnknownFields(other.getUnknownFields());
      return this;
    }

    public final boolean isInitialized() {
      if (!hasId()) {
        
        return false;
      }
      if (!hasName()) {
        
        return false;
      }
      if (!hasTeacherName()) {
        
        return false;
      }
      if (!hasPrice()) {
        
        return false;
      }
      if (!hasDescritpion()) {
        
        return false;
      }
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.yingshibao.app.idl.CourseInfo parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.yingshibao.app.idl.CourseInfo) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int id_ ;
    /**
     * <code>required int32 id = 1;</code>
     *
     * <pre>
     * 课程ID
     * </pre>
     */
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required int32 id = 1;</code>
     *
     * <pre>
     * 课程ID
     * </pre>
     */
    public int getId() {
      return id_;
    }
    /**
     * <code>required int32 id = 1;</code>
     *
     * <pre>
     * 课程ID
     * </pre>
     */
    public Builder setId(int value) {
      bitField0_ |= 0x00000001;
      id_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required int32 id = 1;</code>
     *
     * <pre>
     * 课程ID
     * </pre>
     */
    public Builder clearId() {
      bitField0_ = (bitField0_ & ~0x00000001);
      id_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object name_ = "";
    /**
     * <code>required string name = 2;</code>
     *
     * <pre>
     * 课程名称
     * </pre>
     */
    public boolean hasName() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string name = 2;</code>
     *
     * <pre>
     * 课程名称
     * </pre>
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          name_ = s;
        }
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>required string name = 2;</code>
     *
     * <pre>
     * 课程名称
     * </pre>
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>required string name = 2;</code>
     *
     * <pre>
     * 课程名称
     * </pre>
     */
    public Builder setName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      name_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required string name = 2;</code>
     *
     * <pre>
     * 课程名称
     * </pre>
     */
    public Builder clearName() {
      bitField0_ = (bitField0_ & ~0x00000002);
      name_ = getDefaultInstance().getName();
      onChanged();
      return this;
    }
    /**
     * <code>required string name = 2;</code>
     *
     * <pre>
     * 课程名称
     * </pre>
     */
    public Builder setNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      name_ = value;
      onChanged();
      return this;
    }

    private java.lang.Object teacherName_ = "";
    /**
     * <code>required string teacherName = 3;</code>
     *
     * <pre>
     * 老师姓名
     * </pre>
     */
    public boolean hasTeacherName() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required string teacherName = 3;</code>
     *
     * <pre>
     * 老师姓名
     * </pre>
     */
    public java.lang.String getTeacherName() {
      java.lang.Object ref = teacherName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          teacherName_ = s;
        }
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>required string teacherName = 3;</code>
     *
     * <pre>
     * 老师姓名
     * </pre>
     */
    public com.google.protobuf.ByteString
        getTeacherNameBytes() {
      java.lang.Object ref = teacherName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        teacherName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>required string teacherName = 3;</code>
     *
     * <pre>
     * 老师姓名
     * </pre>
     */
    public Builder setTeacherName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      teacherName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required string teacherName = 3;</code>
     *
     * <pre>
     * 老师姓名
     * </pre>
     */
    public Builder clearTeacherName() {
      bitField0_ = (bitField0_ & ~0x00000004);
      teacherName_ = getDefaultInstance().getTeacherName();
      onChanged();
      return this;
    }
    /**
     * <code>required string teacherName = 3;</code>
     *
     * <pre>
     * 老师姓名
     * </pre>
     */
    public Builder setTeacherNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      teacherName_ = value;
      onChanged();
      return this;
    }

    private int price_ ;
    /**
     * <code>required int32 price = 4;</code>
     *
     * <pre>
     * 课程价格, 0:免费; 其他金额为课程价格，以分为单位。
     * </pre>
     */
    public boolean hasPrice() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required int32 price = 4;</code>
     *
     * <pre>
     * 课程价格, 0:免费; 其他金额为课程价格，以分为单位。
     * </pre>
     */
    public int getPrice() {
      return price_;
    }
    /**
     * <code>required int32 price = 4;</code>
     *
     * <pre>
     * 课程价格, 0:免费; 其他金额为课程价格，以分为单位。
     * </pre>
     */
    public Builder setPrice(int value) {
      bitField0_ |= 0x00000008;
      price_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required int32 price = 4;</code>
     *
     * <pre>
     * 课程价格, 0:免费; 其他金额为课程价格，以分为单位。
     * </pre>
     */
    public Builder clearPrice() {
      bitField0_ = (bitField0_ & ~0x00000008);
      price_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object descritpion_ = "";
    /**
     * <code>required string descritpion = 5;</code>
     *
     * <pre>
     * 课程简介
     * </pre>
     */
    public boolean hasDescritpion() {
      return ((bitField0_ & 0x00000010) == 0x00000010);
    }
    /**
     * <code>required string descritpion = 5;</code>
     *
     * <pre>
     * 课程简介
     * </pre>
     */
    public java.lang.String getDescritpion() {
      java.lang.Object ref = descritpion_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          descritpion_ = s;
        }
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>required string descritpion = 5;</code>
     *
     * <pre>
     * 课程简介
     * </pre>
     */
    public com.google.protobuf.ByteString
        getDescritpionBytes() {
      java.lang.Object ref = descritpion_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        descritpion_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>required string descritpion = 5;</code>
     *
     * <pre>
     * 课程简介
     * </pre>
     */
    public Builder setDescritpion(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
      descritpion_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required string descritpion = 5;</code>
     *
     * <pre>
     * 课程简介
     * </pre>
     */
    public Builder clearDescritpion() {
      bitField0_ = (bitField0_ & ~0x00000010);
      descritpion_ = getDefaultInstance().getDescritpion();
      onChanged();
      return this;
    }
    /**
     * <code>required string descritpion = 5;</code>
     *
     * <pre>
     * 课程简介
     * </pre>
     */
    public Builder setDescritpionBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
      descritpion_ = value;
      onChanged();
      return this;
    }

    // @@protoc_insertion_point(builder_scope:yingshibao.CourseInfo)
  }

  static {
    defaultInstance = new CourseInfo(true);
    defaultInstance.initFields();
  }

  // @@protoc_insertion_point(class_scope:yingshibao.CourseInfo)
}

