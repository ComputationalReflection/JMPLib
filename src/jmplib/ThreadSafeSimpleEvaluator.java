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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getFieldInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
        synchronized (ThreadSafePrimitiveExecutor.class) {
            return super.getFieldInvoker(type, name, info);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T setFieldInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
        synchronized (ThreadSafePrimitiveExecutor.class) {
            return super.setFieldInvoker(type, name, info);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> exec(String classSource) throws StructuralIntercessionException {
        synchronized (ThreadSafeSimpleEvaluator.class) {
            return super.exec(classSource);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T generateEvalInvoker(String code, EvalInvokerData<T> invokerData)
            throws StructuralIntercessionException {
        synchronized (ThreadSafeSimpleEvaluator.class) {
            return super.generateEvalInvoker(code, invokerData);
        }
    }

}
