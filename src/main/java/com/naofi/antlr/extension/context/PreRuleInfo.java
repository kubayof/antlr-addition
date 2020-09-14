package com.naofi.antlr.extension.context;

import com.naofi.antlr.extension.annotation.Pre;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Pair;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

final class PreRuleInfo extends BaseRuleInfo {
    /**
     * Returns null if method is not annotated with @Pre, or PreRuleInfo otherwise.
     */
    static PreRuleInfo createOrNull(Method method, Parser parser, Class<? extends Lexer> lexerClass, Object targetObject,
                                    TreeVisitor visitor, TransformContext transformContext) {
        if (!method.isAnnotationPresent(Pre.class)) {
            return null;
        }

        return new PreRuleInfo(method, parser, lexerClass, targetObject, visitor, transformContext);
    }

    private PreRuleInfo(Method method, Parser parser, Class<? extends Lexer> lexerClass, Object targetObject,
                        TreeVisitor visitor, TransformContext transformContext) {
        super(method, parser, lexerClass, targetObject, visitor, transformContext);
    }

    @Override
    protected int getRule(Method method) {
        return getRule(method, method.getAnnotation(Pre.class).rule());
    }

    @Override
    protected Pair<String, LinkedHashMap<String, String>> createAntlrPattern(Method method) {
        return createAntlrPattern(method, method.getAnnotation(Pre.class).value());
    }
}
