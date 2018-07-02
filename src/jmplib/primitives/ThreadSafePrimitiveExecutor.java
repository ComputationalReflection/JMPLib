package jmplib.primitives;

import jmplib.exceptions.StructuralIntercessionException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A special version of the primitive executor that runs primitives in a thread safe way.
 */
public class ThreadSafePrimitiveExecutor extends PrimitiveExecutor {

    public ThreadSafePrimitiveExecutor(Primitive primitive) {
        super(primitive);
        executedPrimitives = new ConcurrentLinkedDeque<>();
    }

    public ThreadSafePrimitiveExecutor(Queue<Primitive> primitives) {
        super(primitives);
        executedPrimitives = new ConcurrentLinkedDeque<>();
    }

    @Override
    void runAllPrimitives() throws StructuralIntercessionException {
        //Let no primitive executor run in parallel with others to avoid conflicting changes.
        synchronized (ThreadSafePrimitiveExecutor.class) {
            //Class cl = null;
            super.runAllPrimitives();
            // Execute each primitive
         /*   while (!primitives.isEmpty()) {
                Primitive primitive = primitives.poll();
                cl = primitive.getTargetClass();
                synchronized (cl) {
                    // Store the affected ClassContents
                    classContents.addAll(primitive.execute());
                    // Store the primitive in the stack of executed
                    executedPrimitives.push(primitive);
                    safeChange &= primitive.isSafe();
                    // setClassContentsUpdated();
                }
            }
            synchronized (cl) {
                makeChangesEffective();
            }*/
        }

    }
}
