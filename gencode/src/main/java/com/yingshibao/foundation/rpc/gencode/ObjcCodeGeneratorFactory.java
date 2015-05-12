package com.yingshibao.foundation.rpc.gencode;

import com.yingshibao.foundation.rpc.gencode.vm.ObjcCodeGenerator;

/**
 * Created by gaobo on 15/5/12.
 */
public class ObjcCodeGeneratorFactory implements CodeGeneratorFactory {
    @Override
    public CodeGenerator createCodeGenerator() throws Exception {
        return new ObjcCodeGenerator();
    }
}
