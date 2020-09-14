package com.naofi.antlr.extension.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotated with @Pre must return only string constant!!!
 * This is important because of bytecode modifier structure.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pre {
    /**
     * If default - rule is specified in pattern string,
     * for example: @Pre("rule:pattern")
     */
    int rule() default -1;
    /**
     * Stop traversing tree downside if match occurred
     */
    boolean stop() default false;
    /**
     * Lexer mode for current pattern
     */
    String mode() default "DEFAULT_MODE";
    /**
     * Pattern string for rule
     */
    String value();
}
