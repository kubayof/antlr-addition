package com.naofi.lib.examples;

import com.naofi.lib.annotation.Post;
import com.naofi.lib.annotation.Pre;
import com.naofi.lib.annotation.Transform;

import static com.naofi.antlr.MathParser.*;
@Transform("Math")
public class Example {
    @Post("expr: `a` + `b`")
    public static String simpleAddition(TermContext a, TermContext b) {
        System.out.println("Matched parts: '" + a.getText() + "' and '" + b.getText() + "'");
        return a.getText() + " - " + b.getText();//Return null if not want to change tree
    }

    @Post("term: `t`")
    public static String simpleTerm(TermContext t) {
        System.out.println("Found term: " + t.getText());
        return "1";
    }
}
