package jmplib.primitives.impl;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.sourcecode.ClassContent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This visitor class adds the corresponding annotations to the specified class.
 * It does detect and ignore repeated annotations.
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
final class AddGenericTypeToClassVisitor extends VoidVisitorAdapter<Void> {
    jmplib.reflect.TypeVariable<?>[] tvs;
    private String className;
    private StructuralIntercessionException error = null;
    private List<TypeParameter> originalIdList;
    private boolean changedClass = false;

    public AddGenericTypeToClassVisitor(String className, jmplib.reflect.TypeVariable<?>[] tvs) {
        this.className = className;
        this.tvs = tvs;
    }

    public static String parseVersionClassName(String className) {
        return className.split("_")[0].trim();
    }

    public static String getClassName(String className) {
        String nameOnly;
        if (className.contains(".")) {
            String[] parts = className.split(Pattern.quote("."));
            nameOnly = parts[parts.length - 1].trim();
        } else
            nameOnly = className.trim();

        return nameOnly;
    }

    public StructuralIntercessionException getError() {
        return error;
    }

    public List<TypeParameter> getPreviousTypeParameters() {
        return originalIdList;
    }

    private boolean isClassToChange(ClassOrInterfaceDeclaration n) {
        String nameOnly = getClassName(this.className);

        String className = parseVersionClassName(n.getName());

        return className.equals(nameOnly);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        if (isClassToChange(n)) {
            originalIdList = n.getTypeParameters();
            List<String> idListStr = new ArrayList<String>();
            boolean existing = false;

            for (jmplib.reflect.TypeVariable<?> id : tvs) {
                existing = false;
                if (originalIdList != null) {
                    for (TypeParameter originalId : originalIdList) {
                        if (id.getName().equals(originalId.getName())) {
                            existing = true;
                            break;
                        }
                    }
                }
                // Add only non-existing annotations
                if (!existing)
                    idListStr.add(id.getName());
            }

            List<TypeParameter> addedIds = new ArrayList<TypeParameter>();

            for (String str : idListStr) {
                TypeParameter id = new TypeParameter();
                id.setName(str);
                addedIds.add(id);
            }

            n.setTypeParameters(addedIds);
            this.changedClass = true;
        }
        super.visit(n, arg);
    }

    public boolean isChangedClass() {
        return changedClass;
    }
}

/**
 * This visitor class undos the operations performed by the previous visitor.
 *
 * @author redon
 */
final class RemoveGenericTypeFromClassVisitor extends VoidVisitorAdapter<Void> {
    private List<TypeParameter> typesToRestore;

    public RemoveGenericTypeFromClassVisitor(List<TypeParameter> typesToRestore) {
        this.typesToRestore = typesToRestore;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        n.setTypeParameters(typesToRestore);
        super.visit(n, arg);
    }
}

/**
 * This class adds new annotations to existing classes. This class doesn't
 * compile this code, only generates it.
 *
 * @author Jos� Manuel Redondo, Ignacio Lagartos
 */
public class AddGenericTypeToClassPrimitive extends AbstractPrimitive {
    jmplib.reflect.TypeVariable<?>[] typesToAdd;
    private String className;
    private List<TypeParameter> previousTypeParameters;

    public AddGenericTypeToClassPrimitive(String className, ClassContent classContent, jmplib.reflect.TypeVariable<?>[] typesToAdd) {
        super(classContent);
        this.className = className;
        this.typesToAdd = typesToAdd;
    }

    /**
     * Adds the specified annotations to the class
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

        AddGenericTypeToClassVisitor addv = new AddGenericTypeToClassVisitor(className, this.typesToAdd);

        // Visit class declaration and add the corresponding elements
        unit.accept(addv, null);
        if (addv.getError() != null)
            throw addv.getError();

        if (!addv.isChangedClass())
            throw new IllegalArgumentException("Cannot find class: " + className);

        // Update class contents
        classContent.setContent(unit.toString());
        // Save the elements that were really added
        previousTypeParameters = addv.getPreviousTypeParameters();
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

        RemoveGenericTypeFromClassVisitor remv = new RemoveGenericTypeFromClassVisitor(this.previousTypeParameters);

        // Visit class declaration and remove the corresponding elements
        unit.accept(remv, null);

        // Update class contents
        classContent.setContent(unit.toString());
    }

    @Override
    public boolean isSafe() {
        return true;
    }

}
