package com.naofi.lib.context;

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
    private final List<RuleInfo> rules;
    private Enhancer proxy;

    TreeVisitor(Class<?> parserClass, Class<?> lexerClass, Class<?> visitorClass, Method rootRule, List<RuleInfo> rules) {
        this.parserClass = parserClass;
        this.lexerClass = lexerClass;
        this.visitorClass = visitorClass;
        this.rootRule = rootRule;
        this.rules = rules;
        proxy = new Enhancer();
    }

    ParseTree process(ParseTree tree) {
        ParserRuleContext root = new ParserRuleContext();
        root.addChild((RuleContext)tree);//All the nodes must have parent
        tree.setParent(root);
        try {
            Enhancer proxy = new Enhancer();
            proxy.setSuperclass(visitorClass);
            proxy.setCallback((MethodInterceptor)this::process);

            ParseTreeVisitor<Object> visitor = (ParseTreeVisitor<Object>)proxy.create();

            visitor.visit(root);

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return root;
    }

    private Object process(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String name = method.getName();
        if (!name.equals("visit") &&
            !name.equals("visitChildren") &&
            name.startsWith("visit") &&
            args.length == 1 &&
            args[0] instanceof RuleContext) {
            if (processRuleContextPre((RuleContext)args[0])) {
                return null;
            }
            Object result = proxy.invokeSuper(obj, args);
            processRuleContextPost((RuleContext)args[0]);
            return result;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

    private boolean processRuleContextPre(RuleContext context) {
        boolean matched = false;
        for (RuleInfo info : rules) {
            if (info.pre(context) != null) {
                matched = true;
            }
        }
        return matched;
    }

    private void processRuleContextPost(RuleContext context) {
        for (RuleInfo info : rules) {
            info.post(context);
        }
    }
}
