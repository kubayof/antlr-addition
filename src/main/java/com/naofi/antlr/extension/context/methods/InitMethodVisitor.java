package com.naofi.antlr.extension.context.methods;

import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

public class InitMethodVisitor extends MethodVisitor {
    private final Map<Method, String> mapsNames;
    private final String className;
    public InitMethodVisitor(MethodVisitor methodVisitor, Map<Method, String> mapNames, String className) {
        super(ASM5, methodVisitor);
        this.mapsNames = mapNames;
        this.className = className;
    }

    /**
     * Adds initialization of method locals HashMap to constructor.
     */
    @Override
    public void visitCode() {
        super.visitCode();
        for (String fieldName : mapsNames.values()) {
            visitVarInsn(ALOAD, 0);
            visitTypeInsn(NEW, "java/util/HashMap");
            visitInsn(DUP);
            visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            visitFieldInsn(PUTFIELD, className.replace('.', '/'),
                    fieldName, "Ljava/util/HashMap;");
        }
    }
}
