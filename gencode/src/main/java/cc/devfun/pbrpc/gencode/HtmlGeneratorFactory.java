package cc.devfun.pbrpc.gencode;

import cc.devfun.pbrpc.gencode.vm.HtmlGenerator;

public class HtmlGeneratorFactory implements CodeGeneratorFactory {
    @Override
    public CodeGenerator createCodeGenerator() throws Exception {
        return new HtmlGenerator();
    }
}
