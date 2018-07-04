package jmplib.asm.visitor;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import jmplib.annotations.AuxiliaryMethod;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.annotations.NoRedirect;
import jmplib.asm.util.ASMUtils;
import jmplib.config.JMPlibConfig;
import jmplib.util.Templates;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This visitor analyses the class and build a list of new auxiliary fields
 * needed to support the library.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public class ClassCacherVisitor extends ClassVisitor {

    private static Map<Integer, Class<?>> numericTypes = ASMUtils.numericTypes;
    private Class<?> clazz;
    private List<BodyDeclaration> declarations = new ArrayList<>();
    private boolean _defaultConstructor = false;

    public ClassCacherVisitor(int api, ClassVisitor visitor, Class<?> clazz) {
        super(api, visitor);
        this.clazz = clazz;
    }

    public ClassCacherVisitor(int api, Class<?> clazz) {
        super(api);
        this.clazz = clazz;
    }

    /**
     * Adds one invoker for each instance method
     */
    @Override
    public MethodVisitor visitMethod(int arg0, String arg1, String arg2,
                                     String arg3, String[] arg4) {
        if (arg1.equals("<init>") && arg2.equals("()V")) {
            _defaultConstructor = true;
            return super.visitMethod(arg0, arg1, arg2, arg3, arg4);
        }
        if (arg1.equals("<init>") || arg1.equals("<clinit>")
                || Modifier.isStatic(arg0)) {
            return super.visitMethod(arg0, arg1, arg2, arg3, arg4);
        }
        generateInvoker(arg1, arg2, arg4);
        return super.visitMethod(arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Adds auxiliary method for each field
     */
    @Override
    public FieldVisitor visitField(int arg0, String arg1, String arg2,
                                   String arg3, Object arg4) {
        if (Modifier.isStatic(arg0)) {
            generateGetterAndSetter(Type.getType(arg2).getClassName(), arg1,
                    arg0);
            if (numericTypes.containsKey(arg2.hashCode())) {
                generateStaticUnaryMethod(arg2, arg1, arg0);
            }
        } else {
            generateInstanceFieldGetterAndSetter(arg2, arg1, arg0);
            if (numericTypes.containsKey(arg2.hashCode())) {
                generateInstanceUnaryMethod(arg2, arg1, arg0);
            }
        }
        return super.visitField(arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Generates the helper methods for an instance field
     *
     * @param type      The type of the field
     * @param name      The name of the field
     * @param modifiers The modifiers of the field
     */
    private void generateInstanceFieldGetterAndSetter(String type, String name,
                                                      int modifiers) {
        MethodDeclaration getter = null, setter = null;
        Type fieldType = Type.getType(type);
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(
                fieldType.getClassName());
        ClassOrInterfaceType voidClassOrInterfaceType = new ClassOrInterfaceType(
                void.class.getName());

        List<Parameter> getterParams = new ArrayList<>();
        getterParams.add(new Parameter(
                new ClassOrInterfaceType(clazz.getName()),
                new VariableDeclaratorId("o")));
        List<Parameter> setterParams = new ArrayList<>();
        setterParams.add(new Parameter(
                new ClassOrInterfaceType(clazz.getName()),
                new VariableDeclaratorId("o")));
        setterParams.add(new Parameter(classOrInterfaceType,
                new VariableDeclaratorId("value")));

        List<AnnotationExpr> annotations = new ArrayList<>();
        annotations.add(new NormalAnnotationExpr(new NameExpr(
                AuxiliaryMethod.class.getName()),
                new ArrayList<>()));

        getter = new MethodDeclaration(getAuxiliaryMethodVisibility(modifiers)
                | Modifier.STATIC, classOrInterfaceType, "_" + name
                + "_fieldGetter", getterParams);
        setter = new MethodDeclaration(getAuxiliaryMethodVisibility(modifiers)
                | Modifier.STATIC, voidClassOrInterfaceType, "_" + name
                + "_fieldSetter", setterParams);

        getter.setAnnotations(annotations);
        setter.setAnnotations(annotations);


        String bodyGetter;
        String bodySetter;
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
            Object[] args = {name, //%1
                    clazz.getSimpleName() + "_NewVersion_0", //%2
                    clazz.getSimpleName(), //%3
                    fieldType.getClassName(), //%4
                    clazz.getSimpleName() + "_NewVersion_0"}; //%5
            bodyGetter = String.format(
                    Templates.THREAD_SAFE_FIELD_GETTER_TEMPLATE, args);
                    //Templates.FIELD_GETTER_TEMPLATE, args);
            bodySetter = String.format(
                    Templates.THREAD_SAFE_FIELD_SETTER_TEMPLATE, args);
        } else {
            Object[] args = {name, clazz.getSimpleName() + "_NewVersion_0",
                    clazz.getSimpleName()};
            bodyGetter = String.format(
                    Templates.FIELD_GETTER_TEMPLATE, args);
            bodySetter = String.format(
                    Templates.FIELD_SETTER_TEMPLATE, args);
        }
        try {
            getter.setBody(JavaParser.parseBlock(bodyGetter));
            setter.setBody(JavaParser.parseBlock(bodySetter));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        declarations.add(getter);
        if (!Modifier.isFinal(modifiers))
            declarations.add(setter);
    }

    /**
     * Generates auxiliary methods for static fields
     *
     * @param type      The type of the field
     * @param name      The name of the field
     * @param modifiers The modifiers of the field
     */
    private void generateGetterAndSetter(String type, String name, int modifiers) {
        MethodDeclaration getter = null, setter = null;
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(
                type);
        List<AnnotationExpr> annotations = new ArrayList<>();
        annotations.add(new NormalAnnotationExpr(new NameExpr(
                AuxiliaryMethod.class.getName()),
                new ArrayList<>()));
        getter = new MethodDeclaration(getAuxiliaryMethodVisibility(modifiers)
                | Modifier.STATIC, classOrInterfaceType, "_" + name + "_getter");
        getter.setAnnotations(annotations);
        List<Parameter> parameter = new ArrayList<>();
        parameter.add(new Parameter(classOrInterfaceType,
                new VariableDeclaratorId("newValue")));
        setter = new MethodDeclaration(getAuxiliaryMethodVisibility(modifiers)
                | Modifier.STATIC, new ClassOrInterfaceType("void"), "_" + name
                + "_setter", parameter);
        setter.setAnnotations(annotations);
        try {
            getter.setBody(JavaParser.parseBlock("{ return " + clazz.getName()
                    + "._" + name + "_getter(); }"));
            setter.setBody(JavaParser.parseBlock("{" + clazz.getName() + "._"
                    + name + "_setter(newValue); }"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        declarations.add(getter);
        if (!Modifier.isFinal(modifiers))
            declarations.add(setter);
    }

    /**
     * Generates an invoker for an instance method
     *
     * @param name       The name of the method
     * @param desc       The descriptor of the method
     * @param exceptions The exceptions of the method
     */
    private void generateInvoker(String name, String desc, String[] exceptions) {
        MethodDeclaration invoker;
        String returnClassName = Type.getReturnType(desc).getClassName();
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(
                returnClassName);
        List<Parameter> parameter = new ArrayList<>();
        parameter.add(new Parameter(new ClassOrInterfaceType(clazz.getName()),
                new VariableDeclaratorId("o")));
        Type[] types = Type.getArgumentTypes(desc);
        StringBuilder sbParamsNames = new StringBuilder();
        StringBuilder sbParamsTypes = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            parameter.add(new Parameter(new ClassOrInterfaceType(types[i]
                    .getClassName()), new VariableDeclaratorId("param" + i)));
            sbParamsNames.append("param" + i + ", ");
            sbParamsTypes.append(types[i].getClassName() + ".class, ");
        }
        String paramsNames = sbParamsNames.toString();
        String paramTypes = sbParamsTypes.toString();
        if (!paramsNames.isEmpty())
            paramsNames = paramsNames.substring(0, paramsNames.length() - 2);
        if (paramTypes.endsWith(", "))
            paramTypes = paramTypes.substring(0, paramTypes.length() - 2);

        List<AnnotationExpr> annotations = new ArrayList<>();
        annotations.add(new NormalAnnotationExpr(new NameExpr(
                AuxiliaryMethod.class.getName()),
                new ArrayList<MemberValuePair>()));
        invoker = new MethodDeclaration(Modifier.PUBLIC | Modifier.STATIC,
                classOrInterfaceType, "_" + name + "_invoker", parameter);
        invoker.setAnnotations(annotations);
        if (exceptions != null) {
            List<NameExpr> list = new ArrayList<>();
            for (String exception : exceptions) {
                list.add(new NameExpr(exception.replace('/', '.')));
            }
            invoker.setThrows(list);
        }


        String bodyInvoker;
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
            Object[] args = {clazz.getSimpleName(), //%1
                    name, //%2
                    clazz.getSimpleName() + "_NewVersion_0", //%3
                    paramsNames, //%4
                    (returnClassName.equals("void") ? "" : returnClassName + " ret_value = "), //%5
                    (returnClassName.equals("void") ? "" : " return ret_value;"), //%6
                    (returnClassName.equals("void") ? "" : "(" + returnClassName+")"), //%7
                    (paramsNames.equals("") ? "" : ", " + paramsNames), //%8
                    (paramTypes.equals("") ? "" : ", " + paramTypes)}; //%9
            bodyInvoker = String.format(
                    Templates.THREAD_SAFE_INVOKER_BODY_TEMPLATE, args);
        } else {
            Object[] args = {clazz.getSimpleName(), name,
                    clazz.getSimpleName() + "_NewVersion_0", paramsNames,
                    (returnClassName.equals("void") ? "" : "return ")};
            bodyInvoker = String.format(
                    Templates.INVOKER_BODY_TEMPLATE, args);
        }
        try {
            invoker.setBody(JavaParser.parseBlock(bodyInvoker));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        declarations.add(invoker);
    }

    /**
     * Obtains the list of declarations to support the functionality of the
     * library. This declarations are prepared to be added directly to the class
     * by Java Parser.
     *
     * @return
     */
    public List<BodyDeclaration> getDeclarations() {
        return declarations;
    }

    /**
     * Adds the _creator method
     */
    @Override
    public void visitEnd() {
        MethodDeclaration creator = generateCreator();
        declarations.add(creator);
        if (!_defaultConstructor) {
            ConstructorDeclaration constructor = new ConstructorDeclaration(
                    Modifier.PUBLIC, clazz.getSimpleName());
            constructor.setBlock(new BlockStmt());
            declarations.add(constructor);
        }
        super.visitEnd();
    }

    private MethodDeclaration generateCreator() {
        MethodDeclaration creator = null;
        String returnClassName = Type.getType(void.class).getClassName();
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(
                returnClassName);
        List<Parameter> parameter = new ArrayList<>();
        parameter.add(new Parameter(new ClassOrInterfaceType(clazz.getName()),
                new VariableDeclaratorId("o")));
        List<AnnotationExpr> annotations = new ArrayList<>();
        annotations.add(new NormalAnnotationExpr(new NameExpr(NoRedirect.class
                .getName()), new ArrayList<>()));
        annotations.add(new NormalAnnotationExpr(new NameExpr(
                AuxiliaryMethod.class.getName()),
                new ArrayList<>()));
        creator = new MethodDeclaration(Modifier.PRIVATE | Modifier.STATIC,
                classOrInterfaceType, "_creator", parameter);
        creator.setAnnotations(annotations);

        Object[] args = {clazz.getSimpleName() + "_NewVersion_0",
                clazz.getSimpleName()};

        String bodyCreator;
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe())
            bodyCreator = String.format(Templates.THREAD_SAFE_CREATOR_TEMPLATE,
                    args);
        else
            bodyCreator = String.format(Templates.CREATOR_TEMPLATE,
                    args);

        try {
            creator.setBody(JavaParser.parseBlock(bodyCreator));
        } catch (ParseException e) {
            throw new RuntimeException("Errors parsing the creator code");
        }
        return creator;
    }

    /**
     * Return the visibility modifiers of the auxiliary method.
     *
     * @param fieldModifiers The field modifiers
     * @return The visibility modifiers of the method
     */
    private int getAuxiliaryMethodVisibility(int fieldModifiers) {
        int mask = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
        return fieldModifiers & mask;
    }

    /**
     * Generates unary method for static field
     *
     * @param type      The type of the field
     * @param name      The name of the field
     * @param modifiers The modifiers of the field
     */
    private void generateStaticUnaryMethod(String type, String name,
                                           int modifiers) {
        if (Modifier.isFinal(modifiers))
            return;
        MethodDeclaration unary = null;
        Type fieldType = Type.getType(type);
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(
                fieldType.getClassName());
        ClassOrInterfaceType intClassOrInterfaceType = new ClassOrInterfaceType(
                int.class.getName());

        List<Parameter> unaryParams = new ArrayList<>();
        unaryParams.add(new Parameter(intClassOrInterfaceType,
                new VariableDeclaratorId("type")));

        List<AnnotationExpr> annotations = new ArrayList<>();
        annotations.add(new NormalAnnotationExpr(new NameExpr(
                AuxiliaryMethod.class.getName()),
                new ArrayList<>()));

        unary = new MethodDeclaration(getAuxiliaryMethodVisibility(modifiers)
                | Modifier.STATIC, classOrInterfaceType, "_" + name + "_unary",
                unaryParams);

        unary.setAnnotations(annotations);

        Object[] args = {name, clazz.getSimpleName()};

        String bodyUnary = String.format(Templates.STATIC_FIELD_UNARY_TEMPLATE,
                args);

        try {
            unary.setBody(JavaParser.parseBlock(bodyUnary));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        declarations.add(unary);
    }

    /**
     * Generates unary method for instance field
     *
     * @param type      The type of the field
     * @param name      The name of the field
     * @param modifiers The modifiers of the field
     */
    private void generateInstanceUnaryMethod(String type, String name,
                                             int modifiers) {
        if (Modifier.isFinal(modifiers))
            return;
        MethodDeclaration unary = null;
        Type fieldType = Type.getType(type);
        ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(
                fieldType.getClassName());
        ClassOrInterfaceType intClassOrInterfaceType = new ClassOrInterfaceType(
                int.class.getName());

        List<Parameter> unaryParams = new ArrayList<>();
        unaryParams.add(new Parameter(
                new ClassOrInterfaceType(clazz.getName()),
                new VariableDeclaratorId("o")));
        unaryParams.add(new Parameter(intClassOrInterfaceType,
                new VariableDeclaratorId("type")));

        List<AnnotationExpr> annotations = new ArrayList<>();
        annotations.add(new NormalAnnotationExpr(new NameExpr(
                AuxiliaryMethod.class.getName()),
                new ArrayList<>()));

        unary = new MethodDeclaration(getAuxiliaryMethodVisibility(modifiers)
                | Modifier.STATIC, classOrInterfaceType, "_" + name + "_unary",
                unaryParams);

        unary.setAnnotations(annotations);

        String bodyUnary;
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
            Object[] args = {name, clazz.getSimpleName() + "_NewVersion_0",
                    clazz.getSimpleName(),
                    fieldType.getClassName(),
                    clazz.getSimpleName() + "_NewVersion_0"};

            bodyUnary = String.format(Templates.THREAD_SAFE_INSTANCE_FIELD_UNARY_TEMPLATE, args);
        } else {
            Object[] args = {name, clazz.getSimpleName() + "_NewVersion_0",
                    clazz.getSimpleName()};

            bodyUnary = String.format(Templates.INSTANCE_FIELD_UNARY_TEMPLATE, args);
        }
        try {
            unary.setBody(JavaParser.parseBlock(bodyUnary));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        declarations.add(unary);
    }

}
