package com.naofi.lib;

import com.naofi.lib.context.TransformContext;
import com.naofi.lib.examples.Example;
import org.antlr.v4.runtime.*;

import java.lang.reflect.Method;

public class Main {

    public static void main(String[] consoleArgs) throws Exception {
        TransformContext context = new TransformContext("com.naofi.antlr", "expr", Example.class);
        context.process(CharStreams.fromString("1 + 2"));

    }
}
