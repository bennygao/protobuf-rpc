// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: module.proto

package com.yingshibao.app.idl;

public interface TestAllTypesOrBuilder extends
    // @@protoc_insertion_point(interface_extends:yingshibao.TestAllTypes)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional int32 optional_int32 = 1;</code>
   *
   * <pre>
   * Singular
   * </pre>
   */
  boolean hasOptionalInt32();
  /**
   * <code>optional int32 optional_int32 = 1;</code>
   *
   * <pre>
   * Singular
   * </pre>
   */
  int getOptionalInt32();

  /**
   * <code>optional int64 optional_int64 = 2;</code>
   */
  boolean hasOptionalInt64();
  /**
   * <code>optional int64 optional_int64 = 2;</code>
   */
  long getOptionalInt64();

  /**
   * <code>optional uint32 optional_uint32 = 3;</code>
   */
  boolean hasOptionalUint32();
  /**
   * <code>optional uint32 optional_uint32 = 3;</code>
   */
  int getOptionalUint32();

  /**
   * <code>optional uint64 optional_uint64 = 4;</code>
   */
  boolean hasOptionalUint64();
  /**
   * <code>optional uint64 optional_uint64 = 4;</code>
   */
  long getOptionalUint64();

  /**
   * <code>optional sint32 optional_sint32 = 5;</code>
   */
  boolean hasOptionalSint32();
  /**
   * <code>optional sint32 optional_sint32 = 5;</code>
   */
  int getOptionalSint32();

  /**
   * <code>optional sint64 optional_sint64 = 6;</code>
   */
  boolean hasOptionalSint64();
  /**
   * <code>optional sint64 optional_sint64 = 6;</code>
   */
  long getOptionalSint64();

  /**
   * <code>optional fixed32 optional_fixed32 = 7;</code>
   */
  boolean hasOptionalFixed32();
  /**
   * <code>optional fixed32 optional_fixed32 = 7;</code>
   */
  int getOptionalFixed32();

  /**
   * <code>optional fixed64 optional_fixed64 = 8;</code>
   */
  boolean hasOptionalFixed64();
  /**
   * <code>optional fixed64 optional_fixed64 = 8;</code>
   */
  long getOptionalFixed64();

  /**
   * <code>optional sfixed32 optional_sfixed32 = 9;</code>
   */
  boolean hasOptionalSfixed32();
  /**
   * <code>optional sfixed32 optional_sfixed32 = 9;</code>
   */
  int getOptionalSfixed32();

  /**
   * <code>optional sfixed64 optional_sfixed64 = 10;</code>
   */
  boolean hasOptionalSfixed64();
  /**
   * <code>optional sfixed64 optional_sfixed64 = 10;</code>
   */
  long getOptionalSfixed64();

  /**
   * <code>optional float optional_float = 11;</code>
   */
  boolean hasOptionalFloat();
  /**
   * <code>optional float optional_float = 11;</code>
   */
  float getOptionalFloat();

  /**
   * <code>optional double optional_double = 12;</code>
   */
  boolean hasOptionalDouble();
  /**
   * <code>optional double optional_double = 12;</code>
   */
  double getOptionalDouble();

  /**
   * <code>optional bool optional_bool = 13;</code>
   */
  boolean hasOptionalBool();
  /**
   * <code>optional bool optional_bool = 13;</code>
   */
  boolean getOptionalBool();

  /**
   * <code>optional string optional_string = 14;</code>
   */
  boolean hasOptionalString();
  /**
   * <code>optional string optional_string = 14;</code>
   */
  java.lang.String getOptionalString();
  /**
   * <code>optional string optional_string = 14;</code>
   */
  com.google.protobuf.ByteString
      getOptionalStringBytes();

  /**
   * <code>optional bytes optional_bytes = 15;</code>
   */
  boolean hasOptionalBytes();
  /**
   * <code>optional bytes optional_bytes = 15;</code>
   */
  com.google.protobuf.ByteString getOptionalBytes();

  /**
   * <code>optional group OptionalGroup = 16 { ... }</code>
   */
  boolean hasOptionalGroup();
  /**
   * <code>optional group OptionalGroup = 16 { ... }</code>
   */
  com.yingshibao.app.idl.TestAllTypes.OptionalGroup getOptionalGroup();
  /**
   * <code>optional group OptionalGroup = 16 { ... }</code>
   */
  com.yingshibao.app.idl.TestAllTypes.OptionalGroupOrBuilder getOptionalGroupOrBuilder();

  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage optional_nested_message = 18;</code>
   */
  boolean hasOptionalNestedMessage();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage optional_nested_message = 18;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessage getOptionalNestedMessage();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage optional_nested_message = 18;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessageOrBuilder getOptionalNestedMessageOrBuilder();

  /**
   * <code>optional .yingshibao.TestAllTypes.NestedEnum optional_nested_enum = 21;</code>
   */
  boolean hasOptionalNestedEnum();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedEnum optional_nested_enum = 21;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedEnum getOptionalNestedEnum();

  /**
   * <code>optional string optional_string_piece = 24 [ctype = STRING_PIECE];</code>
   */
  boolean hasOptionalStringPiece();
  /**
   * <code>optional string optional_string_piece = 24 [ctype = STRING_PIECE];</code>
   */
  java.lang.String getOptionalStringPiece();
  /**
   * <code>optional string optional_string_piece = 24 [ctype = STRING_PIECE];</code>
   */
  com.google.protobuf.ByteString
      getOptionalStringPieceBytes();

  /**
   * <code>optional string optional_cord = 25 [ctype = CORD];</code>
   */
  boolean hasOptionalCord();
  /**
   * <code>optional string optional_cord = 25 [ctype = CORD];</code>
   */
  java.lang.String getOptionalCord();
  /**
   * <code>optional string optional_cord = 25 [ctype = CORD];</code>
   */
  com.google.protobuf.ByteString
      getOptionalCordBytes();

  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage optional_lazy_message = 27 [lazy = true];</code>
   */
  boolean hasOptionalLazyMessage();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage optional_lazy_message = 27 [lazy = true];</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessage getOptionalLazyMessage();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage optional_lazy_message = 27 [lazy = true];</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessageOrBuilder getOptionalLazyMessageOrBuilder();

  /**
   * <code>repeated int32 repeated_int32 = 31;</code>
   *
   * <pre>
   * Repeated
   * </pre>
   */
  java.util.List<java.lang.Integer> getRepeatedInt32List();
  /**
   * <code>repeated int32 repeated_int32 = 31;</code>
   *
   * <pre>
   * Repeated
   * </pre>
   */
  int getRepeatedInt32Count();
  /**
   * <code>repeated int32 repeated_int32 = 31;</code>
   *
   * <pre>
   * Repeated
   * </pre>
   */
  int getRepeatedInt32(int index);

  /**
   * <code>repeated int64 repeated_int64 = 32;</code>
   */
  java.util.List<java.lang.Long> getRepeatedInt64List();
  /**
   * <code>repeated int64 repeated_int64 = 32;</code>
   */
  int getRepeatedInt64Count();
  /**
   * <code>repeated int64 repeated_int64 = 32;</code>
   */
  long getRepeatedInt64(int index);

  /**
   * <code>repeated uint32 repeated_uint32 = 33;</code>
   */
  java.util.List<java.lang.Integer> getRepeatedUint32List();
  /**
   * <code>repeated uint32 repeated_uint32 = 33;</code>
   */
  int getRepeatedUint32Count();
  /**
   * <code>repeated uint32 repeated_uint32 = 33;</code>
   */
  int getRepeatedUint32(int index);

  /**
   * <code>repeated uint64 repeated_uint64 = 34;</code>
   */
  java.util.List<java.lang.Long> getRepeatedUint64List();
  /**
   * <code>repeated uint64 repeated_uint64 = 34;</code>
   */
  int getRepeatedUint64Count();
  /**
   * <code>repeated uint64 repeated_uint64 = 34;</code>
   */
  long getRepeatedUint64(int index);

  /**
   * <code>repeated sint32 repeated_sint32 = 35;</code>
   */
  java.util.List<java.lang.Integer> getRepeatedSint32List();
  /**
   * <code>repeated sint32 repeated_sint32 = 35;</code>
   */
  int getRepeatedSint32Count();
  /**
   * <code>repeated sint32 repeated_sint32 = 35;</code>
   */
  int getRepeatedSint32(int index);

  /**
   * <code>repeated sint64 repeated_sint64 = 36;</code>
   */
  java.util.List<java.lang.Long> getRepeatedSint64List();
  /**
   * <code>repeated sint64 repeated_sint64 = 36;</code>
   */
  int getRepeatedSint64Count();
  /**
   * <code>repeated sint64 repeated_sint64 = 36;</code>
   */
  long getRepeatedSint64(int index);

  /**
   * <code>repeated fixed32 repeated_fixed32 = 37;</code>
   */
  java.util.List<java.lang.Integer> getRepeatedFixed32List();
  /**
   * <code>repeated fixed32 repeated_fixed32 = 37;</code>
   */
  int getRepeatedFixed32Count();
  /**
   * <code>repeated fixed32 repeated_fixed32 = 37;</code>
   */
  int getRepeatedFixed32(int index);

  /**
   * <code>repeated fixed64 repeated_fixed64 = 38;</code>
   */
  java.util.List<java.lang.Long> getRepeatedFixed64List();
  /**
   * <code>repeated fixed64 repeated_fixed64 = 38;</code>
   */
  int getRepeatedFixed64Count();
  /**
   * <code>repeated fixed64 repeated_fixed64 = 38;</code>
   */
  long getRepeatedFixed64(int index);

  /**
   * <code>repeated sfixed32 repeated_sfixed32 = 39;</code>
   */
  java.util.List<java.lang.Integer> getRepeatedSfixed32List();
  /**
   * <code>repeated sfixed32 repeated_sfixed32 = 39;</code>
   */
  int getRepeatedSfixed32Count();
  /**
   * <code>repeated sfixed32 repeated_sfixed32 = 39;</code>
   */
  int getRepeatedSfixed32(int index);

  /**
   * <code>repeated sfixed64 repeated_sfixed64 = 40;</code>
   */
  java.util.List<java.lang.Long> getRepeatedSfixed64List();
  /**
   * <code>repeated sfixed64 repeated_sfixed64 = 40;</code>
   */
  int getRepeatedSfixed64Count();
  /**
   * <code>repeated sfixed64 repeated_sfixed64 = 40;</code>
   */
  long getRepeatedSfixed64(int index);

  /**
   * <code>repeated float repeated_float = 41;</code>
   */
  java.util.List<java.lang.Float> getRepeatedFloatList();
  /**
   * <code>repeated float repeated_float = 41;</code>
   */
  int getRepeatedFloatCount();
  /**
   * <code>repeated float repeated_float = 41;</code>
   */
  float getRepeatedFloat(int index);

  /**
   * <code>repeated double repeated_double = 42;</code>
   */
  java.util.List<java.lang.Double> getRepeatedDoubleList();
  /**
   * <code>repeated double repeated_double = 42;</code>
   */
  int getRepeatedDoubleCount();
  /**
   * <code>repeated double repeated_double = 42;</code>
   */
  double getRepeatedDouble(int index);

  /**
   * <code>repeated bool repeated_bool = 43;</code>
   */
  java.util.List<java.lang.Boolean> getRepeatedBoolList();
  /**
   * <code>repeated bool repeated_bool = 43;</code>
   */
  int getRepeatedBoolCount();
  /**
   * <code>repeated bool repeated_bool = 43;</code>
   */
  boolean getRepeatedBool(int index);

  /**
   * <code>repeated string repeated_string = 44;</code>
   */
  com.google.protobuf.ProtocolStringList
      getRepeatedStringList();
  /**
   * <code>repeated string repeated_string = 44;</code>
   */
  int getRepeatedStringCount();
  /**
   * <code>repeated string repeated_string = 44;</code>
   */
  java.lang.String getRepeatedString(int index);
  /**
   * <code>repeated string repeated_string = 44;</code>
   */
  com.google.protobuf.ByteString
      getRepeatedStringBytes(int index);

  /**
   * <code>repeated bytes repeated_bytes = 45;</code>
   */
  java.util.List<com.google.protobuf.ByteString> getRepeatedBytesList();
  /**
   * <code>repeated bytes repeated_bytes = 45;</code>
   */
  int getRepeatedBytesCount();
  /**
   * <code>repeated bytes repeated_bytes = 45;</code>
   */
  com.google.protobuf.ByteString getRepeatedBytes(int index);

  /**
   * <code>repeated group RepeatedGroup = 46 { ... }</code>
   */
  java.util.List<com.yingshibao.app.idl.TestAllTypes.RepeatedGroup> 
      getRepeatedGroupList();
  /**
   * <code>repeated group RepeatedGroup = 46 { ... }</code>
   */
  com.yingshibao.app.idl.TestAllTypes.RepeatedGroup getRepeatedGroup(int index);
  /**
   * <code>repeated group RepeatedGroup = 46 { ... }</code>
   */
  int getRepeatedGroupCount();
  /**
   * <code>repeated group RepeatedGroup = 46 { ... }</code>
   */
  java.util.List<? extends com.yingshibao.app.idl.TestAllTypes.RepeatedGroupOrBuilder> 
      getRepeatedGroupOrBuilderList();
  /**
   * <code>repeated group RepeatedGroup = 46 { ... }</code>
   */
  com.yingshibao.app.idl.TestAllTypes.RepeatedGroupOrBuilder getRepeatedGroupOrBuilder(
      int index);

  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_nested_message = 48;</code>
   */
  java.util.List<com.yingshibao.app.idl.TestAllTypes.NestedMessage> 
      getRepeatedNestedMessageList();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_nested_message = 48;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessage getRepeatedNestedMessage(int index);
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_nested_message = 48;</code>
   */
  int getRepeatedNestedMessageCount();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_nested_message = 48;</code>
   */
  java.util.List<? extends com.yingshibao.app.idl.TestAllTypes.NestedMessageOrBuilder> 
      getRepeatedNestedMessageOrBuilderList();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_nested_message = 48;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessageOrBuilder getRepeatedNestedMessageOrBuilder(
      int index);

  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedEnum repeated_nested_enum = 51;</code>
   */
  java.util.List<com.yingshibao.app.idl.TestAllTypes.NestedEnum> getRepeatedNestedEnumList();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedEnum repeated_nested_enum = 51;</code>
   */
  int getRepeatedNestedEnumCount();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedEnum repeated_nested_enum = 51;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedEnum getRepeatedNestedEnum(int index);

  /**
   * <code>repeated string repeated_string_piece = 54 [ctype = STRING_PIECE];</code>
   */
  com.google.protobuf.ProtocolStringList
      getRepeatedStringPieceList();
  /**
   * <code>repeated string repeated_string_piece = 54 [ctype = STRING_PIECE];</code>
   */
  int getRepeatedStringPieceCount();
  /**
   * <code>repeated string repeated_string_piece = 54 [ctype = STRING_PIECE];</code>
   */
  java.lang.String getRepeatedStringPiece(int index);
  /**
   * <code>repeated string repeated_string_piece = 54 [ctype = STRING_PIECE];</code>
   */
  com.google.protobuf.ByteString
      getRepeatedStringPieceBytes(int index);

  /**
   * <code>repeated string repeated_cord = 55 [ctype = CORD];</code>
   */
  com.google.protobuf.ProtocolStringList
      getRepeatedCordList();
  /**
   * <code>repeated string repeated_cord = 55 [ctype = CORD];</code>
   */
  int getRepeatedCordCount();
  /**
   * <code>repeated string repeated_cord = 55 [ctype = CORD];</code>
   */
  java.lang.String getRepeatedCord(int index);
  /**
   * <code>repeated string repeated_cord = 55 [ctype = CORD];</code>
   */
  com.google.protobuf.ByteString
      getRepeatedCordBytes(int index);

  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_lazy_message = 57 [lazy = true];</code>
   */
  java.util.List<com.yingshibao.app.idl.TestAllTypes.NestedMessage> 
      getRepeatedLazyMessageList();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_lazy_message = 57 [lazy = true];</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessage getRepeatedLazyMessage(int index);
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_lazy_message = 57 [lazy = true];</code>
   */
  int getRepeatedLazyMessageCount();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_lazy_message = 57 [lazy = true];</code>
   */
  java.util.List<? extends com.yingshibao.app.idl.TestAllTypes.NestedMessageOrBuilder> 
      getRepeatedLazyMessageOrBuilderList();
  /**
   * <code>repeated .yingshibao.TestAllTypes.NestedMessage repeated_lazy_message = 57 [lazy = true];</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessageOrBuilder getRepeatedLazyMessageOrBuilder(
      int index);

  /**
   * <code>optional int32 default_int32 = 61 [default = 41];</code>
   *
   * <pre>
   * Singular with defaults
   * </pre>
   */
  boolean hasDefaultInt32();
  /**
   * <code>optional int32 default_int32 = 61 [default = 41];</code>
   *
   * <pre>
   * Singular with defaults
   * </pre>
   */
  int getDefaultInt32();

  /**
   * <code>optional int64 default_int64 = 62 [default = 42];</code>
   */
  boolean hasDefaultInt64();
  /**
   * <code>optional int64 default_int64 = 62 [default = 42];</code>
   */
  long getDefaultInt64();

  /**
   * <code>optional uint32 default_uint32 = 63 [default = 43];</code>
   */
  boolean hasDefaultUint32();
  /**
   * <code>optional uint32 default_uint32 = 63 [default = 43];</code>
   */
  int getDefaultUint32();

  /**
   * <code>optional uint64 default_uint64 = 64 [default = 44];</code>
   */
  boolean hasDefaultUint64();
  /**
   * <code>optional uint64 default_uint64 = 64 [default = 44];</code>
   */
  long getDefaultUint64();

  /**
   * <code>optional sint32 default_sint32 = 65 [default = -45];</code>
   */
  boolean hasDefaultSint32();
  /**
   * <code>optional sint32 default_sint32 = 65 [default = -45];</code>
   */
  int getDefaultSint32();

  /**
   * <code>optional sint64 default_sint64 = 66 [default = 46];</code>
   */
  boolean hasDefaultSint64();
  /**
   * <code>optional sint64 default_sint64 = 66 [default = 46];</code>
   */
  long getDefaultSint64();

  /**
   * <code>optional fixed32 default_fixed32 = 67 [default = 47];</code>
   */
  boolean hasDefaultFixed32();
  /**
   * <code>optional fixed32 default_fixed32 = 67 [default = 47];</code>
   */
  int getDefaultFixed32();

  /**
   * <code>optional fixed64 default_fixed64 = 68 [default = 48];</code>
   */
  boolean hasDefaultFixed64();
  /**
   * <code>optional fixed64 default_fixed64 = 68 [default = 48];</code>
   */
  long getDefaultFixed64();

  /**
   * <code>optional sfixed32 default_sfixed32 = 69 [default = 49];</code>
   */
  boolean hasDefaultSfixed32();
  /**
   * <code>optional sfixed32 default_sfixed32 = 69 [default = 49];</code>
   */
  int getDefaultSfixed32();

  /**
   * <code>optional sfixed64 default_sfixed64 = 70 [default = -50];</code>
   */
  boolean hasDefaultSfixed64();
  /**
   * <code>optional sfixed64 default_sfixed64 = 70 [default = -50];</code>
   */
  long getDefaultSfixed64();

  /**
   * <code>optional float default_float = 71 [default = 51.5];</code>
   */
  boolean hasDefaultFloat();
  /**
   * <code>optional float default_float = 71 [default = 51.5];</code>
   */
  float getDefaultFloat();

  /**
   * <code>optional double default_double = 72 [default = 52000];</code>
   */
  boolean hasDefaultDouble();
  /**
   * <code>optional double default_double = 72 [default = 52000];</code>
   */
  double getDefaultDouble();

  /**
   * <code>optional bool default_bool = 73 [default = true];</code>
   */
  boolean hasDefaultBool();
  /**
   * <code>optional bool default_bool = 73 [default = true];</code>
   */
  boolean getDefaultBool();

  /**
   * <code>optional string default_string = 74 [default = "hello"];</code>
   */
  boolean hasDefaultString();
  /**
   * <code>optional string default_string = 74 [default = "hello"];</code>
   */
  java.lang.String getDefaultString();
  /**
   * <code>optional string default_string = 74 [default = "hello"];</code>
   */
  com.google.protobuf.ByteString
      getDefaultStringBytes();

  /**
   * <code>optional bytes default_bytes = 75 [default = "world"];</code>
   */
  boolean hasDefaultBytes();
  /**
   * <code>optional bytes default_bytes = 75 [default = "world"];</code>
   */
  com.google.protobuf.ByteString getDefaultBytes();

  /**
   * <code>optional .yingshibao.TestAllTypes.NestedEnum default_nested_enum = 81 [default = BAR];</code>
   */
  boolean hasDefaultNestedEnum();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedEnum default_nested_enum = 81 [default = BAR];</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedEnum getDefaultNestedEnum();

  /**
   * <code>optional string default_string_piece = 84 [default = "abc", ctype = STRING_PIECE];</code>
   */
  boolean hasDefaultStringPiece();
  /**
   * <code>optional string default_string_piece = 84 [default = "abc", ctype = STRING_PIECE];</code>
   */
  java.lang.String getDefaultStringPiece();
  /**
   * <code>optional string default_string_piece = 84 [default = "abc", ctype = STRING_PIECE];</code>
   */
  com.google.protobuf.ByteString
      getDefaultStringPieceBytes();

  /**
   * <code>optional string default_cord = 85 [default = "123", ctype = CORD];</code>
   */
  boolean hasDefaultCord();
  /**
   * <code>optional string default_cord = 85 [default = "123", ctype = CORD];</code>
   */
  java.lang.String getDefaultCord();
  /**
   * <code>optional string default_cord = 85 [default = "123", ctype = CORD];</code>
   */
  com.google.protobuf.ByteString
      getDefaultCordBytes();

  /**
   * <code>optional uint32 oneof_uint32 = 111;</code>
   */
  boolean hasOneofUint32();
  /**
   * <code>optional uint32 oneof_uint32 = 111;</code>
   */
  int getOneofUint32();

  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage oneof_nested_message = 112;</code>
   */
  boolean hasOneofNestedMessage();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage oneof_nested_message = 112;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessage getOneofNestedMessage();
  /**
   * <code>optional .yingshibao.TestAllTypes.NestedMessage oneof_nested_message = 112;</code>
   */
  com.yingshibao.app.idl.TestAllTypes.NestedMessageOrBuilder getOneofNestedMessageOrBuilder();

  /**
   * <code>optional string oneof_string = 113;</code>
   */
  boolean hasOneofString();
  /**
   * <code>optional string oneof_string = 113;</code>
   */
  java.lang.String getOneofString();
  /**
   * <code>optional string oneof_string = 113;</code>
   */
  com.google.protobuf.ByteString
      getOneofStringBytes();

  /**
   * <code>optional bytes oneof_bytes = 114;</code>
   */
  boolean hasOneofBytes();
  /**
   * <code>optional bytes oneof_bytes = 114;</code>
   */
  com.google.protobuf.ByteString getOneofBytes();
}
