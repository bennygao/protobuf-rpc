// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: yingshibao.proto

package com.yingshibao.app.idl;

public interface CourseTypeOrBuilder extends
    // @@protoc_insertion_point(interface_extends:yingshibao.CourseType)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>required uint32 courseType = 1;</code>
   *
   * <pre>
   * 课程类型 1:我的课程（已报名、已订购); 2:全部课程
   * </pre>
   */
  boolean hasCourseType();
  /**
   * <code>required uint32 courseType = 1;</code>
   *
   * <pre>
   * 课程类型 1:我的课程（已报名、已订购); 2:全部课程
   * </pre>
   */
  int getCourseType();

  /**
   * <code>required uint32 num = 2;</code>
   *
   * <pre>
   * 指定返回列表中最大课程个数
   * </pre>
   */
  boolean hasNum();
  /**
   * <code>required uint32 num = 2;</code>
   *
   * <pre>
   * 指定返回列表中最大课程个数
   * </pre>
   */
  int getNum();

  /**
   * <code>required uint32 pageNum = 3;</code>
   *
   * <pre>
   * 指定返回第几页
   * </pre>
   */
  boolean hasPageNum();
  /**
   * <code>required uint32 pageNum = 3;</code>
   *
   * <pre>
   * 指定返回第几页
   * </pre>
   */
  int getPageNum();
}