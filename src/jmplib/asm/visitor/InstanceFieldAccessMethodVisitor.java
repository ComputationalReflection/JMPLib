package jmplib.asm.visitor;

import jmplib.annotations.AuxiliaryMethod;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.asm.util.ASMUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * This visitor modifies original classes to add auxiliary methods.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public class InstanceFieldAccessMethodVisitor extends ClassVisitor implements
        Opcodes {

    private String internalName;
    private static Map<Integer, Class<?>> numericTypes = ASMUtils.numericTypes;

    public InstanceFieldAccessMethodVisitor(int api, ClassVisitor visitor) {
        super(api, visitor);
    }

    public InstanceFieldAccessMethodVisitor(int api) {
        super(api);
    }

    @Override
    public void visit(int arg0, int arg1, String arg2, String arg3,
                      String arg4, String[] arg5) {
        this.internalName = arg2;
        super.visit(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Adds auxiliary methods for each instance field
     */
    @Override
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        if (!Modifier.isStatic(access)) {
            String methodDesc = "(L" + internalName + ";)" + desc;
            MethodVisitor mvGet = cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "_"
                    + name + "_fieldGetter", methodDesc, null, null);
            mvGet.visitAnnotation(
                    ASMUtils.getDescriptor(AuxiliaryMethod.class), true);
            mvGet.visitCode();
            mvGet.visitVarInsn(ALOAD, 0);
            mvGet.visitFieldInsn(GETFIELD, internalName, name, desc);
            mvGet.visitInsn(getReturn(methodDesc));
            mvGet.visitMaxs(2, 1);
            mvGet.visitEnd();
            methodDesc = "(L" + internalName + ";" + desc + ")V";
            if (!Modifier.isFinal(access)) {
                MethodVisitor mvSet = cv.visitMethod(ACC_PUBLIC | ACC_STATIC,
                        "_" + name + "_fieldSetter", methodDesc, null, null);
                mvSet.visitAnnotation(
                        ASMUtils.getDescriptor(AuxiliaryMethod.class), true);
                mvSet.visitCode();
                Label l0 = new Label();
                mvSet.visitLabel(l0);
                mvSet.visitVarInsn(ALOAD, 0);
                mvSet.visitVarInsn(getLoad(desc), 1);
                mvSet.visitFieldInsn(PUTFIELD, internalName, name, desc);
                mvSet.visitInsn(getReturn(methodDesc));
                Label l2 = new Label();
                mvSet.visitLabel(l2);
                mvSet.visitLocalVariable(name, desc, null, l0, l2, 0);
                mvSet.visitMaxs(3, 3);
                mvSet.visitEnd();
                if (numericTypes.containsKey(desc.hashCode())) {
                    addUnary(name, desc);
                }
            }
        }
        return super.visitField(access, name, desc, signature, value);
    }

    /**
     * Calculates return opcode from its descriptor
     *
     * @param desc The descriptor of the method
     * @return The return opcode
     */
    private int getReturn(String desc) {
        return ASMUtils.getReturnOpcode(desc);
    }

    /**
     * Calculates the load opcode from the var descriptor
     *
     * @param type The var descriptor
     * @return The load opcode
     */
    private int getLoad(String type) {
        return ASMUtils.getVarOpcode(type);
    }

    /**
     * Adds unary method to the class
     *
     * @param name The name of the field
     * @param desc The descriptor of the field
     */
    private void addUnary(String name, String desc) {
        String unaryDesc = "(L" + internalName + ";I)" + desc;
        MethodVisitor unary = cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "_"
                + name + "_unary", unaryDesc, null, null);
        unary.visitAnnotation(ASMUtils.getDescriptor(AuxiliaryMethod.class),
                true);
        Label l0 = new Label();
        unary.visitLabel(l0);
        unary.visitVarInsn(ILOAD, 1);
        Label l1 = new Label();
        Label l2 = new Label();
        Label l3 = new Label();
        Label l4 = new Label();
        Label l5 = new Label();
        unary.visitTableSwitchInsn(1, 4, l5, l1, l2, l3, l4);
        unary.visitLabel(l1);
        unary.visitFrame(F_SAME, 0, null, 0, null);
        unary.visitVarInsn(ALOAD, 0);
        unary.visitInsn(DUP);
        unary.visitFieldInsn(GETFIELD, internalName, name, desc);
        unary.visitInsn(getDup(desc));
        getValue(desc, unary);
        unary.visitInsn(getConst(desc));
        unary.visitInsn(getAdd(desc));
        getConversion(desc, unary);
        getValueOf(desc, unary);
        unary.visitFieldInsn(PUTFIELD, internalName, name, desc);
        unary.visitInsn(getReturn(unaryDesc));
        unary.visitLabel(l2);
        unary.visitFrame(F_SAME, 0, null, 0, null);
        unary.visitVarInsn(ALOAD, 0);
        unary.visitInsn(DUP);
        unary.visitFieldInsn(GETFIELD, internalName, name, desc);
        getValue(desc, unary);
        unary.visitInsn(getConst(desc));
        unary.visitInsn(getAdd(desc));
        getConversion(desc, unary);
        getValueOf(desc, unary);
        unary.visitInsn(getDup(desc));
        unary.visitFieldInsn(PUTFIELD, internalName, name, desc);
        unary.visitInsn(getReturn(unaryDesc));
        unary.visitLabel(l3);
        unary.visitFrame(F_SAME, 0, null, 0, null);
        unary.visitVarInsn(ALOAD, 0);
        unary.visitInsn(DUP);
        unary.visitFieldInsn(GETFIELD, internalName, name, desc);
        unary.visitInsn(getDup(desc));
        getValue(desc, unary);
        unary.visitInsn(getConst(desc));
        unary.visitInsn(getSub(desc));
        getConversion(desc, unary);
        getValueOf(desc, unary);
        unary.visitFieldInsn(PUTFIELD, internalName, name, desc);
        unary.visitInsn(getReturn(unaryDesc));
        unary.visitLabel(l4);
        unary.visitFrame(F_SAME, 0, null, 0, null);
        unary.visitVarInsn(ALOAD, 0);
        unary.visitInsn(DUP);
        unary.visitFieldInsn(GETFIELD, internalName, name, desc);
        getValue(desc, unary);
        unary.visitInsn(getConst(desc));
        unary.visitInsn(getSub(desc));
        getConversion(desc, unary);
        getValueOf(desc, unary);
        unary.visitInsn(getDup(desc));
        unary.visitFieldInsn(PUTFIELD, internalName, name, desc);
        unary.visitInsn(getReturn(unaryDesc));
        unary.visitLabel(l5);
        unary.visitFrame(F_SAME, 0, null, 0, null);
        unary.visitTypeInsn(NEW, "java/lang/RuntimeException");
        unary.visitInsn(DUP);
        unary.visitLdcInsn("Invalid unary type");
        unary.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException",
                "<init>", "(Ljava/lang/String;)V", false);
        unary.visitInsn(ATHROW);
        Label l6 = new Label();
        unary.visitLabel(l6);
        getMax(desc, unary);
        unary.visitEnd();
    }

    /**
     * Calculates the add opcode from the type descriptor
     *
     * @param type The descriptor of the type
     * @return The add opcode
     */
    private int getAdd(String type) {
        return ASMUtils.getAdd(type);
    }

    /**
     * Calculates the sub opcode from the type descriptor
     *
     * @param type The descriptor of the type
     * @return The sub opcode
     */
    private int getSub(String type) {
        return ASMUtils.getSub(type);
    }

    /**
     * Calculates the constant opcode from the type descriptor
     *
     * @param type The descriptor of the type
     * @return The constant opcode
     */
    private int getConst(String type) {
        return ASMUtils.getConst(type);
    }

    /**
     * Calculates the dup opcode from the type descriptor
     *
     * @param type The descriptor of the type
     * @return The dup opcode
     */
    private int getDup(String type) {
        switch (Type.getType(type).getClassName()) {
            case "long":
            case "double":
                return Opcodes.DUP2_X1;
            default:
                return Opcodes.DUP_X1;
        }
    }

    /**
     * Calculates the max stack sizes
     *
     * @param type The descriptor of the field
     * @param mv   The method to set the max sizes
     */
    private void getMax(String type, MethodVisitor mv) {
        switch (Type.getType(type).getClassName()) {
            case "long":
            case "double":
            case "java.lang.Long":
            case "java.lang.Double":
                mv.visitMaxs(6, 2);
                break;
            default:
                mv.visitMaxs(4, 2);
        }
    }

    /**
     * Adds a conversion instruction whenever the type of the field is byte,
     * char or its wrapper types
     *
     * @param type The descriptor of the field
     * @param mv   The method to set conversion instruction
     */
    private void getConversion(String type, MethodVisitor mv) {
        switch (Type.getType(type).getClassName()) {
            case "byte":
            case "java.lang.Byte":
                mv.visitInsn(I2B);
                break;
            case "char":
            case "java.lang.Character":
                mv.visitInsn(I2C);
                break;
            default:
        }
    }

    /**
     * Adds the instruction to convert the wrapper type to primitive type when
     * the type of the field is a wrapper type
     *
     * @param type The type of the field
     * @param mv   The method where the instruction is added
     */
    private void getValue(String type, MethodVisitor mv) {
        ASMUtils.getValue(type, mv);
    }

    /**
     * Adds the instruction to convert the primitive type to wrapper type when
     * the type of the field is a wrapper type
     *
     * @param type The type of the field
     * @param mv   The method where the instruction is added
     */
    private void getValueOf(String type, MethodVisitor mv) {
        ASMUtils.getValueOf(type, mv);
    }

}
