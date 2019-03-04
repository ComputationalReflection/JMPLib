package jmplib.agent.impl;

import jmplib.agent.AbstractTransformer;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.asm.util.ASMUtils;
import jmplib.config.JMPlibConfig;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

class IdiomInfo {
    public AbstractInsnNode last;
    public AbstractInsnNode ins;
    public int opCode;
}

/**
 * This transformer converts isinstance calls into an equivalent call to isAssignableTo using our jmplib.reflect classes,
 * so dynamic changes to class interfaces can be taken into account when using this keyword.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public class IsInstanceTransformer extends AbstractTransformer implements
        Opcodes {

    private static String redirectionTargetJava = "jmplibext/reflect/Introspector";
    private static String redirectionTargetUserCode = "jmplibext/reflect/Introspector";
    private static String redirectionTarget = "jmplibext/reflect/Introspector";

    /**
     * Control which classes are instrumentable depending on its name (debugging only, should be true)
     *
     * @param className
     * @return
     */
    private boolean isInstrumentable(String className) {
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe())
            if (/*className.startsWith("java/lang") ||*/ className.startsWith("sun/"))//|| className.startsWith("java/lang/Thread")
                //||  className.startsWith("java.util.concurrent.locks.ReentrantReadWriteLock"))
                return false;

        return //true;//!className.startsWith("java/lang");//true;//className.contains("QueueCreator") || className.contains("TreeMap");
                !(//className.startsWith("java/lang") ||
                        className.startsWith("org/junit/") ||
                                className.startsWith("com/intellij") ||
                                className.startsWith("polyglot/") //||
                                // className.startsWith("sun/")
                                || className.startsWith("com/github/javaparser"));
    }

    /**
     * It is applicable when it is not the first load of the class, the class is
     * inside the instrumentables collection inside the UpdaterAgent class and
     * it has a new version.
     */
    protected boolean instrumentableClass(String className,
                                          Class<?> classBeingRedefined) {
        return isInstrumentable(className);
    }

    private IdiomInfo locateIdiom(MethodNode methodNode) {
        InsnList instructions = methodNode.instructions;
        Iterator<AbstractInsnNode> it = instructions.iterator();
        int varNumber = 0;
        String classOrInterfaceName = null;
        AbstractInsnNode last = null;
        AbstractInsnNode ins = null;
        while (it.hasNext()) {
            ins = it.next();
            //Locate isinstance or cast calls
            if ((last != null) && (last.getOpcode() == Opcodes.ALOAD)) {
                if ((ins.getOpcode() == Opcodes.INSTANCEOF) || (ins.getOpcode() == Opcodes.CHECKCAST)) {
                    if (methodNode.name.equals("set_OldVersion"))
                        return null;
                    classOrInterfaceName = ((TypeInsnNode) ins).desc;
                    //Discard array conversions
                    if (classOrInterfaceName.startsWith("["))
                        return null;
                    varNumber = ((VarInsnNode) last).var;
                    //System.out.println(methodNode.getClass().getName() + ": Interface = " + classOrInterfaceName);
                    //System.out.println(methodNode.getClass().getName() + ": Var number = " + varNumber);
                    IdiomInfo info = new IdiomInfo();
                    info.last = last;
                    info.ins = ins;
                    info.opCode = ins.getOpcode();
                    return info;
                }
            }
            last = ins;
        }
        return null;
    }

    /**
     * Replaces the located idiom by its corresponding instruction set to hook it with the JMPLib introspector
     * (either the JMPLib one or the boot classpath library interface)
     *
     * @param methodNode
     * @param info
     */
    private void applyIdiomInfo(MethodNode methodNode, IdiomInfo info) {
        InsnList inslist = new InsnList();
        InsnList instructions = methodNode.instructions;
        int varNumber = 0;
        String classOrInterfaceName = null;

        if (info.opCode == Opcodes.INSTANCEOF) {
            //Found an isinstance call? replace instructions for the new ones
            //Compose instruction list
            classOrInterfaceName = ((TypeInsnNode) info.ins).desc;
            varNumber = ((VarInsnNode) info.last).var;

            VarInsnNode i1 = new VarInsnNode(ALOAD, varNumber);
            LdcInsnNode i2 = new LdcInsnNode(Type.getType("L" + classOrInterfaceName + ";"));
            MethodInsnNode i3 = new MethodInsnNode(INVOKESTATIC, redirectionTarget, "instanceOf", "(Ljava/lang/Object;Ljava/lang/Class;)Z", false);

            inslist.add(i1);
            inslist.add(i2);
            inslist.add(i3);

            instructions.insert(info.ins, inslist);
            instructions.remove(info.last);
            instructions.remove(info.ins);
            return;
        }

        //Locate cast calls
        if (info.opCode == Opcodes.CHECKCAST) {
            classOrInterfaceName = ((TypeInsnNode) info.ins).desc;
            varNumber = ((VarInsnNode) info.last).var;

            //System.out.println("Replacing cast = " + varNumber);

            //Found an cast call? replace instructions for the new ones
            //Compose instruction list
            LdcInsnNode i1 = new LdcInsnNode(Type.getType("L" + classOrInterfaceName + ";"));
            VarInsnNode i2 = new VarInsnNode(ALOAD, varNumber);
            MethodInsnNode i3 = new MethodInsnNode(INVOKESTATIC, redirectionTarget, "cast", "(Ljava/lang/Class;Ljava/lang/Object;)Ljava/lang/Object;", false);
            TypeInsnNode i4 = new TypeInsnNode(CHECKCAST, classOrInterfaceName);
            VarInsnNode i5 = new VarInsnNode(ASTORE, 1);

            instructions.insertBefore(info.last, i1);
            instructions.insertBefore(info.ins, i3);
            return;
        }

    }

    private boolean needsWrapper(String className) {
        return className.startsWith("java/") || className.startsWith("org/junit/") ||
                className.startsWith("com/") ||
                className.startsWith("polyglot/") ||
                className.startsWith("sun/")
                || className.startsWith("com/github/javaparser");
    }

    /**
     * Redirects methods to the new version of the class.
     */
    @SuppressWarnings("unchecked")
    @Override
    public byte[] transform(String className, Class<?> classBeingRedefined,
                            byte[] classfileBuffer) {
        if (!isInstrumentable(className)) {
            return classfileBuffer;
        }
        //Pickup the class node
        ClassNode classNode = ASMUtils.getClassNode(classfileBuffer);
        //System.out.println("Class name = " + className);
        if (needsWrapper(className))
            redirectionTarget = redirectionTargetJava;
        else
            redirectionTarget = redirectionTargetUserCode;

        //if (isInstrumentable(className)) {
        List<MethodNode> methods = classNode.methods;
        for (MethodNode methodNode : methods) {
            IdiomInfo info = null;
            do {
                info = locateIdiom(methodNode);
                if (info != null)
                    applyIdiomInfo(methodNode, info);
            }
            while (info != null);
        }
        //}
        //else return classfileBuffer;

        //Rewrite class
        ClassWriter cw = null;
        try {
            cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            classNode.accept(cw);
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
        byte[] bytes = cw.toByteArray();
        return bytes;
    }
}
