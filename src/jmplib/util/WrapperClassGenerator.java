package jmplib.util;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.classversions.VersionTables;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to generate source code of wrapper classes. This class
 * encapsulates dynamic code into classes to compile them and invoke the dynamic
 * code.
 *
 * @author Ignacio Lagartos, Jose Manuel Redondo
 * @version 1.1 (Redondo) Adds the possibility of generating eval code that uses
 * imports to simplify dynamic cod. Adds the final modifier to
 * generated eval methods to improve performance. Adds a map of simple
 * type names to their Class object to improve performance.
 */
@ExcludeFromJMPLib
public class WrapperClassGenerator {

    public static final String GENERATED_INVOKER_PACKAGE = "generatedClasses.invoker";
    public static final String GENERATED_EVAL_PACKAGE = "generatedClasses.eval";

    public static final int MODIFIERS = ModifierSet.PUBLIC | ModifierSet.FINAL;

    private static final Map<String, Class<?>> wrapperClassMap = new HashMap<String, Class<?>>();

    static {
        wrapperClassMap.put("double", Double.class);
        wrapperClassMap.put("int", Integer.class);
        wrapperClassMap.put("float", Float.class);
        wrapperClassMap.put("short", Short.class);
        wrapperClassMap.put("byte", Byte.class);
        wrapperClassMap.put("char", Character.class);
        wrapperClassMap.put("boolean", Boolean.class);
        wrapperClassMap.put("long", Long.class);
    }

    /**
     * Generates a class to wrap the dynamic user code and compile it.
     *
     * @param name                   The name of the class
     * @param code                   The code to wrap
     * @param functionalInterface    The functional interface to invoke the dynamic code
     * @param paramNames             The parameter names
     * @param parametrizationClasses The parametrization classes for the interface
     * @return Class File with the dynamic code wrapped
     * @throws StructuralIntercessionException
     */
    public static <T> File generateEvalClass(String name, String code, Class<T> functionalInterface,
                                             String[] paramNames, Class<?>[] parametrizationClasses) throws StructuralIntercessionException {
        return generateEvalClass(name, code, functionalInterface, paramNames, null, parametrizationClasses);
    }

    /**
     * Generates a class to wrap the dynamic user code and compile it. It also gives
     * the option of adding import clauses to the wrapper class, so the code can be
     * simpler.
     *
     * @param name                   The name of the class
     * @param code                   The code to wrap
     * @param functionalInterface    The functional interface to invoke the dynamic code
     * @param paramNames             The parameter names
     * @param imports                imports to include in the generated code ("java.awt.*",
     *                               "java.util.Vector").
     * @param parametrizationClasses The parametrization classes for the interface
     * @return Class File with the dynamic code wrapped
     * @throws StructuralIntercessionException
     */
    public static <T> File generateEvalClass(String name, String code, Class<T> functionalInterface,
                                             String[] paramNames, String[] imports, Class<?>[] parametrizationClasses)
            throws StructuralIntercessionException {
        Method m = MemberFinder.getMethod(functionalInterface);
        boolean[] genericMap = genericMap(m);
        CompilationUnit cu = new CompilationUnit();
        cu.setPackage(getEvalPackage());
        if (imports != null) {
            List<ImportDeclaration> importList = new ArrayList<ImportDeclaration>();
            for (String imp : imports) {
                ImportDeclaration id = new ImportDeclaration();
                id.setName(new NameExpr(imp));
                importList.add(id);
            }
            cu.setImports(importList);
        }
        ClassOrInterfaceDeclaration type = getType(name, cu);

        List<ClassOrInterfaceType> interfaces = getGenericInterface(parametrizationClasses, functionalInterface);
        type.setImplements(interfaces);

        String evalName = m.getName();

        java.lang.reflect.Type[] paramClasses = MemberFinder.resolveGenericParameters(m, parametrizationClasses);
        java.lang.reflect.Type returnClass = MemberFinder.resolveGenericReturn(m, parametrizationClasses);
        Class<?>[] exceptions = m.getExceptionTypes();

        code = checkCode(code, returnClass.getTypeName());

        MethodDeclaration method = getEvalMethod(code, paramNames, paramClasses, returnClass, evalName, genericMap,
                exceptions);
        ASTHelper.addMember(type, method);
        return createFile(GENERATED_EVAL_PACKAGE + "." + name, cu.toString());
    }

    /**
     * Generates a class to wrap the dynamic user code and compile it. The generated
     * class implements the {@link EnvironmentSetUp} interface.
     *
     * @param name                   The name of the class
     * @param code                   The code to wrap
     * @param functionalInterface    The functional interface to invoke the dynamic code
     * @param paramNames             The parameter names
     * @param parametrizationClasses The parametrization classes for the interface
     * @param environment            The global fields declarations for the wrapper class
     * @return Class File with the dynamic code wrapped
     * @throws StructuralIntercessionException
     */
    public static <T> File generateEvalClass(String name, String code, Class<T> functionalInterface,
                                             String[] paramNames, Class<?>[] parametrizationClasses, Map<String, Class<?>> environment)
            throws StructuralIntercessionException {
        Method m = MemberFinder.getMethod(functionalInterface);
        boolean[] genericMap = genericMap(m);
        CompilationUnit cu = new CompilationUnit();
        cu.setPackage(getEvalPackage());
        ClassOrInterfaceDeclaration type = getType(name, cu);

        List<ClassOrInterfaceType> interfaces = getGenericInterface(parametrizationClasses, functionalInterface);
        type.setImplements(interfaces);

        if (environment == null) {
            environment = new HashMap<String, Class<?>>();
        }

        addGlobalVariables(environment, type);

        String initializeName = "setEnvironment";

        MethodDeclaration method = getSetEnvironmentMethod(environment, initializeName);

        ASTHelper.addMember(type, method);

        String evalcName = m.getName();

        java.lang.reflect.Type[] paramClasses = MemberFinder.resolveGenericParameters(m, parametrizationClasses);
        java.lang.reflect.Type returnClass = MemberFinder.resolveGenericReturn(m, parametrizationClasses);
        Class<?>[] exceptions = m.getExceptionTypes();

        code = checkCode(code, returnClass.getTypeName());

        method = getEvalMethod(code, paramNames, paramClasses, returnClass, evalcName, genericMap, exceptions);
        ASTHelper.addMember(type, method);
        return createFile(GENERATED_EVAL_PACKAGE + "." + name, cu.toString());
    }

    /**
     * Wraps method invocation
     *
     * @param className              The class name
     * @param targetClass            The class that owns the method
     * @param methodName             Method name
     * @param functionalInterface    The functional interface to invoke the call
     * @param parametrizationClasses Parametrization classes for the interface
     * @param isStatic               If the method is static
     * @return Class File with the call code wrapped
     * @throws StructuralIntercessionException
     */
    public static File generateMethodInvoker(String className, Class<?> targetClass, String methodName,
                                             Class<?> functionalInterface, Class<?>[] parametrizationClasses, boolean isStatic)
            throws StructuralIntercessionException {
        Method m = MemberFinder.getMethod(functionalInterface);
        boolean[] genericMap = genericMap(m);
        CompilationUnit cu = new CompilationUnit();
        cu.setPackage(getInvokerPackage());
        ClassOrInterfaceDeclaration type = getType(className, cu);

        List<ClassOrInterfaceType> interfaces = getGenericInterface(parametrizationClasses, functionalInterface);
        type.setImplements(interfaces);

        String invokerName = m.getName();
        MethodDeclaration method = null;
        if (isStatic) {
            method = getInvokerStaticMethod(MemberFinder.resolveGenericParameters(m, parametrizationClasses),
                    MemberFinder.resolveGenericReturn(m, parametrizationClasses), invokerName,
                    VersionTables.getNewVersion(targetClass), methodName, genericMap);
        } else {
            method = getInvokerInstanceMethod(MemberFinder.resolveGenericParameters(m, parametrizationClasses),
                    MemberFinder.resolveGenericReturn(m, parametrizationClasses), invokerName, targetClass, methodName,
                    genericMap);
        }
        ASTHelper.addMember(type, method);
        return createFile(GENERATED_INVOKER_PACKAGE + "." + className, cu.toString());
    }

    /**
     * Wraps fieldGetter invocation
     *
     * @param className              The class name
     * @param targetClass            The class that owns the field
     * @param fieldName              Field name
     * @param functionalInterface    The functional interface to make the call
     * @param parametrizationClasses Parametrization classes for the interface
     * @param isStatic               If the field is static
     * @return Class File with the call code wrapped
     * @throws StructuralIntercessionException
     */
    public static File generateFieldGetter(String className, Class<?> targetClass, String fieldName,
                                           Class<?> functionalInterface, Class<?>[] parametrizationClasses, boolean isStatic)
            throws StructuralIntercessionException {
        Method m = MemberFinder.getMethod(functionalInterface);
        boolean[] genericMap = genericMap(m);
        CompilationUnit cu = new CompilationUnit();
        cu.setPackage(getInvokerPackage());
        ClassOrInterfaceDeclaration type = getType(className, cu);

        List<ClassOrInterfaceType> interfaces = getGenericInterface(parametrizationClasses, functionalInterface);
        type.setImplements(interfaces);

        String invokerName = m.getName();
        MethodDeclaration method = null;
        if (isStatic) {
            method = staticFieldGetter(MemberFinder.resolveGenericParameters(m, parametrizationClasses),
                    MemberFinder.resolveGenericReturn(m, parametrizationClasses), invokerName,
                    VersionTables.getNewVersion(targetClass), fieldName, genericMap);
        } else {
            method = instanceFieldGetter(MemberFinder.resolveGenericParameters(m, parametrizationClasses),
                    MemberFinder.resolveGenericReturn(m, parametrizationClasses), invokerName, targetClass, fieldName,
                    genericMap);
        }
        ASTHelper.addMember(type, method);
        return createFile(GENERATED_INVOKER_PACKAGE + "." + className, cu.toString());
    }

    /**
     * Wraps fieldSetter invocation
     *
     * @param className              The class name
     * @param targetClass            The class that owns the field
     * @param fieldName              Field name
     * @param functionalInterface    The functional interface to make the call
     * @param parametrizationClasses Parametrization classes for the interface
     * @param isStatic               If the field is static
     * @return Class File with the call code wrapped
     * @throws StructuralIntercessionException
     */
    public static File generateFieldSetter(String className, Class<?> targetClass, String fieldName,
                                           Class<?> functionalInterface, Class<?>[] parametrizationClasses, boolean isStatic)
            throws StructuralIntercessionException {
        Method m = MemberFinder.getMethod(functionalInterface);
        boolean[] genericMap = genericMap(m);
        CompilationUnit cu = new CompilationUnit();
        cu.setPackage(getInvokerPackage());
        ClassOrInterfaceDeclaration type = getType(className, cu);

        List<ClassOrInterfaceType> interfaces = getGenericInterface(parametrizationClasses, functionalInterface);
        type.setImplements(interfaces);

        String invokerName = m.getName();
        MethodDeclaration method = null;
        if (isStatic) {
            method = staticFieldSetter(MemberFinder.resolveGenericParameters(m, parametrizationClasses),
                    MemberFinder.resolveGenericReturn(m, parametrizationClasses), invokerName,
                    VersionTables.getNewVersion(targetClass), fieldName, genericMap);
        } else {
            method = instanceFieldSetter(MemberFinder.resolveGenericParameters(m, parametrizationClasses),
                    MemberFinder.resolveGenericReturn(m, parametrizationClasses), invokerName, targetClass, fieldName,
                    genericMap);
        }
        ASTHelper.addMember(type, method);
        return createFile(GENERATED_INVOKER_PACKAGE + "." + className, cu.toString());
    }

    /**
     * Creates a method with a call for a static method
     *
     * @param parameters  Parameter types
     * @param returnType  Return type
     * @param invokerName Name of the method
     * @param lastVersion Class that owns the field
     * @param methodName  The method name
     * @param genericMap  Map with the generic parameters and generic return
     * @return The {@link MethodDeclaration} built
     * @throws StructuralIntercessionException
     */
    private static MethodDeclaration getInvokerStaticMethod(java.lang.reflect.Type[] parameters,
                                                            java.lang.reflect.Type returnType, String invokerName, Class<?> lastVersion, String methodName,
                                                            boolean[] genericMap) throws StructuralIntercessionException {
        ClassOrInterfaceType retClassOrInt;
        if (genericMap[genericMap.length - 1]) {
            retClassOrInt = new ClassOrInterfaceType(getWrapperName(returnType.getTypeName()));
        } else {
            retClassOrInt = new ClassOrInterfaceType(returnType.getTypeName());
        }
        MethodDeclaration method = new MethodDeclaration(MODIFIERS, retClassOrInt, invokerName);

        StringBuilder code = new StringBuilder();
        code = code.append(returnType.equals(void.class) ? "" : "return ");
        code = code.append(lastVersion.getName());
        code = code.append(".");
        code = code.append(methodName);
        code = code.append("(");
        for (int i = 0; i < parameters.length; i++) {
            String paramName = "param" + i;
            ClassOrInterfaceType type;
            if (genericMap[i]) {
                type = new ClassOrInterfaceType(getWrapperName(parameters[i].getTypeName()));
            } else {
                type = new ClassOrInterfaceType(parameters[i].getTypeName());
            }
            Parameter param = ASTHelper.createParameter(type, paramName);
            ASTHelper.addParameter(method, param);
            code = code.append(paramName);
            code = code.append(",");
        }
        String scode = code.substring(0, code.length() - 1);
        scode = scode.concat(");");
        try {
            method.setBody(JavaParser.parseBlock("{" + scode + "}"));
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        return method;
    }

    /**
     * Creates a method with a invoker call for an instance method
     *
     * @param parameters  Parameter types
     * @param returnType  Return type
     * @param invokerName Name of the method
     * @param targetClass Class that owns the field
     * @param methodName  The method name
     * @param genericMap  Map with the generic parameters and generic return
     * @return The {@link MethodDeclaration} built
     * @throws StructuralIntercessionException
     */
    private static MethodDeclaration getInvokerInstanceMethod(java.lang.reflect.Type[] parameters,
                                                              java.lang.reflect.Type returnType, String invokerName, Class<?> targetClass, String methodName,
                                                              boolean[] genericMap) throws StructuralIntercessionException {
        MethodDeclaration method;
        ClassOrInterfaceType retClassOrInt;
        if (genericMap[genericMap.length - 1]) {
            retClassOrInt = new ClassOrInterfaceType(getWrapperName(returnType.getTypeName()));
        } else {
            retClassOrInt = new ClassOrInterfaceType(returnType.getTypeName());
        }
        method = new MethodDeclaration(MODIFIERS, retClassOrInt, invokerName);

        StringBuilder code = new StringBuilder();
        code = code.append(returnType.equals(void.class) ? "" : "return ");
        boolean hasNewVersion = VersionTables.hasNewVersion(targetClass);
        if (!hasNewVersion) {
            code = code.append("param0.");
            code = code.append(methodName);
            code = code.append("(");
        } else {
            Class<?> lastVersion = VersionTables.getNewVersion(targetClass);
            code = code.append(lastVersion.getName());
            code = code.append("._");
            code = code.append(methodName);
            code = code.append("_invoker(");
        }
        for (int i = 0; i < parameters.length; i++) {
            String paramName = "param" + i;
            if (i != 0 || hasNewVersion) {
                code = code.append(paramName);
                code = code.append(",");
            }
            ClassOrInterfaceType type;
            if (genericMap[i]) {
                type = new ClassOrInterfaceType(getWrapperName(parameters[i].getTypeName()));
            } else {
                type = new ClassOrInterfaceType(parameters[i].getTypeName());
            }
            Parameter param = ASTHelper.createParameter(type, paramName);
            ASTHelper.addParameter(method, param);
        }
        String scode;
        if ((parameters.length == 1) && !hasNewVersion)
            scode = code.toString();
        else scode = code.substring(0, code.length() - 1);

        scode = scode.concat(");");
        try {
            method.setBody(JavaParser.parseBlock("{" + scode + "}"));
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        return method;
    }

    /**
     * Creates a method with a fieldGetter call for instance field
     *
     * @param parameters  Parameter types
     * @param returnType  Return type
     * @param invokerName Name of the method
     * @param targetClass Class that owns the field
     * @param fieldName   Field name
     * @param genericMap  Map with the generic parameters and generic return
     * @return The {@link MethodDeclaration} built
     * @throws StructuralIntercessionException
     */
    private static MethodDeclaration instanceFieldGetter(java.lang.reflect.Type[] parameters,
                                                         java.lang.reflect.Type returnType, String invokerName, Class<?> targetClass, String fieldName,
                                                         boolean[] genericMap) throws StructuralIntercessionException {
        MethodDeclaration method;
        ClassOrInterfaceType retClassOrInt;
        if (genericMap[genericMap.length - 1]) {
            retClassOrInt = new ClassOrInterfaceType(getWrapperName(returnType.getTypeName()));
        } else {
            retClassOrInt = new ClassOrInterfaceType(returnType.getTypeName());
        }
        method = new MethodDeclaration(MODIFIERS, retClassOrInt, invokerName);

        String code = "return ";
        Class<?> lastVersion = VersionTables.getNewVersion(targetClass);
        code = code.concat(lastVersion.getName());
        code = code.concat("._");
        code = code.concat(fieldName);
        code = code.concat("_fieldGetter(param0);");
        for (int i = 0; i < parameters.length; i++) {
            String paramName = "param" + i;
            ClassOrInterfaceType type = new ClassOrInterfaceType(parameters[i].getTypeName());
            Parameter param = ASTHelper.createParameter(type, paramName);
            ASTHelper.addParameter(method, param);
        }
        try {
            method.setBody(JavaParser.parseBlock("{" + code + "}"));
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        return method;
    }

    /**
     * Creates a method with a fieldSetter call for instance field
     *
     * @param parameters  Parameter types
     * @param returnType  Return type
     * @param invokerName Name of the method
     * @param targetClass Class that owns the field
     * @param fieldName   Field name
     * @param genericMap  Map with the generic parameters and generic return
     * @return The {@link MethodDeclaration} built
     * @throws StructuralIntercessionException
     */
    private static MethodDeclaration instanceFieldSetter(java.lang.reflect.Type[] parameters,
                                                         java.lang.reflect.Type returnType, String invokerName, Class<?> targetClass, String fieldName,
                                                         boolean[] genericMap) throws StructuralIntercessionException {
        MethodDeclaration method;
        method = new MethodDeclaration(MODIFIERS, ASTHelper.VOID_TYPE, invokerName);

        String code = "";
        Class<?> lastVersion = VersionTables.getNewVersion(targetClass);
        code = code.concat(lastVersion.getName());
        code = code.concat("._");
        code = code.concat(fieldName);
        code = code.concat("_fieldSetter(param0, param1);");
        for (int i = 0; i < parameters.length; i++) {
            String paramName = "param" + i;
            ClassOrInterfaceType type;
            if (genericMap[i]) {
                type = new ClassOrInterfaceType(getWrapperName(parameters[i].getTypeName()));
            } else {
                type = new ClassOrInterfaceType(parameters[i].getTypeName());
            }
            Parameter param = ASTHelper.createParameter(type, paramName);
            ASTHelper.addParameter(method, param);
        }
        try {
            method.setBody(JavaParser.parseBlock("{" + code + "}"));
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        return method;
    }

    /**
     * Creates a method with a Getter call for static field
     *
     * @param parameters  Parameter types
     * @param returnType  Return type
     * @param invokerName Name of the method
     * @param targetClass Class that owns the field
     * @param fieldName   Field name
     * @param genericMap  Map with the generic parameters and generic return
     * @return The {@link MethodDeclaration} built
     * @throws StructuralIntercessionException
     */
    private static MethodDeclaration staticFieldGetter(java.lang.reflect.Type[] parameters,
                                                       java.lang.reflect.Type returnType, String invokerName, Class<?> targetClass, String fieldName,
                                                       boolean[] genericMap) throws StructuralIntercessionException {
        MethodDeclaration method;
        ClassOrInterfaceType retClassOrInt;
        if (genericMap[genericMap.length - 1]) {
            retClassOrInt = new ClassOrInterfaceType(getWrapperName(returnType.getTypeName()));
        } else {
            retClassOrInt = new ClassOrInterfaceType(returnType.getTypeName());
        }
        method = new MethodDeclaration(MODIFIERS, retClassOrInt, invokerName);

        String code = "return ";
        Class<?> lastVersion = VersionTables.getNewVersion(targetClass);
        code = code.concat(lastVersion.getName());
        code = code.concat("._");
        code = code.concat(fieldName);
        code = code.concat("_getter();");
        try {
            method.setBody(JavaParser.parseBlock("{" + code + "}"));
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        return method;
    }

    /**
     * Creates a method with a Setter call for static field
     *
     * @param parameters  Parameter types
     * @param returnType  Return type
     * @param invokerName Name of the method
     * @param targetClass Class that owns the field
     * @param fieldName   Field name
     * @param genericMap  Map with the generic parameters and generic return
     * @return The {@link MethodDeclaration} built
     * @throws StructuralIntercessionException
     */
    private static MethodDeclaration staticFieldSetter(java.lang.reflect.Type[] parameters,
                                                       java.lang.reflect.Type returnType, String invokerName, Class<?> targetClass, String fieldName,
                                                       boolean[] genericMap) throws StructuralIntercessionException {
        MethodDeclaration method;
        method = new MethodDeclaration(MODIFIERS, ASTHelper.VOID_TYPE, invokerName);

        String code = "";
        Class<?> lastVersion = VersionTables.getNewVersion(targetClass);
        code = code.concat(lastVersion.getName());
        code = code.concat("._");
        code = code.concat(fieldName);
        code = code.concat("_setter(param0);");
        for (int i = 0; i < parameters.length; i++) {
            String paramName = "param" + i;
            ClassOrInterfaceType type;
            if (genericMap[i]) {
                type = new ClassOrInterfaceType(getWrapperName(parameters[i].getTypeName()));
            } else {
                type = new ClassOrInterfaceType(parameters[i].getTypeName());
            }
            Parameter param = ASTHelper.createParameter(type, paramName);
            ASTHelper.addParameter(method, param);
        }
        try {
            method.setBody(JavaParser.parseBlock("{" + code + "}"));
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        return method;
    }

    /**
     * Returns invoker package created
     *
     * @return {@link PackageDeclaration}
     */
    private static PackageDeclaration getInvokerPackage() {
        return new PackageDeclaration(ASTHelper.createNameExpr(GENERATED_INVOKER_PACKAGE));
    }

    /**
     * Builds the eval method with the dynamic code and the functional interface
     * method data.
     *
     * @param code         The dynamic code
     * @param paramNames   Parameter names
     * @param paramClasses Parameter types
     * @param returnType   Return type
     * @param evalName     Name of the method
     * @param genericMap   Map with the generic parameters and generic return
     * @param exceptions
     * @return The {@link MethodDeclaration} built
     * @throws StructuralIntercessionException
     */
    private static MethodDeclaration getEvalMethod(String code, String[] paramNames,
                                                   java.lang.reflect.Type[] paramClasses, java.lang.reflect.Type returnType, String evalName,
                                                   boolean[] genericMap, Class<?>[] exceptions) throws StructuralIntercessionException {
        MethodDeclaration method;
        ClassOrInterfaceType retClassOrInt;
        if (genericMap[genericMap.length - 1]) {
            retClassOrInt = new ClassOrInterfaceType(getWrapperName(returnType.getTypeName()));
        } else {
            retClassOrInt = new ClassOrInterfaceType(returnType.getTypeName());
        }
        method = new MethodDeclaration(MODIFIERS, retClassOrInt, evalName);
        try {
            method.setBody(JavaParser.parseBlock("{" + code + "}"));
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        for (int i = 0; i < paramNames.length; i++) {
            ClassOrInterfaceType type;
            if (genericMap[i]) {
                type = new ClassOrInterfaceType(getWrapperName(paramClasses[i].getTypeName()));
            } else {
                type = new ClassOrInterfaceType(paramClasses[i].getTypeName());
            }
            Parameter param = ASTHelper.createParameter(type, paramNames[i]);
            ASTHelper.addParameter(method, param);
        }
        if (method.getThrows() == null)
            method.setThrows(new ArrayList<NameExpr>());
        for (Class<?> exception : exceptions) {
            method.getThrows().add(JavaParserUtils.classToNameExpr(exception));
        }
        return method;
    }

    /**
     * Creates the method that implements the interface {@link EnvironmentSetUp} .
     * The body of the generated method cast al variables to the declared types to
     * set the values in the class.
     *
     * @param variables      The fields declarations
     * @param initializeName The name of the method
     * @return The {@link MethodDeclaration} built
     */
    private static MethodDeclaration getSetEnvironmentMethod(Map<String, Class<?>> variables, String initializeName) {
        MethodDeclaration method = new MethodDeclaration(ModifierSet.PUBLIC, ASTHelper.VOID_TYPE, initializeName);
        List<Statement> statements = new ArrayList<Statement>();
        for (String key : variables.keySet()) {
            NameExpr target = new NameExpr(key);
            NameExpr map = new NameExpr("environment");
            MethodCallExpr call = new MethodCallExpr(map, "get");
            ASTHelper.addArgument(call, new StringLiteralExpr(key));
            CastExpr castExpr = new CastExpr(JavaParserUtils.transform(variables.get(key)), call);
            AssignExpr assignExpr = new AssignExpr(target, castExpr, Operator.assign);
            statements.add(new ExpressionStmt(assignExpr));
        }
        BlockStmt body = new BlockStmt(statements);
        ClassOrInterfaceType mapType = new ClassOrInterfaceType(Map.class.getName());
        List<Type> types = new ArrayList<Type>();
        types.add(ASTHelper.createReferenceType("String", 0));
        types.add(ASTHelper.createReferenceType("Object", 0));
        mapType.setTypeArgs(types);
        Parameter param = ASTHelper.createParameter(mapType, "environment");
        ASTHelper.addParameter(method, param);
        method.setBody(body);
        return method;
    }

    /**
     * Adds global fields to the class
     *
     * @param variables Fields declarations
     * @param type      Class to modify
     */
    private static void addGlobalVariables(Map<String, Class<?>> variables, ClassOrInterfaceDeclaration type) {
        for (String key : variables.keySet()) {
            Type fieldType = JavaParserUtils.transform(variables.get(key));
            VariableDeclaratorId id = new VariableDeclaratorId(key);
            VariableDeclarator declarator = new VariableDeclarator(id);
            FieldDeclaration field = new FieldDeclaration(Modifier.PRIVATE, fieldType, declarator);
            ASTHelper.addMember(type, field);
        }
    }

    /**
     * Return a parametrized interface list
     *
     * @param generics       Parametrization classes
     * @param interfaceClass Interface class to parametrize
     * @return List of interfaces
     */
    private static List<ClassOrInterfaceType> getGenericInterface(Class<?>[] generics, Class<?> interfaceClass) {
        List<ClassOrInterfaceType> interfaces = new ArrayList<ClassOrInterfaceType>();
        ClassOrInterfaceType interfaceType = new ClassOrInterfaceType(interfaceClass.getName());
        List<Type> typeArgs = new ArrayList<Type>();
        for (Class<?> type : generics) {
            if (type.isPrimitive()) {
                typeArgs.add(JavaParserUtils.transform(getWrapperClass(type)));
            } else {
                typeArgs.add(JavaParserUtils.transform(type));
            }
        }
        if (!typeArgs.isEmpty())
            interfaceType.setTypeArgs(typeArgs);
        interfaces.add(interfaceType);
        return interfaces;
    }

    /**
     * Gets the type inside the compilation unit
     *
     * @param name Name of the class
     * @param cu   {@link CompilationUnit}
     * @return {@link ClassOrInterfaceDeclaration} with the class
     */
    private static ClassOrInterfaceDeclaration getType(String name, CompilationUnit cu) {
        ClassOrInterfaceDeclaration type = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC, false, name);
        ASTHelper.addTypeDeclaration(cu, type);
        return type;
    }

    /**
     * Returns eval package created
     *
     * @return {@link PackageDeclaration}
     */
    private static PackageDeclaration getEvalPackage() {
        return new PackageDeclaration(ASTHelper.createNameExpr(GENERATED_EVAL_PACKAGE));
    }

    /**
     * Check if the code needs modifications to compile as return word or semicolon
     * at the end of the sentence.
     *
     * @param code        Dynamic code
     * @param returnClass Return type of the enclosing method
     * @return The code checked and modified if needed
     */
    private static String checkCode(String code, String returnClass) {
        if (!returnClass.equals(void.class.getName()))
            code = checkReturn(code);
        code = checkSemiColon(code);
        return code;
    }

    /**
     * Check if the code needs a semicolon
     *
     * @param code Dynamic code
     * @return The code with semicolon if needed
     */
    private static String checkSemiColon(String code) {
        if (!code.contains(";")) {
            return code.concat(";");
        }
        return code;
    }

    /**
     * Check if the code needs to insert return word and insert it if it needed
     *
     * @param code Dynamic code
     * @return The code with return word if needed
     */
    private static String checkReturn(String code) {
        if (!code.contains("return")) {
            long numberOfSemiColons = code.chars().filter(c -> c == ';').count();
            if (numberOfSemiColons <= 1 && !code.contains("throw")) {
                return "return ".concat(code);
            }
        }
        return code;
    }

    /**
     * Creates a java file inside the folder generated_src
     *
     * @param name The name of the file
     * @param text The content of the file
     * @return The {@link File} object
     * @throws StructuralIntercessionException
     */
    public static File createFile(String name, String text) throws StructuralIntercessionException {
        File file = new File("generated_src/" + name.replace('.', '/') + ".java");
        BufferedWriter writer;
        try {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        return file;
    }

    /**
     * Returns the wrapper class or null if the class provided is not a primitive
     * type
     *
     * @param primitiveClass Class to get wrapper
     * @return Wrapper class or null
     */
    private static Class<?> getWrapperClass(Class<?> primitiveClass) {
        return WrapperClassGenerator.wrapperClassMap.get(primitiveClass.getName());
    }

    /**
     * Obtains the name of the wrapper type if it is a primitive type, if not,
     * returns the same name
     *
     * @param name The name of the class
     * @return The wrapper class if the name represents a primitive type or the same
     */
    private static String getWrapperName(String name) {
        Class<?> toRet = WrapperClassGenerator.wrapperClassMap.get(name);
        if (toRet == null)
            return name;
        return toRet.getName();
    }

    /**
     * Provides an array where it is specified wich arguments are generic or if the
     * return is generic in a method. The format of the array is
     * {@code [arg1, arg2, ..., argN, return]}
     *
     * @param m The method to map
     * @return Boolean array with the information
     */
    private static boolean[] genericMap(Method m) {
        java.lang.reflect.Type[] params = m.getGenericParameterTypes();
        boolean[] map = new boolean[params.length + 1];
        for (int i = 0; i < params.length; i++) {
            map[i] = params[i] instanceof TypeVariable<?>;
        }
        map[map.length - 1] = m.getGenericReturnType() instanceof TypeVariable<?>;
        return map;
    }

}
