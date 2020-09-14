package com.naofi.antlr.extension.context;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AntlrInfo {
    private static final Map<String, AntlrInfo> grammar_info = new HashMap<>();

    public static AntlrInfo getForGrammar(String grammarName, String grammarPackage) {
        if (grammar_info.containsKey(grammarName)) {
            return grammar_info.get(grammarName);
        }

        AntlrInfo info = new AntlrInfo(grammarName, grammarPackage);
        grammar_info.put(grammarName, info);
        return info;
    }

    private final Class<? extends Lexer> lexerClass;
    private final Class<? extends Parser> parserClass;
    private Class<? extends ParseTreeVisitor<Object>> visitorClass;
    private final ParserRuleUtils parserRuleUtils;

    @SuppressWarnings("unchecked")
    private AntlrInfo(String grammarName, String grammarPackage) {
        String lexerName = grammarPackage + "." + grammarName + "Lexer";
        try {
            lexerClass = (Class<? extends Lexer>)Class.forName(lexerName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find lexer in package '" + grammarPackage + "', expected lexer name: '" + lexerName + "'");
        }

        String parserName = grammarPackage + "." + grammarName + "Parser";
        try {
            parserClass = (Class<? extends Parser>)Class.forName(parserName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find parser in package '" + grammarPackage + "', expected parser name: '" + parserName + "'");
        }

        String visitorName = grammarPackage + "." + grammarName + "BaseVisitor";
        try {
            visitorClass = (Class<? extends ParseTreeVisitor<Object>>)Class.forName(visitorName);
        } catch (ClassNotFoundException e) {
            try {
                visitorName = grammarPackage + "." + grammarName + "ParserBaseVisitor";
                visitorClass = (Class<? extends ParseTreeVisitor<Object>>)Class.forName(visitorName);
            } catch (ClassNotFoundException e1) {
                throw new IllegalStateException("Cannot find visitor in package '" + grammarPackage + "', expected visitor name: '" + visitorName + "'");
            }
        }

        try {
            parserRuleUtils = ParserRuleUtils.getForParser(parserClass);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    public Class<? extends Lexer> getLexerClass() {
        return lexerClass;
    }

    public Class<? extends Parser> getParserClass() {
        return parserClass;
    }

    public ParserRuleUtils getParserRuleUtils() {
        return parserRuleUtils;
    }

    public Class<? extends ParseTreeVisitor<Object>> getVisitorClass() {
        return visitorClass;
    }

    public Pair<ParseTree, Parser> parse(String code, String ruleName) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleName);
        return parse(code, ruleMethod);
    }

    public Pair<ParseTree, Parser> parse(String code, String ruleName, int lexerMode) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleName);
        return parse(code, ruleMethod, lexerMode);
    }

    public Pair<ParseTree, Parser> parse(CharStream chars, String ruleName) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleName);
        return parse(chars, ruleMethod);
    }

    public Pair<ParseTree, Parser> parse(CharStream chars, String ruleName, int lexerMode) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleName);
        return parse(chars, ruleMethod, lexerMode);
    }

    public Pair<ParseTree, Parser> parse(String code, int ruleNumber) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleNumber);
        return parse(code, ruleMethod);
    }

    public Pair<ParseTree, Parser> parse(String code, int ruleNumber, int lexerMode) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleNumber);
        return parse(code, ruleMethod, lexerMode);
    }

    public Pair<ParseTree, Parser> parse(CharStream chars, int ruleNumber) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleNumber);
        return parse(chars, ruleMethod);
    }

    public Pair<ParseTree, Parser> parse(CharStream chars, int ruleNumber, int lexerMode) {
        Method ruleMethod = parserRuleUtils.getMethod(ruleNumber);
        return parse(chars, ruleMethod, lexerMode);
    }

    public Pair<ParseTree, Parser> parse(String code, Method ruleMethod) {
        return parse(code, ruleMethod, Lexer.DEFAULT_MODE);
    }

    public Pair<ParseTree, Parser> parse(String code, Method ruleMethod, int lexerMode) {
        CharStream chars = CharStreams.fromString(code);
        return parse(chars, ruleMethod, lexerMode);
    }

    public Pair<ParseTree, Parser> parse(CharStream chars, Method ruleMethod) {
        return parse(chars, ruleMethod, Lexer.DEFAULT_MODE);
    }

    public Pair<ParseTree, Parser> parse(CharStream chars, Method ruleMethod, int lexerMode) {
        try {
            Lexer lexer = lexerClass.getConstructor(CharStream.class).newInstance(chars);
            lexer.pushMode(lexerMode);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Parser parser = parserClass.getConstructor(TokenStream.class).newInstance(tokens);
            return new Pair<>((ParseTree) ruleMethod.invoke(parser), parser);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
