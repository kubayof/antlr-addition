package com.naofi.antlr.extension.context;

public class Java8TransformContext extends ProjectSpecificTransformContext {
    private final static String rootRuleName = "compilationUnit";
    
    public Java8TransformContext(Object... transformObjects) {
        super(rootRuleName, transformObjects);
    }

    public Java8TransformContext(Class<?>... transformClasses) {
        super(rootRuleName, transformClasses);
    }
}
