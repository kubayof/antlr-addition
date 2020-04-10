package com.naofi.lib.examples;

import com.naofi.lib.annotation.Post;
import com.naofi.lib.annotation.Transform;

import static com.naofi.antlr.MathParser.*;
@Transform("Math")
public class Example {

    @Post("expr: `a` + `b`")
    public static String simpleAddition(ExprContext a, TermContext b) {
        System.out.println("Matched parts: '" + a.getText() + "' and '" + b.getText() + "'");
        return "$a - $b";
    }
}
