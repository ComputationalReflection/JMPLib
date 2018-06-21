package jmplib.reflect;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.sourcecode.SourceCodeCache;

import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A class containing utility methods to convert JavaParser method declarations
 * to Java reflection entities.
 *
 * @author Jose Manuel Redondo Lopez
 */
public class DeclarationUtils {

    /**************** METHODS *****************************/
    /**
     * Utility method to get the method type.
     *
     * @param md A JavaParser method declaration
     * @return
     * @throws ClassNotFoundException
     */
    public static MethodType getMethodType(MethodDeclaration md) throws StructuralIntercessionException {
        try {
            // Get parameter names
            List<com.github.javaparser.ast.body.Parameter> parameters = md.getParameters();
            java.lang.Class<?>[] parameterTypes = new java.lang.Class<?>[parameters.size()];
            int counter = 0;
            for (com.github.javaparser.ast.body.Parameter p : parameters)
                parameterTypes[counter++] = JavaParserUtils.typeName2Class(p.getType().toString());
            java.lang.Class<?> returnType = JavaParserUtils.typeName2Class(md.getType().toString());

            return MethodType.methodType(returnType, parameterTypes);
        } catch (Exception e) {
            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Utility method to get parameter names.
     *
     * @param md A JavaParser method declaration
     * @return
     */
    public static String[] getParameterNames(MethodDeclaration md) {
        // Get parameter names
        List<com.github.javaparser.ast.body.Parameter> parameters = md.getParameters();
        String[] parameterNames = new String[parameters.size()];
        int counter = 0;
        for (com.github.javaparser.ast.body.Parameter p : parameters)
            parameterNames[counter++] = p.getId().toString();
        return parameterNames;
    }

    /**
     * Utility method to get a method body.
     *
     * @param md A JavaParser method declaration
     * @return The method body
     */
    public static String getSourceCode(MethodDeclaration md) {
        // Get and parse method body
        String body = md.getBody().toString();
        if (body.startsWith("{"))
            body = body.substring(1);
        if (body.endsWith("}"))
            body = body.substring(0, body.length() - 1);
        return body;
    }

    @SuppressWarnings("rawtypes")
    public static Type getGenericReturnType(MethodDeclaration m) {
        List<TypeParameter> l = m.getTypeParameters();
        if (l.size() == 0)
            return null;
        com.github.javaparser.ast.type.Type returnType = m.getType();
        for (TypeParameter tp : l) {
            if (returnType.toString().equals(tp.toString()))
                return new jmplib.reflect.TypeVariable(returnType.toString());
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public static jmplib.reflect.TypeVariable<?>[] getGenericMethodTypeParameters(MethodDeclaration m) {
        List<TypeParameter> l = m.getTypeParameters();
        if (l.size() == 0)
            return null;
        jmplib.reflect.TypeVariable<?>[] ret = new jmplib.reflect.TypeVariable<?>[l.size()];
        int cont = 0;
        for (TypeParameter tp : l) {
            ret[cont++] = new jmplib.reflect.TypeVariable(tp.toString());
        }
        return ret;
    }

    private static ClassOrInterfaceDeclaration getClassOrInterfaceParentNode(Node m)
            throws StructuralIntercessionException {
        Node n = m.getParentNode();
        while (true) {
            if (n.getClass() == ClassOrInterfaceDeclaration.class)
                return (ClassOrInterfaceDeclaration) n;
            n = n.getParentNode();
            if (n == null)
                break;
        }
        throw new StructuralIntercessionException("Cannot locate the class that owns this method declaration: " + m);
    }

    public static java.lang.Class<?> getDeclaringClass(MethodDeclaration m) throws StructuralIntercessionException {
        ClassOrInterfaceDeclaration classNode = getClassOrInterfaceParentNode(m);
        String rawClassName = classNode.getName();
        String className = SourceCodeCache.getOriginalClassNameFromVersion(rawClassName);
        String pack = "";
        CompilationUnit cu = getCompilationUnit(m);

        if (cu != null)
            pack = cu.getPackage().getName() + ".";

        try {
            return JavaParserUtils.typeName2Class(pack + className);
        } catch (Exception e) {
            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }
    }

    public static Type[] getExceptionTypes(MethodDeclaration md) throws StructuralIntercessionException {
        try {
            List<NameExpr> names = md.getThrows();
            if (names.size() == 0)
                return null;
            Type[] excepts = new Type[names.size()];
            int cont = 0;
            for (NameExpr expr : names) {
                excepts[cont++] = JavaParserUtils.typeName2Class(expr.getName());
            }
            return excepts;
        } catch (Exception e) {
            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }
    }

    private static CompilationUnit getCompilationUnit(Node n) {
        while (n != null) {
            if (n.getParentNode().getClass() == CompilationUnit.class) {
                return (CompilationUnit) n.getParentNode();
            }
            n = n.getParentNode();
        }
        return null;
    }

    /************************ FIELDS *********************************/

    public static java.lang.Class<?> getDeclaringClass(FieldDeclaration m) throws StructuralIntercessionException {
        ClassOrInterfaceDeclaration classNode = getClassOrInterfaceParentNode(m);
        String rawClassName = classNode.getName();
        String className = SourceCodeCache.getOriginalClassNameFromVersion(rawClassName);
        String pack = "";

        CompilationUnit cu = getCompilationUnit(m);

        if (cu != null)
            pack = cu.getPackage().getName() + ".";

        try {
            return JavaParserUtils.typeName2Class(pack + className);
        } catch (Exception e) {
            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }
    }

    public static java.lang.Class<?> getFieldType(FieldDeclaration m) throws StructuralIntercessionException {
        com.github.javaparser.ast.type.Type t = m.getType();
        try {
            return JavaParserUtils.typeName2Class(t.toString());
        } catch (Exception e) {
            CompilationUnit cu = getCompilationUnit(t);
            String pack = "";
            if (cu != null)
                pack = cu.getPackage().getName() + ".";
            try {
                return JavaParserUtils.typeName2Class(pack + t.toString());
            } catch (Exception e2) {
                throw new StructuralIntercessionException(e2.getMessage(), e2.getCause());
            }
        }
    }

    /********************** CLASSES **************************/

    public static java.lang.Class<?> getDecoratedClass(ClassOrInterfaceDeclaration classNode) throws StructuralIntercessionException {
        String rawClassName = classNode.getName();
        String className = SourceCodeCache.getOriginalClassNameFromVersion(rawClassName);
        String pack = "";
        CompilationUnit cu = getCompilationUnit(classNode);

        if (cu != null)
            pack = cu.getPackage().getName() + ".";

        try {
            return JavaParserUtils.typeName2Class(pack + className);
        } catch (Exception e) {
            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }
    }
}
