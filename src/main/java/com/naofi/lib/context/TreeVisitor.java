package com.naofi.lib.context;

import com.naofi.antlr.MathParser;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import java.lang.reflect.Method;
import java.util.List;

class TreeVisitor {
    private Class<?> parserClass;
    private Class<?> lexerClass;
    private Class<?> visitorClass;
    private Method rootRule;
    private List<RuleInfo> rules;
    private Enhancer proxy;

    TreeVisitor(Class<?> parserClass, Class<?> lexerClass, Class<?> visitorClass, Method rootRule, List<RuleInfo> rules) {
        this.parserClass = parserClass;
        this.lexerClass = lexerClass;
        this.visitorClass = visitorClass;
        this.rootRule = rootRule;
        this.rules = rules;
        proxy = new Enhancer();
    }

    void process(ParseTree tree) {
        try {
            Enhancer proxy = new Enhancer();
            proxy.setSuperclass(visitorClass);
            proxy.setCallback((MethodInterceptor)TreeVisitor::process);

            ParseTreeVisitor<Object> visitor = (ParseTreeVisitor<Object>)proxy.create();

            visitor.visit(tree);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static Object process(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String name = method.getName();
        if (!name.equals("visit") &&
            !name.equals("visitChildren") &&
            name.startsWith("visit") &&
            args.length == 1 &&
            args[0] instanceof RuleContext) {
            processRuleContext((RuleContext)args[0]);
        }

        return proxy.invokeSuper(obj, args);
    }

    private static void processRuleContext(RuleContext context) {
        System.out.println(context.getText());
    }
}
