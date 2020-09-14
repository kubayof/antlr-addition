package com.naofi.antlr.extension.context.methods;

import org.objectweb.asm.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

/**
 * Inserts code, which puts method locals to map before each return statement.
 */
public class PutMethodLocalsVisitor extends MethodVisitor {
    private final LinkedHashMap<String, String> paramsNamesTypes;
    private final String className;
    private final String mapFieldName;
    private Object ldcInsnValue;
    public PutMethodLocalsVisitor(MethodVisitor methodVisitor, LinkedHashMap<String, String> paramsNamesTypes,
                                  String className, String mapName) {
        super(ASM5, methodVisitor);
        this.paramsNamesTypes = paramsNamesTypes;
        this.className = className;
        this.mapFieldName = mapName;
    }

    @Override
    public void visitCode() {
        writeLdcInsn();
        super.visitCode();
    }

    @Override
    public void visitParameter(String name, int access) {
        writeLdcInsn();
        super.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        writeLdcInsn();
        return super.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        writeLdcInsn();
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        writeLdcInsn();
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        writeLdcInsn();
        super.visitAnnotableParameterCount(parameterCount, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        writeLdcInsn();
        return super.visitParameterAnnotation(parameter, descriptor, visible);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        writeLdcInsn();
        super.visitAttribute(attribute);
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        writeLdcInsn();
        super.visitFrame(type, numLocal, local, numStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        if (ldcInsnValue != null) {
            if (opcode == Opcodes.ARETURN) {
                saveParams();
            }
            writeLdcInsn();
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        writeLdcInsn();
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        writeLdcInsn();
        ldcInsnValue = null;
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        writeLdcInsn();
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        writeLdcInsn();
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
        writeLdcInsn();
        super.visitMethodInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        writeLdcInsn();
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        writeLdcInsn();
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        writeLdcInsn();
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        writeLdcInsn();
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object value) {
        writeLdcInsn();
        ldcInsnValue = value;
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        writeLdcInsn();
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        writeLdcInsn();
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        writeLdcInsn();
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        writeLdcInsn();
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        writeLdcInsn();
        return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        writeLdcInsn();
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        writeLdcInsn();
        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        writeLdcInsn();
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor, boolean visible) {
        writeLdcInsn();
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        writeLdcInsn();
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        writeLdcInsn();
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        writeLdcInsn();
        super.visitEnd();
    }

    private void writeLdcInsn() {
        if (ldcInsnValue != null) {
            super.visitLdcInsn(ldcInsnValue);
            ldcInsnValue = null;
        }
    }

    private void saveParams() {
        int i = 1;
        Object ldcInsnSave;
        for (Map.Entry<String, String> param : paramsNamesTypes.entrySet()) {
            ldcInsnSave = ldcInsnValue;
            visitVarInsn(ALOAD, 0);
            visitFieldInsn(GETFIELD, className.replace('.', '/'),
                    mapFieldName, "Ljava/util/HashMap;");
            visitLdcInsn(param.getKey());
            visitVarInsn(ALOAD, i);
            visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap",
                    "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
            visitInsn(Opcodes.POP);
            i++;
            ldcInsnValue = ldcInsnSave;
        }
    }
}
