package com.naofi.lib;

import net.sf.cglib.proxy.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class Main {

    static String str = "Hello world";

    public static void main(String[] consoleArgs) throws Exception {
//        TransformContext context = new TransformContext("com.naofi.antlr", "expr", Example.class);
//        context.process(CharStreams.fromString("1 + 2"));
        Map proxy = (Map) Proxy.newProxyInstance(
                Main.class.getClassLoader(),
                new Class[] {Map.class},
                new DynamicInvocationHandler()
        );

        //proxy.put("a", "b");
        Field f = Main.class.getDeclaredField("str");
        f.setAccessible(true);
        f.set(null, "b");

        System.out.println(str);
    }

    static class DynamicInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            System.out.println("Interceptor, invoked method '" + method.getName() + "'");
            return null;
        }
    }
}
