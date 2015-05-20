package com.yingshibao.foundation.rpc.gencode;

import com.yingshibao.foundation.rpc.gencode.vm.HtmlGenerator;

public class HtmlGeneratorFactory implements CodeGeneratorFactory {
    @Override
    public CodeGenerator createCodeGenerator() throws Exception {
        return new HtmlGenerator();
    }
}
