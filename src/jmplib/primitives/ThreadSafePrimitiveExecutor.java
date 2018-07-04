package jmplib.primitives;

import jmplib.exceptions.StructuralIntercessionException;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A special version of the primitive executor that runs primitives in a thread safe way.
 */
public class ThreadSafePrimitiveExecutor extends PrimitiveExecutor {

    //public static final ReadWriteLock threadSafeMonitor = new ReentrantReadWriteLock();

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
            super.runAllPrimitives();
        }
        /*
        try {
            try {
                ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();
            }
            catch (Exception ex) {}
            ThreadSafePrimitiveExecutor.threadSafeMonitor.writeLock().lock();
            super.runAllPrimitives();
            ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();
            ThreadSafePrimitiveExecutor.threadSafeMonitor.writeLock().unlock();
        }
        catch (StructuralIntercessionException ex) {
            ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();
            ThreadSafePrimitiveExecutor.threadSafeMonitor.writeLock().unlock();
            throw ex;
        }*/
    }
}
