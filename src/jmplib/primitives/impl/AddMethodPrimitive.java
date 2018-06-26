package jmplib.primitives.impl;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.type.Type;
import jmplib.annotations.AuxiliaryMethod;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.MethodPrimitive;
import jmplib.sourcecode.ClassContent;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * This class adds new methods to existing class source code. This class doesn't
 * compile this code, only generates it.
 *
 * @author Ignacio Lagartos, Jose Manuel Redondo
 */
@ExcludeFromJMPLib
public class AddMethodPrimitive extends MethodPrimitive {
    public static final int MODIFIERS = Modifier.PUBLIC | Modifier.STATIC | ModifierSet.FINAL;

    protected MethodDeclaration declaration = null, invoker = null;
    jmplib.reflect.TypeVariable<?>[] methodTypeParameters;
    private String name, body;
    private String[] paramNames;
    private java.lang.reflect.Type[] genericParamTypes;
    private java.lang.reflect.Type genericReturnType;
    private List<BodyDeclaration> oldMembers = new ArrayList<BodyDeclaration>();

    public AddMethodPrimitive(ClassContent classContent, String name, Class<?> returnClass, Class<?>[] parameterClasses,
                              Class<?>[] exceptions, String[] paramNames, String body, int modifiers) {
        super(classContent, returnClass, parameterClasses, exceptions, modifiers);
        this.body = body;
        this.name = name;
        this.paramNames = paramNames;
    }

    public AddMethodPrimitive(ClassContent classContent, String name, Class<?> returnClass, Class<?>[] parameterClasses,
                              Class<?>[] exceptions, String[] paramNames, String body, int modifiers,
                              java.lang.reflect.Type[] genericParamTypes, java.lang.reflect.Type genericReturnType,
                              jmplib.reflect.TypeVariable<?>[] methodTypeParameters) {
        super(classContent, returnClass, parameterClasses, exceptions, modifiers);
        this.body = body;
        this.name = name;
        this.paramNames = paramNames;
        this.genericParamTypes = genericParamTypes;
        this.genericReturnType = genericReturnType;
        this.methodTypeParameters = methodTypeParameters;
    }

    public AddMethodPrimitive(ClassContent classContent, String name, Class<?> returnClass, Class<?>[] parameterClasses,
                              String[] paramNames, String body, int modifiers) {
        super(classContent, returnClass, parameterClasses, modifiers);
        this.body = body;
        this.name = name;
        this.paramNames = paramNames;
    }

    /**
     * Generates the members needed in the primitive execution
     *
     * @param name       The name of the method
     * @param paramNames Parameter names
     * @param body       Body of the method
     * @throws ParseException
     * @throws StructuralIntercessionException
     */
    protected void initializeFields(String name, String[] paramNames, String body)
            throws ParseException, StructuralIntercessionException {
        generateInvoker(name);
        generateMethod(name, paramNames, body);
    }

    /**
     * Generates invoker method
     *
     * @param name The name of the method
     * @throws ParseException
     */
    protected void generateInvoker(String name) throws ParseException {
        Type returnType = JavaParserUtils.transform(returnClass);
        List<Parameter> parameter = new ArrayList<Parameter>();
        parameter.add(new Parameter(JavaParserUtils.transform(clazz), new VariableDeclaratorId("o")));
        String paramsNames = "";
        for (int i = 0; i < parameterClasses.length; i++) {
            parameter.add(new Parameter(JavaParserUtils.transform(parameterClasses[i]),
                    new VariableDeclaratorId("param" + i)));
            paramsNames += "param" + i + ", ";
        }
        if (!paramsNames.isEmpty())
            paramsNames = paramsNames.substring(0, paramsNames.length() - 2);
        List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
        NameExpr exp = JavaParserUtils.classToNameExpr(AuxiliaryMethod.class);
        annotations.add(new NormalAnnotationExpr(exp, null));
        invoker = new MethodDeclaration(AddMethodPrimitive.MODIFIERS, returnType, "_" + name + "_invoker", parameter);
        invoker.setAnnotations(annotations);
        setThrows(invoker);

        invoker.setBody(JavaParser.parseBlock(getBodyInvoker(name, paramsNames)));
    }


    /**
     * Generates the method declaration
     *
     * @param name       The name of the method
     * @param paramNames Parameter names
     * @param body       Body of the method
     * @throws ParseException
     * @throws StructuralIntercessionException
     */
    protected void generateMethod(String name, String[] paramNames, String body)
            throws ParseException, StructuralIntercessionException {
        Type returnType = null;
        if (this.genericReturnType == null)
            returnType = JavaParserUtils.transform(returnClass);
        else
            returnType = JavaParserUtils.transform(genericReturnType);

        if (parameterClasses == null || parameterClasses.length == 0) {
            declaration = new MethodDeclaration(modifiers, returnType, name);
        } else {
            declaration = new MethodDeclaration(modifiers, returnType, name,
                    generateParams(name, paramNames, returnType));
        }

        if (this.methodTypeParameters != null) {
            List<TypeParameter> mtp = new ArrayList<TypeParameter>();
            for (jmplib.reflect.TypeVariable<?> t : this.methodTypeParameters) {
                TypeParameter tp = new TypeParameter();
                tp.setName(t.getName());
                mtp.add(tp);
            }
            declaration.setTypeParameters(mtp);
        }

        setThrows(declaration);
        generateBody(body);
    }

    /**
     * Generates the parameter list of the method and creates the declaration
     *
     * @param name       Name of the method
     * @param paramNames Parameter names
     * @param returnType The return type
     * @throws StructuralIntercessionException
     */
    private List<Parameter> generateParams(String name, String[] paramNames, Type returnType)
            throws StructuralIntercessionException {
        List<Parameter> parameters = new ArrayList<Parameter>();
        if (Modifier.isStatic(modifiers) && paramNames.length != parameterClasses.length) {
            throw new StructuralIntercessionException("Adding static methods, the number of params "
                    + "in the interface must match with the parameter names");
        }

        if ((this.genericParamTypes == null) || (this.genericParamTypes.length == 0)) {
            for (int i = 0; i < parameterClasses.length; i++) {
                parameters.add(new Parameter(JavaParserUtils.transform(parameterClasses[i]),
                        new VariableDeclaratorId(paramNames[i])));
            }
        } else {
            for (int i = 0; i < genericParamTypes.length; i++) {
                parameters.add(new Parameter(JavaParserUtils.transform(genericParamTypes[i]),
                        new VariableDeclaratorId(paramNames[i])));
            }
        }
        return parameters;
    }

    /**
     * Parse the body of the method
     *
     * @param body Source code of the method
     * @throws ParseException
     */
    protected void generateBody(String body) throws ParseException {
        if (Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers)) {
            // Do nothing
        } else {
            declaration.setBody(JavaParser.parseBlock("{" + body + "}"));
        }
    }

    /**
     * Adds the method and the invoker to the source code of the class
     */
    @Override
    protected void executePrimitive() throws StructuralIntercessionException {
        // Create the new methods
        try {
            initializeFields(name, paramNames, body);
        } catch (ParseException e) {
            throw new StructuralIntercessionException(e.getMessage(), e);
        }
        CompilationUnit unit;
        try {
            unit = JavaParserUtils.parse(classContent.getContent());
        } catch (ParseException e) {
            throw new StructuralIntercessionException("An exception was thrown parsing the class. " + e.getMessage(),
                    e);
        }
        TypeDeclaration td = JavaParserUtils.searchType(unit,
                clazz.getName() + "_NewVersion_" + classContent.getVersion());

        //Save old members
        for (BodyDeclaration member : td.getMembers())
            oldMembers.add(member);

        td.getMembers().add(declaration);
        if (!Modifier.isStatic(modifiers)) {
            td.getMembers().add(invoker);
        }
        classContent.setContent(unit.toString());
    }

    /**
     * Reverts the changes
     */
    @Override
    protected void undoPrimitive() throws StructuralIntercessionException {
        CompilationUnit unit;
        try {
            unit = JavaParserUtils.parse(classContent.getContent());
        } catch (ParseException e) {
            throw new StructuralIntercessionException("An exception was thrown parsing the class. " + e.getMessage(),
                    e);
        }
        TypeDeclaration td = JavaParserUtils.searchType(unit,
                clazz.getName() + "_NewVersion_" + classContent.getVersion());
        int numberOfMethods = td.getMembers().size();
        int expectedNumber = 0;

        td.setMembers(oldMembers);
        //td.getMembers().remove(declaration);

        if (!Modifier.isStatic(modifiers)) {
            //td.getMembers().remove(invoker);
            expectedNumber = numberOfMethods - 2;
        } else {
            expectedNumber = numberOfMethods - 1;
        }
        if (expectedNumber != td.getMembers().size()) {
            throw new RuntimeException(
                    "An error occurred performing the undo action. " + "The methods didn't remove correctly.");
        }
        classContent.setContent(unit.toString());
    }
}
