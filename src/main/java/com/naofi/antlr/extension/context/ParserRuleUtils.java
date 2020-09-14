package com.naofi.antlr.extension.context;


import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RuleContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * For work with antlr parser methods
 */
public class ParserRuleUtils {
    private static final Map<Class<? extends Parser>, ParserRuleUtils> context_utils = new HashMap<>();

    public static ParserRuleUtils getForParser(Class<? extends Parser> parserClass) throws NoSuchFieldException {
        if (context_utils.containsKey(parserClass)) {
            return context_utils.get(parserClass);
        }
        ParserRuleUtils utils = new ParserRuleUtils(parserClass);
        context_utils.put(parserClass, utils);
        return utils;
    }


    private final Map<Class<? extends RuleContext>, String> contexts_rules = new HashMap<>();
    private final Map<String, Class<? extends RuleContext>> rules_contexts = new HashMap<>();
    private final Map<Class<? extends RuleContext>, Method> contexts_methods = new HashMap<>();
    private final Map<Method, Class<? extends RuleContext>> methods_contexts = new HashMap<>();
    private final Map<String, Method> rules_methods = new HashMap<>();
    private final Map<Method, String> methods_rules = new HashMap<>();
    private final Map<Integer, String> number_rule = new HashMap<>();
    private final Map<String, Integer> rule_number = new HashMap<>();

    private ParserRuleUtils(Class<? extends Parser> parserClass) throws NoSuchFieldException {
        String[] ruleNames = getParserRuleNames(parserClass);
        init(parserClass, ruleNames);
    }

    @SuppressWarnings("unchecked")
    private void init(Class<? extends Parser> parserClass, String[] ruleNames) {
        for (Method method : parserClass.getDeclaredMethods()) {
            Class<?> returnType = method.getReturnType();
            if (RuleContext.class.isAssignableFrom(returnType)) {
                Class<? extends RuleContext> returnContext = (Class<? extends RuleContext>) returnType;
                String ruleName = getRuleName(method.getName(), ruleNames);
                contexts_rules.put(returnContext, ruleName);
                rules_contexts.put(ruleName, returnContext);
                contexts_methods.put(returnContext, method);
                methods_contexts.put(method, returnContext);
                rules_methods.put(ruleName, method);
                methods_rules.put(method, ruleName);
                try {
                    Field ruleNumField = parserClass.getField("RULE_" + ruleName);
                    ruleNumField.setAccessible(true);
                    Integer number = (Integer) ruleNumField.get(null);
                    number_rule.put(number, ruleName);
                    rule_number.put(ruleName, number);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    public String getRuleName(String name, String[] ruleNames) {
        for (String ruleName : ruleNames) {
            if (name.toLowerCase().equals(ruleName.toLowerCase())) {
                return ruleName;
            }
        }
        throw new IllegalStateException("Cannot find rule for name '" + name + "'");
    }

    private String[] getParserRuleNames(Class<? extends Parser> parserClass) throws NoSuchFieldException {
        Field ruleNamesField = parserClass.getDeclaredField("ruleNames");
        ruleNamesField.setAccessible(true);
        try {
            return (String[])ruleNamesField.get(null);
        } catch (IllegalAccessException e) {
            //Unreachable, ruleNames is public field
            throw new IllegalStateException(e);
        }
    }

    public String getRuleName(Class<? extends RuleContext> contextClass) {
        return contexts_rules.get(contextClass);
    }

    public String getRuleName(Method method) {
        return methods_rules.get(method);
    }

    public String getRuleName(int ruleNumber) {
        return number_rule.get(ruleNumber);
    }

    public Class<? extends RuleContext> getContextClass(String rule) {
        return rules_contexts.get(rule);
    }

    public Class<? extends RuleContext> getContextClass(Method method) {
        return methods_contexts.get(method);
    }

    public Class<? extends RuleContext> getContextClass(int ruleNumber) {
        return rules_contexts.get(number_rule.get(ruleNumber));
    }

    public Method getMethod(Class<? extends RuleContext> context) {
        return contexts_methods.get(context);
    }

    public Method getMethod(String rule) {
        return rules_methods.get(rule);
    }

    public Method getMethod(int ruleNumber) {
        return rules_methods.get(number_rule.get(ruleNumber));
    }

    public int getRuleNumber(Class<? extends RuleContext> context) {
        return rule_number.get(contexts_rules.get(context));
    }

    public int getRuleNumber(String rule) {
        return rule_number.get(rule);
    }

    public int getRuleNumber(Method method) {
        return rule_number.get(methods_rules.get(method));
    }
}
