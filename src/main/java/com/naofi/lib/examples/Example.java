package com.naofi.lib.examples;

import com.naofi.lib.annotation.Post;
import com.naofi.lib.annotation.Transform;

import static com.naofi.antlr.MathParser.*;
@Transform("Math")
public class Example {

    @Post("expr: `a` + `b`")
    public String simpleAddition(ExprContext a, TermContext b) {
        return "$a - $b";
    }
}
