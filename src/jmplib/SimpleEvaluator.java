package jmplib;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import jmplib.agent.UpdaterAgent;
import jmplib.compiler.ClassCompiler;
import jmplib.compiler.PolyglotAdapter;
import jmplib.config.JMPlibConfig;
import jmplib.exceptions.CompilationFailedException;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.EvalInvokerData;
import jmplib.invokers.MemberInvokerData;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.*;
import jmplib.util.intercessor.IntercessorTypeConversion;
import jmplib.util.intercessor.IntercessorUtils;
import jmplib.util.intercessor.IntercessorValidators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class SimpleEvaluator implements IEvaluator {
    private static int INVOKER_COUNTER = 0;
    private static SimpleEvaluator _instance;
    private static int evalVersion = 0;

    /* **************************************
     * INSTANCE CREATION
     **************************************/

    /**
     * Use createEvaluator instead.
     */
    public SimpleEvaluator() {
    }


    /**
     * {@inheritDoc}
     */
    public IEvaluator createEvaluator() {
        if (_instance == null)
            _instance = new SimpleEvaluator();

        return _instance;
    }

    /* **************************************
     * ACCESS TO METHODS AND FIELDS
     **************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getMethodInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
        Class<T> functionalInterface = info.getFunctionalInterface();
        int modifiers = info.getModifiers();
        Class<?>[] parametrizationClasses = IntercessorTypeConversion.type2JavaClass(info.getParametrizationClasses());
        Class<?> clazz = IntercessorTypeConversion.type2JavaClass(type);

        // Checking params
        IntercessorValidators.checkInvokerParams(clazz, name, functionalInterface, modifiers);
        try {
            IntercessorUtils.checkVisibility(clazz, name, functionalInterface, modifiers, parametrizationClasses);
        } catch (NoSuchMethodException e) {
            throw new StructuralIntercessionException(
                    "getMethodInvoker could not be executed due to the following reasons: " + e.getMessage(), e);
        }
        boolean isStatic = Modifier.isStatic(modifiers);
        String className = "Generated_Invoker_Class_" + ++INVOKER_COUNTER;
        File file = null;
        try {
            file = WrapperClassGenerator.generateMethodInvoker(className, clazz, name, functionalInterface,
                    parametrizationClasses, isStatic);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "getInvoker could not be executed due to the following reasons: " + e.getMessage(), e.getCause());
        }
        T invoker = null;
        try {
            invoker = IntercessorUtils.compileFile(file, className, WrapperClassGenerator.GENERATED_INVOKER_PACKAGE);
        } catch (CompilationFailedException e) {
            throw new StructuralIntercessionException(
                    "getInvoker could not be executed due to the following reasons: " + e.getMessage(), e);
        }
        return invoker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getFieldInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
        Class<T> functionalInterface = info.getFunctionalInterface();
        int modifiers = info.getModifiers();
        Class<?>[] parametrizationClasses = IntercessorTypeConversion.type2JavaClass(info.getParametrizationClasses());
        Class<?> clazz = IntercessorTypeConversion.type2JavaClass(type);

        // Checking params
        IntercessorValidators.checkInvokerParams(clazz, name, functionalInterface, modifiers);
        try {
            IntercessorUtils.checkVisibility(clazz, name);
        } catch (NoSuchFieldException e) {
            throw new StructuralIntercessionException(
                    "getFieldInvoker could not be executed due to the following reasons: " + e.getMessage(), e);
        }
        boolean isStatic = Modifier.isStatic(modifiers);
        String className = "Generated_Invoker_Class_" + ++INVOKER_COUNTER;
        File file = null;
        try {
            file = WrapperClassGenerator.generateFieldGetter(className, clazz, name, functionalInterface,
                    parametrizationClasses, isStatic);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "getFieldInvoker could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }
        T invoker = null;
        try {
            invoker = IntercessorUtils.compileFile(file, className, WrapperClassGenerator.GENERATED_INVOKER_PACKAGE);
        } catch (CompilationFailedException e) {
            throw new StructuralIntercessionException(
                    "getFieldInvoker could not be executed due to the following reasons: " + e.getMessage(), e);
        }
        return invoker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T setFieldInvoker(Type type, String name, MemberInvokerData<T> info) throws StructuralIntercessionException {
        Class<T> functionalInterface = info.getFunctionalInterface();
        int modifiers = info.getModifiers();
        Class<?>[] parametrizationClasses = IntercessorTypeConversion.type2JavaClass(info.getParametrizationClasses());
        Class<?> clazz = IntercessorTypeConversion.type2JavaClass(type);

        // Checking params
        IntercessorValidators.checkInvokerParams(clazz, name, functionalInterface, modifiers);
        try {
            IntercessorUtils.checkVisibility(clazz, name);
        } catch (NoSuchFieldException e) {
            throw new StructuralIntercessionException(
                    "setFieldInvoker could not be executed due to the following reasons: " + e.getMessage(), e);
        }
        boolean isStatic = Modifier.isStatic(modifiers);
        String className = "Generated_Invoker_Class_" + ++INVOKER_COUNTER;
        File file = null;
        try {
            file = WrapperClassGenerator.generateFieldSetter(className, clazz, name, functionalInterface,
                    parametrizationClasses, isStatic);
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "setFieldInvoker could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }
        T invoker = null;
        try {
            invoker = IntercessorUtils.compileFile(file, className, WrapperClassGenerator.GENERATED_INVOKER_PACKAGE);
        } catch (CompilationFailedException e) {
            throw new StructuralIntercessionException(
                    "setFieldInvoker could not be executed due to the following reasons: " + e.getMessage(), e);
        }
        return invoker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> createEmptyClass(String packageName, String className, AnnotatedElement... importClasses)
            throws StructuralIntercessionException {
        String[] imports = IntercessorTypeConversion.getImportString(importClasses);
        StringBuffer source = new StringBuffer("");

        if (packageName != null) {
            if (!packageName.startsWith("package "))
                source.append("package ");

            source.append(packageName);

            if (!packageName.endsWith(";"))
                source.append(';');
        }

        source.append("\n\n");

        if (imports != null) {
            IntercessorValidators.checkAddImportParameters(imports);
            for (String imp : imports) {
                source.append(imp);
                source.append("\n");
            }
        }
        source.append("public class ");
        source.append(className);
        source.append("{\n}\n");

        return exec(source.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> exec(String classSource) throws StructuralIntercessionException {
        File source = null;
        try {
            boolean inheritance = false;
            CompilationUnit unit = JavaParserUtils.parse(classSource);
            String pack = unit.getPackage().getName().toString();
            TypeDeclaration type = unit.getTypes().get(0);
            if (type instanceof ClassOrInterfaceDeclaration) {
                ClassOrInterfaceDeclaration decl = (ClassOrInterfaceDeclaration) type;
                inheritance = decl.getExtends() != null && decl.getExtends().size() > 0;
            }
            String name = pack + "." + type.getName();
            String originalSrcPath = JMPlibConfig.getInstance().getOriginalSrcPath();
            source = new File(originalSrcPath.concat(name.replace('.', '/').concat(".java")));
            if (!source.exists()) {
                source.createNewFile();
            } else {
                throw new StructuralIntercessionException("The class already exist in the src folder");
            }
            FileWriter writer = new FileWriter(source);
            if (!inheritance) {
                writer.write(classSource);
            } else {
                EvaluatorUtils.setEmptyBodies(unit);
                writer.write(unit.toStringWithoutComments());
            }
            writer.close();
            JavaSourceFromString[] instrumented = PolyglotAdapter.instrument(source);
            ClassCompiler.getInstance().compile(ClassPathUtil.getApplicationClassPath(), instrumented);
            Class<?> clazz = Class.forName(name);
            if (!clazz.isInterface()) {
                if (inheritance) {
                    writer = new FileWriter(source);
                    writer.write(classSource);
                    writer.close();
                }
                SourceCodeCache.getInstance().getClassContent(clazz);
                InheritanceTables.put(clazz.getSuperclass(), clazz);
                UpdaterAgent.updateClass(clazz);
                new SimpleIntercessor().createIntercessor().addField(clazz, new jmplib.reflect.Field(boolean.class, "__newclass__"));
            }
            source.delete();
            return clazz;
        } catch (ParseException | IOException | CompilationFailedException | ClassNotFoundException
                | StructuralIntercessionException e) {
            if (source != null) {
                source.delete();
            }
            throw new StructuralIntercessionException(
                    "exec could not be executed due to the following reasons: " + e.getMessage(), e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T generateEvalInvoker(String code, EvalInvokerData<T> invokerData)
            throws StructuralIntercessionException {
        String name = "Generated_Eval_Class_" + ++evalVersion;
        File file = null;
        try {
            Class<?>[] pClasses = IntercessorTypeConversion.type2JavaClass(invokerData.getParametrizationClasses());
            // Ensure that the imports are in the correct format.
            if (invokerData.getImports() != null) {
                String[] importStr = IntercessorTypeConversion.getImportString(invokerData.getImports());
                IntercessorValidators.checkAddImportParameters(importStr);
                file = WrapperClassGenerator.generateEvalClass(name, code, invokerData.getFunctionalInterface(),
                        invokerData.getParamNames(), importStr, pClasses);
            } else {
                file = WrapperClassGenerator.generateEvalClass(name, code, invokerData.getFunctionalInterface(),
                        invokerData.getParamNames(), pClasses, invokerData.getEnvironment());
            }
        } catch (StructuralIntercessionException e) {
            throw new StructuralIntercessionException(
                    "generateEvalInvoker could not be executed due to the following reasons: " + e.getMessage(),
                    e.getCause());
        }
        T evalInvoker;
        try {
            evalInvoker = IntercessorUtils.compileFile(file, name, WrapperClassGenerator.GENERATED_EVAL_PACKAGE);
        } catch (CompilationFailedException e) {
            throw new StructuralIntercessionException(
                    "generateEvalInvoker could not be executed due to the following reasons: " + e.getMessage(), e);
        }
        return evalInvoker;
    }

}
