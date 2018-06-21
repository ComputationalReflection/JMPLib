package jmplib.asm.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import jmplib.annotations.ExcludeFromJMPLib;

/**
 * Helper class to encapsulate the most common ASM operations
 *
 * @author Ignacio Lagartos, Jose Manuel Redondo
 * @version 1.1 Code refactoring, simple type - Class map.
 */
@ExcludeFromJMPLib
public class ASMUtils {

    public static final Map<Integer, Class<?>> numericTypes = new HashMap<>();

    /**
     * Maps simple types to their corresponding Class object
     */
    @SuppressWarnings("rawtypes")
    private static final Map<String, Class> simpleClassFromName = new HashMap<>();

    static {
        simpleClassFromName.put("byte", byte.class);
        simpleClassFromName.put("short", short.class);
        simpleClassFromName.put("int", int.class);
        simpleClassFromName.put("long", long.class);
        simpleClassFromName.put("char", char.class);
        simpleClassFromName.put("float", float.class);
        simpleClassFromName.put("double", double.class);
        simpleClassFromName.put("boolean", boolean.class);
        simpleClassFromName.put("void", void.class);

        numericTypes.put(ASMUtils.getDescriptor(int.class).hashCode(),
                int.class);
        numericTypes.put(ASMUtils.getDescriptor(Integer.class).hashCode(),
                Integer.class);
        numericTypes.put(ASMUtils.getDescriptor(short.class).hashCode(),
                short.class);
        numericTypes.put(ASMUtils.getDescriptor(Short.class).hashCode(),
                Short.class);
        numericTypes.put(ASMUtils.getDescriptor(long.class).hashCode(),
                long.class);
        numericTypes.put(ASMUtils.getDescriptor(Long.class).hashCode(),
                Long.class);
        numericTypes.put(ASMUtils.getDescriptor(float.class).hashCode(),
                float.class);
        numericTypes.put(ASMUtils.getDescriptor(Float.class).hashCode(),
                Float.class);
        numericTypes.put(ASMUtils.getDescriptor(double.class).hashCode(),
                double.class);
        numericTypes.put(ASMUtils.getDescriptor(Double.class).hashCode(),
                Double.class);
        numericTypes.put(ASMUtils.getDescriptor(byte.class).hashCode(),
                byte.class);
        numericTypes.put(ASMUtils.getDescriptor(Byte.class).hashCode(),
                Byte.class);
        numericTypes.put(ASMUtils.getDescriptor(char.class).hashCode(),
                char.class);
        numericTypes.put(ASMUtils.getDescriptor(Character.class).hashCode(),
                Character.class);
    }

    /**
     * This method transforms a {@link Type} to {@link Class} object.
     *
     * @param type The {@link Type} object to transform.
     * @return The {@link Class} represented by the {@link Type} object or null if
     * the type doesn't represent a valid {@link Class}.
     */
    public static Class<?> getClass(Type type) {
        String className = type.getClassName();
        try {
            if (!className.contains("["))
                return Class.forName(className);
            else
                return Class.forName(type.getDescriptor());
        } catch (ClassNotFoundException e) {
            Class<?> toRet = ASMUtils.simpleClassFromName.get(className);
            if (toRet == null)
                throw new IllegalArgumentException("The type didn't represent a class");
            return toRet;
        }
    }

    /**
     * This method obtain the {@link ClassNode} of the given class.
     *
     * @param reader The class which {@link ClassNode} have to be returned.
     * @return The {@link ClassNode} of the given class.
     */
    private static ClassNode getClassNode(ClassReader reader) {
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        return classNode;
    }

    /**
     * This method obtain the {@link ClassNode} of the given class.
     *
     * @param clazz The class which {@link ClassNode} have to be returned.
     * @return The {@link ClassNode} of the given class.
     */
    public static ClassNode getClassNode(Class<?> clazz) {
        try {
            return getClassNode(new ClassReader(Type.getInternalName(clazz)));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
     * This method obtain the {@link ClassNode} of the given class bytes.
     *
     * @param bytes The class which {@link ClassNode} have to be returned.
     * @return The {@link ClassNode} of the given class.
     */
    public static ClassNode getClassNode(byte[] bytes) {
        return getClassNode(new ClassReader(bytes));
    }

    /**
     * This method obtain the {@link ClassNode} from a {@link InputStream}.
     *
     * @param is The stream to read the bytes
     * @return The {@link ClassNode} of the given class.
     * @throws IOException If there are problems with the stream
     */
    public static ClassNode getClassNode(InputStream is) throws IOException {
        return getClassNode(new ClassReader(is));
    }

    /**
     * This method obtain the {@link ClassNode} from the internal name of the class
     * .
     *
     * @param internalName The internal name of the class
     * @return The {@link ClassNode} of the given class.
     * @throws IOException If there are problems reading the class
     */
    public static ClassNode getClassNode(String internalName) throws IOException {
        return getClassNode(new ClassReader(internalName));
    }

    /**
     * Returns the ASMified code of the class
     *
     * @param classNode The class to ASMified
     * @return The code to generate the class
     */
    private static String getASMified(ClassNode classNode) {
        ASMifier asMifier = new ASMifier();
        TraceClassVisitor visitor = new TraceClassVisitor(null, asMifier, null);
        classNode.accept(visitor);
        return asMifier.getText().toString();
    }

    /**
     * Returns the ASMified code of the class
     *
     * @param clazz The class to ASMified
     * @return The code to generate the class
     */
    public static String getASMified(Class<?> clazz) {
        return getASMified(getClassNode(clazz));
    }

    /**
     * Returns the ASMified code of the class
     *
     * @param bytes The bytes of the class to ASMified
     * @return The code to generate the class
     */
    public static String getASMified(byte[] bytes) {
        return getASMified(getClassNode(bytes));
    }

    /**
     * Returns the ASMified code of the class
     *
     * @param is The {@link InputStream} to read the bytes of the class to ASMified
     * @return The code to generate the class
     */
    public static String getASMified(InputStream is) throws IOException {
        return getASMified(getClassNode(is));
    }

    /**
     * Return the bytecode of the class
     *
     * @param classNode The class to obtain the bytecode
     * @return The bytecode of the class
     */
    private static String byteCodeSpy(ClassNode classNode) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        TraceClassVisitor visitor = new TraceClassVisitor(new PrintWriter(os));
        classNode.accept(visitor);
        return os.toString();
    }

    /**
     * Return the bytecode of the class
     *
     * @param clazz The class to obtain the bytecode
     * @return The bytecode of the class
     */
    public static String byteCodeSpy(Class<?> clazz) {
        return byteCodeSpy(getClassNode(clazz));
    }

    /**
     * Return the bytecode of the class
     *
     * @param bytes The bytes of the class to obtain the bytecode
     * @return The bytecode of the class
     */
    public static String byteCodeSpy(byte[] bytes) {
        return byteCodeSpy(getClassNode(bytes));
    }

    /**
     * Return the bytecode of the class
     *
     * @param is The {@link InputStream} to read the bytes of the class to obtain
     *           the bytecode
     * @return The bytecode of the class
     */
    public static String byteCodeSpy(InputStream is) throws IOException {
        return byteCodeSpy(getClassNode(is));
    }

    /**
     * Return the max stack size for a method
     *
     * @param methodNode Method to calculate max stack
     * @return The max stack of the method
     */
    public static int getMaxStack(MethodNode methodNode) {
        return Type.getArgumentsAndReturnSizes(methodNode.desc);
    }

    /**
     * Return the argument types of the method
     *
     * @param methodNode The method node
     * @return The argument types
     */
    public static Type[] getArgumentTypes(MethodNode methodNode) {
        return Type.getArgumentTypes(methodNode.desc);
    }

    /**
     * Create a {@link VarInsnNode} from a {@link Type} and the index of the
     * parameter
     *
     * @param type   Type of the var
     * @param number Index of the var
     * @return The {@link VarInsnNode}
     */
    public static VarInsnNode getParamInsn(Type type, int number) {
        return new VarInsnNode(getVarOpcode(type.getDescriptor()), number);
    }

    /**
     * Obtains a static {@link MethodInsnNode}
     *
     * @param className  The internal name class that owns the method
     * @param methodName The name of the method
     * @param descriptor The descriptor of the method
     * @return The node of the static call
     */
    public static MethodInsnNode getMethodInsnStatic(String className, String methodName, String descriptor) {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, className, methodName, descriptor, false);
    }

    /**
     * Obtains the return node for one method
     *
     * @param methodNode The method node
     * @return The return node
     */
    public static InsnNode getReturnInsn(MethodNode methodNode) {
        return new InsnNode(getReturnOpcode(methodNode.desc));
    }

    /**
     * Checks of the method is static
     *
     * @param methodNode The method
     * @return return if it is static
     */
    public static boolean isStatic(MethodNode methodNode) {
        return Modifier.isStatic(methodNode.access);
    }

    /**
     * Obtains the descriptor of the class
     *
     * @param clazz The class
     * @return The descriptor of the class
     */
    public static String getDescriptor(Class<?> clazz) {
        return Type.getDescriptor(clazz);
    }

    /**
     * Obtains the internal name of the class
     *
     * @param clazz The class
     * @return The internal name of the class
     */
    public static String getInternalName(Class<?> clazz) {
        return clazz.getName().replace('.', '/');
    }

    /**
     * Return the return opcode for one method descriptor
     *
     * @param methodDesc Method descriptor
     * @return The return opcode
     */
    public static int getReturnOpcode(String methodDesc) {
        switch (Type.getReturnType(methodDesc).getClassName()) {
            case "void":
                return Opcodes.RETURN;
            case "short":
            case "byte":
            case "boolean":
            case "char":
            case "int":
                return Opcodes.IRETURN;
            case "long":
                return Opcodes.LRETURN;
            case "float":
                return Opcodes.FRETURN;
            case "double":
                return Opcodes.DRETURN;
            default:
                return Opcodes.ARETURN;
        }
    }

    /**
     * Obtains the var opcode for a descriptor
     *
     * @param varDesc Descriptor of the var
     * @return The opcode
     */
    public static int getVarOpcode(String varDesc) {
        switch (Type.getType(varDesc).getClassName()) {
            case "short":
            case "byte":
            case "boolean":
            case "char":
            case "int":
                return Opcodes.ILOAD;
            case "long":
                return Opcodes.LLOAD;
            case "float":
                return Opcodes.FLOAD;
            case "double":
                return Opcodes.DLOAD;
            default:
                return Opcodes.ALOAD;
        }
    }

    /**
     * Obtains the {@link InsnList} with the load of all parameters of the method.
     *
     * @param desc Descriptor of the method
     * @return The list with the {@link VarInsnNode} of the parameters
     */
    public static InsnList getVarInsnList(String desc) {
        InsnList instructions = new InsnList();
        Type[] params = Type.getArgumentTypes(desc);
        int index = 1;
        for (Type type : params) {
            instructions.add(new VarInsnNode(ASMUtils.getVarOpcode(type.getDescriptor()), index));
            index = nextIndex(index, type.getDescriptor());
        }
        return instructions;
    }

    /**
     * Obtains the {@link InsnList} with the load of the parameters of the method
     * avoiding the first parameter.
     *
     * @param desc The descriptor of the method
     * @return The list with the {@link VarInsnNode} of the parameters
     */
    public static InsnList getVarInvokerInsnList(String desc) {
        InsnList instructions = new InsnList();
        Type[] params = Type.getArgumentTypes(desc);
        int index = 1;
        for (int i = 1; i < params.length; i++) {
            instructions.add(new VarInsnNode(ASMUtils.getVarOpcode(params[i].getDescriptor()), index));
            index = nextIndex(index, params[i].getDescriptor());
        }
        return instructions;
    }

    /**
     * Calculates the next index
     *
     * @param index Actual index
     * @param desc  Descriptor of the actual variable
     * @return The next index
     */
    public static int nextIndex(int index, String desc) {
        if (desc.equals("D") || desc.equals("J")) {
            return index + 2;
        } else {
            return index + 1;
        }
    }

    /**
     * Check if the class have errors
     *
     * @param bytes Bytes of the class
     * @return Errores found
     */
    public static String checkClass(byte[] bytes) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        CheckClassAdapter.verify(new ClassReader(bytes), false, pw);
        return sw.toString();
    }

    /**
     * @param methodDesc
     * @param index
     * @return
     */
    public static int getFirstAvailableIndex(String methodDesc, int index) {
        Type[] vars = Type.getArgumentTypes(methodDesc);
        for (Type type : vars) {
            index = nextIndex(index, type.getDescriptor());
        }
        return index;
    }

    /**
     * Adds the instruction to convert the primitive type to wrapper type when
     * the type of the field is a wrapper type
     *
     * @param type The type of the field
     * @param mv   The method where the instruction is added
     */
    public static void getValueOf(String type, MethodVisitor mv) {
        switch (Type.getType(type).getClassName()) {
            case "java.lang.Short":
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf",
                        "(S)Ljava/lang/Short;", false);
                break;
            case "java.lang.Byte":
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf",
                        "(B)Ljava/lang/Byte;", false);
                break;
            case "java.lang.Integer":
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf",
                        "(I)Ljava/lang/Integer;", false);
                break;
            case "java.lang.Float":
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf",
                        "(F)Ljava/lang/Float;", false);
                break;
            case "java.lang.Character":
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf",
                        "(C)Ljava/lang/Character;", false);
                break;
            case "java.lang.Long":
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf",
                        "(J)Ljava/lang/Long;", false);
                break;
            case "java.lang.Double":
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf",
                        "(D)Ljava/lang/Double;", false);
                break;
            default:
        }
    }

    /**
     * Calculates the add opcode from the type descriptor
     *
     * @param type The descriptor of the type
     * @return The add opcode
     */
    public static int getAdd(String type) {
        switch (Type.getType(type).getClassName()) {
            case "long":
            case "java.lang.Long":
                return Opcodes.LADD;
            case "float":
            case "java.lang.Float":
                return Opcodes.FADD;
            case "double":
            case "java.lang.Double":
                return Opcodes.DADD;
            default:
                return Opcodes.IADD;
        }
    }

    /**
     * Calculates the sub opcode from the type descriptor
     *
     * @param type The descriptor of the type
     * @return The sub opcode
     */
    public static int getSub(String type) {
        switch (Type.getType(type).getClassName()) {
            case "long":
            case "java.lang.Long":
                return Opcodes.LSUB;
            case "float":
            case "java.lang.Float":
                return Opcodes.FSUB;
            case "double":
            case "java.lang.Double":
                return Opcodes.DSUB;
            default:
                return Opcodes.ISUB;
        }
    }

    /**
     * Calculates the constant opcode from the type descriptor
     *
     * @param type The descriptor of the type
     * @return The constant opcode
     */
    public static int getConst(String type) {
        switch (Type.getType(type).getClassName()) {
            case "long":
            case "java.lang.Long":
                return Opcodes.LCONST_1;
            case "float":
            case "java.lang.Float":
                return Opcodes.FCONST_1;
            case "java.lang.Double":
            case "double":
                return Opcodes.DCONST_1;
            default:
                return Opcodes.ICONST_1;
        }
    }

    /**
     * Adds a conversion instruction whenever the type of the field is byte,
     * char or its wrapper types
     *
     * @param type The descriptor of the field
     * @param mv   The method to set conversion instruction
     */
    public static void getConversion(String type, MethodVisitor mv) {
        switch (Type.getType(type).getClassName()) {
            case "byte":
            case "java.lang.Byte":
                mv.visitInsn(Opcodes.I2B);
                break;
            case "char":
            case "java.lang.Character":
                mv.visitInsn(Opcodes.I2C);
                break;
            default:
        }
    }

    /**
     * Adds the instruction to convert the wrapper type to primitive type when
     * the type of the field is a wrapper type
     *
     * @param type
     *            The type of the field
     * @param mv
     *            The method where the instruction is added
     */
    public static void getValue(String type, MethodVisitor mv) {
        switch (Type.getType(type).getClassName()) {
            case "java.lang.Short":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue",
                        "()S", false);
                break;
            case "java.lang.Byte":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue",
                        "()B", false);
                break;
            case "java.lang.Integer":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue",
                        "()I", false);
                break;
            case "java.lang.Float":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue",
                        "()F", false);
                break;
            case "java.lang.Character":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Character",
                        "charValue", "()C", false);
                break;
            case "java.lang.Long":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue",
                        "()J", false);
                break;
            case "java.lang.Double":
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Double",
                        "doubleValue", "()D", false);
                break;
            default:
        }
    }
}
