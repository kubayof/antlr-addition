package com.naofi.antlr.extension.context;

import com.naofi.antlr.extension.annotation.*;
import com.naofi.antlr.extension.context.methods.AddLocalsMapVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ExtendedParseTreePatternMatcher;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.List;

public abstract class BaseRuleInfo {
    protected final ParseTreePattern pattern;
    protected final Class<? extends Lexer> lexerClass;
    protected final Class<? extends Parser> parserClass;
    protected final Method method;
    protected final Method ruleMethod;
    protected final LinkedHashMap<String, String> patternParamsNamesRules;
    protected final Object targetObject;
    protected TreeVisitor visitor;
    protected TransformContext transformContext;
    protected boolean isStop;
    protected int lexerMode;

    protected BaseRuleInfo(Method method, Parser parser, Class<? extends Lexer> lexerClass, Object targetObject,
                           TreeVisitor visitor, TransformContext transformContext) {
        this.targetObject = targetObject;
        this.method = method;
        parserClass = parser.getClass();
        this.lexerClass = lexerClass;
        Pair<String, LinkedHashMap<String, String>> antlrPatternAndParams = createAntlrPattern(method);
        String antlrPattern = antlrPatternAndParams.a;
        patternParamsNamesRules = antlrPatternAndParams.b;
        int rule = getRule(method);
        try {
            ruleMethod = ParserRuleUtils.getForParser(parserClass).getMethod(rule);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }

        try {
            Lexer lexer = lexerClass.getConstructor(CharStream.class).newInstance(new Object[] {null});
            ExtendedParseTreePatternMatcher patternMatcher = new ExtendedParseTreePatternMatcher(lexer, parser);
            lexerMode = getLexerMode(method, lexerClass);
            this.pattern = patternMatcher.compile(antlrPattern, rule, lexerMode);
//            this.pattern = parser.compileParseTreePattern(antlrPattern, rule);
        } catch (NoViableAltException | NoSuchMethodException | IllegalAccessException |
                InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Cannot parse pattern of method '" + method.getName() + "': '" +
                    antlrPattern + "'", e);
        }
        this.visitor = visitor;
        this.transformContext = transformContext;
        isStop = getIsStop(method);
    }

    public boolean isStop() {
        return isStop;
    }

    private boolean getIsStop(Method method) {
        if (method.isAnnotationPresent(Pre.class)) {
            if (method.isAnnotationPresent(Stop.class)) {
                Stop stop = method.getAnnotation(Stop.class);
                return stop.value();
            }
            Pre pre = method.getAnnotation(Pre.class);
            return pre.stop();
        } else if (method.isAnnotationPresent(Post.class)) {
            if (method.isAnnotationPresent(Stop.class)) {
                throw new IllegalStateException("@Stop annotation cannot be used with @Post, use it only with @Pre");
            }
            return false;
        }
        return false;
    }

    private int getLexerMode(Method method, Class<? extends Lexer> lexerClass) {
        try {
            String modeName;
            if (method.isAnnotationPresent(Mode.class)) {
                Mode mode = method.getAnnotation(Mode.class);
                modeName = mode.value();
            } else if (method.isAnnotationPresent(Pre.class)) {
                Pre pre = method.getAnnotation(Pre.class);
                modeName = pre.mode();
            } else if (method.isAnnotationPresent(Post.class)) {
                Post post = method.getAnnotation(Post.class);
                modeName = post.mode();
            } else {
                throw new IllegalStateException("Cannot infer lexer mode for method '" + method.getName() + "'");
            }

            Field modeNamesField = lexerClass.getField("modeNames");
            String[] modeNames = (String[])modeNamesField.get(null);
            return Arrays.asList(modeNames).indexOf(modeName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            //Unreachable if use antlr 4.7.2
            throw new IllegalStateException(e);
        }
    }

    /**
     * Invokes method if match succeed.
     * Rewrites tree using method return value, otherwise - does nothing.
     *
     * @param context current context
     * @return current or newly inserted subtree
     */
    public RuleContext invoke(RuleContext context) {
        ParseTreeMatch match = pattern.match(context);
        if (!match.succeeded()) {
            return null;
        }
        List<Object> args = new ArrayList<>();

        Map<String, ParseTree> matchedVars = new HashMap<>();
        Map<String, Integer> ruleNamesMap = new HashMap<>();
        for (Map.Entry<String, String> entry : patternParamsNamesRules.entrySet()) {
            String varName = entry.getKey();
            String ruleName = entry.getValue();
            if (ruleNamesMap.containsKey(ruleName)) {
                int num = ruleNamesMap.get(ruleName);
                matchedVars.put(varName, match.getAll(ruleName).get(num));
                ruleNamesMap.replace(ruleName, num + 1);
            } else {
                matchedVars.put(varName, match.getAll(ruleName).get(0));
                ruleNamesMap.put(ruleName, 1);
            }
        }

        Parameter[] params = method.getParameters();
        for (Parameter param : params) {
            String paramName = param.getName();
            if (TransformContext.class.isAssignableFrom(param.getType())) {
                args.add(transformContext);
            } else if (RuleContext.class.isAssignableFrom(param.getType())) {
                args.add(matchedVars.getOrDefault(paramName, null));
            } else {
                throw new IllegalStateException("Method arguments must extend TransformContext or RuleContext: '"
                        + paramName +"' is of class '" + param.getType().getName() + "'");
            }
        }

        String result = invokeMethod(args);
        if (result != null) {
            RuleContext insertedTree = (RuleContext) rewriteTree(result, context);
            return insertedTree;
        }
        return context;
    }

    /**
     * Rewrites current RuleContext with context parsed from newTree string.
     *
     * @param newTree string, representing new tree
     * @param context current RuleContext
     * @return newly inserted tree
     */
    private ParseTree rewriteTree(String newTree, RuleContext context) {
        try {
            Lexer lexer = lexerClass.getConstructor(CharStream.class).newInstance(CharStreams.fromString(newTree));
            lexer.pushMode(lexerMode);
            Parser parser = parserClass.getConstructor(TokenStream.class).newInstance(new CommonTokenStream(lexer));
            ParseTree tree = (ParseTree) ruleMethod.invoke(parser);

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
            return tree;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    protected abstract int getRule(Method method);

    /**
     * Returns rule for @Pre::value.
     * Rule must be specified in 'rule' method of @Pre annotation,
     * or using @Rule annotation.
     *
     * @param method method annotated with @Pre
     * @return name of rule
     * @Rule has higher precedence than @Pre::rule
     */
    protected int getRule(Method method, int rule) {
        if (method.isAnnotationPresent(Rule.class)) {
            return method.getAnnotation(Rule.class).value();
        }

        if (rule == -1) {
            throw new IllegalStateException("Cannot infer rule from `" + method.getAnnotation(Pre.class).value() + "`");
        }

        return rule;
    }

    protected abstract Pair<String, LinkedHashMap<String, String>> createAntlrPattern(Method method);

    /**
     * Converts pattern to antlr format.
     *
     * @return new pattern, for example "<term> + <term>"
     */
    protected Pair<String, LinkedHashMap<String, String>> createAntlrPattern(Method method, String oldPattern) {
        StringBuilder builder = new StringBuilder();
        LinkedHashMap<String, String> patternParamsNamesTypes = new LinkedHashMap<>();
        Map<String, Class<?>> params = getMethodParametersMap(method);
        int i = 0;
        int len = oldPattern.length();
        while (i < len) {
            if (oldPattern.charAt(i) == '$') {
                i++;
                int start = i;
                if (Character.isAlphabetic(oldPattern.charAt(i))) {
                    i++;
                } else {
                    throw new IllegalStateException("First symbol of java variable must be a letter");
                }
                while ((i < len) && (Character.isLetterOrDigit(oldPattern.charAt(i)))) {
                    i++;
                }

                String varName = oldPattern.substring(start, i);
                Class<?> varClass = params.get(varName);
                if (varClass == null) {
                    throw new IllegalStateException("Cannot find variable '" + varName + "' in arguments of method '" +
                            method.getName() + "'");
                }
                if (!varClass.getSuperclass().equals(ParserRuleContext.class)) {
                    throw new IllegalStateException("Argument '" + varName + "' of method '" + method.getName() +
                            "' must extend does not extend RuleContext, so you can't use it in pattern");
                }
                String simpleName = varClass.getSimpleName();
                String ruleName = getRuleName(simpleName.substring(0, simpleName.length() - 7));
                builder.append("<");
                builder.append(ruleName);
                patternParamsNamesTypes.put(varName, ruleName);
                builder.append(">");
            } else {
                builder.append(oldPattern.charAt(i));
                i++;
            }
        }

        return new Pair<>(builder.toString(), patternParamsNamesTypes);
    }

    private Map<String, Class<?>> getMethodParametersMap(Method method) {
        Map<String, Class<?>> params = new HashMap<>();
        for (Parameter p : method.getParameters()) {
            params.put(p.getName(), p.getType());
        }

        return params;
    }

    private String getRuleName(String var) {
        for (String name : getRuleNames()) {
            if (name.toLowerCase().equals(var.toLowerCase())) {
                return name;
            }
        }

        throw new IllegalStateException("Cannot find rule with name '" + var + "'");
    }

    private List<String> getRuleNames() {
        String[] ruleNames;
        try {
            Field field = parserClass.getField("ruleNames");
            field.setAccessible(true);
            ruleNames = (String[]) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return Arrays.asList(ruleNames);
    }

    @SuppressWarnings("unchecked")
    private String invokeMethod(List<Object> args) {
        try {
            Object[] params = args.toArray(Object[]::new);
            String result = (String) method.invoke(targetObject, params);
            if (result != null) {
                String fieldName = AddLocalsMapVisitor.getGeneratedFieldName(method.getName());
                Field field = targetObject.getClass().getDeclaredField(fieldName);
                Map<Object, Object> paramValues = (Map<Object, Object>) field.get(targetObject);
                return composeResult(result, paramValues);
            }
            return null;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    private String composeResult(String oldPattern, Map<Object, Object> paramValues) {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        int len = oldPattern.length();
        while (i < len) {
            if (oldPattern.charAt(i) == '$') {
                int start = ++i;
                if (Character.isAlphabetic(oldPattern.charAt(i))) {
                    ++i;
                } else {
                    throw new IllegalStateException("First symbol of java variable must be a letter");
                }
                while ((i < len) && (Character.isLetterOrDigit(oldPattern.charAt(i)))) {
                    ++i;
                }

                String varName = oldPattern.substring(start, i);
                builder.append(((RuleContext) paramValues.get(varName)).getText());
            } else {
                builder.append(oldPattern.charAt(i));
                i++;
            }
        }

        return builder.toString();
    }

    public void setTreeVisitor(TreeVisitor visitor) {
        this.visitor = visitor;
    }
}
