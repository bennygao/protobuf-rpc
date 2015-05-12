package com.yingshibao.app.server;

import com.google.protobuf.TextFormat;
import com.yingshibao.app.idl.UserInfo;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by gaobo on 15/5/12.
 */
public class ParseObjcData {
    public static void main(String[] args) throws Exception {
        InputStream is = new FileInputStream("/tmp/data");
        DataInputStream dis = new DataInputStream(is);
        int stamp = dis.readInt();
        int serviceId = dis.readInt();
        byte stage = dis.readByte();
        System.out.println(String.format("stamp=%d service=%d stage=%d", stamp, serviceId, stage));
        UserInfo userInfo = UserInfo.PARSER.parseFrom(dis);
        System.out.println(TextFormat.printToUnicodeString(userInfo));
    }
}
