package jmplib.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;

/**
 * Convenience class created to represent TypeVariables added dynamically.
 *
 * @param <D>
 * @author Jose Manuel Redondo Lopez
 */
public class TypeVariable<D extends GenericDeclaration> implements java.lang.reflect.TypeVariable<D> {

    private String name;
    private D owner;
    private Type[] bounds;

    public TypeVariable(String name) {
        this.name = name;
    }

    public TypeVariable(String name, D owner, Type... bounds) {
        this.name = name;
        this.owner = owner;
        this.bounds = bounds;
    }

    @Override
    public <T extends Annotation> T getAnnotation(java.lang.Class<T> annotationClass) {
        return owner.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return owner.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return owner.getDeclaredAnnotations();
    }

    @Override
    public Type[] getBounds() {
        return bounds;
    }

    @Override
    public D getGenericDeclaration() {
        return owner;
    }

    public void setGenericDeclaration(D owner) {
        this.owner = owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

}
