## Usage
Add to your pom.xml:

```xml
<dependency>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-runtime</artifactId>
    <version>4.7.1</version>
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
if match happened, program will replace matched context with rule matched from method return using grammar-rule

```java
@Transform("Math")
public class Example {
    @Post("expr: `a` + `b`")
    public static String simpleAddition(TermContext a, TermContext b) {
        return "1 - 2";
    }

    @Post("term: `t`")
    public static String simpleTerm(TermContext t) {
        return "1";
    }
}
```

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
