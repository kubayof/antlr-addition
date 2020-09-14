package com.naofi.antlr.extension.context;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import java.lang.reflect.Method;
import java.util.List;

class TreeVisitor {
    private final AntlrInfo antlrInfo;
    private final Method rootRule;
    private final List<BaseRuleInfo> preRules;
    private final List<BaseRuleInfo> postRules;
    private Enhancer proxy;

    TreeVisitor(AntlrInfo antlrInfo, Method rootRule, List<BaseRuleInfo> preRules, List<BaseRuleInfo> postRules) {
        this.antlrInfo = antlrInfo;
        this.rootRule = rootRule;
        this.preRules = preRules;
        this.postRules = postRules;
        proxy = new Enhancer();
    }

    ParseTree process(ParseTree tree) {
        ParserRuleContext root = new ParserRuleContext();
        root.addChild((RuleContext)tree);//All the nodes must have parent
        tree.setParent(root);

        return enhanceAndVisit(root);
    }

    ParseTree processWithoutParent(ParseTree tree) {
        return enhanceAndVisit(tree);
    }


    @SuppressWarnings("unchecked")
    private ParseTree enhanceAndVisit(ParseTree tree) {
        try {
            Enhancer proxy = new Enhancer();
            proxy.setSuperclass(antlrInfo.getVisitorClass());
            proxy.setCallback((MethodInterceptor)this::process);

            ParseTreeVisitor<Object> visitor = (ParseTreeVisitor<Object>)proxy.create();

            visitor.visit(tree);
            return tree;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Object process(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String name = method.getName();
        if (!name.equals("visit") &&
                !name.equals("visitChildren") &&
                name.startsWith("visit") &&
                args.length == 1 &&
                args[0] instanceof RuleContext) {
            Pair<Boolean, RuleContext> stopAndNewContext = processRuleContextPre((RuleContext)args[0]);
            boolean isStop = stopAndNewContext.a;
            if (isStop) {
                return null;
            }
            RuleContext newContext = stopAndNewContext.b;
            Object[] newArgs = new Object[]{newContext};
            Object result = proxy.invokeSuper(obj, newArgs);
            processRuleContextPost(newContext);
            return result;
        } else {
            return proxy.invokeSuper(obj, args);
        }
    }

    private Pair<Boolean, RuleContext> processRuleContextPre(RuleContext context) {
        boolean stop = true;
        boolean matched = false;
        RuleContext prevResult = context;
        RuleContext result = context;
        for (BaseRuleInfo info : preRules) {
            result = info.invoke(result);
            if (result == null) {
                result = prevResult;
            } else {
                matched = true;
                if (!info.isStop) {
                    stop = false;
                }
                prevResult = result;
            }
//            if (info.invoke(context) != null) {
//                matched = true;
//            }
        }

        if (!(matched && stop)) {
            stop = false;
        }
        return new Pair<>(stop, result);
    }

    private void processRuleContextPost(RuleContext context) {
        for (BaseRuleInfo info : postRules) {
            info.invoke(context);
        }
    }
}
