package com.naofi.antlr.extension.context;

public class HtmlTransformContext extends ProjectSpecificTransformContext {
    private final static String rootRuleName = "htmlDocument";

    public HtmlTransformContext(Object... transformObjects) {
        super(rootRuleName, transformObjects);
    }

    public HtmlTransformContext(Class<?>... transformClasses) {
        super(rootRuleName, transformClasses);
    }
}
