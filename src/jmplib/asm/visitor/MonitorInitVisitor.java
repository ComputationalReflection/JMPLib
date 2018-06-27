package jmplib.asm.visitor;


import jmplib.util.Templates;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * This class initializes the jmplib monitor used to implement thread-safe invokers and creators
 */
class MonitorInitializationVisitor extends MethodVisitor implements Opcodes {
    private String className;

    MonitorInitializationVisitor(String className, MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
        this.className = className;
    }

    /**
     * Adds a monitor initializator just before each return clause. It cannot be done at the beginning of the method
     * as constructors must invoke the superclass constructor as the first instruction.
     * @param opcode Opcode to instrument
     */
    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            //mv.visitCode();
            /*if (!className.endsWith("ContainerClass")) {
                mv.visitInsn(opcode);
                return;
            }*/

            //mv.visitVarInsn(ALOAD, 0);
            //mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(NEW, "java/util/concurrent/locks/ReentrantReadWriteLock");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/concurrent/locks/ReentrantReadWriteLock", "<init>", "()V", false);
            //System.out.println("lock initialization executed for class = " + className);
            mv.visitFieldInsn(PUTFIELD, className, Templates.JMPLIB_MONITOR_NAME, "Ljava/util/concurrent/locks/ReadWriteLock;");
        }
        mv.visitInsn(opcode);
    }

   /* @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitMaxs(maxStack + 3, maxLocals+1);
    }*/
}

public class MonitorInitVisitor extends ClassVisitor implements Opcodes {
    private String className;

    public MonitorInitVisitor(String className, int api, ClassVisitor visitor) {
        super(api, visitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("<init>")) {
            return new MonitorInitializationVisitor(className, mv);
        }
        return mv;
    }

}
