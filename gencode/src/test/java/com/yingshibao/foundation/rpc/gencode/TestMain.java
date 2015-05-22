package com.yingshibao.foundation.rpc.gencode;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by gaobo on 15/5/12.
 */
public class TestMain {

    private static void testBinary() {
        byte abyte = 9;
        int mask = 0x0080;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; ++i) {
            if ((abyte & mask) > 0) {
                sb.append('1');
            } else {
                sb.append('0');
            }

            mask >>>= 1;
        }

        System.out.println(sb.toString());
    }

    private static void testRpcGenerator() throws Exception {
        InputStream istream = new FileInputStream("proto/proto.bin");
        RpcGenerator gen = new RpcGenerator(istream, new HtmlGeneratorFactory(), "test");
        gen.generate();
        istream.close();
    }

    public static void main(String[] args) throws Exception {
        testRpcGenerator();
    }
}
