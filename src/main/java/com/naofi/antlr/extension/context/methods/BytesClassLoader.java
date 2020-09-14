package com.naofi.antlr.extension.context.methods;

/**
 * Load class from byte array.
 */
public class BytesClassLoader extends ClassLoader {
    public Class<?> loadClass(String name, byte[] bytes) throws ClassNotFoundException {
        Class<?> clazz;
        try {
            clazz = findClass(name);
        } catch (ClassNotFoundException e) {
            clazz = defineClass(name, bytes, 0, bytes.length);
        }
        return clazz;
    }
}