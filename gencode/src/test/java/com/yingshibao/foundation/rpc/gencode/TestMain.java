package com.yingshibao.foundation.rpc.gencode;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by gaobo on 15/5/12.
 */
public class TestMain {
    public static void main(String[] args) throws Exception {
        InputStream istream = new FileInputStream("proto/proto.bin");
        RpcGenerator gen = new RpcGenerator(istream, new HtmlGeneratorFactory(), "test");
        gen.generate();

        istream.close();
        System.exit(0);
    }
}
