package cc.devfun.pbrpc.gencode;

import cc.devfun.pbrpc.gencode.vm.ObjcCodeGenerator;

/**
 * Created by gaobo on 15/5/12.
 */
public class ObjcCodeGeneratorFactory implements CodeGeneratorFactory {
    @Override
    public CodeGenerator createCodeGenerator() throws Exception {
        return new ObjcCodeGenerator();
    }
}
