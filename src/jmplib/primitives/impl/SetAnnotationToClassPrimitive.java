package jmplib.primitives.impl;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
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
 * This visitor class sets the corresponding annotations to the specified class.
 * It does detect and ignore repeated annotations.
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
final class SetAnnotationToClassVisitor extends VoidVisitorAdapter<Void> {
    private Class<?>[] annotations;
    private String className;
    private StructuralIntercessionException error = null;
    private List<AnnotationExpr> originalIdList;

    public SetAnnotationToClassVisitor(String className, Class<?>[] annotations) {
        this.className = className;
        this.annotations = annotations;
    }

    public StructuralIntercessionException getError() {
        return error;
    }

    public List<AnnotationExpr> getPreviousAnnotations() {
        return originalIdList;
    }

    private boolean isClassToChange(ClassOrInterfaceDeclaration n) {
        String nameOnly;

        if (this.className.contains(".")) {
            String[] parts = className.split(Pattern.quote("."));
            nameOnly = parts[parts.length - 1].trim();
        } else
            nameOnly = this.className.trim();

        String className = n.getName().split("_")[0].trim();

        return className.equals(nameOnly);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {

        if (isClassToChange(n)) {
            originalIdList = n.getAnnotations();
            List<String> idListStr = new ArrayList<String>();

            for (Class<?> id : annotations) {
                idListStr.add(id.getName());
            }

            List<AnnotationExpr> addedIds = new ArrayList<AnnotationExpr>();

            for (String str : idListStr) {
                AnnotationExpr id = new NormalAnnotationExpr();
                id.setName(new NameExpr(str));
                addedIds.add(id);
            }

            n.setAnnotations(addedIds);
        }
        super.visit(n, arg);
    }
}

/**
 * This visitor class undos the operations performed by the previous visitor.
 *
 * @author redon
 */
final class UnsetAnnotationToClassVisitor extends VoidVisitorAdapter<Void> {
    private List<AnnotationExpr> annotationsToRestore;

    public UnsetAnnotationToClassVisitor(List<AnnotationExpr> annotationsToRestore) {
        this.annotationsToRestore = annotationsToRestore;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        n.setAnnotations(annotationsToRestore);
        super.visit(n, arg);
    }
}

/**
 * This class sets new annotations to existing classes. This class doesn't
 * compile this code, only generates it.
 *
 * @author Jos� Manuel Redondo, Ignacio Lagartos
 */
public class SetAnnotationToClassPrimitive extends AbstractPrimitive {
    private Class<?>[] annotations;
    private String className;
    private List<AnnotationExpr> previousAnnotations;

    public SetAnnotationToClassPrimitive(String className, ClassContent classContent, Class<?>[] annotations) {
        super(classContent);
        this.className = className;
        this.annotations = annotations;
    }

    /**
     * Set the specified annotations to the class
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

        SetAnnotationToClassVisitor addv = new SetAnnotationToClassVisitor(className, this.annotations);

        // Visit class declaration and add the corresponding elements
        unit.accept(addv, null);
        if (addv.getError() != null)
            throw addv.getError();

        // Update class contents
        classContent.setContent(unit.toString());
        // Save the elements that were really added
        previousAnnotations = addv.getPreviousAnnotations();
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

        UnsetAnnotationToClassVisitor remv = new UnsetAnnotationToClassVisitor(this.previousAnnotations);

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
