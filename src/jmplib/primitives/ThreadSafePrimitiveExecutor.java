package jmplib.primitives;

import jmplib.exceptions.StructuralIntercessionException;

import java.util.Queue;

/**
 * A special version of the primitive executor that runs primitives in a thread safe way.
 */
public class ThreadSafePrimitiveExecutor extends PrimitiveExecutor {

    public ThreadSafePrimitiveExecutor(Primitive primitive) {
        super(primitive);
    }

    public ThreadSafePrimitiveExecutor(Queue<Primitive> primitives) {
        super(primitives);
    }

    private void runAllPrimitives() throws StructuralIntercessionException {
        // Execute each primitive
        while (!primitives.isEmpty()) {
            Primitive primitive = primitives.poll();
            // Store the affected ClassContents
            synchronized (primitive.getTargetClass()) {
                classContents.addAll(primitive.execute());
            }
            // Store the primitive in the stack of executed
            executedPrimitives.push(primitive);
            safeChange &= primitive.isSafe();
            // setClassContentsUpdated();
        }
        makeChangesEffective();
    }

    /**
     * Executes all primitives in order. If an error happens all primitives are
     * undone in inverse order. The new versions are compiled and the last
     * versions are redirected to the new version.
     *
     * @throws StructuralIntercessionException If an error occurs when executing the primitives.
     */
    public void executePrimitives()
            throws StructuralIntercessionException {
        try {
            /* As only transaction intercessors contain more than one primitive, we block all the transaction primitives
             using the own PrimitiveExecutor class as a lock guard. This way the transaction is executed without
             being interrupted by other transactions.

             Simple intercessors in a thread-safe JMPLib block by the class object that is the target of each primitive*/
            if (primitives.size() > 1) {
                synchronized (ThreadSafePrimitiveExecutor.class) {
                    runAllPrimitives();
                }
            } else runAllPrimitives();
        } catch (StructuralIntercessionException e) {
            // If the primitive fails, undo the changes of all primitive
            // executed previously
            //e.printStackTrace();
            undoChanges();
            throw e;
        }
    }
}
