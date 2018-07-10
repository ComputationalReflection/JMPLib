package jmplib.primitives;

import jmplib.exceptions.StructuralIntercessionException;
import jmplib.sourcecode.ClassContent;

import java.util.Set;

/**
 * This interface is used to implement the command pattern.
 *
 * @author Ignacio Lagartos
 */
public interface Primitive {

    /**
     * Return the class that is modified by this primitive.
     *
     * @return A java Class object.
     */
    Class getTargetClass();

    /**
     * Revert all the changes.
     *
     * @throws StructuralIntercessionException
     */
    void undo() throws StructuralIntercessionException;

    /**
     * Apply the primitive over the {@link ClassContent}
     *
     * @return A set of {@link ClassContent} modified by the primitive
     * @throws StructuralIntercessionException If there are errors while performing the modificactions
     */
    Set<ClassContent> execute() throws StructuralIntercessionException;

    /**
     * Show if the primitive could provoke errors in the application
     *
     * @return false, if the application needs to recompile, true otherwise
     */
    boolean isSafe();

}
