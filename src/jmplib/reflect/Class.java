package jmplib.reflect;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import jmplib.SimpleIntercessor;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.sourcecode.ClassContent;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.InheritanceTables;
import jmplib.util.intercessor.IntercessorTypeConversion;
import sun.reflect.CallerSensitive;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;

/**
 * This is a decorator of the java.lang.Class class that allows introspection
 * operations taking into account the latest version of the decorated class. It
 * provides the same functionality of the decorated class, but hiding the
 * version mechanisms of JMPLib from the caller. Therefore, the original
 * decorated class is shown as directly containing all the runtime structural
 * changes that are performed using JMPLib.
 * <p>
 * The class name and signature is the same than the original class to
 * facilitate using jmplib.reflect functionalities in old code that uses
 * java.lang.reflect code.
 *
 * @author Jose Manuel Redondo
 */
public class Class<T> implements java.io.Serializable, GenericDeclaration, Type, AnnotatedElement {

    /**
     *
     */
    private static final long serialVersionUID = -7167324192924804702L;

    /**
     * Original java.lang.Class object
     */
    private java.lang.Class<?> decoratedClass;

    private ClassOrInterfaceDeclaration customClassDeclaration;

    public Class(ClassOrInterfaceDeclaration cl) throws StructuralIntercessionException {
        this.customClassDeclaration = cl;
        this.decoratedClass = DeclarationUtils.getDecoratedClass(cl);
    }

    /**
     * Only the Introspector should be able to return instances of this class
     *
     * @param clazz_
     */
    protected Class(java.lang.Class<?> clazz_) {
        this.decoratedClass = clazz_;
    }

    /**
     * Returns the {@code Class} object associated with the class or
     * <p>
     * interface with the given string name. Invoking this method is
     * <p>
     * equivalent to:
     * <p>
     * <p>
     * <p>
     * <blockquote>
     * <p>
     * {@code Class.forName(className, true, currentLoader)}
     * <p>
     * </blockquote>
     * <p>
     * <p>
     * <p>
     * where {@code currentLoader} denotes the defining class loader of
     * <p>
     * the current class.
     * <p>
     * <p>
     * <p>
     * <p>
     * For example, the following code fragment returns the
     * <p>
     * runtime {@code Class} descriptor for the class named
     * <p>
     * {@code java.lang.Thread}:
     * <p>
     * <p>
     * <p>
     * <blockquote>
     * <p>
     * {@code Class t = Class.forName("java.lang.Thread")}
     * <p>
     * </blockquote>
     * <p>
     * <p>
     * <p>
     * A call to {@code forName("X")} causes the class named
     * <p>
     * {@code X} to be initialized.
     *
     * @param className the fully qualified name of the desired class.
     * @return the {@code Class} object for the class with the
     * <p>
     * specified name.
     * @throws LinkageError                if the linkage fails
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails
     * @throws ClassNotFoundException      if the class cannot be located
     */

    @SuppressWarnings({"unchecked", "rawtypes"})
    @CallerSensitive
    public static Class<?> forName(String className) throws ClassNotFoundException {
        return new Class(java.lang.Class.forName(className));
    }

    /**
     * Returns the {@code Class} object associated with the class or
     * <p>
     * interface with the given string name, using the given class loader.
     * <p>
     * Given the fully qualified name for a class or interface (in the same
     * <p>
     * format returned by {@code getName}) this method attempts to
     * <p>
     * locate, load, and link the class or interface. The specified class
     * <p>
     * loader is used to load the class or interface. If the parameter
     * <p>
     * {@code loader} is null, the class is loaded through the bootstrap
     * <p>
     * class loader. The class is initialized only if the
     * <p>
     * {@code initialize} parameter is {@code true} and if it has
     * <p>
     * not been initialized earlier.
     * <p>
     * <p>
     * <p>
     * <p>
     * If {@code name} denotes a primitive type or void, an attempt
     * <p>
     * will be made to locate a user-defined class in the unnamed package whose
     * <p>
     * name is {@code name}. Therefore, this method cannot be used to
     * <p>
     * obtain any of the {@code Class} objects representing primitive
     * <p>
     * types or void.
     * <p>
     * <p>
     * <p>
     * <p>
     * If {@code name} denotes an array class, the component type of
     * <p>
     * the array class is loaded but not initialized.
     * <p>
     * <p>
     * <p>
     * <p>
     * For example, in an instance method the expression:
     * <p>
     * <p>
     * <p>
     * <blockquote>
     * <p>
     * {@code Class.forName("Foo")}
     * <p>
     * </blockquote>
     * <p>
     * <p>
     * <p>
     * is equivalent to:
     * <p>
     * <p>
     * <p>
     * <blockquote>
     * <p>
     * {@code Class.forName("Foo", true, this.getClass().getClassLoader())}
     * <p>
     * </blockquote>
     * <p>
     * <p>
     * <p>
     * Note that this method throws errors related to loading, linking or
     * <p>
     * initializing as specified in Sections 12.2, 12.3 and 12.4 of <em>The
     * <p>
     * Java Language Specification</em>.
     * <p>
     * Note that this method does not check whether the requested class
     * <p>
     * is accessible to its caller.
     * <p>
     * <p>
     * <p>
     * <p>
     * If the {@code loader} is {@code null}, and a security
     * <p>
     * manager is present, and the caller's class loader is not null, then this
     * <p>
     * method calls the security manager's {@code checkPermission} method
     * <p>
     * with a {@code RuntimePermission("getClassLoader")} permission to
     * <p>
     * ensure it's ok to access the bootstrap class loader.
     *
     * @param name       fully qualified name of the desired class
     * @param initialize if {@code true} the class will be initialized.
     *                   <p>
     *                   See Section 12.4 of <em>The Java Language Specification</em>.
     * @param loader     class loader from which the class must be loaded
     * @return class object representing the desired class
     * @throws LinkageError                if the linkage fails
     * @throws ExceptionInInitializerError if the initialization provoked
     *                                     <p>
     *                                     by this method fails
     * @throws ClassNotFoundException      if the class cannot be located by
     *                                     <p>
     *                                     the specified class loader
     * @see java.lang.Class#forName(String)
     * @see java.lang.ClassLoader
     * @since 1.2
     */

    @CallerSensitive
    public static Class<?> forName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
        java.lang.Class<?> clazz = java.lang.Class.forName(name, initialize, loader);
        return new Class<>(clazz);
    }

    /**
     * Get the current last version of the decorated class.
     *
     * @return
     */
    public java.lang.Class<?> getLastVersion() {
        return VersionTables.getNewVersion(this.decoratedClass);
    }

    /**
     * Returns the decorated class from the java.lang package.
     *
     * @return
     */
    public java.lang.Class<?> getDecoratedClass() {
        return this.decoratedClass;
    }

    /**
     * Returns a {@code Method} object that reflects the specified public
     * <p>
     * member method of the class or interface represented by this
     * <p>
     * {@code Class} object. The {@code name} parameter is a
     * <p>
     * {@code String} specifying the simple name of the desired method. The
     * <p>
     * {@code parameterTypes} parameter is an array of {@code Class}
     * <p>
     * objects that identify the method's formal parameter types, in declared
     * <p>
     * order. If {@code parameterTypes} is {@code null}, it is
     * <p>
     * treated as if it were an empty array.
     * <p>
     * <p>
     * <p>
     * <p>
     * If the {@code name} is "{@code <init>}" or "{@code <clinit>}" a
     * <p>
     * {@code NoSuchMethodException} is raised. Otherwise, the method to
     * <p>
     * be reflected is determined by the algorithm that follows. Let C be the
     * <p>
     * class or interface represented by this object:
     * <p>
     * <OL>
     * <p>
     * <LI>C is searched for a <I>matching method</I>, as defined below. If a
     * <p>
     * matching method is found, it is reflected.</LI>
     * <p>
     * <LI>If no matching method is found by step 1 then:
     * <p>
     * <OL TYPE="a">
     * <p>
     * <LI>If C is a class other than {@code Object}, then this algorithm is
     * <p>
     * invoked recursively on the superclass of C.</LI>
     * <p>
     * <LI>If C is the class {@code Object}, or if C is an interface, then
     * <p>
     * the superinterfaces of C (if any) are searched for a matching
     * <p>
     * method. If any such method is found, it is reflected.</LI>
     * <p>
     * </OL>
     * </LI>
     * <p>
     * </OL>
     * <p>
     * <p>
     * <p>
     * <p>
     * To find a matching method in a class or interface C:&nbsp; If C
     * <p>
     * declares exactly one public method with the specified name and exactly
     * <p>
     * the same formal parameter types, that is the method reflected. If more
     * <p>
     * than one such method is found in C, and one of these methods has a
     * <p>
     * return type that is more specific than any of the others, that method is
     * <p>
     * reflected; otherwise one of the methods is chosen arbitrarily.
     * <p>
     * <p>
     * <p>
     * <p>
     * Note that there may be more than one matching method in a
     * <p>
     * class because while the Java language forbids a class to
     * <p>
     * declare multiple methods with the same signature but different
     * <p>
     * return types, the Java virtual machine does not. This
     * <p>
     * increased flexibility in the virtual machine can be used to
     * <p>
     * implement various language features. For example, covariant
     * <p>
     * returns can be implemented with {@linkplain
     * <p>
     * java.lang.reflect.Method#isBridge bridge methods}; the bridge
     * <p>
     * method and the method being overridden would have the same
     * <p>
     * signature but different return types.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, then this
     * <p>
     * method does not find the {@code clone()} method.
     * <p>
     * <p>
     * <p>
     * <p>
     * Static methods declared in superinterfaces of the class or interface
     * <p>
     * represented by this {@code Class} object are not considered members of
     * <p>
     * the class or interface.
     *
     * @param name           the name of the method
     * @param parameterTypes the list of parameters
     * @return the {@code Method} object that matches the specified
     * <p>
     * {@code name} and {@code parameterTypes}
     * @throws NoSuchMethodException if a matching method is not found
     *                               <p>
     *                               or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
     * @throws NullPointerException  if {@code name} is {@code null}
     * @throws SecurityException     If a security manager, <i>s</i>, is present and
     *                               <p>
     *                               the caller's class loader is not the same as or an
     *                               <p>
     *                               ancestor of the class loader for the current class and
     *                               <p>
     *                               invocation of {@link SecurityManager#checkPackageAccess
     *                               <p>
     *                               s.checkPackageAccess()} denies access to the package
     *                               <p>
     *                               of this class.
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */
    @CallerSensitive
    public Method getMethod(String name, java.lang.Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();

        java.lang.reflect.Method m = lastVersion.getMethod(name, parameterTypes);
        Method ret = new Method(m);

        if (IntrospectionUtils.isJmpLibMethod(ret, lastVersion.getMethods()))
            throw new NoSuchMethodException(m.getName() + IntrospectionUtils.argumentTypesToString(parameterTypes));
        return ret;
    }

    /**
     * Converts the object to a string. The string representation is the
     * <p>
     * string "class" or "interface", followed by a space, and then by the
     * <p>
     * fully qualified name of the class in the format returned by
     * <p>
     * {@code getName}. If this {@code Class} object represents a
     * <p>
     * primitive type, this method returns the name of the primitive type. If
     * <p>
     * this {@code Class} object represents void this method returns
     * <p>
     * "void".
     *
     * @return a string representation of this class object.
     */

    public String toString() {
        return this.decoratedClass.toString();
    }

    /**
     * Returns a string describing this {@code Class}, including
     * <p>
     * information about modifiers and type parameters.
     * <p>
     * <p>
     * <p>
     * The string is formatted as a list of type modifiers, if any,
     * <p>
     * followed by the kind of type (empty string for primitive types
     * <p>
     * and {@code class}, {@code enum}, {@code interface}, or
     * <p>
     * <code>&#64;</code>{@code interface}, as appropriate), followed
     * <p>
     * by the type's name, followed by an angle-bracketed
     * <p>
     * comma-separated list of the type's type parameters, if any.
     * <p>
     * <p>
     * <p>
     * A space is used to separate modifiers from one another and to
     * <p>
     * separate any modifiers from the kind of type. The modifiers
     * <p>
     * occur in canonical order. If there are no type parameters, the
     * <p>
     * type parameter list is elided.
     * <p>
     * <p>
     * <p>
     * <p>
     * Note that since information about the runtime representation
     * <p>
     * of a type is being generated, modifiers not present on the
     * <p>
     * originating source code or illegal on the originating source
     * <p>
     * code may be present.
     *
     * @return a string describing this {@code Class}, including
     * <p>
     * information about modifiers and type parameters
     * @since 1.8
     */

    public String toGenericString() {
        return this.decoratedClass.toGenericString();
    }

    /**
     * Creates a new instance of the class represented by this {@code Class}
     * <p>
     * object. The class is instantiated as if by a {@code new}
     * <p>
     * expression with an empty argument list. The class is initialized if it
     * <p>
     * has not already been initialized.
     * <p>
     * <p>
     * <p>
     * <p>
     * Note that this method propagates any exception thrown by the
     * <p>
     * nullary constructor, including a checked exception. Use of
     * <p>
     * this method effectively bypasses the compile-time exception
     * <p>
     * checking that would otherwise be performed by the compiler.
     * <p>
     * The {@link
     * <p>
     * java.lang.reflect.Constructor#newInstance(java.lang.Object...)
     * <p>
     * Constructor.newInstance} method avoids this problem by wrapping
     * <p>
     * any exception thrown by the constructor in a (checked) {@link
     * <p>
     * java.lang.reflect.InvocationTargetException}.
     *
     * @return a newly allocated instance of the class represented by this
     * <p>
     * object.
     * @throws IllegalAccessException      if the class or its nullary
     *                                     <p>
     *                                     constructor is not accessible.
     * @throws InstantiationException      if this {@code Class} represents an abstract class,
     *                                     <p>
     *                                     an interface, an array class, a primitive type, or void;
     *                                     <p>
     *                                     or if the class has no nullary constructor;
     *                                     <p>
     *                                     or if the instantiation fails for some other reason.
     * @throws ExceptionInInitializerError if the initialization
     *                                     <p>
     *                                     provoked by this method fails.
     * @throws SecurityException           If a security manager, <i>s</i>, is present and
     *                                     <p>
     *                                     the caller's class loader is not the same as or an
     *                                     <p>
     *                                     ancestor of the class loader for the current class and
     *                                     <p>
     *                                     invocation of {@link SecurityManager#checkPackageAccess
     *                                     <p>
     *                                     s.checkPackageAccess()} denies access to the package
     *                                     <p>
     *                                     of this class.
     */

    @SuppressWarnings("unchecked")
    @CallerSensitive
    public T newInstance() throws InstantiationException, IllegalAccessException {
        java.lang.Class<?> lastVersion = getLastVersion();

        return (T) lastVersion.newInstance();
    }

    /**
     * Determines if the specified {@code Object} is assignment-compatible
     * <p>
     * with the object represented by this {@code Class}. This method is
     * <p>
     * the dynamic equivalent of the Java language {@code instanceof}
     * <p>
     * operator. The method returns {@code true} if the specified
     * <p>
     * {@code Object} argument is non-null and can be cast to the
     * <p>
     * reference type represented by this {@code Class} object without
     * <p>
     * raising a {@code ClassCastException.} It returns {@code false}
     * <p>
     * otherwise.
     * <p>
     * <p>
     * <p>
     * <p>
     * Specifically, if this {@code Class} object represents a
     * <p>
     * declared class, this method returns {@code true} if the specified
     * <p>
     * {@code Object} argument is an instance of the represented class (or
     * <p>
     * of any of its subclasses); it returns {@code false} otherwise. If
     * <p>
     * this {@code Class} object represents an array class, this method
     * <p>
     * returns {@code true} if the specified {@code Object} argument
     * <p>
     * can be converted to an object of the array class by an identity
     * <p>
     * conversion or by a widening reference conversion; it returns
     * <p>
     * {@code false} otherwise. If this {@code Class} object
     * <p>
     * represents an interface, this method returns {@code true} if the
     * <p>
     * class or any superclass of the specified {@code Object} argument
     * <p>
     * implements this interface; it returns {@code false} otherwise. If
     * <p>
     * this {@code Class} object represents a primitive type, this method
     * <p>
     * returns {@code false}.
     *
     * @param obj the object to check
     * @return true if {@code obj} is an instance of this class
     * @since JDK1.1
     */

    public boolean isInstance(Object obj) {
        java.lang.Class<?> lastVersion = getLastVersion();
        List<java.lang.Class<?>> versions = VersionTables.getVersions(this.decoratedClass);

        if (lastVersion.isInstance(obj) || this.decoratedClass.isInstance(obj))
            return true;

        // Returns true if the object is an instance of any of the class versions
        for (java.lang.Class<?> version : versions) {
            if (version.isInstance(obj))
                return true;
        }
        return false;
    }

    /**
     * Determines if the class or interface represented by this
     * <p>
     * {@code Class} object is either the same as, or is a superclass or
     * <p>
     * superinterface of, the class or interface represented by the specified
     * <p>
     * {@code Class} parameter. It returns {@code true} if so;
     * <p>
     * otherwise it returns {@code false}. If this {@code Class}
     * <p>
     * object represents a primitive type, this method returns
     * <p>
     * {@code true} if the specified {@code Class} parameter is
     * <p>
     * exactly this {@code Class} object; otherwise it returns
     * <p>
     * {@code false}.
     * <p>
     * <p>
     * <p>
     * <p>
     * Specifically, this method tests whether the type represented by the
     * <p>
     * specified {@code Class} parameter can be converted to the type
     * <p>
     * represented by this {@code Class} object via an identity conversion
     * <p>
     * or via a widening reference conversion. See <em>The Java Language
     * <p>
     * Specification</em>, sections 5.1.1 and 5.1.4 , for details.
     *
     * @param cls the {@code Class} object to be checked
     * @return the {@code boolean} value indicating whether objects of the
     * <p>
     * type {@code cls} can be assigned to objects of this class
     * @throws NullPointerException if the specified Class parameter is
     *                              <p>
     *                              null.
     * @since JDK1.1
     */

    public boolean isAssignableFrom(java.lang.Class<?> cls) {
        java.lang.Class<?> lastVersion = getLastVersion();
        List<java.lang.Class<?>> versions = VersionTables.getVersions(this.decoratedClass);

        if (lastVersion.isAssignableFrom(cls) || this.decoratedClass.isAssignableFrom(cls))
            return true;

        // Returns true if the object is an instance of any of the class versions
        for (java.lang.Class<?> version : versions) {
            if (version.isAssignableFrom(cls))
                return true;
        }
        return false;
    }

    public boolean isAssignableFrom(Class<?> cls) {
        return isAssignableFrom(cls.getDecoratedClass());
    }

    /**
     * Determines if the specified {@code Class} object represents an
     * <p>
     * interface type.
     *
     * @return {@code true} if this object represents an interface;
     * <p>
     * {@code false} otherwise.
     */

    public boolean isInterface() {
        return this.decoratedClass.isInterface();
    }

    /**
     * Determines if this {@code Class} object represents an array class.
     *
     * @return {@code true} if this object represents an array class;
     * <p>
     * {@code false} otherwise.
     * @since JDK1.1
     */

    public boolean isArray() {
        return this.decoratedClass.isArray();
    }

    /**
     * Determines if the specified {@code Class} object represents a
     * <p>
     * primitive type.
     * <p>
     * <p>
     * <p>
     * <p>
     * There are nine predefined {@code Class} objects to represent
     * <p>
     * the eight primitive types and void. These are created by the Java
     * <p>
     * Virtual Machine, and have the same names as the primitive types that
     * <p>
     * they represent, namely {@code boolean}, {@code byte},
     * <p>
     * {@code char}, {@code short}, {@code int},
     * <p>
     * {@code long}, {@code float}, and {@code double}.
     * <p>
     * <p>
     * <p>
     * <p>
     * These objects may only be accessed via the following public static
     * <p>
     * final variables, and are the only {@code Class} objects for which
     * <p>
     * this method returns {@code true}.
     *
     * @return true if and only if this class represents a primitive type
     * @see java.lang.Boolean#TYPE
     * @see java.lang.Character#TYPE
     * @see java.lang.Byte#TYPE
     * @see java.lang.Short#TYPE
     * @see java.lang.Integer#TYPE
     * @see java.lang.Long#TYPE
     * @see java.lang.Float#TYPE
     * @see java.lang.Double#TYPE
     * @see java.lang.Void#TYPE
     * @since JDK1.1
     */

    public boolean isPrimitive() {
        return this.decoratedClass.isPrimitive();
    }

    /**
     * Returns true if this {@code Class} object represents an annotation
     * <p>
     * type. Note that if this method returns true, {@link #isInterface()}
     * <p>
     * would also return true, as all annotation types are also interfaces.
     *
     * @return {@code true} if this class object represents an annotation
     * <p>
     * type; {@code false} otherwise
     * @since 1.5
     */

    public boolean isAnnotation() {
        return this.decoratedClass.isAnnotation();
    }

    /**
     * Returns {@code true} if this class is a synthetic class;
     * <p>
     * returns {@code false} otherwise.
     *
     * @return {@code true} if and only if this class is a synthetic class as
     * <p>
     * defined by the Java Language Specification.
     * @jls 13.1 The Form of a Binary
     * @since 1.5
     */

    public boolean isSynthetic() {
        return this.decoratedClass.isSynthetic();
    }

    /**
     * Returns the name of the entity (class, interface, array class,
     * <p>
     * primitive type, or void) represented by this {@code Class} object,
     * <p>
     * as a {@code String}.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this class object represents a reference type that is not an
     * <p>
     * array type then the binary name of the class is returned, as specified
     * <p>
     * by
     * <p>
     * <cite>The Java&trade; Language Specification</cite>.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this class object represents a primitive type or void, then the
     * <p>
     * name returned is a {@code String} equal to the Java language
     * <p>
     * keyword corresponding to the primitive type or void.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this class object represents a class of arrays, then the internal
     * <p>
     * form of the name consists of the name of the element type preceded by
     * <p>
     * one or more '{@code [}' characters representing the depth of the array
     * <p>
     * nesting. The encoding of element type names is as follows:
     * <p>
     * <p>
     * <p>
     * <blockquote>
     * <table summary="Element types and encodings">
     * <p>
     * <tr>
     * <th>Element Type
     * <th>&nbsp;&nbsp;&nbsp;
     * <th>Encoding
     * <p>
     * <tr>
     * <td>boolean
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>Z
     * <p>
     * <tr>
     * <td>byte
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>B
     * <p>
     * <tr>
     * <td>char
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>C
     * <p>
     * <tr>
     * <td>class or interface
     * <p>
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>L<i>classname</i>;
     * <p>
     * <tr>
     * <td>double
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>D
     * <p>
     * <tr>
     * <td>float
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>F
     * <p>
     * <tr>
     * <td>int
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>I
     * <p>
     * <tr>
     * <td>long
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>J
     * <p>
     * <tr>
     * <td>short
     * <td>&nbsp;&nbsp;&nbsp;
     * <td align=center>S
     * <p>
     * </table>
     * </blockquote>
     * <p>
     * <p>
     * <p>
     * <p>
     * The class or interface name <i>classname</i> is the binary name of
     * <p>
     * the class specified above.
     * <p>
     * <p>
     * <p>
     * <p>
     * Examples:
     * <p>
     * <blockquote>
     *
     * <pre>
     * <p>
     * String.class.getName()
     * <p>
     *     returns "java.lang.String"
     * <p>
     * byte.class.getName()
     * <p>
     *     returns "byte"
     * <p>
     * (new Object[3]).getClass().getName()
     * <p>
     *     returns "[Ljava.lang.Object;"
     * <p>
     * (new int[3][4][5][6][7][8][9]).getClass().getName()
     * <p>
     *     returns "[[[[[[[I"
     * <p>
     * </pre>
     *
     * </blockquote>
     *
     * @return the name of the class or interface
     * <p>
     * represented by this object.
     */

    public String getName() {
        // Version names are not shown to the public
        return this.decoratedClass.getName();
    }

    /**
     * Returns the class loader for the class. Some implementations may use
     * <p>
     * null to represent the bootstrap class loader. This method will return
     * <p>
     * null in such implementations if this class was loaded by the bootstrap
     * <p>
     * class loader.
     * <p>
     * <p>
     * <p>
     * <p>
     * If a security manager is present, and the caller's class loader is
     * <p>
     * not null and the caller's class loader is not the same as or an ancestor of
     * <p>
     * the class loader for the class whose class loader is requested, then
     * <p>
     * this method calls the security manager's {@code checkPermission}
     * <p>
     * method with a {@code RuntimePermission("getClassLoader")}
     * <p>
     * permission to ensure it's ok to access the class loader for the class.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object
     * <p>
     * represents a primitive type or void, null is returned.
     *
     * @return the class loader that loaded the class or interface
     * <p>
     * represented by this object.
     * @throws SecurityException if a security manager exists and its
     *                           <p>
     *                           {@code checkPermission} method denies
     *                           <p>
     *                           access to the class loader for the class.
     * @see java.lang.ClassLoader
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */

    @CallerSensitive
    public ClassLoader getClassLoader() {
        java.lang.Class<?> lastVersion = getLastVersion();
        return lastVersion.getClassLoader();
    }

    /**
     * Returns an array of {@code TypeVariable} objects that represent the
     * <p>
     * type variables declared by the generic declaration represented by this
     * <p>
     * {@code GenericDeclaration} object, in declaration order. Returns an
     * <p>
     * array of length 0 if the underlying generic declaration declares no type
     * <p>
     * variables.
     *
     * @return an array of {@code TypeVariable} objects that represent
     * <p>
     * the type variables declared by this generic declaration
     * @throws java.lang.reflect.GenericSignatureFormatError if the generic
     *                                                       <p>
     *                                                       signature of this generic declaration does not conform to
     *                                                       <p>
     *                                                       the format specified in
     *                                                       <p>
     *                                                       <cite>The Java&trade; Virtual Machine Specification</cite>
     * @since 1.5
     */
    public TypeVariable<?>[] getTypeParameters() {
        java.lang.Class<?> lastVersion = getLastVersion();
        return lastVersion.getTypeParameters();
    }

    /**
     * Returns the {@code Class} representing the superclass of the entity
     * <p>
     * (class, interface, primitive type or void) represented by this
     * <p>
     * {@code Class}. If this {@code Class} represents either the
     * <p>
     * {@code Object} class, an interface, a primitive type, or void, then
     * <p>
     * null is returned. If this object represents an array class then the
     * <p>
     * {@code Class} object representing the {@code Object} class is
     * <p>
     * returned.
     *
     * @return the superclass of the class represented by this object.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<? super T> getSuperclass() {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.Class<?> cl = lastVersion.getSuperclass();

        if (cl == null)
            return null;
        return new Class(cl);
    }

    /**
     * Returns the {@code Type} representing the direct superclass of
     * <p>
     * the entity (class, interface, primitive type or void) represented by
     * <p>
     * this {@code Class}.
     * <p>
     * <p>
     * <p>
     * <p>
     * If the superclass is a parameterized type, the {@code Type}
     * <p>
     * object returned must accurately reflect the actual type
     * <p>
     * parameters used in the source code. The parameterized type
     * <p>
     * representing the superclass is created if it had not been
     * <p>
     * created before. See the declaration of {@link
     * <p>
     * java.lang.reflect.ParameterizedType ParameterizedType} for the
     * <p>
     * semantics of the creation process for parameterized types. If
     * <p>
     * this {@code Class} represents either the {@code Object}
     * <p>
     * class, an interface, a primitive type, or void, then null is
     * <p>
     * returned. If this object represents an array class then the
     * <p>
     * {@code Class} object representing the {@code Object} class is
     * <p>
     * returned.
     *
     * @return the superclass of the class represented by this object
     * @throws java.lang.reflect.GenericSignatureFormatError         if the generic
     *                                                               <p>
     *                                                               class signature does not conform to the format specified in
     *                                                               <p>
     *                                                               <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException                               if the generic superclass
     *                                                               <p>
     *                                                               refers to a non-existent type declaration
     * @throws java.lang.reflect.MalformedParameterizedTypeException if the
     *                                                               <p>
     *                                                               generic superclass refers to a parameterized type that cannot be
     *                                                               <p>
     *                                                               instantiated for any reason
     * @since 1.5
     */

    public Type getGenericSuperclass() {
        return this.decoratedClass.getGenericSuperclass();
    }

    /**
     * Gets the package for this class. The class loader of this class is used
     * <p>
     * to find the package. If the class was loaded by the bootstrap class
     * <p>
     * loader the set of packages loaded from CLASSPATH is searched to find the
     * <p>
     * package of the class. Null is returned if no package object was created
     * <p>
     * by the class loader of this class.
     * <p>
     * <p>
     * <p>
     * <p>
     * Packages have attributes for versions and specifications only if the
     * <p>
     * information was defined in the manifests that accompany the classes, and
     * <p>
     * if the class loader created the package instance with the attributes
     * <p>
     * from the manifest.
     *
     * @return the package of the class, or null if no package
     * <p>
     * information is available from the archive or codebase.
     */

    public Package getPackage() {
        return this.decoratedClass.getPackage();
    }

    /**
     * Determines the interfaces implemented by the class or interface
     * <p>
     * represented by this object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents a class, the return value is an array
     * <p>
     * containing objects representing all interfaces implemented by the
     * <p>
     * class. The order of the interface objects in the array corresponds to
     * <p>
     * the order of the interface names in the {@code implements} clause
     * <p>
     * of the declaration of the class represented by this object. For
     * <p>
     * example, given the declaration:
     * <p>
     * <blockquote>
     * <p>
     * {@code class Shimmer implements FloorWax, DessertTopping { ... }}
     * <p>
     * </blockquote>
     * <p>
     * suppose the value of {@code s} is an instance of
     * <p>
     * {@code Shimmer}; the value of the expression:
     * <p>
     * <blockquote>
     * <p>
     * {@code s.getClass().getInterfaces()[0]}
     * <p>
     * </blockquote>
     * <p>
     * is the {@code Class} object that represents interface
     * <p>
     * {@code FloorWax}; and the value of:
     * <p>
     * <blockquote>
     * <p>
     * {@code s.getClass().getInterfaces()[1]}
     * <p>
     * </blockquote>
     * <p>
     * is the {@code Class} object that represents interface
     * <p>
     * {@code DessertTopping}.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents an interface, the array contains objects
     * <p>
     * representing all interfaces extended by the interface. The order of the
     * <p>
     * interface objects in the array corresponds to the order of the interface
     * <p>
     * names in the {@code extends} clause of the declaration of the
     * <p>
     * interface represented by this object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents a class or interface that implements no
     * <p>
     * interfaces, the method returns an array of length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents a primitive type or void, the method
     * <p>
     * returns an array of length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, the
     * <p>
     * interfaces {@code Cloneable} and {@code java.io.Serializable} are
     * <p>
     * returned in that order.
     *
     * @return an array of interfaces implemented by this class.
     */

    public Class<?>[] getInterfaces() {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.Class<?>[] ints = lastVersion.getInterfaces();
        return IntrospectionUtils.decorateClassList(ints);
    }

    /**
     * Returns the {@code Type}s representing the interfaces
     * <p>
     * directly implemented by the class or interface represented by
     * <p>
     * this object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If a superinterface is a parameterized type, the
     * <p>
     * {@code Type} object returned for it must accurately reflect
     * <p>
     * the actual type parameters used in the source code. The
     * <p>
     * parameterized type representing each superinterface is created
     * <p>
     * if it had not been created before. See the declaration of
     * <p>
     * {@link java.lang.reflect.ParameterizedType ParameterizedType}
     * <p>
     * for the semantics of the creation process for parameterized
     * <p>
     * types.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents a class, the return value is an
     * <p>
     * array containing objects representing all interfaces
     * <p>
     * implemented by the class. The order of the interface objects in
     * <p>
     * the array corresponds to the order of the interface names in
     * <p>
     * the {@code implements} clause of the declaration of the class
     * <p>
     * represented by this object. In the case of an array class, the
     * <p>
     * interfaces {@code Cloneable} and {@code Serializable} are
     * <p>
     * returned in that order.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents an interface, the array contains
     * <p>
     * objects representing all interfaces directly extended by the
     * <p>
     * interface. The order of the interface objects in the array
     * <p>
     * corresponds to the order of the interface names in the
     * <p>
     * {@code extends} clause of the declaration of the interface
     * <p>
     * represented by this object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents a class or interface that
     * <p>
     * implements no interfaces, the method returns an array of length
     * <p>
     * 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this object represents a primitive type or void, the
     * <p>
     * method returns an array of length 0.
     *
     * @return an array of interfaces implemented by this class
     * @throws java.lang.reflect.GenericSignatureFormatError         if the generic class signature does not conform to the format
     *                                                               <p>
     *                                                               specified in
     *                                                               <p>
     *                                                               <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException                               if any of the generic
     *                                                               <p>
     *                                                               superinterfaces refers to a non-existent type declaration
     * @throws java.lang.reflect.MalformedParameterizedTypeException if any of the generic superinterfaces refer to a parameterized
     *                                                               <p>
     *                                                               type that cannot be instantiated for any reason
     * @since 1.5
     */

    public Type[] getGenericInterfaces() {
        java.lang.Class<?> lastVersion = getLastVersion();
        return lastVersion.getGenericInterfaces();
    }

    /**
     * Returns the {@code Class} representing the component type of an
     * <p>
     * array. If this class does not represent an array class this method
     * <p>
     * returns null.
     *
     * @return the {@code Class} representing the component type of this
     * <p>
     * class if this class is an array
     * @see java.lang.reflect.Array
     * @since JDK1.1
     */

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Class<?> getComponentType() {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.Class<?> cl = lastVersion.getComponentType();
        if (cl == null)
            return null;
        return new Class(cl);
    }

    /**
     * Returns the Java language modifiers for this class or interface, encoded
     * <p>
     * in an integer. The modifiers consist of the Java Virtual Machine's
     * <p>
     * constants for {@code public}, {@code protected},
     * <p>
     * {@code private}, {@code final}, {@code static},
     * <p>
     * {@code abstract} and {@code interface}; they should be decoded
     * <p>
     * using the methods of class {@code Modifier}.
     * <p>
     * <p>
     * <p>
     * <p>
     * If the underlying class is an array class, then its
     * <p>
     * {@code public}, {@code private} and {@code protected}
     * <p>
     * modifiers are the same as those of its component type. If this
     * <p>
     * {@code Class} represents a primitive type or void, its
     * <p>
     * {@code public} modifier is always {@code true}, and its
     * <p>
     * {@code protected} and {@code private} modifiers are always
     * <p>
     * {@code false}. If this object represents an array class, a
     * <p>
     * primitive type or void, then its {@code final} modifier is always
     * <p>
     * {@code true} and its interface modifier is always
     * <p>
     * {@code false}. The values of its other modifiers are not determined
     * <p>
     * by this specification.
     * <p>
     * <p>
     * <p>
     * <p>
     * The modifier encodings are defined in <em>The Java Virtual Machine
     * <p>
     * Specification</em>, table 4.1.
     *
     * @return the {@code int} representing the modifiers for this class
     * @see java.lang.reflect.Modifier
     * @since JDK1.1
     */

    public int getModifiers() {
        java.lang.Class<?> lastVersion = getLastVersion();
        return lastVersion.getModifiers();
    }

    /**
     * Gets the signers of this class.
     *
     * @return the signers of this class, or null if there are no signers. In
     * <p>
     * particular, this method returns null if this object represents
     * <p>
     * a primitive type or void.
     * @since JDK1.1
     */
    public Object[] getSigners() {
        java.lang.Class<?> lastVersion = getLastVersion();
        return lastVersion.getSigners();
    }

    /**
     * If this {@code Class} object represents a local or anonymous
     * <p>
     * class within a method, returns a {@link
     * <p>
     * java.lang.reflect.Method Method} object representing the
     * <p>
     * immediately enclosing method of the underlying class. Returns
     * <p>
     * {@code null} otherwise.
     * <p>
     * <p>
     * <p>
     * In particular, this method returns {@code null} if the underlying
     * <p>
     * class is a local or anonymous class immediately enclosed by a type
     * <p>
     * declaration, instance initializer or static initializer.
     *
     * @return the immediately enclosing method of the underlying class, if
     * <p>
     * that class is a local or anonymous class; otherwise {@code null}.
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     *                           <p>
     *                           following conditions is met:
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <ul>
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as the
     *                           <p>
     *                           class loader of the enclosing class and invocation of
     *                           <p>
     *                           {@link SecurityManager#checkPermission
     *                           <p>
     *                           s.checkPermission} method with
     *                           <p>
     *                           {@code RuntimePermission("accessDeclaredMembers")}
     *                           <p>
     *                           denies access to the methods within the enclosing class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the enclosing class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of the enclosing class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           </ul>
     * @since 1.5
     */

    @CallerSensitive
    public Method getEnclosingMethod() throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.reflect.Method method = lastVersion.getEnclosingMethod();

        if (method == null)
            return null;
        return new Method(method);
    }

    /**
     * If this {@code Class} object represents a local or anonymous
     * <p>
     * class within a constructor, returns a {@link
     * <p>
     * java.lang.reflect.Constructor Constructor} object representing
     * <p>
     * the immediately enclosing constructor of the underlying
     * <p>
     * class. Returns {@code null} otherwise. In particular, this
     * <p>
     * method returns {@code null} if the underlying class is a local
     * <p>
     * or anonymous class immediately enclosed by a type declaration,
     * <p>
     * instance initializer or static initializer.
     *
     * @return the immediately enclosing constructor of the underlying class, if
     * <p>
     * that class is a local or anonymous class; otherwise {@code null}.
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     *                           <p>
     *                           following conditions is met:
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <ul>
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as the
     *                           <p>
     *                           class loader of the enclosing class and invocation of
     *                           <p>
     *                           {@link SecurityManager#checkPermission
     *                           <p>
     *                           s.checkPermission} method with
     *                           <p>
     *                           {@code RuntimePermission("accessDeclaredMembers")}
     *                           <p>
     *                           denies access to the constructors within the enclosing class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the enclosing class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of the enclosing class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           </ul>
     * @since 1.5
     */

    @SuppressWarnings({"rawtypes", "unchecked"})
    @CallerSensitive
    public Constructor<?> getEnclosingConstructor() throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.reflect.Constructor method = lastVersion.getEnclosingConstructor();

        if (method == null)
            return null;
        return new Constructor(method);

    }

    /**
     * If the class or interface represented by this {@code Class} object
     * <p>
     * is a member of another class, returns the {@code Class} object
     * <p>
     * representing the class in which it was declared. This method returns
     * <p>
     * null if this class or interface is not a member of any other class. If
     * <p>
     * this {@code Class} object represents an array class, a primitive
     * <p>
     * type, or void,then this method returns null.
     *
     * @return the declaring class for this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and the caller's
     *                           <p>
     *                           class loader is not the same as or an ancestor of the class
     *                           <p>
     *                           loader for the declaring class and invocation of {@link
     *                           <p>
     *                           SecurityManager#checkPackageAccess s.checkPackageAccess()}
     *                           <p>
     *                           denies access to the package of the declaring class
     * @since JDK1.1
     */

    @SuppressWarnings({"unchecked", "rawtypes"})
    @CallerSensitive
    public Class<?> getDeclaringClass() throws SecurityException {
        java.lang.Class<?> cl = this.decoratedClass.getDeclaringClass();
        if (cl == null)
            return null;
        return new Class(cl);
    }

    /**
     * Returns the immediately enclosing class of the underlying
     * <p>
     * class. If the underlying class is a top level class this
     * <p>
     * method returns {@code null}.
     *
     * @return the immediately enclosing class of the underlying class
     * @throws SecurityException If a security manager, <i>s</i>, is present and the caller's
     *                           <p>
     *                           class loader is not the same as or an ancestor of the class
     *                           <p>
     *                           loader for the enclosing class and invocation of {@link
     *                           <p>
     *                           SecurityManager#checkPackageAccess s.checkPackageAccess()}
     *                           <p>
     *                           denies access to the package of the enclosing class
     * @since 1.5
     */

    @SuppressWarnings({"unchecked", "rawtypes"})
    @CallerSensitive
    public Class<?> getEnclosingClass() throws SecurityException {
        java.lang.Class<?> cl = this.decoratedClass.getEnclosingClass();
        if (cl == null)
            return null;
        return new Class(cl);
    }

    /**
     * Returns the simple name of the underlying class as given in the
     * <p>
     * source code. Returns an empty string if the underlying class is
     * <p>
     * anonymous.
     * <p>
     * <p>
     * <p>
     * <p>
     * The simple name of an array is the simple name of the
     * <p>
     * component type with "[]" appended. In particular the simple
     * <p>
     * name of an array whose component type is anonymous is "[]".
     *
     * @return the simple name of the underlying class
     * @since 1.5
     */

    public String getSimpleName() {
        return this.decoratedClass.getSimpleName();
    }

    /**
     * Return an informative string for the name of this type.
     *
     * @return an informative string for the name of this type
     * @since 1.8
     */

    public String getTypeName() {
        return this.decoratedClass.getTypeName();
    }

    /**
     * Returns the canonical name of the underlying class as
     * <p>
     * defined by the Java Language Specification. Returns null if
     * <p>
     * the underlying class does not have a canonical name (i.e., if
     * <p>
     * it is a local or anonymous class or an array whose component
     * <p>
     * type does not have a canonical name).
     *
     * @return the canonical name of the underlying class if it exists, and
     * <p>
     * {@code null} otherwise.
     * @since 1.5
     */

    public String getCanonicalName() {
        return this.decoratedClass.getCanonicalName();
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * <p>
     * is an anonymous class.
     *
     * @return {@code true} if and only if this class is an anonymous class.
     * @since 1.5
     */

    public boolean isAnonymousClass() {
        return this.decoratedClass.isAnonymousClass();
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * <p>
     * is a local class.
     *
     * @return {@code true} if and only if this class is a local class.
     * @since 1.5
     */

    public boolean isLocalClass() {
        return this.decoratedClass.isLocalClass();
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * <p>
     * is a member class.
     *
     * @return {@code true} if and only if this class is a member class.
     * @since 1.5
     */

    public boolean isMemberClass() {
        return this.decoratedClass.isMemberClass();
    }

    /**
     * Returns an array containing {@code Class} objects representing all
     * <p>
     * the public classes and interfaces that are members of the class
     * <p>
     * represented by this {@code Class} object. This includes public
     * <p>
     * class and interface members inherited from superclasses and public class
     * <p>
     * and interface members declared by the class. This method returns an
     * <p>
     * array of length 0 if this {@code Class} object has no public member
     * <p>
     * classes or interfaces. This method also returns an array of length 0 if
     * <p>
     * this {@code Class} object represents a primitive type, an array
     * <p>
     * class, or void.
     *
     * @return the array of {@code Class} objects representing the public
     * <p>
     * members of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and
     *                           <p>
     *                           the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class.
     * @since JDK1.1
     */

    @CallerSensitive
    public Class<?>[] getClasses() {
        java.lang.Class<?>[] classes = this.decoratedClass.getClasses();
        if (classes == null)
            return null;
        return IntrospectionUtils.decorateClassList(classes);
    }

    /**
     * Returns an array containing {@code Field} objects reflecting all
     * <p>
     * the accessible public fields of the class or interface represented by
     * <p>
     * this {@code Class} object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a class or interface with no
     * <p>
     * no accessible public fields, then this method returns an array of length
     * <p>
     * 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a class, then this method
     * <p>
     * returns the public fields of the class and of all its superclasses.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an interface, then this
     * <p>
     * method returns the fields of the interface and of all its
     * <p>
     * superinterfaces.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, a primitive
     * <p>
     * type, or void, then this method returns an array of length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * The elements in the returned array are not sorted and are not in any
     * <p>
     * particular order.
     *
     * @return the array of {@code Field} objects representing the
     * <p>
     * public fields
     * @throws SecurityException If a security manager, <i>s</i>, is present and
     *                           <p>
     *                           the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class.
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     * @since JDK1.1
     */

    @CallerSensitive
    public Field[] getFields() throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        Field[] fields = IntrospectionUtils.decorateFieldList(lastVersion.getFields());

        return IntrospectionUtils.filterJMPLibFields(fields);
    }

    /**
     * Returns an array containing {@code Method} objects reflecting all the
     * <p>
     * public methods of the class or interface represented by this {@code
     *
     * <p>
     * Class} object, including those declared by the class or interface and
     * <p>
     * those inherited from superclasses and superinterfaces.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a type that has multiple
     * <p>
     * public methods with the same name and parameter types, but different
     * <p>
     * return types, then the returned array has a {@code Method} object for
     * <p>
     * each such method.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a type with a class
     * <p>
     * initialization method {@code <clinit>}, then the returned array does
     * <p>
     * <em>not</em> have a corresponding {@code Method} object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, then the
     * <p>
     * returned array has a {@code Method} object for each of the public
     * <p>
     * methods inherited by the array type from {@code Object}. It does not
     * <p>
     * contain a {@code Method} object for {@code clone()}.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an interface then the
     * <p>
     * returned array does not contain any implicitly declared methods from
     * <p>
     * {@code Object}. Therefore, if no methods are explicitly declared in
     * <p>
     * this interface or any of its superinterfaces then the returned array
     * <p>
     * has length 0. (Note that a {@code Class} object which represents a class
     * <p>
     * always has public methods, inherited from {@code Object}.)
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a primitive type or void,
     * <p>
     * then the returned array has length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * Static methods declared in superinterfaces of the class or interface
     * <p>
     * represented by this {@code Class} object are not considered members of
     * <p>
     * the class or interface.
     * <p>
     * <p>
     * <p>
     * <p>
     * The elements in the returned array are not sorted and are not in any
     * <p>
     * particular order.
     *
     * @return the array of {@code Method} objects representing the
     * <p>
     * public methods of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and
     *                           <p>
     *                           the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class.
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */

    @CallerSensitive
    public Method[] getMethods() throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.reflect.Method[] mets = lastVersion.getMethods();

        /*
         * System.out.println("UNFILTERED:"); for (java.lang.reflect.Method m: mets)
         * System.out.println(m);
         */

        java.lang.reflect.Method[] filteredMets = IntrospectionUtils.filterJMPLibMethods(mets);

        /*
         * System.out.println("FILTERED:"); for (java.lang.reflect.Method m:
         * filteredMets) System.out.println(m);
         */

        return IntrospectionUtils.decorateMethodList(filteredMets);
    }

    /**
     * Returns an array containing {@code Constructor} objects reflecting
     * <p>
     * all the public constructors of the class represented by this
     * <p>
     * {@code Class} object. An array of length 0 is returned if the
     * <p>
     * class has no public constructors, or if the class is an array class, or
     * <p>
     * if the class reflects a primitive type or void.
     * <p>
     * <p>
     * <p>
     * Note that while this method returns an array of {@code
     *
     * <p>
     * Constructor<T>} objects (that is an array of constructors from
     * <p>
     * this class), the return type of this method is {@code
     *
     * <p>
     * Constructor<?>[]} and <em>not</em> {@code Constructor<T>[]} as
     * <p>
     * might be expected. This less informative return type is
     * <p>
     * necessary since after being returned from this method, the
     * <p>
     * array could be modified to hold {@code Constructor} objects for
     * <p>
     * different classes, which would violate the type guarantees of
     * <p>
     * {@code Constructor<T>[]}.
     *
     * @return the array of {@code Constructor} objects representing the
     * <p>
     * public constructors of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and
     *                           <p>
     *                           the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class.
     * @since JDK1.1
     */

    @CallerSensitive
    public Constructor<?>[] getConstructors() throws SecurityException {
        return IntrospectionUtils.decorateConstructorList(this.decoratedClass.getConstructors());
    }

    /**
     * Returns a {@code Field} object that reflects the specified public member
     * <p>
     * field of the class or interface represented by this {@code Class}
     * <p>
     * object. The {@code name} parameter is a {@code String} specifying the
     * <p>
     * simple name of the desired field.
     * <p>
     * <p>
     * <p>
     * <p>
     * The field to be reflected is determined by the algorithm that
     * <p>
     * follows. Let C be the class or interface represented by this object:
     * <p>
     * <p>
     * <p>
     * <OL>
     * <p>
     * <LI>If C declares a public field with the name specified, that is the
     * <p>
     * field to be reflected.</LI>
     * <p>
     * <LI>If no field was found in step 1 above, this algorithm is applied
     * <p>
     * recursively to each direct superinterface of C. The direct
     * <p>
     * superinterfaces are searched in the order they were declared.</LI>
     * <p>
     * <LI>If no field was found in steps 1 and 2 above, and C has a
     * <p>
     * superclass S, then this algorithm is invoked recursively upon S.
     * <p>
     * If C has no superclass, then a {@code NoSuchFieldException}
     * <p>
     * is thrown.</LI>
     * <p>
     * </OL>
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, then this
     * <p>
     * method does not find the {@code length} field of the array type.
     *
     * @param name the field name
     * @return the {@code Field} object of this class specified by
     * <p>
     * {@code name}
     * @throws NoSuchFieldException if a field with the specified name is
     *                              <p>
     *                              not found.
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException    If a security manager, <i>s</i>, is present and
     *                              <p>
     *                              the caller's class loader is not the same as or an
     *                              <p>
     *                              ancestor of the class loader for the current class and
     *                              <p>
     *                              invocation of {@link SecurityManager#checkPackageAccess
     *                              <p>
     *                              s.checkPackageAccess()} denies access to the package
     *                              <p>
     *                              of this class.
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     * @since JDK1.1
     */

    @CallerSensitive
    public Field getField(String name) throws NoSuchFieldException, SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        Field ret = new Field(lastVersion.getField(name));

        if (IntrospectionUtils.isJmpLibAddedField(ret))
            throw new NoSuchFieldException(name);

        return ret;
    }

    /**
     * Returns a {@code Constructor} object that reflects the specified
     * <p>
     * public constructor of the class represented by this {@code Class}
     * <p>
     * object. The {@code parameterTypes} parameter is an array of
     * <p>
     * {@code Class} objects that identify the constructor's formal
     * <p>
     * parameter types, in declared order.
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an inner class
     * <p>
     * declared in a non-static context, the formal parameter types
     * <p>
     * include the explicit enclosing instance as the first parameter.
     * <p>
     * <p>
     * <p>
     * <p>
     * The constructor to reflect is the public constructor of the class
     * <p>
     * represented by this {@code Class} object whose formal parameter
     * <p>
     * types match those specified by {@code parameterTypes}.
     *
     * @param parameterTypes the parameter array
     * @return the {@code Constructor} object of the public constructor that
     * <p>
     * matches the specified {@code parameterTypes}
     * @throws NoSuchMethodException if a matching method is not found.
     * @throws SecurityException     If a security manager, <i>s</i>, is present and
     *                               <p>
     *                               the caller's class loader is not the same as or an
     *                               <p>
     *                               ancestor of the class loader for the current class and
     *                               <p>
     *                               invocation of {@link SecurityManager#checkPackageAccess
     *                               <p>
     *                               s.checkPackageAccess()} denies access to the package
     *                               <p>
     *                               of this class.
     * @since JDK1.1
     */

    @CallerSensitive
    public java.lang.reflect.Constructor<?> getConstructor(java.lang.Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        return this.decoratedClass.getConstructor(parameterTypes);
    }

    @CallerSensitive
    public Constructor<?> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        return new Constructor<>(
                this.decoratedClass.getConstructor(IntrospectionUtils.undecorateClassList(parameterTypes)));
    }

    /**
     * Returns an array of {@code Class} objects reflecting all the
     * <p>
     * classes and interfaces declared as members of the class represented by
     * <p>
     * this {@code Class} object. This includes public, protected, default
     * <p>
     * (package) access, and private classes and interfaces declared by the
     * <p>
     * class, but excludes inherited classes and interfaces. This method
     * <p>
     * returns an array of length 0 if the class declares no classes or
     * <p>
     * interfaces as members, or if this {@code Class} object represents a
     * <p>
     * primitive type, an array class, or void.
     *
     * @return the array of {@code Class} objects representing all the
     * <p>
     * declared members of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     *                           <p>
     *                           following conditions is met:
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <ul>
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as the
     *                           <p>
     *                           class loader of this class and invocation of
     *                           <p>
     *                           {@link SecurityManager#checkPermission
     *                           <p>
     *                           s.checkPermission} method with
     *                           <p>
     *                           {@code RuntimePermission("accessDeclaredMembers")}
     *                           <p>
     *                           denies access to the declared classes within this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           </ul>
     * @since JDK1.1
     */

    @CallerSensitive
    public Class<?>[] getDeclaredClasses() throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.Class<?>[] cls = lastVersion.getDeclaredClasses();
        if (cls == null)
            return null;
        return IntrospectionUtils.decorateClassList(lastVersion.getDeclaredClasses());
    }

    /**
     * Returns an array of {@code Field} objects reflecting all the fields
     * <p>
     * declared by the class or interface represented by this
     * <p>
     * {@code Class} object. This includes public, protected, default
     * <p>
     * (package) access, and private fields, but excludes inherited fields.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a class or interface with no
     * <p>
     * declared fields, then this method returns an array of length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, a primitive
     * <p>
     * type, or void, then this method returns an array of length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * The elements in the returned array are not sorted and are not in any
     * <p>
     * particular order.
     *
     * @return the array of {@code Field} objects representing all the
     * <p>
     * declared fields of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     *                           <p>
     *                           following conditions is met:
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <ul>
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as the
     *                           <p>
     *                           class loader of this class and invocation of
     *                           <p>
     *                           {@link SecurityManager#checkPermission
     *                           <p>
     *                           s.checkPermission} method with
     *                           <p>
     *                           {@code RuntimePermission("accessDeclaredMembers")}
     *                           <p>
     *                           denies access to the declared fields within this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           </ul>
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     * @since JDK1.1
     */

    @CallerSensitive
    public Field[] getDeclaredFields() throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        return IntrospectionUtils
                .filterJMPLibFields(IntrospectionUtils.decorateFieldList(lastVersion.getDeclaredFields()));
    }

    /**
     * Returns an array containing {@code Method} objects reflecting all the
     * <p>
     * declared methods of the class or interface represented by this {@code
     *
     * <p>
     * Class} object, including public, protected, default (package)
     * <p>
     * access, and private methods, but excluding inherited methods.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a type that has multiple
     * <p>
     * declared methods with the same name and parameter types, but different
     * <p>
     * return types, then the returned array has a {@code Method} object for
     * <p>
     * each such method.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a type that has a class
     * <p>
     * initialization method {@code <clinit>}, then the returned array does
     * <p>
     * <em>not</em> have a corresponding {@code Method} object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a class or interface with no
     * <p>
     * declared methods, then the returned array has length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, a primitive
     * <p>
     * type, or void, then the returned array has length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * The elements in the returned array are not sorted and are not in any
     * <p>
     * particular order.
     *
     * @return the array of {@code Method} objects representing all the
     * <p>
     * declared methods of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     *                           <p>
     *                           following conditions is met:
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <ul>
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as the
     *                           <p>
     *                           class loader of this class and invocation of
     *                           <p>
     *                           {@link SecurityManager#checkPermission
     *                           <p>
     *                           s.checkPermission} method with
     *                           <p>
     *                           {@code RuntimePermission("accessDeclaredMembers")}
     *                           <p>
     *                           denies access to the declared methods within this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           </ul>
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */

    @CallerSensitive
    public Method[] getDeclaredMethods() throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        return IntrospectionUtils
                .filterJMPLibMethods(IntrospectionUtils.decorateMethodList(lastVersion.getDeclaredMethods()));
    }

    /**
     * Returns an array of {@code Constructor} objects reflecting all the
     * <p>
     * constructors declared by the class represented by this
     * <p>
     * {@code Class} object. These are public, protected, default
     * <p>
     * (package) access, and private constructors. The elements in the array
     * <p>
     * returned are not sorted and are not in any particular order. If the
     * <p>
     * class has a default constructor, it is included in the returned array.
     * <p>
     * This method returns an array of length 0 if this {@code Class}
     * <p>
     * object represents an interface, a primitive type, an array class, or
     * <p>
     * void.
     * <p>
     * <p>
     * <p>
     * <p>
     * See <em>The Java Language Specification</em>, section 8.2.
     *
     * @return the array of {@code Constructor} objects representing all the
     * <p>
     * declared constructors of this class
     * @throws SecurityException If a security manager, <i>s</i>, is present and any of the
     *                           <p>
     *                           following conditions is met:
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <ul>
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as the
     *                           <p>
     *                           class loader of this class and invocation of
     *                           <p>
     *                           {@link SecurityManager#checkPermission
     *                           <p>
     *                           s.checkPermission} method with
     *                           <p>
     *                           {@code RuntimePermission("accessDeclaredMembers")}
     *                           <p>
     *                           denies access to the declared constructors within this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           <li>the caller's class loader is not the same as or an
     *                           <p>
     *                           ancestor of the class loader for the current class and
     *                           <p>
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           <p>
     *                           s.checkPackageAccess()} denies access to the package
     *                           <p>
     *                           of this class
     *                           <p>
     *                           <p>
     *                           <p>
     *                           </ul>
     * @since JDK1.1
     */

    @CallerSensitive
    public Constructor<?>[] getDeclaredConstructors() throws SecurityException {
        return IntrospectionUtils.decorateConstructorList(this.decoratedClass.getDeclaredConstructors());
    }

    /**
     * Returns a {@code Field} object that reflects the specified declared
     * <p>
     * field of the class or interface represented by this {@code Class}
     * <p>
     * object. The {@code name} parameter is a {@code String} that specifies
     * <p>
     * the simple name of the desired field.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, then this
     * <p>
     * method does not find the {@code length} field of the array type.
     *
     * @param name the name of the field
     * @return the {@code Field} object for the specified field in this
     * <p>
     * class
     * @throws NoSuchFieldException if a field with the specified name is
     *                              <p>
     *                              not found.
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException    If a security manager, <i>s</i>, is present and any of the
     *                              <p>
     *                              following conditions is met:
     *                              <p>
     *                              <p>
     *                              <p>
     *                              <ul>
     *                              <p>
     *                              <p>
     *                              <p>
     *                              <li>the caller's class loader is not the same as the
     *                              <p>
     *                              class loader of this class and invocation of
     *                              <p>
     *                              {@link SecurityManager#checkPermission
     *                              <p>
     *                              s.checkPermission} method with
     *                              <p>
     *                              {@code RuntimePermission("accessDeclaredMembers")}
     *                              <p>
     *                              denies access to the declared field
     *                              <p>
     *                              <p>
     *                              <p>
     *                              <li>the caller's class loader is not the same as or an
     *                              <p>
     *                              ancestor of the class loader for the current class and
     *                              <p>
     *                              invocation of {@link SecurityManager#checkPackageAccess
     *                              <p>
     *                              s.checkPackageAccess()} denies access to the package
     *                              <p>
     *                              of this class
     *                              <p>
     *                              <p>
     *                              <p>
     *                              </ul>
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     * @since JDK1.1
     */

    @CallerSensitive
    public Field getDeclaredField(String name) throws NoSuchFieldException, SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        return new Field(lastVersion.getDeclaredField(name));
    }

    /**
     * Returns a {@code Method} object that reflects the specified
     * <p>
     * declared method of the class or interface represented by this
     * <p>
     * {@code Class} object. The {@code name} parameter is a
     * <p>
     * {@code String} that specifies the simple name of the desired
     * <p>
     * method, and the {@code parameterTypes} parameter is an array of
     * <p>
     * {@code Class} objects that identify the method's formal parameter
     * <p>
     * types, in declared order. If more than one method with the same
     * <p>
     * parameter types is declared in a class, and one of these methods has a
     * <p>
     * return type that is more specific than any of the others, that method is
     * <p>
     * returned; otherwise one of the methods is chosen arbitrarily. If the
     * <p>
     * name is "&lt;init&gt;"or "&lt;clinit&gt;" a {@code NoSuchMethodException}
     * <p>
     * is raised.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an array type, then this
     * <p>
     * method does not find the {@code clone()} method.
     *
     * @param name           the name of the method
     * @param parameterTypes the parameter array
     * @return the {@code Method} object for the method of this class
     * <p>
     * matching the specified name and parameters
     * @throws NoSuchMethodException if a matching method is not found.
     * @throws NullPointerException  if {@code name} is {@code null}
     * @throws SecurityException     If a security manager, <i>s</i>, is present and any of the
     *                               <p>
     *                               following conditions is met:
     *                               <p>
     *                               <p>
     *                               <p>
     *                               <ul>
     *                               <p>
     *                               <p>
     *                               <p>
     *                               <li>the caller's class loader is not the same as the
     *                               <p>
     *                               class loader of this class and invocation of
     *                               <p>
     *                               {@link SecurityManager#checkPermission
     *                               <p>
     *                               s.checkPermission} method with
     *                               <p>
     *                               {@code RuntimePermission("accessDeclaredMembers")}
     *                               <p>
     *                               denies access to the declared method
     *                               <p>
     *                               <p>
     *                               <p>
     *                               <li>the caller's class loader is not the same as or an
     *                               <p>
     *                               ancestor of the class loader for the current class and
     *                               <p>
     *                               invocation of {@link SecurityManager#checkPackageAccess
     *                               <p>
     *                               s.checkPackageAccess()} denies access to the package
     *                               <p>
     *                               of this class
     *                               <p>
     *                               <p>
     *                               <p>
     *                               </ul>
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */

    @CallerSensitive
    public Method getDeclaredMethod(String name, java.lang.Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        return new Method(lastVersion.getDeclaredMethod(name, parameterTypes));
    }

    /**
     * Returns a {@code Constructor} object that reflects the specified
     * <p>
     * constructor of the class or interface represented by this
     * <p>
     * {@code Class} object. The {@code parameterTypes} parameter is
     * <p>
     * an array of {@code Class} objects that identify the constructor's
     * <p>
     * formal parameter types, in declared order.
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an inner class
     * <p>
     * declared in a non-static context, the formal parameter types
     * <p>
     * include the explicit enclosing instance as the first parameter.
     *
     * @param parameterTypes the parameter array
     * @return The {@code Constructor} object for the constructor with the
     * <p>
     * specified parameter list
     * @throws NoSuchMethodException if a matching method is not found.
     * @throws SecurityException     If a security manager, <i>s</i>, is present and any of the
     *                               <p>
     *                               following conditions is met:
     *                               <p>
     *                               <p>
     *                               <p>
     *                               <ul>
     *                               <p>
     *                               <p>
     *                               <p>
     *                               <li>the caller's class loader is not the same as the
     *                               <p>
     *                               class loader of this class and invocation of
     *                               <p>
     *                               {@link SecurityManager#checkPermission
     *                               <p>
     *                               s.checkPermission} method with
     *                               <p>
     *                               {@code RuntimePermission("accessDeclaredMembers")}
     *                               <p>
     *                               denies access to the declared constructor
     *                               <p>
     *                               <p>
     *                               <p>
     *                               <li>the caller's class loader is not the same as or an
     *                               <p>
     *                               ancestor of the class loader for the current class and
     *                               <p>
     *                               invocation of {@link SecurityManager#checkPackageAccess
     *                               <p>
     *                               s.checkPackageAccess()} denies access to the package
     *                               <p>
     *                               of this class
     *                               <p>
     *                               <p>
     *                               <p>
     *                               </ul>
     * @since JDK1.1
     */

    @SuppressWarnings({"rawtypes", "unchecked"})
    @CallerSensitive
    public java.lang.reflect.Constructor<T> getDeclaredConstructor(java.lang.Class... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        return (java.lang.reflect.Constructor<T>) this.decoratedClass.getDeclaredConstructor(parameterTypes);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes)
            throws NoSuchMethodException, SecurityException {
        return new Constructor(
                this.decoratedClass.getDeclaredConstructor(IntrospectionUtils.undecorateClassList(parameterTypes)));
    }

    /**
     * Finds a resource with a given name. The rules for searching resources
     * <p>
     * associated with a given class are implemented by the defining
     * <p>
     * {@linkplain ClassLoader class loader} of the class. This method
     * <p>
     * delegates to this object's class loader. If this object was loaded by
     * <p>
     * the bootstrap class loader, the method delegates to {@link
     * <p>
     * ClassLoader#getSystemResourceAsStream}.
     * <p>
     * <p>
     * <p>
     * <p>
     * Before delegation, an absolute resource name is constructed from the
     * <p>
     * given resource name using this algorithm:
     * <p>
     * <p>
     * <p>
     * <ul>
     * <p>
     * <p>
     * <p>
     * <li>If the {@code name} begins with a {@code '/'}
     * <p>
     * (<tt>'&#92;u002f'</tt>), then the absolute name of the resource is the
     * <p>
     * portion of the {@code name} following the {@code '/'}.
     * <p>
     * <p>
     * <p>
     * <li>Otherwise, the absolute name is of the following form:
     * <p>
     * <p>
     * <p>
     * <blockquote>
     * <p>
     * {@code modified_package_name/name}
     * <p>
     * </blockquote>
     * <p>
     * <p>
     * <p>
     * <p>
     * Where the {@code modified_package_name} is the package name of this
     * <p>
     * object with {@code '/'} substituted for {@code '.'}
     * <p>
     * (<tt>'&#92;u002e'</tt>).
     * <p>
     * <p>
     * <p>
     * </ul>
     *
     * @param name name of the desired resource
     * @return A {@link java.io.InputStream} object or {@code null} if
     * <p>
     * no resource with this name is found
     * @throws NullPointerException If {@code name} is {@code null}
     * @since JDK1.1
     */

    public InputStream getResourceAsStream(String name) {
        return this.decoratedClass.getResourceAsStream(name);
    }

    /**
     * Finds a resource with a given name. The rules for searching resources
     * <p>
     * associated with a given class are implemented by the defining
     * <p>
     * {@linkplain ClassLoader class loader} of the class. This method
     * <p>
     * delegates to this object's class loader. If this object was loaded by
     * <p>
     * the bootstrap class loader, the method delegates to {@link
     * <p>
     * ClassLoader#getSystemResource}.
     * <p>
     * <p>
     * <p>
     * <p>
     * Before delegation, an absolute resource name is constructed from the
     * <p>
     * given resource name using this algorithm:
     * <p>
     * <p>
     * <p>
     * <ul>
     * <p>
     * <p>
     * <p>
     * <li>If the {@code name} begins with a {@code '/'}
     * <p>
     * (<tt>'&#92;u002f'</tt>), then the absolute name of the resource is the
     * <p>
     * portion of the {@code name} following the {@code '/'}.
     * <p>
     * <p>
     * <p>
     * <li>Otherwise, the absolute name is of the following form:
     * <p>
     * <p>
     * <p>
     * <blockquote>
     * <p>
     * {@code modified_package_name/name}
     * <p>
     * </blockquote>
     * <p>
     * <p>
     * <p>
     * <p>
     * Where the {@code modified_package_name} is the package name of this
     * <p>
     * object with {@code '/'} substituted for {@code '.'}
     * <p>
     * (<tt>'&#92;u002e'</tt>).
     * <p>
     * <p>
     * <p>
     * </ul>
     *
     * @param name name of the desired resource
     * @return A {@link java.net.URL} object or {@code null} if no
     * <p>
     * resource with this name is found
     * @since JDK1.1
     */

    public java.net.URL getResource(String name) {
        return this.decoratedClass.getResource(name);
    }

    /**
     * Returns the {@code ProtectionDomain} of this class. If there is a
     * <p>
     * security manager installed, this method first calls the security
     * <p>
     * manager's {@code checkPermission} method with a
     * <p>
     * {@code RuntimePermission("getProtectionDomain")} permission to
     * <p>
     * ensure it's ok to get the
     * <p>
     * {@code ProtectionDomain}.
     *
     * @return the ProtectionDomain of this class
     * @throws SecurityException if a security manager exists and its
     *                           <p>
     *                           {@code checkPermission} method doesn't allow
     *                           <p>
     *                           getting the ProtectionDomain.
     * @see java.security.ProtectionDomain
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     * @since 1.2
     */

    public java.security.ProtectionDomain getProtectionDomain() {
        return this.decoratedClass.getProtectionDomain();
    }

    /**
     * Returns the assertion status that would be assigned to this
     * <p>
     * class if it were to be initialized at the time this method is invoked.
     * <p>
     * If this class has had its assertion status set, the most recent
     * <p>
     * setting will be returned; otherwise, if any package default assertion
     * <p>
     * status pertains to this class, the most recent setting for the most
     * <p>
     * specific pertinent package default assertion status is returned;
     * <p>
     * otherwise, if this class is not a system class (i.e., it has a
     * <p>
     * class loader) its class loader's default assertion status is returned;
     * <p>
     * otherwise, the system class default assertion status is returned.
     * <p>
     * <p>
     * <p>
     * Few programmers will have any need for this method; it is provided
     * <p>
     * for the benefit of the JRE itself. (It allows a class to determine at
     * <p>
     * the time that it is initialized whether assertions should be enabled.)
     * <p>
     * Note that this method is not guaranteed to return the actual
     * <p>
     * assertion status that was (or will be) associated with the specified
     * <p>
     * class when it was (or will be) initialized.
     *
     * @return the desired assertion status of the specified class.
     * @see java.lang.ClassLoader#setClassAssertionStatus
     * @see java.lang.ClassLoader#setPackageAssertionStatus
     * @see java.lang.ClassLoader#setDefaultAssertionStatus
     * @since 1.4
     */

    public boolean desiredAssertionStatus() {
        return this.decoratedClass.desiredAssertionStatus();
    }

    /**
     * Returns true if and only if this class was declared as an enum in the
     * <p>
     * source code.
     *
     * @return true if and only if this class was declared as an enum in the
     * <p>
     * source code
     * @since 1.5
     */

    public boolean isEnum() {
        return this.decoratedClass.isEnum();
    }

    /**
     * Returns the elements of this enum class or null if this
     * <p>
     * Class object does not represent an enum type.
     *
     * @return an array containing the values comprising the enum class
     * <p>
     * represented by this Class object in the order they're
     * <p>
     * declared, or null if this Class object does not
     * <p>
     * represent an enum type
     * @since 1.5
     */

    @SuppressWarnings("unchecked")
    public T[] getEnumConstants() {
        return (T[]) this.decoratedClass.getEnumConstants();
    }

    /**
     * Casts an object to the class or interface represented
     * <p>
     * by this {@code Class} object.
     *
     * @param obj the object to be cast
     * @return the object after casting, or null if obj is null
     * @throws ClassCastException if the object is not
     *                            <p>
     *                            null and is not assignable to the type T.
     * @since 1.5
     */

    @SuppressWarnings("unchecked")
    public T cast(Object obj) {
        try {
            Object lastVersion = IntrospectionUtils.getLatestObjectVersion(obj);

            // System.out.println("Last version: " + lastVersion);
            return (T) this.decoratedClass.cast(lastVersion);
        } catch (Exception ex) {
            Class<?> objClass = Introspector.getClass(obj);
            throw new ClassCastException("Cannot cast " + objClass.getName() + " to " + this.decoratedClass.getName());
        }
    }

    /**
     * Casts this {@code Class} object to represent a subclass of the class
     * <p>
     * represented by the specified class object. Checks that the cast
     * <p>
     * is valid, and throws a {@code ClassCastException} if it is not. If
     * <p>
     * this method succeeds, it always returns a reference to this class object.
     * <p>
     * <p>
     * <p>
     * <p>
     * This method is useful when a client needs to "narrow" the type of
     * <p>
     * a {@code Class} object to pass it to an API that restricts the
     * <p>
     * {@code Class} objects that it is willing to accept. A cast would
     * <p>
     * generate a compile-time warning, as the correctness of the cast
     * <p>
     * could not be checked at runtime (because generic types are implemented
     * <p>
     * by erasure).
     *
     * @param <U>   the type to cast this class object to
     * @param clazz the class of the type to cast this class object to
     * @return this {@code Class} object, cast to represent a subclass of
     * <p>
     * the specified class object.
     * @throws ClassCastException if this {@code Class} object does not
     *                            <p>
     *                            represent a subclass of the specified class (here "subclass"
     *                            includes
     *                            <p>
     *                            the class itself).
     * @since 1.5
     */
    public <U> java.lang.Class<? extends U> asSubclass(java.lang.Class<U> clazz) {
        return this.decoratedClass.asSubclass(clazz);
    }

    public <U> Class<? extends U> asSubclass(Class<U> clazz) {
        return new Class<>(this.decoratedClass.asSubclass(clazz.getDecoratedClass()));
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */

    public <A extends Annotation> A getAnnotation(java.lang.Class<A> annotationClass) {
        return this.getLastVersion().getAnnotation(annotationClass);
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return this.getAnnotation((java.lang.Class<A>) annotationClass.getDecoratedClass());
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */

    @Override
    public boolean isAnnotationPresent(java.lang.Class<? extends Annotation> annotationClass) {
        return this.decoratedClass.isAnnotationPresent(annotationClass);
    }

    @SuppressWarnings("unchecked")
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return this.decoratedClass
                .isAnnotationPresent((java.lang.Class<? extends Annotation>) annotationClass.getDecoratedClass());
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */

    @Override
    public <A extends Annotation> A[] getAnnotationsByType(java.lang.Class<A> annotationClass) {
        return this.getLastVersion().getAnnotationsByType(annotationClass);
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        return this.getLastVersion().getAnnotationsByType((java.lang.Class<A>) annotationClass.getDecoratedClass());
    }

    /**
     * @since 1.5
     */

    public Annotation[] getAnnotations() {
        return this.getLastVersion().getAnnotations();
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */

    @Override

    public <A extends Annotation> A getDeclaredAnnotation(java.lang.Class<A> annotationClass) {
        return this.decoratedClass.getDeclaredAnnotation(annotationClass);
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        return this.decoratedClass.getDeclaredAnnotation((java.lang.Class<A>) annotationClass.getDecoratedClass());
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */

    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(java.lang.Class<A> annotationClass) {
        return this.decoratedClass.getDeclaredAnnotationsByType(annotationClass);
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationClass) {
        return this.decoratedClass
                .getDeclaredAnnotationsByType((java.lang.Class<A>) annotationClass.getDecoratedClass());
    }

    /**
     * @since 1.5
     */

    public Annotation[] getDeclaredAnnotations() {
        return this.decoratedClass.getDeclaredAnnotations();
    }

    /**
     * Returns an {@code AnnotatedType} object that represents the use of a
     * <p>
     * type to specify the superclass of the entity represented by this {@code
     *
     * <p>
     * Class} object. (The <em>use</em> of type Foo to specify the superclass
     * <p>
     * in '... extends Foo' is distinct from the <em>declaration</em> of type
     * <p>
     * Foo.)
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a type whose declaration
     * <p>
     * does not explicitly indicate an annotated superclass, then the return
     * <p>
     * value is an {@code AnnotatedType} object representing an element with no
     * <p>
     * annotations.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} represents either the {@code Object} class, an
     * <p>
     * interface type, an array type, a primitive type, or void, the return
     * <p>
     * value is {@code null}.
     *
     * @return an object representing the superclass
     * @since 1.8
     */

    public AnnotatedType getAnnotatedSuperclass() {
        return this.getLastVersion().getAnnotatedSuperclass();
    }

    /**
     * Returns an array of {@code AnnotatedType} objects that represent the use
     * <p>
     * of types to specify superinterfaces of the entity represented by this
     * <p>
     * {@code Class} object. (The <em>use</em> of type Foo to specify a
     * <p>
     * superinterface in '... implements Foo' is distinct from the
     * <p>
     * <em>declaration</em> of type Foo.)
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a class, the return value is
     * <p>
     * an array containing objects representing the uses of interface types to
     * <p>
     * specify interfaces implemented by the class. The order of the objects in
     * <p>
     * the array corresponds to the order of the interface types used in the
     * <p>
     * 'implements' clause of the declaration of this {@code Class} object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents an interface, the return
     * <p>
     * value is an array containing objects representing the uses of interface
     * <p>
     * types to specify interfaces directly extended by the interface. The
     * <p>
     * order of the objects in the array corresponds to the order of the
     * <p>
     * interface types used in the 'extends' clause of the declaration of this
     * <p>
     * {@code Class} object.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents a class or interface whose
     * <p>
     * declaration does not explicitly indicate any annotated superinterfaces,
     * <p>
     * the return value is an array of length 0.
     * <p>
     * <p>
     * <p>
     * <p>
     * If this {@code Class} object represents either the {@code Object}
     * <p>
     * class, an array type, a primitive type, or void, the return value is an
     * <p>
     * array of length 0.
     *
     * @return an array representing the superinterfaces
     * @since 1.8
     */

    public AnnotatedType[] getAnnotatedInterfaces() {
        return this.getLastVersion().getAnnotatedInterfaces();
    }

    @Override
    public boolean equals(Object obj) {
        java.lang.Class<?> targetCls = null;

        if (java.lang.Class.class.isInstance(obj))
            targetCls = (java.lang.Class<?>) obj;
        else {
            if (Class.class.isInstance(obj))
                targetCls = (java.lang.Class<?>) ((Class<?>) obj).decoratedClass;
            else
                return false;
        }

        java.lang.Class<?> lastVersion = getLastVersion();
        if (lastVersion == null)
            return this.decoratedClass.equals(targetCls);

        List<java.lang.Class<?>> versions = VersionTables.getVersions(this.decoratedClass);

        if (lastVersion.equals(targetCls) || this.decoratedClass.equals(targetCls))
            return true;

        // Returns true if the object is an instance of any of the class versions
        for (java.lang.Class<?> version : versions) {
            if (version.equals(targetCls))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return decoratedClass.hashCode();
    }

    /********************************************
     * EXTRA FUNCTIONALITY INCORPORATED BY JMPLIB
     ********************************************/

    /**
     * Get the loaded subclasses of this class.
     *
     * @return A list with the loaded subclasses of the class represented by this
     * class object.
     */
    public List<java.lang.Class<?>> getSubClasses() {
        return InheritanceTables.getSubclasses(this.decoratedClass);
    }

    /**
     * Print a series of annotations
     *
     * @param source
     * @param annotations
     */
    private void printAnnotations(StringBuffer source, Annotation[] annotations) {
        int counter = 0;
        if (annotations != null) {
            source.append("\t");
            for (Annotation at : annotations) {
                source.append("@");
                source.append(at.annotationType().getSimpleName());
                if (counter < annotations.length - 1)
                    source.append(", ");
                counter++;
            }
        }
    }

    /**
     * Pretty-prints a method source body, indenting it a certain amount of tabs and
     * removing empty lines
     *
     * @param source  Code
     * @param indents Indentation levels
     * @return
     */
    private String indentSource(String source, int indents) {
        String[] lines1 = source.split("[\\r\\n]+");
        String indentStr = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indents; i++)
            indentStr += "\t";
        int i = 0;
        while (i < lines1.length) {
            sb.append(indentStr);
            sb.append(lines1[i].trim());
            sb.append("\n");
            i++;
        }
        return sb.toString();
    }

    /**
     * Outputs the Java source code of a class, if available.
     *
     * @return
     * @throws StructuralIntercessionException
     */
    @SuppressWarnings({"rawtypes", "unlikely-arg-type"})
    public String getSourceCode() throws StructuralIntercessionException {
        if (this.isInterface()) {
            CompilationUnit unit = JavaParserUtils.getCompilationUnit(this.getDecoratedClass());
            return JavaParserUtils.searchTypeFromOriginalVersion(unit, this.getDecoratedClass().getName()).toString();
        }
        StringBuffer source = new StringBuffer("");
        Field[] fields = IntrospectionUtils.filterJMPLibFields(getDeclaredFields());
        Method[] methods = IntrospectionUtils.filterJMPLibMethods(getDeclaredMethods());

        AnnotatedElement[] importClasses = SimpleIntercessor.getInstance().getImports(this.getDecoratedClass());
        String[] imports = IntercessorTypeConversion.getImportString(importClasses);
        if (imports != null) {
            for (String imp : imports) {
                source.append(imp);
            }
        }
        source.append("\n");

        printAnnotations(source, this.getAnnotations());

        source.append("\n");
        source.append(Modifier.toString(this.getModifiers()));
        source.append(" ");

        source.append("class ");
        source.append(this.getSimpleName());
        IntrospectionUtils.addGenericTypes(source, this.getTypeParameters());

        Class superclass = getSuperclass();

        if (!superclass.equals(Object.class)) {
            source.append(" extends ");
            source.append(superclass.getName());
            IntrospectionUtils.addGenericTypes(source, superclass.getTypeParameters());
        }
        Class[] interfs = IntrospectionUtils.filterJMPLibInterfaces(this.getInterfaces());
        if (interfs != null) {
            if (interfs.length > 0)
                source.append(" implements ");
            int counter = 0;
            for (Class interf : interfs) {
                if (counter > 0)
                    source.append(", ");
                source.append(interf.getName());
                IntrospectionUtils.addGenericTypes(source, interf.getTypeParameters());
                counter++;
            }
        }
        source.append(" {\n");

        for (Field f : fields) {
            source.append("\t");
            source.append(Modifier.toString(f.getModifiers()));
            source.append(" ");
            source.append(f.getType().getSimpleName());
            source.append(" ");
            source.append(f.getName());
            source.append(";");
            source.append("\n");
        }

        for (Method m : methods) {
            try {
                String body = m.getSourceCode();
                printAnnotations(source, m.getAnnotations());
                source.append("\n\t");
                source.append(Modifier.toString(m.getModifiers()));
                source.append(" ");
                source.append(m.getReturnType().getSimpleName());
                source.append(" ");
                source.append(m.getName());
                source.append(m.getParameterString());
                source.append("\n\t{");
                source.append(indentSource(body, 2));
                source.append("\n\t}");
            } catch (IllegalAccessException e) {
            }
        }
        source.append("\n}\n");

        return source.toString().trim();
    }

    /**
     * Get all the methods whose name matches a regular expression
     *
     * @param regexp Regular expression
     * @return List of methods matching the expression
     * @throws SecurityException
     */
    public Method[] getMethods(String regexp) throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.reflect.Method[] mets = lastVersion.getMethods();

        java.lang.reflect.Method[] filteredMets = IntrospectionUtils.filterJMPLibMethods(mets);

        java.lang.reflect.Method[] matchedMets = Arrays.stream(filteredMets).filter(m -> m.getName().matches(regexp))
                .toArray(size -> new java.lang.reflect.Method[size]);

        return IntrospectionUtils.decorateMethodList(matchedMets);
    }

    /**
     * Get all the fields whose name matches a regular expression
     *
     * @param regexp Regular expression
     * @return List of methods matching the expression
     * @throws SecurityException
     */
    public Field[] getFields(String regexp) throws SecurityException {
        java.lang.Class<?> lastVersion = getLastVersion();
        java.lang.reflect.Field[] fields = lastVersion.getFields();

        java.lang.reflect.Field[] filtered = IntrospectionUtils.filterJMPLibFields(fields);

        java.lang.reflect.Field[] matched = Arrays.stream(filtered).filter(m -> m.getName().matches(regexp))
                .toArray(size -> new java.lang.reflect.Field[size]);

        return IntrospectionUtils.decorateFieldList(matched);
    }

    /**
     * Gets the JavaParser ClassOrInterfaceDeclaration node from this class.
     *
     * @return
     * @throws StructuralIntercessionException
     */
    public ClassOrInterfaceDeclaration getClassDeclaration() throws StructuralIntercessionException {
        if (this.customClassDeclaration != null)
            return customClassDeclaration;
        java.lang.Class<?> declaringClass = this.getDecoratedClass();
        if (this.isInterface()) {
            CompilationUnit unit = JavaParserUtils.getCompilationUnit(declaringClass);
            return (ClassOrInterfaceDeclaration) JavaParserUtils.searchTypeFromOriginalVersion(unit, declaringClass.getName());
        }
        // Class content
        ClassContent classContent;

        try {
            classContent = SourceCodeCache.getInstance().getClassContent(declaringClass);
            CompilationUnit unit = JavaParserUtils.parse(classContent.getContent());
            return (ClassOrInterfaceDeclaration) JavaParserUtils.searchTypeFromOriginalVersion(unit, declaringClass.getName());
        } catch (Exception e) {
            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }
    }
}
