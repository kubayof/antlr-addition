package com.naofi.antlr.extension.context.methods;

import com.naofi.antlr.extension.annotation.Post;
import com.naofi.antlr.extension.annotation.Pre;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Adds map with locals for each method to class.
 * Modifies each method bytecode to set locals before return statement.
 */
public class AddLocalsMapVisitor extends ClassVisitor {
    private final Map<Method, String> mapsNames;
    private final Map<String, Method> methodNames;
    private final String className;

    AddLocalsMapVisitor(ClassVisitor classVisitor, List<Method> transformMethods, String className) {
        super(Opcodes.ASM5, classVisitor);
        this.mapsNames = new HashMap<>();
        this.methodNames = new HashMap<>();
        for (Method method : transformMethods) {
            String fieldName = getGeneratedFieldName(method.getName());
            mapsNames.put(method, fieldName);
            methodNames.put(method.getName(), method);
        }
        this.className = className;
    }

    public static Class<?> transform(Class<?> clazz) {
        try {
            byte[] classBytes = Objects.requireNonNull(clazz
                    .getClassLoader()
                    .getResourceAsStream(clazz.getName().replace('.', File.separatorChar) + ".class"))
                    .readAllBytes();

            String generatedClassName = clazz.getName();
            ClassReader reader = new ClassReader(classBytes);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS|ClassWriter.COMPUTE_FRAMES);
            List<Method> transformMethodNames = new ArrayList<>();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Pre.class) ||
                        method.isAnnotationPresent(Post.class)) {
                    transformMethodNames.add(method);
                }
            }
            ClassVisitor visitor = new AddLocalsMapVisitor(writer, transformMethodNames, generatedClassName);
            reader.accept(visitor, ClassReader.SKIP_FRAMES);
            byte[] transformedClassBytes = writer.toByteArray();
            BytesClassLoader bytesClassLoader = new BytesClassLoader();
            return bytesClassLoader.loadClass(generatedClassName, transformedClassBytes);
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (methodNames.containsKey(name)) {
            String fieldName = mapsNames.get(methodNames.get(name));
            cv.visitField(Opcodes.ACC_PUBLIC,
                    fieldName, "Ljava/util/HashMap;", null, null)
                    .visitEnd();

            LinkedHashMap<String,String> paramsNamesTypes = new LinkedHashMap<>();
            Method method = methodNames.get(name);
            for (Parameter param : method.getParameters()) {
                paramsNamesTypes.put(param.getName(), Type.getDescriptor(param.getType()));
            }
            return new PutMethodLocalsVisitor(super.visitMethod(access, name, descriptor, signature, exceptions),
                    paramsNamesTypes, className, getGeneratedFieldName(name));
        } else if (name.equals("<init>")) {
            return new InitMethodVisitor(super.visitMethod(access, name, descriptor, signature, exceptions),
                    mapsNames,
                    className);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    /**
     * @return map with names and types (ASM5) of method parameters
     */
    private Map<String,String> getParamsNamesTypes(Method method) {
        return null;
    }

    public static String getGeneratedFieldName(String methodName) {
        return "__" + methodName + "Locals__";
    }
}
