package com.naofi.antlr;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;

import java.lang.reflect.Method;

public class TestVisitor extends MathBaseVisitor<String> {
    @Override
    public String visitAddExpr(MathParser.AddExprContext ctx) {
        System.out.println(ctx.getText());
        visitChildren(ctx);
        return null;
    }

    @Override
    public String visitTerminal(MathParser.TerminalContext ctx) {
        System.out.println(ctx.getText());
        visitChildren(ctx);
        return null;
    }

    @Override
    public String visitFactor(MathParser.FactorContext ctx) {
        System.out.println(ctx.getText());
        visitChildren(ctx);
        return null;
    }

    @Override
    public String visitNum(MathParser.NumContext ctx) {
        System.out.println(ctx.getText());
        visitChildren(ctx);
        return null;
    }

    @Override
    public String visitVar(MathParser.VarContext ctx) {
        System.out.println(ctx.getText());
        visitChildren(ctx);
        return null;
    }

    @Override
    public String visitParExpr(MathParser.ParExprContext ctx) {
        System.out.println(ctx.getText());
        visitChildren(ctx);
        return null;
    }
}
