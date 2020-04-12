package com.naofi.lib;

import com.naofi.antlr.MathLexer;
import com.naofi.antlr.MathParser;
import com.naofi.lib.context.TransformContext;
import com.naofi.lib.examples.Example;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class Main {

    public static void main(String[] consoleArgs) throws Exception {
        CharStream text = CharStreams.fromString("1 + 2");
        TransformContext context = new TransformContext("com.naofi.antlr", "expr", Example.class);
        ParseTree result = context.process(text);
        System.out.println("Result: " + result.getText());
    }

    private static ParseTree parse(String text) {
        MathLexer lexer = new MathLexer(CharStreams.fromString(text));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MathParser parser = new MathParser(tokens);

        return parser.expr();
    }
}
