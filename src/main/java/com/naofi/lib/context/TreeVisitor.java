package com.naofi.lib.context;

import com.naofi.antlr.TestVisitor;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

class TreeVisitor {
    private Class<?> parserClass;
    private Class<?> lexerClass;
    private Class<?> visitorClass;
    private Method rootRule;
    private List<Method> preMethods;
    private List<Method> postMethods;
    private Enhancer proxy;

    TreeVisitor(Class<?> parserClass, Class<?> lexerClass, Class<?> visitorClass, Method rootRule, List<Method> preMethods, List<Method> postMethods) {
        this.parserClass = parserClass;
        this.lexerClass = lexerClass;
        this.visitorClass = visitorClass;
        this.rootRule = rootRule;
        this.preMethods = preMethods;
        this.postMethods = postMethods;
        proxy = new Enhancer();
    }

    void process(CharStream chars) {
        try {
            Lexer lexer = (Lexer) lexerClass.getConstructor(CharStream.class).newInstance(chars);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Parser parser = (Parser) parserClass.getConstructor(TokenStream.class).newInstance(tokens);
            ParseTree tree = (ParseTree) rootRule.invoke(parser);

            Method visit = AbstractParseTreeVisitor.class.getDeclaredMethod("visit", ParseTree.class);
            Method visitChildren = AbstractParseTreeVisitor.class.getDeclaredMethod("visitChildren", RuleNode.class);
            proxy.setSuperclass(TestVisitor.class);
            proxy.setCallback((MethodInterceptor) (obj, method, args, pr) -> {
                //Pre methods
                if (!method.getName().equals("visit") && method.getName().startsWith("visit")) {
                    RuleContext context = (RuleContext) args[0]; // Visitor methods accept only one argument
                    System.out.println(context.getText());
                }
//                pr.invokeSuper(method, args);
                pr.invokeSuper(obj, args);
//
//                Method vs = obj.getClass().getDeclaredMethod("visitChildren", RuleNode.class);
//                vs.invoke(obj, context);
                //Post methods
                return "WORLD";
            });




            ParseTreeVisitor<String> visitor = (ParseTreeVisitor<String>) proxy.create();
            System.out.println(visitor.getClass());
//            Arrays.stream(visitor.getClass().getDeclaredMethods()).forEach(System.out::println);
//            TestVisitor visitor = new TestVisitor();
            System.out.println(visitor.visit(tree));
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("EXCEPTION");
            //throw new IllegalStateException(e);
        }
    }
}
