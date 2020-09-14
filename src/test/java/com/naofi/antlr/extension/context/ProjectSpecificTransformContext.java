package com.naofi.antlr.extension.context;

public class ProjectSpecificTransformContext extends TransformContext {
    private final static String grammarPackage = "com.naofi.antlr.extension.generated";

    public ProjectSpecificTransformContext(String rootRuleName, Object... transformObjects) {
        super(grammarPackage, rootRuleName, transformObjects);
    }

    public ProjectSpecificTransformContext(String rootRuleName, Class<?>... transformClasses) {
        super(grammarPackage, rootRuleName, transformClasses);
    }
}
