package com.naofi.lib.context;

import com.naofi.antlr.MathLexer;
import com.naofi.lib.annotation.Post;
import com.naofi.lib.annotation.Pre;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RuleInfo {
    private final ParseTreePattern pattern;
    private final Method method;
    private Pre pre = null;
    private Post post = null;
    private final char begin = '`';
    private final char end = '`';
    private final Map<String, Class<?>> params = new HashMap<>();
    private final List<String> ruleNames;
    private final List<String> paramRuleNames;
    private final Parser parser;
    private Method ruleMethod;
    private final Class<?> lexerClass;
    private final Class<?> parserClass;

    RuleInfo(Method method, Parser parser, Class<?> lexerClass) {
        paramRuleNames = new ArrayList<>();
        this.lexerClass = lexerClass;
        parserClass = parser.getClass();
        this.method = method;
        this.parser = parser;
        checkParameters();

        String[] ruleNames;
        try {
            Field field = parserClass.getField("ruleNames");
            field.setAccessible(true);
            ruleNames = (String[])field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        this.ruleNames = Arrays.asList(ruleNames);

        String patternStr;
        if (method.isAnnotationPresent(Pre.class)) {
            pre = method.getAnnotation(Pre.class);
            patternStr = pre.value();
        } else {
            post = method.getAnnotation(Post.class);
            patternStr = post.value();
        }

        Pattern grammarDefPattern = Pattern.compile("[A-Za-z0-9]+:");
        Matcher matcher = grammarDefPattern.matcher(patternStr);
        if (!matcher.lookingAt()) {
            throw new IllegalStateException("Cannot find grammar rule in expr '" + patternStr + "'");
        }
        String grammar = patternStr.substring(0, matcher.end() - 1);

        setRootRule(grammar);
//        System.out.println("GRAMMAR: " + grammar);

        int ruleId;
        try {
            String ruleFieldName = "RULE_" + grammar;
            Field ruleField = parserClass.getDeclaredField(ruleFieldName);
            ruleField.setAccessible(true);
            ruleId = (Integer)ruleField.get(null);
        } catch (NoSuchFieldException|IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        StringBuilder builder = new StringBuilder();
        int i = matcher.end();
        char c;
        int size = patternStr.length();
        while (i < size) {
            c = patternStr.charAt(i);
            i++;
            if (c == '<') {
                builder.append("\\<");
            } else if (c == '>') {
                builder.append("\\>");
            } else if (c == '\\') {//escape character
                builder.append(c);
                if (i < size) {
                    builder.append(patternStr.charAt(i));
                    i++;
                } else {
                    throw new IllegalStateException("Unexpected end of pattern after escape character: " + patternStr);
                }
            } else if (c == begin) {
                builder.append('<');
                int start = i;
                do {
                    i++;
                    c = patternStr.charAt(i);
                } while ((c != end) && (c < size));
                int end = i;
                i++;
                String var = patternStr.substring(start, end);
                if (!params.containsKey(var)) {
                    throw new IllegalStateException("Cannot resolve variable '" + var + "' in method " + method.getName());
                }
                String simpleName = params.get(var).getSimpleName();
                String ruleName = getRuleName(simpleName.substring(0, simpleName.length() - 7));
                paramRuleNames.add(ruleName);
                builder.append(ruleName);
                builder.append(">");

            } else {
                builder.append(c);
            }
        }

//        System.out.println("Formatted string: '" + builder.toString() + "', rule id: " + ruleId);
        try {
            pattern = parser.compileParseTreePattern(builder.toString(), ruleId);
        } catch (NoViableAltException e) {
            throw new IllegalStateException("Cannot found rule for '" + patternStr + "'", e);
        }
    }

    public List<ParseTree> pre(RuleContext context) {
        if (pre == null) {
            return null;
        }
        return process(context);
    }

    public List<ParseTree> post(RuleContext context) {
        if (post == null) {
            return null;
        }
        return process(context);
    }

    private List<ParseTree> process(RuleContext context) {
        ParseTreeMatch match =  pattern.match(context);
        if (!match.succeeded()) {
            return null;
        }
        List<ParseTree> args = new ArrayList<>();
        Map<String, Integer> ruleNamesMap = new HashMap<>();
        for (String p : paramRuleNames) {
            if (ruleNamesMap.containsKey(p)) {
                int num = ruleNamesMap.get(p);
                args.add(match.getAll(p).get(num));
                ruleNamesMap.replace(p, num+1);
            } else {
                args.add(match.getAll(p).get(0));
                ruleNamesMap.put(p, 1);
            }
        }
        String result = invokeMethod(args);
        try {
            Lexer lexer = (Lexer)lexerClass.getConstructor(CharStream.class).newInstance(CharStreams.fromString(result));
            Parser parser = (Parser)parserClass.getConstructor(TokenStream.class).newInstance(new CommonTokenStream(lexer));
            ParseTree tree = (ParseTree)ruleMethod.invoke(parser);

            ParserRuleContext parent = (ParserRuleContext) context.getParent();
            List<ParseTree> children = new ArrayList<>();
            for (ParseTree child : parent.children) {
                if (child == context) {
                    children.add(tree);
                } else {
                    children.add(child);
                }
            }
            parent.children = children;

        } catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException|InstantiationException e) {
            throw new IllegalStateException(e);
        }
        return args;
    }

    private String invokeMethod(List<ParseTree> args) {
        try {
            ParseTree[] params = args.toArray(ParseTree[]::new);
            return (String) method.invoke(null, params);
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    //Pre processing methods
    private void checkParameters() {
        for (Parameter p : method.getParameters()) {
            if(p.getType().getSuperclass().equals(ParserRuleContext.class)) {
                params.put(p.getName(), p.getType());
            } else {
                throw new IllegalStateException("All the arguments of transform method '" + method.getName() + "' must extend RuleContext: '" +
                        p.isNamePresent() + ":" + p.getName() + "' of type: '" + p.getType().getName() + "'");

            }
        }
    }

    private void setRootRule(String rule) {
        try {
            String ruleName = getRuleName(rule);
            ruleMethod = parser.getClass().getDeclaredMethod(ruleName);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getRuleName(String var) {
        for (String name : ruleNames) {
            if (name.toLowerCase().equals(var.toLowerCase())) {
                return name;
            }
        }

        throw new IllegalStateException("Cannot find rule with name '" + var + "' in method " + method.getName());
    }
}
