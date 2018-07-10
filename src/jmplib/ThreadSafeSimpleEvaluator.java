package jmplib;

import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.EvalInvokerData;
import jmplib.invokers.MemberInvokerData;
import jmplib.primitives.ThreadSafePrimitiveExecutor;

import java.lang.reflect.Type;

public class ThreadSafeSimpleEvaluator extends SimpleEvaluator implements IEvaluator {
    /* **************************************
     * INSTANCE CREATION
     **************************************/

    /**
     * Use createEvaluator instead.
     */
    ThreadSafeSimpleEvaluator() {
    }


    /* **************************************
     * ACCESS TO METHODS AND FIELDS
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getMethodInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
        synchronized (ThreadSafePrimitiveExecutor.class) {
            return super.getMethodInvoker(type, name, info);
        }
        /*
        jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();
        T ret = super.getMethodInvoker(type, name, info);
        jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();
        return ret;*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getFieldInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
        synchronized (ThreadSafePrimitiveExecutor.class) {
            return super.getFieldInvoker(type, name, info);
        }
/*        jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();
        T ret = super.getFieldInvoker(type, name, info);
        jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();
        return ret;*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T setFieldInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
         synchronized (ThreadSafePrimitiveExecutor.class) {
             return super.setFieldInvoker(type, name, info);
        }
        /*
        jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().lock();
        T ret = super.setFieldInvoker(type, name, info);
        jmplib.primitives.ThreadSafePrimitiveExecutor.threadSafeMonitor.readLock().unlock();
        return ret;*/
    }
}
