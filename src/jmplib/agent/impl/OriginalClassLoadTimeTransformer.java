package jmplib.agent.impl;

import jmplib.agent.AbstractTransformer;
import jmplib.agent.UpdaterAgent;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.asm.visitor.*;
import jmplib.config.JMPlibConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * This transformer modifies original classes to add new fields and methods to
 * support the functionality of the library .
 *
 * @author Nacho Lagartos
 */
@ExcludeFromJMPLib
public class OriginalClassLoadTimeTransformer extends AbstractTransformer {

    /**
     * Adds new fields and methods to the class to support the functionality of the
     * library. Additionally, adds the class to the instrumentables collection
     * inside the UpdaterAgent class.
     */
    @Override
    protected byte[] transform(String className, Class<?> classBeingRedefined, byte[] classfileBuffer) {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        NewVersionVisitor newVersion = new NewVersionVisitor(ASM5, writer);
        ConstructorVisitor constructorAnnotation = new ConstructorVisitor(ASM5, newVersion, false);
        StaticFieldAccessMethodVisitor accessMethod = new StaticFieldAccessMethodVisitor(ASM5, constructorAnnotation);
        InstanceFieldAccessMethodVisitor instanceAccessMethod = new InstanceFieldAccessMethodVisitor(ASM5, accessMethod);
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
            MonitorInitVisitor miv = new MonitorInitVisitor(className, ASM5, instanceAccessMethod);
            reader.accept(miv, 0);
        } else {
            reader.accept(instanceAccessMethod, 0);
        }
        UpdaterAgent.instrumentables.put(className.hashCode(), className);

        return writer.toByteArray();
    }

    /**
     * It is applicable when it is the first load of the class, the class is not a
     * version class and there is a source file of the class inside the src
     * specified folder.
     */
    @Override
    protected boolean instrumentableClass(String className, Class<?> classBeingRedefined) {
        if (classBeingRedefined != null)
            return false;
        if (className.contains("_NewVersion_"))
            return false;
        if (!(new File(JMPlibConfig.getInstance().getOriginalSrcPath().concat(className).concat(".java")).exists()))
            return false;
        return true;
    }

}
