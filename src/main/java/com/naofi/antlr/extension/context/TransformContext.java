package com.naofi.antlr.extension.context;

import com.naofi.antlr.extension.annotation.Post;
import com.naofi.antlr.extension.annotation.Pre;
import com.naofi.antlr.extension.annotation.Transform;
import com.naofi.antlr.extension.context.methods.AddLocalsMapVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.*;

public class TransformContext {
    private final String rootRuleName;
    private Method rootMethod;
    private final String grammarPackage;
    private String grammarName = null;
    private final List<BaseRuleInfo> preRules;
    private final List<BaseRuleInfo> postRules;
    private TreeVisitor treeVisitor;
    private Parser parser = null;
    private Lexer lexer = null;
    private ParseTree tree;
    private CharStream chars;
    private final Map<Class<?>, Object> transformClassObjects;
    private AntlrInfo antlrInfo;

    /**
     * Initialize objects passed to this constructor only using TransformContext::create static method!!
     * Classes must be modified by ASM before usage.
     * Or call AddLocalsMapVisitor.transform(..) manually to transform class.
     */
    public TransformContext(String grammarPackage, String rootRuleName, Object... transformObjects) {
        disableUnsafeWarning();
        this.grammarPackage = grammarPackage;
        this.rootRuleName = rootRuleName;
        preRules = new ArrayList<>();
        postRules = new ArrayList<>();
        this.transformClassObjects = new HashMap<>();
        for (Object transformObject : transformObjects) {

            transformClassObjects.put(transformObject.getClass(), transformObject);
        }
    }

    public static Object create(Class<?> clazz, Object... initArgs) {
        Class<?> transformedClass = AddLocalsMapVisitor.transform(clazz);
        Class<?>[] parameterTypes = new Class<?>[initArgs.length];
        for (int i = 0; i < initArgs.length; i++) {
            parameterTypes[i] = initArgs[i].getClass();
        }
        try {
            return transformedClass.getConstructor(parameterTypes).newInstance(initArgs);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public TransformContext(String grammarPackage, String rootRuleName, Class<?>... transformClasses) {
        disableUnsafeWarning();
        this.grammarPackage = grammarPackage;
        this.rootRuleName = rootRuleName;
        preRules = new ArrayList<>();
        postRules = new ArrayList<>();
        this.transformClassObjects = new HashMap<>();
        for (Class<?> transformClass : transformClasses) {
            try {
                Class<?> transformedClass = AddLocalsMapVisitor.transform(transformClass);
                transformClassObjects.put(transformedClass, transformedClass.getConstructor().newInstance());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getTransformObject(Class<?> clazz) {
        String className = clazz.getCanonicalName();
        for (Object obj : transformClassObjects.values()) {
            if (obj.getClass().getCanonicalName().equals(className)) {
                return (T) obj;
            }
        }
        return null;
    }

    public ParseTree process(String code) {
        return process(CharStreams.fromString(code));
    }

    public ParseTree process(CharStream chars) {
        this.chars = chars;
        if (parser == null) {
            for (Class<?> cl : transformClassObjects.keySet()) {
                processClass(cl);
            }
            treeVisitor = new TreeVisitor(antlrInfo, rootMethod, preRules, postRules);
        } else {
            Pair<ParseTree, Parser> misc = antlrInfo.parse(chars, rootMethod);
            tree = misc.a;
            parser = misc.b;
        }

        for (BaseRuleInfo info : preRules) {
            info.setTreeVisitor(treeVisitor);
        }

        for (BaseRuleInfo info : postRules) {
            info.setTreeVisitor(treeVisitor);
        }
        return treeVisitor.process(tree);
    }

    private void processClass(Class<?> cl) {
        if (!cl.isAnnotationPresent(Transform.class)) {
            throw new IllegalStateException("Annotation @Transform('grammar name') is not specified for class " + cl.getCanonicalName());
        }

        Transform transformAnnotation = cl.getAnnotation(Transform.class);
        String grammarName = transformAnnotation.value();
        if (this.grammarName == null) {
            this.grammarName = grammarName;
            antlrInfo = AntlrInfo.getForGrammar(grammarName, grammarPackage);
            rootMethod = antlrInfo.getParserRuleUtils().getMethod(rootRuleName);
            Pair<ParseTree, Parser> misc = antlrInfo.parse(chars, rootMethod);
            tree = misc.a;
            parser = misc.b;
        } else {
            if (!this.grammarName.equals(grammarName)) {
                throw new IllegalStateException("Classes in one TransformContext must have same grammar");
            }
        }

        Method[] methods = cl.getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Post.class)) {
                if (method.getReturnType() != String.class &&
                        method.getReturnType() != void.class) {
                    throw new IllegalStateException("Method return type must be String or Void: " + method);
                }
                postRules.add(PostRuleInfo.createOrNull(method, parser, antlrInfo.getLexerClass(),
                        transformClassObjects.get(method.getDeclaringClass()), null,
                        this));
            } else if (method.isAnnotationPresent(Pre.class)) {
                if (method.getReturnType() != String.class &&
                        method.getReturnType() != void.class) {
                    throw new IllegalStateException("Method return type must be String or Void: " + method);
                }
                preRules.add(PreRuleInfo.createOrNull(method, parser, antlrInfo.getLexerClass(),
                        transformClassObjects.get(method.getDeclaringClass()), null,
                        this));
            }
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

    @SuppressWarnings("unchecked")
    public <T extends RuleContext> T parse(String data, Class<? extends RuleContext> returnRuleClass, int lexerMode) {
        ParserRuleUtils utils = antlrInfo.getParserRuleUtils();
        Method ruleMethod = utils.getMethod(returnRuleClass);
        return (T) antlrInfo.parse(data, ruleMethod, lexerMode).a;
    }

    /**
     * Parses tree using current grammar
     *
     * @return parse tree
     */
    @SuppressWarnings("unchecked")
    public <T extends RuleContext> T parse(String data, Class<? extends RuleContext> returnRuleClass) {
        ParserRuleUtils utils = antlrInfo.getParserRuleUtils();
        Method ruleMethod = utils.getMethod(returnRuleClass);
        return (T) antlrInfo.parse(data, ruleMethod).a;
    }
}
