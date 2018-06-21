package jmplib.primitives.impl;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.sourcecode.ClassContent;

import java.util.ArrayList;
import java.util.List;

/**
 * This visitor class adds the corresponding interface to the class contents.
 *
 * @author redon
 */
@ExcludeFromJMPLib
final class AddInterfaceVisitor extends VoidVisitorAdapter<Void> {
    private Class<?> interfaceToAdd;
    private StructuralIntercessionException error = null;
    private Class<?>[] typeParameters;

    public AddInterfaceVisitor(Class<?> interfaceToAdd, Class<?>... typeParameters) {
        this.interfaceToAdd = interfaceToAdd;
        this.typeParameters = typeParameters;
    }

    public StructuralIntercessionException getError() {
        return error;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        List<ClassOrInterfaceType> interfaces = n.getImplements();
        for (ClassOrInterfaceType cl : interfaces) {
            if ((cl.getName().equals(interfaceToAdd.getName()))
                    || (cl.toString().startsWith(interfaceToAdd.getName()))) {
                error = new StructuralIntercessionException(
                        "The class " + n.getName() + " already implements the interface " + interfaceToAdd.getName());
                return;
            }
        }

        ClassOrInterfaceType interfaceType = new ClassOrInterfaceType(interfaceToAdd.getName());
        if (typeParameters.length > 0) {
            List<Type> typeParams = new ArrayList<Type>();
            for (java.lang.Class<?> tp : typeParameters) {
                typeParams.add(JavaParserUtils.transform(tp));
            }
            interfaceType.setTypeArgs(typeParams);
        }
        interfaces.add(interfaceType);
        n.setImplements(interfaces);
        super.visit(n, arg);
    }
}

/**
 * This visitor class undos the operations performed by the previous visitor.
 *
 * @author redon
 */
final class RemoveInterfaceVisitor extends VoidVisitorAdapter<Void> {
    private Class<?> interfaceToRemove;

    public RemoveInterfaceVisitor(Class<?> interfaceToRemove) {
        this.interfaceToRemove = interfaceToRemove;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        List<ClassOrInterfaceType> interfaces = n.getImplements();
        List<ClassOrInterfaceType> newImplements = new ArrayList<ClassOrInterfaceType>();

        for (ClassOrInterfaceType cl : interfaces) {
            if (!(cl.getName().equals(interfaceToRemove.getName())
                    || (cl.toString().startsWith(interfaceToRemove.getName())))) {
                newImplements.add(cl);
            }
        }

        n.setImplements(newImplements);
        super.visit(n, arg);
    }
}

/**
 * This class adds new interfaces to existing classes. This class doesn't
 * compile this code, only generates it.
 *
 * @author Jos� Manuel Redondo, Ignacio Lagartos
 */
public class AddInterfacePrimitive extends AbstractPrimitive {
    private Class<?> interfaceToAdd;
    private Class<?>[] typeParameters;

    public AddInterfacePrimitive(ClassContent classContent, Class<?> interf, Class<?>... typeParameters) {
        super(classContent);
        this.interfaceToAdd = interf;
        this.typeParameters = typeParameters;
    }

    /**
     * Adds the interface to the source code of the class
     */
    @Override
    protected void executePrimitive() throws StructuralIntercessionException {
        // Obtain the class
        CompilationUnit unit;
        try {
            unit = JavaParserUtils.parse(classContent.getContent());
        } catch (ParseException e) {
            throw new StructuralIntercessionException("An exception was thrown parsing the class. " + e.getMessage(),
                    e);
        }

        AddInterfaceVisitor addv = new AddInterfaceVisitor(this.interfaceToAdd, this.typeParameters);

        // Visit class declaration and add the corresponding interface
        unit.accept(addv, null);
        if (addv.getError() != null)
            throw addv.getError();

        // Update class contents
        classContent.setContent(unit.toString());
    }

    /**
     * Reverts the changes
     */
    @Override
    protected void undoPrimitive() throws StructuralIntercessionException {
        // super.undo();
        // Obtain the class
        CompilationUnit unit;
        try {
            unit = JavaParserUtils.parse(classContent.getContent());
        } catch (ParseException e) {
            throw new StructuralIntercessionException("An exception was thrown parsing the class. " + e.getMessage(),
                    e);
        }

        RemoveInterfaceVisitor remv = new RemoveInterfaceVisitor(this.interfaceToAdd);

        // Visit class declaration and remove the corresponding interface
        unit.accept(remv, null);

        // Update class contents
        classContent.setContent(unit.toString());
    }

    @Override
    public boolean isSafe() {
        return true;
    }

}
