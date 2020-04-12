package com.naofi.lib;

import com.naofi.antlr.MathLexer;
import com.naofi.antlr.MathParser;
import com.naofi.lib.context.TransformContext;
import com.naofi.lib.examples.Example;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ErrorNodeImpl;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.pattern.TokenTagToken;

import java.lang.reflect.Method;
import java.util.List;

import static com.naofi.antlr.MathParser.*;

public class Main {

    public static void main(String[] consoleArgs) throws Exception {
        CharStream text = CharStreams.fromString("1 + 2");
        TransformContext context = new TransformContext("com.naofi.antlr", "expr", Example.class);
        ParseTree result = context.process(text);
        System.out.println("Result: " + result.getText());


//        MathLexer lexer = new MathLexer(CharStreams.fromString("1 + 2"));
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        MathParser parser = new MathParser(tokens);
//        ParseTree tree = parser.expr();
//        ExprContext context = (ExprContext)tree;
//
//
//        MathLexer childLexer = new MathLexer(CharStreams.fromString("3 * "));
//        MathParser childParser = new MathParser(new CommonTokenStream(childLexer));
//        ParserRuleContext childContext = childParser.expr();
//
//        context.children.clear();
//
//        context.addChild(childContext);
//        System.out.println(tree.getText());


//        TokenStreamRewriter rewriter = new TokenStreamRewriter(tokens);
//        rewriter.insertAfter(0, "+3");
//        rewriter.token
//        System.out.println(rewriter.getText());
    }

    private static ParseTree parse(String text) {
        MathLexer lexer = new MathLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MathParser parser = new MathParser(tokens);

        return parser.expr();
    }
}
