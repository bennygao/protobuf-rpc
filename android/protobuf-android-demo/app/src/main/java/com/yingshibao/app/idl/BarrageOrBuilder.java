// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: yingshibao.proto

package com.yingshibao.app.idl;

public interface BarrageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:yingshibao.Barrage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required string senderNickname = 1;</code>
   *
   * <pre>
   * 发弹幕用户昵称
   * </pre>
   */
  boolean hasSenderNickname();
  /**
   * <code>required string senderNickname = 1;</code>
   *
   * <pre>
   * 发弹幕用户昵称
   * </pre>
   */
  java.lang.String getSenderNickname();
  /**
   * <code>required string senderNickname = 1;</code>
   *
   * <pre>
   * 发弹幕用户昵称
   * </pre>
   */
  com.google.protobuf.ByteString
      getSenderNicknameBytes();

  /**
   * <code>required string message = 2;</code>
   *
   * <pre>
   * 弹幕消息
   * </pre>
   */
  boolean hasMessage();
  /**
   * <code>required string message = 2;</code>
   *
   * <pre>
   * 弹幕消息
   * </pre>
   */
  java.lang.String getMessage();
  /**
   * <code>required string message = 2;</code>
   *
   * <pre>
   * 弹幕消息
   * </pre>
   */
  com.google.protobuf.ByteString
      getMessageBytes();
}