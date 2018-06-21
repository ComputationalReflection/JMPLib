package jmplib.reflect;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.sourcecode.ClassContent;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.intercessor.IntercessorTypeConversion;
import jmplib.util.intercessor.IntercessorValidators;
import sun.reflect.CallerSensitive;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;

public class Field extends AccessibleObject implements Member {
    /**
     * JMPLib postfix to methods introduced to get field values
     */
    private static String getterPostfix = "_fieldGetter";
    /**
     * JMPLib postfix to methods introduced to set field values
     */
    private static String setterPostfix = "_fieldSetter";
    /**
     * JMPLib postfix to methods introduced to get static field values
     */
    private static String staticGetterPostfix = "_getter";
    /**
     * JMPLib postfix to methods introduced to set static field values
     */
    private static String staticSetterPostfix = "_setter";
    /**
     * Decorated java.lang.reflect.Field
     */
    private java.lang.reflect.Field decoratedField;
    /**
     * Class that originally declared the decorated field.
     */
    private Class<?> declaringClass;
    /**
     * The user has supplied field data manually and this Field do not correspond to
     * a java.lang.reflect.Field
     */
    private boolean customField = false;

    private java.lang.Class<?> customType;

    private Type customGenericType;

    private String customName;

    private int customModifiers;

    private String customInit;

    private FieldDeclaration customFieldDeclaration;

    public Field(String name) {
        IntercessorValidators.checkFieldParams(Modifier.PUBLIC, name);
        this.customField = true;
        this.customName = name;
        this.customModifiers = Modifier.PUBLIC;
    }

    public Field(Type type, String name) {
        this(null, Modifier.PUBLIC, type, name, null);
    }

    public Field(int modifiers, Type type, String name) {
        this(null, modifiers, type, name, null);
    }

    public Field(Type type, String name, String init) {
        this(null, Modifier.PUBLIC, type, name, init);
    }

    public Field(int modifiers, Type type, String name, String init) {
        this(null, modifiers, type, name, init);
    }

    public Field(Type clazz, Type type, String name) {
        this(clazz, Modifier.PUBLIC, type, name, null);
    }

    public Field(Type clazz, int modifiers, Type type, String name) {
        this(clazz, modifiers, type, name, null);
    }

    public Field(Type clazz, int modifiers, Type type, String name, String init) {
        IntercessorValidators.checkFieldParams(modifiers, type, name, init);
        this.customField = true;
        this.declaringClass = IntercessorTypeConversion.type2Class(clazz);
        this.customModifiers = modifiers;
        try {
            this.customType = IntercessorTypeConversion.type2JavaClass(type);
        } catch (Exception e) {
            if (IntercessorValidators.canRepresentGenericType(type)) {
                this.customType = Object.class;
                this.customGenericType = type;
            } else
                throw new IllegalArgumentException("Field type do not represent a Class or a suitable generic type");
        }
        this.customName = name;
        this.customInit = init;
    }

    public Field(FieldDeclaration fd) {
        this.customFieldDeclaration = fd;

        this.customField = true;
        try {
            this.declaringClass = Introspector.decorateClass(DeclarationUtils.getDeclaringClass(fd));
        } catch (Exception ex) {

        }
        this.customModifiers = fd.getModifiers();

        try {
            this.customType = DeclarationUtils.getFieldType(fd);
        } catch (Exception e) {
            if (IntercessorValidators.canRepresentGenericType(customType)) {
                this.customGenericType = customType;
                this.customType = Object.class;
            } else
                throw new IllegalArgumentException("Field type do not represent a Class or a suitable generic type");
        }
        VariableDeclarator vd = fd.getVariables().get(0);
        this.customName = vd.getId().toString();
        this.customInit = vd.getInit().toString();
    }

    /**
     * Field decorator constructor.
     *
     * @param originalField
     */
    protected Field(java.lang.reflect.Field originalField) {
        this.decoratedField = originalField;
        java.lang.Class<?> originalDeclaringClass = originalField.getDeclaringClass();

        java.lang.Class<?> version = VersionTables.isVersionOf(originalDeclaringClass);
        if (version == null)
            version = originalDeclaringClass;

        this.declaringClass = new Class<>(version);
    }

    /**
     * @return the customInit
     */
    public String getCustomInit() {
        return customInit;
    }

    /**
     * @param customInit the customInit to set
     */
    public void setCustomInit(String customInit) {
        this.customInit = customInit;
    }

    /**
     * Returns the {@code Class} object representing the class or interface that
     * declares the field represented by this {@code Field} object.
     */
    public java.lang.Class<?> getDeclaringClass() {
        return declaringClass.getDecoratedClass();
    }

    /**
     * Returns the name of the field represented by this {@code Field} object.
     */
    public String getName() {
        if (customField)
            return customName;
        return decoratedField.getName();
    }

    /**
     * Returns the Java language modifiers for the field represented by this
     * {@code Field} object, as an integer. The {@code Modifier} class should be
     * used to decode the modifiers.
     *
     * @see Modifier
     */

    public int getModifiers() {
        if (customField)
            return customModifiers;
        return decoratedField.getModifiers();
    }

    /**
     * Returns {@code true} if this field represents an element of an enumerated
     * type; returns {@code false} otherwise.
     *
     * @return {@code true} if and only if this field represents an element of an
     * enumerated type.
     * @since 1.5
     */

    public boolean isEnumConstant() {
        if (customField)
            return false;
        return decoratedField.isEnumConstant();
    }

    /**
     * Returns {@code true} if this field is a synthetic
     * <p>
     * field; returns {@code false} otherwise.
     *
     * @return true if and only if this field is a synthetic
     * <p>
     * field as defined by the Java Language Specification.
     * @since 1.5
     */

    public boolean isSynthetic() {
        if (customField)
            return false;
        return decoratedField.isSynthetic();
    }

    /**
     * Returns a {@code Class} object that identifies the
     * <p>
     * declared type for the field represented by this
     * <p>
     * {@code Field} object.
     *
     * @return a {@code Class} object identifying the declared
     * <p>
     * type of the field represented by this object
     */
    public java.lang.Class<?> getType() {
        if (customField)
            return customType;
        return decoratedField.getType();
    }

    /**
     * Returns a {@code Type} object that represents the declared type for
     * <p>
     * the field represented by this {@code Field} object.
     *
     *
     *
     * <p>
     * If the {@code Type} is a parameterized type, the
     * <p>
     * {@code Type} object returned must accurately reflect the
     * <p>
     * actual type parameters used in the source code.
     *
     *
     *
     * <p>
     * If the type of the underlying field is a type variable or a
     * <p>
     * parameterized type, it is created. Otherwise, it is resolved.
     *
     * @return a {@code Type} object that represents the declared type for
     * <p>
     * the field represented by this {@code Field} object
     * @throws GenericSignatureFormatError         if the generic field
     *                                             <p>
     *                                             signature does not conform to the format specified in
     *
     *                                             <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException             if the generic type
     *                                             <p>
     *                                             signature of the underlying field refers to a non-existent
     *                                             <p>
     *                                             type declaration
     * @throws MalformedParameterizedTypeException if the generic
     *                                             <p>
     *                                             signature of the underlying field refers to a parameterized type
     *                                             <p>
     *                                             that cannot be instantiated for any reason
     * @since 1.5
     */
    public Type getGenericType() {
        if (customField)
            return customGenericType;
        return decoratedField.getGenericType();
    }

    /**
     * Compares this {@code Field} against the specified object. Returns
     * <p>
     * true if the objects are the same. Two {@code Field} objects are the same if
     * <p>
     * they were declared by the same class and have the same name
     * <p>
     * and type.
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Field) {
            if (customField) {
                Field ftemp = (Field) obj;
                return customName.equals(ftemp.getName()) && customType.equals(ftemp.getType());
            }
            return this.decoratedField.equals(((Field) obj).decoratedField);
        } else {
            if (obj != null && obj instanceof java.lang.reflect.Field) {
                if (customField) {
                    java.lang.reflect.Field ftemp = (java.lang.reflect.Field) obj;
                    return customName.equals(ftemp.getName()) && customType.equals(ftemp.getType());
                }
                return this.decoratedField.equals((java.lang.reflect.Field) obj);
            }
        }

        return false;

    }

    /**
     * Returns a hashcode for this {@code Field}. This is computed as the
     * <p>
     * exclusive-or of the hashcodes for the underlying field's
     * <p>
     * declaring class name and its name.
     */
    public int hashCode() {
        if (customField)
            return customName.hashCode();
        return decoratedField.hashCode();
    }

    /**
     * Returns a string describing this {@code Field}. The format is
     * <p>
     * the access modifiers for the field, if any, followed
     * <p>
     * by the field type, followed by a space, followed by
     * <p>
     * the fully-qualified name of the class declaring the field,
     * <p>
     * followed by a period, followed by the name of the field.
     * <p>
     * For example:
     *
     * <pre>
     *
     *    public static final int java.lang.Thread.MIN_PRIORITY
     *
     *    private int java.io.FileDescriptor.fd
     *
     * </pre>
     *
     *
     *
     * <p>
     * The modifiers are placed in canonical order as specified by
     * <p>
     * "The Java Language Specification". This is {@code public},
     * <p>
     * {@code protected} or {@code private} first, and then other
     * <p>
     * modifiers in the following order: {@code static}, {@code final},
     * <p>
     * {@code transient}, {@code volatile}.
     *
     * @return a string describing this {@code Field}
     * @jls 8.3.1 Field Modifiers
     */
    public String toString() {
        int mod = getModifiers();
        if (customField) {
            String typeStr;
            if (customType == null)
                typeStr = "<unspecified type>";
            else
                typeStr = getType().getTypeName();
            String classStr;
            if (declaringClass == null)
                classStr = "<unbound field>";
            else
                classStr = getDeclaringClass().getTypeName();

            return (((mod == 0) ? "" : (Modifier.toString(mod) + " ")) + typeStr + " " + classStr + "." + getName());
        }
        return (((mod == 0) ? "" : (Modifier.toString(mod) + " ")) + getType().getTypeName() + " "
                + getDeclaringClass().getTypeName() + "." + getName());
    }

    /**
     * Returns a string describing this {@code Field}, including
     * <p>
     * its generic type. The format is the access modifiers for the
     * <p>
     * field, if any, followed by the generic field type, followed by
     * <p>
     * a space, followed by the fully-qualified name of the class
     * <p>
     * declaring the field, followed by a period, followed by the name
     * <p>
     * of the field.
     *
     *
     *
     * <p>
     * The modifiers are placed in canonical order as specified by
     * <p>
     * "The Java Language Specification". This is {@code public},
     * <p>
     * {@code protected} or {@code private} first, and then other
     * <p>
     * modifiers in the following order: {@code static}, {@code final},
     * <p>
     * {@code transient}, {@code volatile}.
     *
     * @return a string describing this {@code Field}, including
     * <p>
     * its generic type
     * @jls 8.3.1 Field Modifiers
     * @since 1.5
     */
    public String toGenericString() {
        int mod = getModifiers();
        Type fieldType = getGenericType();
        return (((mod == 0) ? "" : (Modifier.toString(mod) + " ")) + fieldType.getTypeName() + " "
                + getDeclaringClass().getTypeName() + "." + getName());
    }

    /**
     * Calls the corresponding getter method of a field added by JMPLib
     *
     * @param currentVer
     * @param obj
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private Object invokeFieldGetterMethod(Object currentVer, Object obj)
            throws IllegalAccessException, IllegalArgumentException {
        java.lang.reflect.Method currentMethod;
        boolean isStatic = false;

        // Obtain the most recent version of the object.
        try {
            isStatic = Modifier.isStatic(this.getModifiers());
            if (isStatic) {
                // Obtain the corresponding field getter in the latest version of the object
                currentMethod = currentVer.getClass().getMethod("_" + this.getName() + Field.staticGetterPostfix);
            } else {
                // Obtain the corresponding field getter in the latest version of the object
                currentMethod = currentVer.getClass().getMethod("_" + this.getName() + Field.getterPostfix,
                        obj.getClass());
            }
            if (isStatic) {
                // Invoke it through Java reflection
                return currentMethod.invoke(currentVer);
            }
            // Invoke it through Java reflection
            return currentMethod.invoke(currentVer, obj);
        } catch (SecurityException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Returns the value of the field represented by this {@code Field}, on
     * <p>
     * the specified object. The value is automatically wrapped in an
     * <p>
     * object if it has a primitive type.
     *
     *
     *
     * <p>
     * The underlying field's value is obtained as follows:
     *
     *
     *
     * <p>
     * If the underlying field is a static field, the {@code obj} argument
     * <p>
     * is ignored; it may be null.
     *
     *
     *
     * <p>
     * Otherwise, the underlying field is an instance field. If the
     * <p>
     * specified {@code obj} argument is null, the method throws a
     * <p>
     * {@code NullPointerException}. If the specified object is not an
     * <p>
     * instance of the class or interface declaring the underlying
     * <p>
     * field, the method throws an {@code IllegalArgumentException}.
     *
     *
     *
     * <p>
     * If this {@code Field} object is enforcing Java language access control, and
     * <p>
     * the underlying field is inaccessible, the method throws an
     * <p>
     * {@code IllegalAccessException}.
     * <p>
     * If the underlying field is static, the class that declared the
     * <p>
     * field is initialized if it has not already been initialized.
     *
     *
     *
     * <p>
     * Otherwise, the value is retrieved from the underlying instance
     * <p>
     * or static field. If the field has a primitive type, the value
     * <p>
     * is wrapped in an object before being returned, otherwise it is
     * <p>
     * returned as is.
     *
     *
     *
     * <p>
     * If the field is hidden in the type of {@code obj},
     * <p>
     * the field's value is obtained according to the preceding rules.
     *
     * @param obj object from which the represented field's value is
     *            <p>
     *            to be extracted
     * @return the value of the represented field in object
     * <p>
     * {@code obj}; primitive values are wrapped in an appropriate
     * <p>
     * object before being returned
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof).
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     */
    @CallerSensitive
    public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).get(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.get(obj);
            }
            return this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance {@code boolean} field.
     *
     * @param obj the object to extract the {@code boolean} value
     *            <p>
     *            from
     * @return the value of the {@code boolean} field
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code boolean} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */
    @CallerSensitive
    public boolean getBoolean(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getBoolean(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getBoolean(obj);
            }

            return (boolean) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance {@code byte} field.
     *
     * @param obj the object to extract the {@code byte} value
     *            <p>
     *            from
     * @return the value of the {@code byte} field
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code byte} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */

    @CallerSensitive
    public byte getByte(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getByte(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getByte(obj);
            }

            return (byte) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance field of type
     * <p>
     * {@code char} or of another primitive type convertible to
     * <p>
     * type {@code char} via a widening conversion.
     *
     * @param obj the object to extract the {@code char} value
     *            <p>
     *            from
     * @return the value of the field converted to type {@code char}
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code char} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */
    @CallerSensitive
    public char getChar(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getChar(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getChar(obj);
            }

            return (char) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance field of type
     * <p>
     * {@code short} or of another primitive type convertible to
     * <p>
     * type {@code short} via a widening conversion.
     *
     * @param obj the object to extract the {@code short} value
     *            <p>
     *            from
     * @return the value of the field converted to type {@code short}
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code short} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */
    @CallerSensitive
    public short getShort(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getShort(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getShort(obj);
            }

            return (short) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance field of type
     * <p>
     * {@code int} or of another primitive type convertible to
     * <p>
     * type {@code int} via a widening conversion.
     *
     * @param obj the object to extract the {@code int} value
     *            <p>
     *            from
     * @return the value of the field converted to type {@code int}
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code int} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */
    @CallerSensitive
    public int getInt(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getInt(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getInt(obj);
            }

            return (int) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance field of type
     * <p>
     * {@code long} or of another primitive type convertible to
     * <p>
     * type {@code long} via a widening conversion.
     *
     * @param obj the object to extract the {@code long} value
     *            <p>
     *            from
     * @return the value of the field converted to type {@code long}
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code long} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */
    @CallerSensitive
    public long getLong(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getLong(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getLong(obj);
            }

            return (long) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance field of type
     * <p>
     * {@code float} or of another primitive type convertible to
     * <p>
     * type {@code float} via a widening conversion.
     *
     * @param obj the object to extract the {@code float} value
     *            <p>
     *            from
     * @return the value of the field converted to type {@code float}
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code float} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */
    @CallerSensitive
    public float getFloat(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getFloat(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getFloat(obj);
            }

            return (float) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Gets the value of a static or instance field of type
     * <p>
     * {@code double} or of another primitive type convertible to
     * <p>
     * type {@code double} via a widening conversion.
     *
     * @param obj the object to extract the {@code double} value
     *            <p>
     *            from
     * @return the value of the field converted to type {@code double}
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is inaccessible.
     * @throws IllegalArgumentException    if the specified object is not
     *                                     <p>
     *                                     an instance of the class or interface declaring the
     *                                     <p>
     *                                     underlying field (or a subclass or implementor
     *                                     <p>
     *                                     thereof), or if the field value cannot be
     *                                     <p>
     *                                     converted to the type {@code double} by a
     *                                     <p>
     *                                     widening conversion.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#get
     */
    @CallerSensitive
    public double getDouble(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot get the value of a field not bound to a class");
                        return this.declaringClass.getField(this.customName).getDouble(obj);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                return this.decoratedField.getDouble(obj);
            }

            return (double) this.invokeFieldGetterMethod(currentVer, obj);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Calls the corresponding getter method of a field added by JMPLib
     *
     * @param currentVer
     * @param obj
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    private void invokeFieldSetterMethod(Object currentVer, Object obj, Object value)
            throws IllegalAccessException, IllegalArgumentException {
        java.lang.reflect.Method currentMethod;
        boolean isStatic = false;

        // Obtain the most recent version of the object.
        try {
            isStatic = Modifier.isStatic(this.getModifiers());
            if (isStatic) {
                // Obtain the corresponding field getter in the latest version of the object
                currentMethod = currentVer.getClass().getMethod("_" + this.getName() + Field.staticSetterPostfix,
                        this.getType());
            } else {
                // Obtain the corresponding field getter in the latest version of the object
                currentMethod = currentVer.getClass().getMethod("_" + this.getName() + Field.setterPostfix,
                        obj.getClass(), this.getType());
            }
            // Invoke it through Java reflection
            if (isStatic)
                currentMethod.invoke(currentVer, value);
            else
                currentMethod.invoke(currentVer, obj, value);
        } catch (SecurityException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException(
                    "The field " + this.getName() + " does not exist in class " + obj.getClass().getName());
        }
    }

    /**
     * Sets the field represented by this {@code Field} object on the
     * <p>
     * specified object argument to the specified new value. The new
     * <p>
     * value is automatically unwrapped if the underlying field has a
     * <p>
     * primitive type.
     *
     *
     *
     * <p>
     * The operation proceeds as follows:
     *
     *
     *
     * <p>
     * If the underlying field is static, the {@code obj} argument is
     * <p>
     * ignored; it may be null.
     *
     *
     *
     * <p>
     * Otherwise the underlying field is an instance field. If the
     * <p>
     * specified object argument is null, the method throws a
     * <p>
     * {@code NullPointerException}. If the specified object argument is not
     * <p>
     * an instance of the class or interface declaring the underlying
     * <p>
     * field, the method throws an {@code IllegalArgumentException}.
     *
     *
     *
     * <p>
     * If this {@code Field} object is enforcing Java language access control, and
     * <p>
     * the underlying field is inaccessible, the method throws an
     * <p>
     * {@code IllegalAccessException}.
     *
     *
     *
     * <p>
     * If the underlying field is final, the method throws an
     * <p>
     * {@code IllegalAccessException} unless {@code setAccessible(true)}
     * <p>
     * has succeeded for this {@code Field} object
     * <p>
     * and the field is non-static. Setting a final field in this way
     * <p>
     * is meaningful only during deserialization or reconstruction of
     * <p>
     * instances of classes with blank final fields, before they are
     * <p>
     * made available for access by other parts of a program. Use in
     * <p>
     * any other context may have unpredictable effects, including cases
     * <p>
     * in which other parts of a program continue to use the original
     * <p>
     * value of this field.
     *
     *
     *
     * <p>
     * If the underlying field is of a primitive type, an unwrapping
     * <p>
     * conversion is attempted to convert the new value to a value of
     * <p>
     * a primitive type. If this attempt fails, the method throws an
     * <p>
     * {@code IllegalArgumentException}.
     *
     *
     *
     * <p>
     * If, after possible unwrapping, the new value cannot be
     * <p>
     * converted to the type of the underlying field by an identity or
     * <p>
     * widening conversion, the method throws an
     * <p>
     * {@code IllegalArgumentException}.
     *
     *
     *
     * <p>
     * If the underlying field is static, the class that declared the
     * <p>
     * field is initialized if it has not already been initialized.
     *
     *
     *
     * <p>
     * The field is set to the possibly unwrapped and widened new value.
     *
     *
     *
     * <p>
     * If the field is hidden in the type of {@code obj},
     * <p>
     * the field's value is set according to the preceding rules.
     *
     * @param obj   the object whose field should be modified
     * @param value the new value for the field of {@code obj}
     *              <p>
     *              being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     */
    @CallerSensitive
    public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).set(obj, value);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.set(obj, value);
                return;
            }

            this.invokeFieldSetterMethod(currentVer, obj, value);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as a {@code boolean} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, zObj)},
     * <p>
     * where {@code zObj} is a {@code Boolean} object and
     * <p>
     * {@code zObj.booleanValue() == z}.
     *
     * @param obj the object whose field should be modified
     * @param z   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */

    @CallerSensitive

    public void setBoolean(Object obj, boolean z) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setBoolean(obj, z);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setBoolean(obj, z);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, z);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as a {@code byte} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, bObj)},
     * <p>
     * where {@code bObj} is a {@code Byte} object and
     * <p>
     * {@code bObj.byteValue() == b}.
     *
     * @param obj the object whose field should be modified
     * @param b   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */

    @CallerSensitive

    public void setByte(Object obj, byte b) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setByte(obj, b);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setByte(obj, b);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, b);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as a {@code char} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, cObj)},
     * <p>
     * where {@code cObj} is a {@code Character} object and
     * <p>
     * {@code cObj.charValue() == c}.
     *
     * @param obj the object whose field should be modified
     * @param c   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */
    @CallerSensitive
    public void setChar(Object obj, char c) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setChar(obj, c);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setChar(obj, c);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, c);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as a {@code short} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, sObj)},
     * <p>
     * where {@code sObj} is a {@code Short} object and
     * <p>
     * {@code sObj.shortValue() == s}.
     *
     * @param obj the object whose field should be modified
     * @param s   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */
    @CallerSensitive
    public void setShort(Object obj, short s) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setShort(obj, s);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setShort(obj, s);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, s);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as an {@code int} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, iObj)},
     * <p>
     * where {@code iObj} is a {@code Integer} object and
     * <p>
     * {@code iObj.intValue() == i}.
     *
     * @param obj the object whose field should be modified
     * @param i   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */
    @CallerSensitive
    public void setInt(Object obj, int i) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setInt(obj, i);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setInt(obj, i);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, i);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as a {@code long} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, lObj)},
     * <p>
     * where {@code lObj} is a {@code Long} object and
     * <p>
     * {@code lObj.longValue() == l}.
     *
     * @param obj the object whose field should be modified
     * @param l   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */
    @CallerSensitive
    public void setLong(Object obj, long l) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setLong(obj, l);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setLong(obj, l);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, l);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as a {@code float} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, fObj)},
     * <p>
     * where {@code fObj} is a {@code Float} object and
     * <p>
     * {@code fObj.floatValue() == f}.
     *
     * @param obj the object whose field should be modified
     * @param f   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */
    @CallerSensitive
    public void setFloat(Object obj, float f) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setFloat(obj, f);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setFloat(obj, f);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, f);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * Sets the value of a field as a {@code double} on the specified object.
     * <p>
     * This method is equivalent to
     * <p>
     * {@code set(obj, dObj)},
     * <p>
     * where {@code dObj} is a {@code Double} object and
     * <p>
     * {@code dObj.doubleValue() == d}.
     *
     * @param obj the object whose field should be modified
     * @param d   the new value for the field of {@code obj}
     *            <p>
     *            being modified
     * @throws IllegalAccessException      if this {@code Field} object
     *                                     <p>
     *                                     is enforcing Java language access control and the underlying
     *                                     <p>
     *                                     field is either inaccessible or final.
     * @throws IllegalArgumentException    if the specified object is not an
     *                                     <p>
     *                                     instance of the class or interface declaring the underlying
     *                                     <p>
     *                                     field (or a subclass or implementor thereof),
     *                                     <p>
     *                                     or if an unwrapping conversion fails.
     * @throws NullPointerException        if the specified object is null
     *                                     <p>
     *                                     and the field is an instance field.
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails.
     * @see Field#set
     */
    @CallerSensitive
    public void setDouble(Object obj, double d) throws IllegalArgumentException, IllegalAccessException {
        try {
            // Obtain the most recent version of the object.
            Object currentVer = IntrospectionUtils.getLatestObjectVersion(obj);
            if (currentVer == null) {
                if (customField) {
                    try {
                        if (this.declaringClass == null)
                            throw new IllegalArgumentException("Cannot set the value of a field not bound to a class");
                        this.declaringClass.getField(this.customName).setDouble(obj, d);
                        return;
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }
                }
                this.decoratedField.setDouble(obj, d);
                return;
            }
            this.invokeFieldSetterMethod(currentVer, obj, d);
        } catch (InvocationTargetException e) {
            throw new IllegalAccessException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalAccessException("The field " + this.decoratedField.getName() + " does not exist in class "
                    + obj.getClass().getName());
        }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    public <T extends Annotation> T getAnnotation(java.lang.Class<T> annotationClass) {
        if (customField)
            return null;
        return decoratedField.getAnnotation(annotationClass);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <T extends Annotation> T[] getAnnotationsByType(java.lang.Class<T> annotationClass) {
        if (customField)
            return null;
        return decoratedField.getAnnotationsByType(annotationClass);
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getDeclaredAnnotations() {
        if (customField)
            return null;
        return decoratedField.getDeclaredAnnotations();
    }

    /**
     * Returns an AnnotatedType object that represents the use of a type to specify
     * <p>
     * the declared type of the field represented by this Field.
     *
     * @return an object representing the declared type of the field
     * <p>
     * represented by this Field
     * @since 1.8
     */
    public AnnotatedType getAnnotatedType() {
        if (customField)
            return null;
        return decoratedField.getAnnotatedType();
    }

    /**
     * Get the init value for custom fields.
     *
     * @return
     */
    public String getInit() {
        return customInit;
    }

    /********************************************
     * EXTRA FUNCTIONALITY INCORPORATED BY JMPLIB
     ********************************************/

    /**
     * Get the decorated Java Field
     *
     * @return
     */
    public final java.lang.reflect.Field getDecoratedField() {
        return decoratedField;
    }

    /**
     * Get the corresponding JavaParser FieldDeclaration node
     *
     * @return
     * @throws StructuralIntercessionException
     */
    public FieldDeclaration getFieldDeclaration() throws StructuralIntercessionException {
        if (this.customFieldDeclaration != null)
            return customFieldDeclaration;

        java.lang.Class<?> declaringClass = this.getDeclaringClass();
        // Class content
        ClassContent classContent;
        CompilationUnit unit;

        try {
            classContent = SourceCodeCache.getInstance().getClassContent(declaringClass);
            unit = JavaParserUtils.parse(classContent.getContent());
            return JavaParserUtils.searchFieldDeclaration(unit, declaringClass, this.getName());
        } catch (Exception e) {
            try {
                StringReader sr = new StringReader(SourceCodeCache.getInstance().getSourceFromSrcZipExtractor().getSourceCode(this.getDeclaringClass().getName()));
                unit = JavaParser.parse(sr, true);
                return JavaParserUtils.searchFieldDeclarationFromJavaParserCU(unit, declaringClass, this.getName());
            }
            catch(Exception ex) {
                throw new StructuralIntercessionException(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * Get the Field source code
     *
     * @return
     */
    public String getSourceCode() {
        String txt = this.getType().getName() + " " + this.getName();
        String init = this.getInit();
        if (init != null) {
            return txt + " = " + init + ";";
        }
        return txt + ";";
    }
}
