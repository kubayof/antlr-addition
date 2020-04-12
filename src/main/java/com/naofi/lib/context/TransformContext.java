package com.naofi.lib.context;

import com.naofi.lib.annotation.Post;
import com.naofi.lib.annotation.Pre;
import com.naofi.lib.annotation.Transform;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TransformContext {
    private Class<?> parserClass;
    private Class<?> lexerClass;
    private Class<?> visitorClass;
    private String rootRuleName;
    private Method rootMethod;
    private String grammarPackage;
    private String grammarName = null;
    private List<RuleInfo> rules;
    private TreeVisitor treeVisitor;
    private Parser parser = null;
    private ParseTree tree;
    private CharStream chars;
    private Class<?>[] transformClasses;

    public TransformContext(String grammarPackage, String rootRuleName, Class<?>... transformClasses) {
        disableUnsafeWarning();
        this.grammarPackage = grammarPackage;
        this.rootRuleName = rootRuleName;
        rules = new ArrayList<>();
        this.transformClasses = transformClasses;
    }

    public ParseTree process (CharStream chars) {
        this.chars = chars;
        if (parser == null) {
            for (Class<?> cl : transformClasses) {
                processClass(cl);
            }
            treeVisitor = new TreeVisitor(parserClass, lexerClass, visitorClass, rootMethod, rules);
        } else {
            createParserAndTree();
        }

        return treeVisitor.process(tree);
    }

    private void checkRootRule() {
        try {
            rootMethod = parserClass.getMethod(rootRuleName);
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
            checkRootRule();
            createParserAndTree();
        } else {
            if (!this.grammarName.equals(grammarName)) {
                throw new IllegalStateException("Classes in one TransformContext must have same grammar");
            }
        }

        Method[] methods = cl.getDeclaredMethods();

        for (Method method : methods) {
            if (method.getReturnType() != String.class) {
                throw new IllegalStateException("Method return type must be String: " + method);
            }
            if (!Modifier.isStatic(method.getModifiers())) {
                throw new IllegalStateException("Method '" + method.getName() + "' is not static");
            }
            if (method.isAnnotationPresent(Post.class)) {
                rules.add(new RuleInfo(method, parser, lexerClass));
            } else if (method.isAnnotationPresent(Pre.class)) {

                rules.add(new RuleInfo(method, parser, lexerClass));
            }
        }
    }

    private void findProcessors() {
        String lexerName = grammarPackage + "." + grammarName + "Lexer";
        try {
            lexerClass = Class.forName(lexerName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find lexer in package '" + grammarPackage + "', expected lexer name: '" + lexerName + "'");
        }

        String parserName = grammarPackage + "." + grammarName + "Parser";
        try {
            parserClass = Class.forName(parserName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find parser in package '" + grammarPackage + "', expected parser name: '" + parserName + "'");
        }

        String visitorName = grammarPackage + "." + grammarName + "BaseVisitor";
        try {
            visitorClass = Class.forName(visitorName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find visitor in package '" + grammarPackage + "', expected visitor name: '" + visitorName + "'");
        }
    }

    private void createParserAndTree() {
        try {
            Lexer lexer = (Lexer) lexerClass.getConstructor(CharStream.class).newInstance(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = (Parser) parserClass.getConstructor(TokenStream.class).newInstance(tokens);
            tree = (ParseTree) rootMethod.invoke(parser);
        } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e) {
            throw new IllegalStateException(e);
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
