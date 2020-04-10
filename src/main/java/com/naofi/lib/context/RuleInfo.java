package com.naofi.lib.context;

import com.naofi.lib.annotation.Post;
import com.naofi.lib.annotation.Pre;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

class RuleInfo {
    ParseTreePattern pattern;
    Method method;
    Pre pre = null;
    Post post = null;
    private char begin = '#';
    private char end = '#';

    RuleInfo(Method method) {
        this.method = method;
        String patternStr;
        Map<String, Class<?>> params = new HashMap<>();
        for (Parameter p : method.getParameters()) {
            if(p.getType().isAssignableFrom(RuleContext.class)) {
                params.put(p.getName(), p.getType());
            } else {
                throw new IllegalStateException("All the arguments of transform method '" + method.getName() + "' must extend RuleContext: '" +
                        p.getName() + "' of type: '" + p.getType().getName() + "'");
            }
        }
        if (method.isAnnotationPresent(Pre.class)) {
            pre = method.getAnnotation(Pre.class);
            patternStr = pre.value();
        } else {
            post = method.getAnnotation(Post.class);
            patternStr = post.value();
        }
        StringBuilder builder = new StringBuilder();
        int i = 0;
        char c;
        int size = patternStr.length();
        while (i < size) {
            c = patternStr.charAt(i);
            i++;
            if (c == '\\') {//escape character
                builder.append(c);
                if (i < size) {
                    builder.append(patternStr.charAt(i));
                    i++;
                } else {
                    throw new IllegalStateException("Unexpected end of pattern after escape character: " + patternStr);
                }
            } else if (c == begin) {
                builder.append('<');
                i++;
                c = patternStr.charAt(i);
                int start = i;
                while ((c != end) && (c < size)) {
                    i++;
                }
                int end = i;
                String var = patternStr.substring(start, end);
                if (!params.containsKey(var)) {
                    throw new IllegalStateException("Cannot resolve variable '" + var + "' in method " + method.getName());
                }
                System.out.println(params.get(0).getTypeName());

            } else {
                builder.append(c);
            }
        }
    }





    private Object pre(RuleContext context) {

        return null;
    }

    private Object post(RuleContext context) {

        return null;
    }
}
