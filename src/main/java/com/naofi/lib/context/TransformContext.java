package com.naofi.lib.context;

import com.naofi.lib.annotation.Post;
import com.naofi.lib.annotation.Pre;
import com.naofi.lib.annotation.Transform;
import org.antlr.v4.runtime.CharStream;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class TransformContext {
    private Class<?> parser;
    private Class<?> lexer;
    private Class<?> visitor;
    private String rootRuleName;
    private Method rootMethod;
    private String grammarPackage;
    private String grammarName = null;
    private List<RuleInfo> rules;
    private TreeVisitor treeVisitor;

    public TransformContext(String grammarPackage, String rootRuleName, Class<?>... transformClasses) {
        disableUnsafeWarning();
        this.grammarPackage = grammarPackage;
        this.rootRuleName = rootRuleName;
        for (Class<?> cl : transformClasses) {
            processClass(cl);
        }
        checkRootRule();
        treeVisitor = new TreeVisitor(parser, lexer, visitor, rootMethod, rules);
    }

    public void process(CharStream chars) {
        treeVisitor.process(chars);
    }

    private void checkRootRule() {
        try {
            rootMethod = parser.getMethod(rootRuleName);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Cannot find parser rule with name '" + rootRuleName + "'");
        }
    }

    private void processClass(Class<?> cl) {
        if (!cl.isAnnotationPresent(Transform.class)) {
            throw new IllegalStateException("Annotation @Transform('grammar name') is not specified for class " + cl.getCanonicalName());
        }

        Transform transformAnnotation = cl.getAnnotation(Transform.class);
        String grammarName = transformAnnotation.value();
        if (this.grammarName == null) {
            this.grammarName = grammarName;
            findProcessors();
        } else {
            if (!this.grammarName.equals(grammarName)) {
                throw new IllegalStateException("Classes in one TransformContext must have same grammar");
            }
        }

        Method[] methods = cl.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Post.class)) {
                if (method.getReturnType() != String.class) {
                    throw new IllegalStateException("Method return type must be String: " + method);
                }
                rules.add(new RuleInfo(method));
            } else if (method.isAnnotationPresent(Pre.class)) {
                if (method.getReturnType() != String.class) {
                    throw new IllegalStateException("Method return type must be String: " + method);
                }
                rules.add(new RuleInfo(method));
            }
        }
    }

    private void findProcessors() {
        String lexerName = grammarPackage + "." + grammarName + "Lexer";
        try {
            lexer = Class.forName(lexerName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find lexer in package '" + grammarPackage + "'");
        }

        String parserName = grammarPackage + "." + grammarName + "Parser";
        try {
            parser = Class.forName(parserName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find parser in package '" + grammarPackage + "'");
        }

        String visitorName = grammarPackage + "." + grammarName + "BaseVisitor";
        try {
            visitor = Class.forName(visitorName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find visitor in package '" + grammarPackage + "'");
        }
    }

    private void disableUnsafeWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);

            Class<?> cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception e) {
            // Do nothing
        }
    }
}
