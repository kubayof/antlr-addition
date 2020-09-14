package com.naofi.antlr.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Value - grammar name, lexer name is grammar_name + "Lexer", parser name is grammar_name + "Parser"
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transform {
    /**
     * @return grammar name
     */
    String value();
}
