package com.naofi.antlr.extension;

import com.naofi.antlr.extension.context.HtmlTransformContext;
import com.naofi.antlr.extension.context.Java8TransformContext;
import com.naofi.antlr.extension.context.TransformContext;
import com.naofi.antlr.extension.transformations.CollectHrefs;
import com.naofi.antlr.extension.transformations.ForToWhile;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class SimpleTests {
    @Test
    public void collectHrefsTest() throws IOException {
        StringBuilder hrefStringBuilder = new StringBuilder();
        TransformContext htmlTransformContext = new HtmlTransformContext(
                TransformContext.create(CollectHrefs.class, hrefStringBuilder)
        );
        String htmlCode = readResource("test1.html");
        htmlTransformContext.process(htmlCode);

        String result = hrefStringBuilder.toString();
        Assert.assertEquals("\"A bit of text\"\n", result);
    }

    @Test
    public void forToWhileLoopJava() throws IOException {
        TransformContext java8TransformContext = new Java8TransformContext(ForToWhile.class);
        String javaCode = readResource("Example.java");

        ParseTree result = java8TransformContext.process(javaCode);
        //GetText skips spaces, so if you want to use transformed code you need to write your own dumper
        Assert.assertEquals("publicclassExample{publicstaticvoidmain(String[]args)" +
                "{inti;while(i<10){System.out.println(\"Hello world\");i+=1;}}}<EOF>",
                result.getText());
    }

    private String readResource(String resourceName) throws IOException {
        return Files.readString(Path.of(
                Objects.requireNonNull(
                        SimpleTests.class.getClassLoader().getResource(resourceName)).getPath()));
    }
}
