package com.naofi.lib;

import com.naofi.antlr.MathLexer;
import com.naofi.antlr.MathParser;
import com.naofi.lib.context.TransformContext;
import com.naofi.lib.examples.Example;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;

import java.lang.reflect.Method;

public class Main {

    public static void main(String[] consoleArgs) throws Exception {
        CharStream text = CharStreams.fromString("(1 + 2) * (5 + 6)");
        TransformContext context = new TransformContext("com.naofi.antlr", "expr", text, Example.class);

//        MathLexer lexer = new MathLexer(text);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        MathParser parser = new MathParser(tokens);
//
//        ParseTreePattern pattern = parser.compileParseTreePattern("<expr> + <expr> ", MathParser.RULE_expr);
    }
}
