package jmplib.primitives;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.asm.util.ASMUtils;
import jmplib.config.JMPlibConfig;
import jmplib.sourcecode.ClassContent;
import jmplib.util.Templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Superclass for all method primitives
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public abstract class MethodPrimitive extends AbstractPrimitive {

    protected Class<?> returnClass = null;
    protected Class<?>[] parameterClasses = null, exceptionClasses = null;
    protected String[] exceptions = null;
    protected int modifiers = 0;

    public MethodPrimitive(ClassContent classContent, Class<?> returnClass,
                           Class<?>[] parameterClasses, Class<?>[] exceptions, int modifiers) {
        super(classContent);
        this.modifiers = modifiers;
        this.returnClass = returnClass;
        this.parameterClasses = parameterClasses;
        this.exceptionClasses = exceptions;
    }

    public MethodPrimitive(ClassContent classContent, Class<?> returnClass,
                           Class<?>[] parameterClasses, int modifiers) {
        super(classContent);
        this.modifiers = modifiers;
        this.returnClass = returnClass;
        this.parameterClasses = parameterClasses;
    }

    public MethodPrimitive(ClassContent classContent, Class<?> returnClass,
                           Class<?>[] parameterClasses) {
        super(classContent);
        this.returnClass = returnClass;
        this.parameterClasses = parameterClasses;
    }

    /**
     * Creates the bytecode descriptor of the method
     *
     * @return Descriptor
     */
    protected String getDescriptor() {
        String descriptor = "(";
        descriptor = descriptor
                .concat(Arrays.stream(parameterClasses)
                        .map(ASMUtils::getDescriptor)
                        .collect(Collectors.joining())).concat(")")
                .concat(ASMUtils.getDescriptor(returnClass));
        return descriptor;
    }

    /**
     * Creates the bytecode descriptor of the invoker method
     *
     * @return Descriptor
     */
    protected String getInvokerDescriptor() {
        String descriptor = "(".concat(ASMUtils.getDescriptor(classContent
                .getClazz()));
        descriptor = descriptor
                .concat(Arrays.stream(parameterClasses)
                        .map(ASMUtils::getDescriptor)
                        .collect(Collectors.joining())).concat(")")
                .concat(ASMUtils.getDescriptor(returnClass));
        return descriptor;
    }

    private String getProperReturnClassName() {
        //For arrays
        if ((this.returnClass.toString().startsWith("[")) || (this.returnClass.toString().startsWith("class [")))
            return this.returnClass.getSimpleName();
        String strReturn = returnClass.toString();
        if (strReturn.startsWith("class "))
            strReturn = strReturn.substring("class ".length(), strReturn.length());
        return strReturn;
    }
    /**
     * Generates the body of an invoker method
     *
     * @param name        Method name
     * @param paramsNames Param names
     * @return Body of the invoker method
     */
    protected String getBodyInvoker(String name, String paramsNames, String paramTypes) {
        if (JMPlibConfig.getInstance().getConfigureAsThreadSafe()) {
            Object[] args = {clazz.getSimpleName(), name,
                    clazz.getSimpleName() + "_NewVersion_"
                            + (classContent.isUpdated() ? classContent.getVersion() - 1 : classContent.getVersion()),
                    paramsNames, (returnClass.getName().equals("void") ? "" : getProperReturnClassName() + " ret_value = "),
                    (returnClass.getName().equals("void") ? "return;" : " return ret_value;"),
                    (returnClass.getName().equals("void") ? "" : "(" + getProperReturnClassName()+")"),
                    (paramsNames.equals("") ? "" : ", " + paramsNames), //%8
                    (paramTypes.equals("") ? "" : ", " + paramTypes)}; //%9

            return String.format(Templates.THREAD_SAFE_INVOKER_BODY_TEMPLATE, args);
        } else {
            Object[] args = {clazz.getSimpleName(), name,
                    clazz.getSimpleName() + "_NewVersion_"
                            + (classContent.isUpdated() ? classContent.getVersion() - 1 : classContent.getVersion()),
                    paramsNames, (returnClass.getName().equals("void") ? "" : "return ")};
            return String.format(Templates.INVOKER_BODY_TEMPLATE, args);
        }
    }

    /**
     * Put the exception names into the throws clause of the method
     *
     * @param invoker Method declaration to put exceptions in
     */
    protected void setThrows(MethodDeclaration invoker) {
        if (exceptionClasses != null) {
            List<NameExpr> list = new ArrayList<NameExpr>();
            for (Class<?> exception : exceptionClasses) {
                list.add(new NameExpr(exception.getName()));
            }
            invoker.setThrows(list);
        }
    }
}
