package com.naofi.antlr.extension.transformations;

import com.naofi.antlr.extension.annotation.Pre;
import com.naofi.antlr.extension.annotation.Transform;

import static com.naofi.antlr.extension.generated.Java8Parser.*;

@Transform("Java8")
public class ForToWhile {
    @Pre(value = "for ( $forInit ; $cond ; $forUpdate ) { $statements }", rule = RULE_statement)
    public String forToWhile(ForInitContext forInit, ExpressionContext cond, ForUpdateContext forUpdate,
                             BlockStatementsContext statements) {

        return "while ( $cond ) { $statements $forUpdate;}";
    }
}
