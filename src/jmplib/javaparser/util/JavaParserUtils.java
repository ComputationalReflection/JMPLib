package jmplib.javaparser.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.sourcecode.ClassContent;
import jmplib.sourcecode.SourceCodeCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Helper class to encapsulate the most common Java Parser operations
 *
 * @author Ignacio Lagartos, Jos� Manuel Redondo
 * @version 1.1: Modifications were made to enable working with generic methods.
 */
@ExcludeFromJMPLib
public class JavaParserUtils {

    /**
     * Parse the source of the class
     *
     * @param source The source code
     * @return The {@link CompilationUnit} with the AST of the class
     * @throws ParseException When the class cannot be parsed
     */
    public static CompilationUnit parse(String source) throws ParseException {
        Reader reader = new StringReader(source);
        CompilationUnit unit;
        unit = JavaParser.parse(reader, false);
        return unit;
    }

    /**
     * Converts a class to {@link NameExpr} node
     *
     * @param clazz The class
     * @return {@link NameExpr} node
     */
    public static NameExpr classToNameExpr(Class<?> clazz) {
        NameExpr expr = null;
        String[] fragments = clazz.getName().split("\\.");
        for (int i = 0; i < fragments.length; i++) {
            if (expr == null) {
                expr = new NameExpr(fragments[i]);
            } else {
                expr = new QualifiedNameExpr(expr, fragments[i]);
            }
        }
        return expr;
    }

    /**
     * Tranforms a class to {@link Type} node
     *
     * @param clazz The class
     * @return {@link Type} node
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Type transform(Class<?> clazz) {
        Type type = null;
        int arrayCount = 0;
        if (clazz.getComponentType() != null) {
            String name = clazz.getName();
            arrayCount = (name.length() - name.replace("[", "").length());
            while (clazz.getComponentType() != null)
                clazz = clazz.getComponentType();
        }
        if (clazz.getPackage() == null && "void".equals(clazz.getName())) {
            return new VoidType();
        } else if (clazz.getPackage() == null) {
            String name = clazz.getName();
            String capitalizeName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Primitive primitive = Primitive.valueOf(capitalizeName);
            type = new PrimitiveType(primitive);
            if (arrayCount == 0)
                return type;
        } else {
            String name = clazz.getName();
            String[] parts = name.split("\\.");
            ClassOrInterfaceType scope = null;
            for (int i = 0; i < parts.length - 1; i++) {
                scope = new ClassOrInterfaceType(scope, parts[i]);
            }
            type = new ClassOrInterfaceType(scope, parts[parts.length - 1]);
        }
        type = new ReferenceType(type, arrayCount);
        if (arrayCount > 0) {
            ReferenceType refType = (ReferenceType) type;
            List arraysAnnotations = new LinkedList();
            arraysAnnotations.add(null);
            refType.setArraysAnnotations(arraysAnnotations);
            return refType;
        }
        return type;
    }

    /**
     * Transforms {@link java.lang.reflect.Type} to java parser {@link Type}
     *
     * @param type {@link java.lang.reflect.Type}
     * @return Java parser {@link Type} or null
     */
    @SuppressWarnings("rawtypes")
    public static Type transform(java.lang.reflect.Type type) {
        if (type instanceof Class<?>)
            return transform((Class<?>) type);
        if (type instanceof ParameterizedType) {
            int arrayCount = 0;
            ParameterizedType pType = (ParameterizedType) type;
            java.lang.reflect.Type rawType = pType.getRawType();
            Class<?> rawClass = null;
            if (rawType instanceof Class<?>) {
                rawClass = (Class<?>) rawType;
                if (rawClass.getComponentType() != null) {
                    String name = rawClass.getName();
                    arrayCount = (name.length() - name.replace("[", "").length());
                    while (rawClass.getComponentType() != null)
                        rawClass = rawClass.getComponentType();
                }
                String name = rawClass.getTypeName();
                String[] parts = name.split("\\.");
                ClassOrInterfaceType scope = null;
                for (int i = 0; i < parts.length - 1; i++) {
                    scope = new ClassOrInterfaceType(scope, parts[i]);
                }
                Type resultType = null;
                java.lang.reflect.Type[] parametrizationsTypes = pType.getActualTypeArguments();
                if (parametrizationsTypes != null) {
                    List<Type> typeArgs = new ArrayList<Type>();
                    for (int i = 0; i < parametrizationsTypes.length; i++) {
                        typeArgs.add(transform(parametrizationsTypes[i]));
                    }
                    resultType = new ClassOrInterfaceType(0, 0, 0, 0, scope, parts[parts.length - 1], typeArgs);
                } else {
                    resultType = new ClassOrInterfaceType(scope, parts[parts.length - 1]);
                }
                resultType = new ReferenceType(resultType, arrayCount);
                return resultType;
            }
        }
        if (type instanceof java.lang.reflect.TypeVariable) {
            return new ClassOrInterfaceType(((java.lang.reflect.TypeVariable) type).toString());
        }
        return null;
    }

    /**
     * Obtains the {@link TypeDeclaration} of the specified class from the
     * compilation Unit
     *
     * @param unit  The Compilation unit
     * @param clazz The class
     * @return Return the {@link TypeDeclaration} or null
     */
    public static TypeDeclaration searchTypeFromJavaParserCU(CompilationUnit unit, Class<?> clazz) {
        String packageName = unit.getPackage().getName().toString();
        for (TypeDeclaration td : unit.getTypes()) {
            String tdName = packageName + "." + td.getName();
            if (tdName.equals(clazz.getName())) {
                return td;
            }
        }
        return null;
    }

    /**
     * Obtains the {@link TypeDeclaration} of the specified class from the
     * compilation Unit
     *
     * @param unit  The Compilation unit
     * @param clazz The class
     * @return Return the {@link TypeDeclaration} or null
     */
    public static TypeDeclaration searchType(CompilationUnit unit, Class<?> clazz) {
        String versionName = "";
        try {
            ClassContent classContent = SourceCodeCache.getInstance().getClassContent(clazz);
            versionName = clazz.getName() + "_NewVersion_" + classContent.getVersion();
        } catch (StructuralIntercessionException e) {
            versionName = clazz.getName() + "_NewVersion_0";
        }

        String packageName = unit.getPackage().getName().toString();
        for (TypeDeclaration td : unit.getTypes()) {
            String tdName = packageName + "." + td.getName();
            if (tdName.equals(versionName)) {
                return td;
            }
        }
        return null;
    }

    /**
     * Obtains the {@link TypeDeclaration} of the specified class from the
     * compilation Unit
     *
     * @param unit      The Compilation unit
     * @param clazzName The name of the class
     * @return Return the {@link TypeDeclaration} or null
     */
    public static TypeDeclaration searchType(CompilationUnit unit, String clazzName) {
        String packageName = unit.getPackage().getName().toString();
        for (TypeDeclaration td : unit.getTypes()) {
            String tdName = packageName + "." + td.getName();
            //System.out.println(tdName + " : " + clazzName);
            if (tdName.equals(clazzName)) {
                return td;
            }
        }
        return null;
    }

    /**
     * Obtains the {@link TypeDeclaration} of the specified class from the
     * compilation Unit regardless if it is a version class
     *
     * @param unit      The Compilation unit
     * @param clazzName The name of the class
     * @return Return the {@link TypeDeclaration} or null
     */
    public static TypeDeclaration searchTypeFromOriginalVersion(CompilationUnit unit, String clazzName) {
        String packageName = unit.getPackage().getName().toString();
        for (TypeDeclaration td : unit.getTypes()) {
            String tdName = packageName + "." + td.getName();
            if (tdName.startsWith(clazzName)) {
                return td;
            }
        }
        return null;
    }

    /**
     * Search a method node in the compilation unit
     *
     * @param unit             The compilation Unit
     * @param clazz            The class owner
     * @param name             The name of the method
     * @param parameterClasses The parameter classes
     * @param returnClass      The type of the method
     * @return The method node
     * @throws NoSuchMethodException If the method is not in the CompilationUnit
     */
    public static MethodDeclaration searchMethodFromJavaParserCU(CompilationUnit unit, Class<?> clazz, String name,
                                                 Class<?>[] parameterClasses, Class<?> returnClass) throws NoSuchMethodException {
        TypeDeclaration typeDeclaration = searchTypeFromJavaParserCU(unit, clazz);
        List<BodyDeclaration> members = typeDeclaration.getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;

                if (checkMethod(method, unit.getImports(), unit.getPackage(), name, parameterClasses, returnClass)) {
                    return method;
                }
            }
        }
        String params = "";
        for (Class<?> parameterClass : parameterClasses) {
            params += parameterClass.getName() + ",";
        }
        if (!"".equals(params))
            params = params.substring(0, params.length() - 1);
        throw new NoSuchMethodException("The method " + name + "(" + params + "): " + returnClass.getName()
                + " does not exist in the class " + clazz.getName());
    }

    /**
     * Search a method node in the compilation unit
     *
     * @param unit             The compilation Unit
     * @param clazz            The class owner
     * @param name             The name of the method
     * @param parameterClasses The parameter classes
     * @param returnClass      The type of the method
     * @return The method node
     * @throws NoSuchMethodException If the method is not in the CompilationUnit
     */
    public static MethodDeclaration searchMethod(CompilationUnit unit, Class<?> clazz, String name,
                                                 Class<?>[] parameterClasses, Class<?> returnClass) throws NoSuchMethodException {
        TypeDeclaration typeDeclaration = searchType(unit, clazz);
        List<BodyDeclaration> members = typeDeclaration.getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                /*
                 * System.out.println(name + "("); for (int i = 0;i< parameterClasses.length;
                 * i++) System.out.print(parameterClasses[i] + ", "); System.out.println("):" +
                 * returnClass);
                 *
                 * System.out.println(method);
                 */
                if (checkMethod(method, unit.getImports(), unit.getPackage(), name, parameterClasses, returnClass)) {
                    return method;
                }
            }
        }
        String params = "";
        for (Class<?> parameterClass : parameterClasses) {
            params += parameterClass.getName() + ",";
        }
        if (!"".equals(params))
            params = params.substring(0, params.length() - 1);
        throw new NoSuchMethodException("The method " + name + "(" + params + "): " + returnClass.getName()
                + " does not exist in the class " + clazz.getName());
    }

    /**
     * Search a method node in the compilation unit
     *
     * @param unit  The compilation Unit
     * @param clazz The class owner
     * @param name  The name of the method
     * @return The method node
     * @throws NoSuchMethodException If the method is not in the CompilationUnit
     */
    public static MethodDeclaration searchMethod(CompilationUnit unit, Class<?> clazz, String name)
            throws NoSuchMethodException {
        TypeDeclaration typeDeclaration = searchType(unit, clazz);
        List<BodyDeclaration> members = typeDeclaration.getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                if (checkName(method, name)) {
                    return method;
                }
            }
        }
        throw new NoSuchMethodException("The method " + name + " does not exist in the class " + clazz.getName());
    }

    /**
     * Search a field node in the compilation unit
     *
     * @param unit  The compilation Unit
     * @param clazz The class owner
     * @param name  The name of the field
     * @return The field node
     * @throws NoSuchFieldException If the field is not in the CompilationUnit
     */
    public static VariableDeclarator searchField(CompilationUnit unit, Class<?> clazz, String name)
            throws NoSuchFieldException {
        TypeDeclaration typeDeclaration = searchType(unit, clazz);
        List<BodyDeclaration> members = typeDeclaration.getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) member;
                for (VariableDeclarator declaration : field.getVariables()) {
                    if (declaration.getId().getName().equals(name))
                        return declaration;
                }
            }
        }
        throw new NoSuchFieldException("The field \"" + name + "\" does not exist in the class " + clazz.getName());
    }

    /**
     * Search a field declaration in the compilation unit
     *
     * @param unit  The compilation Unit
     * @param clazz The class owner
     * @param name  The name of the field
     * @return The field node
     * @throws NoSuchFieldException If the field is not in the CompilationUnit
     */
    public static FieldDeclaration searchFieldDeclarationFromJavaParserCU(CompilationUnit unit, Class<?> clazz, String name)
            throws NoSuchFieldException {
        TypeDeclaration typeDeclaration = searchTypeFromJavaParserCU(unit, clazz);
        List<BodyDeclaration> members = typeDeclaration.getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) member;
                for (VariableDeclarator declaration : field.getVariables()) {
                    if (declaration.getId().getName().equals(name))
                        return field;
                }
            }
        }
        throw new NoSuchFieldException("The field \"" + name + "\" does not exist in the class " + clazz.getName());
    }

    /**
     * Search a field declaration in the compilation unit
     *
     * @param unit  The compilation Unit
     * @param clazz The class owner
     * @param name  The name of the field
     * @return The field node
     * @throws NoSuchFieldException If the field is not in the CompilationUnit
     */
    public static FieldDeclaration searchFieldDeclaration(CompilationUnit unit, Class<?> clazz, String name)
            throws NoSuchFieldException {
        TypeDeclaration typeDeclaration = searchType(unit, clazz);
        List<BodyDeclaration> members = typeDeclaration.getMembers();
        for (BodyDeclaration member : members) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) member;
                for (VariableDeclarator declaration : field.getVariables()) {
                    if (declaration.getId().getName().equals(name))
                        return field;
                }
            }
        }
        throw new NoSuchFieldException("The field \"" + name + "\" does not exist in the class " + clazz.getName());
    }

    /**
     * Check if the method is the searched method
     *
     * @param method             The {@link MethodDeclaration}
     * @param imports            The imports of the class to resolve types
     * @param packageDeclaration The package of the class
     * @param name               The name of the searched method
     * @param parameterClasses   The parameter types
     * @param returnClass        The return class
     * @return {@code true} if the method matches
     */
    private static boolean checkMethod(MethodDeclaration method, List<ImportDeclaration> imports,
                                       PackageDeclaration packageDeclaration, String name, Class<?>[] parameterClasses, Class<?> returnClass) {
        if (checkName(method, name) && checkReturnClass(method, imports, packageDeclaration, returnClass)
                && checkParameterClasses(method, imports, packageDeclaration, parameterClasses)) {
            return true;
        }
        return false;
    }

    /**
     * Check if the names of the methods are equals
     *
     * @param method The {@code MethodDeclaration}
     * @param name   The name expected
     * @return {@code true} if matches
     */
    private static boolean checkName(MethodDeclaration method, String name) {
        return name.equals(method.getName());
    }

    private static final String[] primitiveTypeNames = {"byte", "short", "int", "double", "long", "String", "float",
            "boolean", "char"};

    /**
     * Tells if the passed type is a generic type parameter
     *
     * @param method
     * @param param
     * @return
     */
    public static boolean isATypeParameter(MethodDeclaration method, Type param) {
        if (param instanceof ReferenceType) {
            try {
                String str = param.toString().trim();
                for (String t : primitiveTypeNames)
                    if (t.equals(str))
                        return false;

                Class.forName(param.toString().replaceAll("class ", "").trim());
                return false;
            } catch (Exception ex) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tells if the passed parameter is a generic type parameter
     *
     * @param method
     * @param param
     * @return
     */
    public static boolean isATypeParameter(MethodDeclaration method, Parameter param) {
        return isATypeParameter(method, param.getType());
    }

    /**
     * Check the parameters if are equals
     *
     * @param method             The {@link MethodDeclaration}
     * @param imports            the imports of the class
     * @param packageDeclaration the package of the class
     * @param parameterClasses   The parameter classes expected
     * @return {@code true} if matches
     */
    private static boolean checkParameterClasses(MethodDeclaration method, List<ImportDeclaration> imports,
                                                 PackageDeclaration packageDeclaration, Class<?>[] parameterClasses) {
        Parameter[] parameters;
        if (method.getParameters() != null) {
            parameters = method.getParameters().toArray(new Parameter[0]);
        } else {
            return parameterClasses.length == 0;
        }
        if (parameters.length != parameterClasses.length) {
            return false;
        }

        for (int i = 0; i < parameterClasses.length; i++) {
            // System.out.println(parameters[i] + " is a type parameter " +
            // isATypeParameter(method, parameters[i]) );
            if (checkParameter(parameters[i], parameterClasses[i], imports, packageDeclaration)) {
                continue;
            }
            else {
                if (isATypeParameter(method, parameters[i])) {
                    if (!parameterClasses[i].getName().equals("java.lang.Object"))
                        return false;
                }
                else return false;
            }

            /*
            if (isATypeParameter(method, parameters[i])) {
                if (!parameterClasses[i].getName().equals("java.lang.Object"))
                    return false;
                continue;
            }
            if (!checkParameter(parameters[i], parameterClasses[i], imports, packageDeclaration)) {
                return false;
            }*/
        }
        return true;
    }

    /**
     * Checks of the return type matches
     *
     * @param method             The {@code MethodDeclaration}
     * @param imports            The imports of the class
     * @param packageDeclaration The package of the class
     * @param returnClass        The return class expected
     * @return {@code true} if matches
     */
    private static boolean checkReturnClass(MethodDeclaration method, List<ImportDeclaration> imports,
                                            PackageDeclaration packageDeclaration, Class<?> returnClass) {
        // System.out.println(method.getType() + ": " + isATypeParameter(method,
        // method.getType()));
        if (checkType(method.getType(), returnClass, imports, packageDeclaration))
            return true;

        if (isATypeParameter(method, method.getType())) {
            if (!returnClass.getName().equals("java.lang.Object"))
                return false;
            return true;
        }
        return false;

        /*
        if (isATypeParameter(method, method.getType())) {
            if (!returnClass.getName().equals("java.lang.Object"))
                return false;
            return true;
        }
        return checkType(method.getType(), returnClass, imports, packageDeclaration);*/
    }

    /**
     * Check the argument
     *
     * @param param              The {@code Parameter}
     * @param clazz              The class expected
     * @param imports            The imports of the class
     * @param packageDeclaration The package of the class
     * @return {@code true} if matches
     */
    private static boolean checkParameter(Parameter param, Class<?> clazz, List<ImportDeclaration> imports,
                                          PackageDeclaration packageDeclaration) {
        if (param.getId().getArrayCount() > 0) {
            if (!clazz.isArray())
                return false;
            if (param.getId().getArrayCount() != clazz.getName().chars().filter(c -> c == '[').count()) {
                return false;
            }
            clazz = clazz.getComponentType();
        }
        return checkType(param.getType(), clazz, imports, packageDeclaration);
    }

    /**
     * Check type
     *
     * @param type               The {@link Type}
     * @param clazz              The class expected
     * @param imports            The imports of the class
     * @param packageDeclaration The package of the class
     * @return {@code true} if matches
     */
    private static boolean checkType(Type type, Class<?> clazz, List<ImportDeclaration> imports,
                                     PackageDeclaration packageDeclaration) {
        if (type instanceof ReferenceType) {
            ReferenceType ref = (ReferenceType) type;
            if (ref.getArrayCount() > 0) {
                if (!clazz.isArray())
                    return false;
                if (ref.getArrayCount() != clazz.getName().chars().filter(c -> c == '[').count()) {
                    return false;
                }
                clazz = clazz.getComponentType();
            }
            type = ref.getType();
        }
        if (type instanceof PrimitiveType) {
            return checkPrimitiveType((PrimitiveType) type, clazz);
        } else if (type instanceof ClassOrInterfaceType) {
            return checkClassOrInterfaceType((ClassOrInterfaceType) type, clazz, imports, packageDeclaration);
        } else if (type instanceof VoidType) {
            return void.class.getName().equals(clazz.getName());
        }
        return false;
    }

    /**
     * Checks {@link ClassOrInterfaceType}
     *
     * @param type               The {@link ClassOrInterfaceType}
     * @param clazz              The class expected
     * @param imports            The imports of the class
     * @param packageDeclaration The package declaration
     * @return {@code true} if matches
     */
    private static boolean checkClassOrInterfaceType(ClassOrInterfaceType type, Class<?> clazz,
                                                     List<ImportDeclaration> imports, PackageDeclaration packageDeclaration) {
        if (type.getScope() != null) {
            return type.toString().equals(clazz.getName());
        } else {
            if (isOnImports(clazz, imports)) {
                return type.getName().equals(clazz.getSimpleName());
            } else {
                if (clazz.getName().startsWith("java.lang")) {
                    return type.getName().equals(clazz.getSimpleName());
                } else if (clazz.getPackage() != null) {
                    if (packageDeclaration.getName().toString().equals(clazz.getPackage().getName())) {
                        return type.getName().equals(clazz.getSimpleName());
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the class is inside the import declaration
     *
     * @param clazz   The class to search for
     * @param imports The import declaration
     * @return {@code true} if the class is imported
     */
    private static boolean isOnImports(Class<?> clazz, List<ImportDeclaration> imports) {
        if (imports == null)
            return false;
        for (ImportDeclaration importDeclaration : imports) {
            if (!importDeclaration.isAsterisk()) {
                if (importDeclaration.getName().toString().equals(clazz.getName())) {
                    return true;
                }
            } else {
                String importExp = importDeclaration.getName().toString();
                if (clazz.getName().startsWith(importExp.substring(0, importExp.length() - 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks {@link PrimitiveType}
     *
     * @param type  The {@link PrimitiveType}
     * @param clazz The class expected
     * @return {@code true} if matches
     */
    private static boolean checkPrimitiveType(PrimitiveType type, Class<?> clazz) {
        Primitive param = null;
        String name = clazz.getSimpleName();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        try {
            param = PrimitiveType.Primitive.valueOf(name);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return type.getType().equals(param);
    }

    /**
     * Maps simple types to their corresponding Class object
     */
    @SuppressWarnings("rawtypes")
    private static final Map<String, Class> simpleClassFromName = new HashMap<String, Class>();

    static {
        simpleClassFromName.put("byte", byte.class);
        simpleClassFromName.put("short", short.class);
        simpleClassFromName.put("int", int.class);
        simpleClassFromName.put("long", long.class);
        simpleClassFromName.put("char", char.class);
        simpleClassFromName.put("float", float.class);
        simpleClassFromName.put("double", double.class);
        simpleClassFromName.put("boolean", boolean.class);
        simpleClassFromName.put("void", void.class);
        simpleClassFromName.put("String", String.class);
    }

    /**
     * Maps a JavaParser type name to its corresponding Java class
     *
     * @param typeName
     * @return
     * @throws ClassNotFoundException
     */
    public static java.lang.Class<?> typeName2Class(String typeName) throws ClassNotFoundException {
        Class<?> cl = simpleClassFromName.get(typeName);

        if (cl == null)
            return Class.forName(typeName);

        return cl;
    }

    /**
     * Obtain a tentative .java file name from the .class file name, useful if the .java file resides in the same folder
     * as the .class file.
     *
     * @param classObject
     * @return
     */
    private static String getJavaFromClass(Class<?> classObject) {
        URL classPath = classObject.getResource(classObject.getSimpleName() + ".class");
        String javaFile = classPath.toString().replace("file:/", "");
        javaFile = javaFile.substring(0, javaFile.length() - ".class".length()); // Remove ".class"
        return javaFile + ".java";
    }

    private static String existInSrc(String javaFilePath, String javaFileName) {
        String transf = javaFilePath.replace("/bin", "/src");
        File f = new File(transf);
        if (f.exists())
            return transf;
        return null;
    }

    private static String existInSrcNoBasePackage(String javaFilePath, String javaFileName) {
        String transf = javaFilePath.replace("/bin", "/test");
        File f = new File(transf);
        if (f.exists())
            return transf;
        return null;
    }

    private static String existInClassPath(String javaFilePath, String javaFileName) {
        String transf;
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(";");

        for (String path : paths) {
            if (path.endsWith(".jar"))
                continue;
            transf = path + "/" + javaFileName;
            File f = new File(transf);
            if (f.exists()) return transf;
        }
        return null;
    }

    private static List<BiFunction<String, String, String>> pathFunctions;

    static {
        pathFunctions = Arrays.asList(
                JavaParserUtils::existInSrc,
                JavaParserUtils::existInSrcNoBasePackage,
                JavaParserUtils::existInClassPath
        );
    }

    /**
     * Obtains a JavaParser compilation unit from a class object.
     *
     * @param classObject Class object
     * @return A JavaParser CompilationUnit
     * @throws StructuralIntercessionException If some problem occurs trying to locate the source of the .class file
     */
    public static CompilationUnit getCompilationUnit(Class<?> classObject) throws StructuralIntercessionException {
        String javaFilePath = getJavaFromClass(classObject);

        FileInputStream in;
        String javaFile = null;
        for (BiFunction<String, String, String> bf : pathFunctions) {
            javaFile = bf.apply(javaFilePath, classObject.getSimpleName() + ".java");
            if (javaFile != null) break;
        }

        if (javaFile == null)
            throw new StructuralIntercessionException("Cannot find a suitable .java file for " + classObject.getSimpleName());

        try {
            in = new FileInputStream(javaFile);
        } catch (Exception e) {

            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }

        // parse the file
        try {
            return JavaParser.parse(in);
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e.getCause());
        }
    }
}
