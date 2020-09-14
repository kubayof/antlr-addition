## Usage
Add to your pom.xml:

```xml
<dependency>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-runtime</artifactId>
    <version>4.8</version>
</dependency>
```

Create class annotated with @Transform("grammar-name").
There are two annotations: 
<ul>
<li>@Pre("grammar-rule:expression")</li>
<li>@Post("grammar-rule:expression")</li>
</ul>
Where:
<ul>
<li>grammar-rule - rule to parse expression and method return</li>
<li>expression - will be matched with antlr-generated parser</li>
</ul>
Program will try to match rules from methods, annotated with @Pre, if match happened - method will be called
and traverse will be stopped on subtrees, after, program will traverse all the subtrees and try to match
rules from methods annotated with @Post;

For all the types of methods (@Post && @Pre):
if match happened, program will replace matched context with rule matched from method return using grammar-rule:

This class will not modify tree:
```java
@Transform("Math")
public class Example1 { 
    @Post(value = "$a + $b", rule = MathParser.RULE_expr)
    public static void simpleAddition(TermContext a, TermContext b) {
        //Some logic
    }
}
```

And this will modify:

```java
@Transform("Math")
public class Example {
    @Post(value = "$a + $b", rule = MathParser.RULE_expr)
    public static String simpleAddition(TermContext a, TermContext b) {
        return "$a - 3";
    }

    @Post(value = "$t", rule = MathParser.RULE_term)
    public static String simpleTerm(TermContext t) {
        return "-$t";
    }
}
```

Methds annotated with `@Pre` or `@Post` may return void (tree will not be changed)
or String (matched subtree will be replaced with tree built from returned string)

Creating context:

```java
CharStream text = CharStreams.fromString("1 + 2");
TransformContext context = new TransformContext("com.naofi.antlr", "expr", Example.class);
ParseTree result = context.process(text);
System.out.println("Result: " + result.getText());
```
<ul>
<li>"com.naofi.antlr" - package, that contains lexer, parser and visitor</li>
<li>"expr" - root rule name</li>
<li>Example.class - class with transformations</li>
</ul>

If you need to create `Example` using constructor with arguments use `TransformContext.create(...)` static method:

```java
CharStream text = CharStreams.fromString("1 + 2");
TransformContext context = new TransformContext("com.naofi.antlr", "expr", 
    TransformContext.create(Example.class, arg1, arg2 ...)
);
ParseTree result = context.process(text);
System.out.println("Result: " + result.getText());
```

If you need to parse some code inside transform method you can add `TransformContext` to arguments list
and use method parse:
```java
@Transform("Math")
public class Example3 { 
    @Post(value = "$a + $b", rule = MathParser.RULE_expr)
    public static String simpleAddition(TransformContext t, TermContext a, TermContext b, TermContext newB) {
        newB = t.parse("3", TermContext.class);
        return "$a - $newB";
    }
}
```

###Important

Java compiler do not includes information about method params names, so you need to
add compiler option `-parameters` (see build.gradle of project)
