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
import jmplib.primitives.impl.util.VisitorUtil;
import jmplib.sourcecode.ClassContent;

import java.util.ArrayList;
import java.util.List;

/**
 * This visitor class adds the corresponding annotations to the specified class.
 * It does detect and ignore repeated annotations.
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
final class AddAnnotationToClassVisitor extends VoidVisitorAdapter<Void> {
    private Class<?>[] annotations;
    private String className;
    private StructuralIntercessionException error = null;
    private List<AnnotationExpr> originalIdList;
    private boolean changedClass = false;

    public AddAnnotationToClassVisitor(String className, Class<?>[] annotations) {
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
        String nameOnly = VisitorUtil.getClassName(this.className);

        String className = VisitorUtil.parseVersionClassName(n.getName());

        return className.equals(nameOnly);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        if (isClassToChange(n)) {
            originalIdList = n.getAnnotations();
            List<String> idListStr = new ArrayList<String>();
            boolean existing = false;

            for (Class<?> id : annotations) {
                existing = false;
                if (originalIdList != null) {
                    for (AnnotationExpr originalId : originalIdList) {
                        if (id.getName().equals(originalId.getName().getName())) {
                            existing = true;
                            break;
                        }
                    }
                }
                // Add only non-existing annotations
                if (!existing)
                    idListStr.add(id.getName());
            }

            List<AnnotationExpr> addedIds = new ArrayList<AnnotationExpr>();

            for (String str : idListStr) {
                AnnotationExpr id = new NormalAnnotationExpr();
                id.setName(new NameExpr(str));
                addedIds.add(id);
            }

            n.setAnnotations(addedIds);
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
final class RemoveAnnotationToClassVisitor extends VoidVisitorAdapter<Void> {
    private List<AnnotationExpr> annotationsToRestore;

    public RemoveAnnotationToClassVisitor(List<AnnotationExpr> annotationsToRestore) {
        this.annotationsToRestore = annotationsToRestore;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        n.setAnnotations(annotationsToRestore);
        super.visit(n, arg);
    }
}

/**
 * This class adds new annotations to existing classes. This class doesn't
 * compile this code, only generates it.
 *
 * @author Jos� Manuel Redondo, Ignacio Lagartos
 */
public class AddAnnotationToClassPrimitive extends AbstractPrimitive {
    private Class<?>[] annotations;
    private String className;
    private List<AnnotationExpr> previousAnnotations;

    public AddAnnotationToClassPrimitive(String className, ClassContent classContent, Class<?>[] annotations) {
        super(classContent);
        this.className = className;
        this.annotations = annotations;
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

        AddAnnotationToClassVisitor addv = new AddAnnotationToClassVisitor(className, this.annotations);

        // Visit class declaration and add the corresponding elements
        unit.accept(addv, null);
        if (addv.getError() != null)
            throw addv.getError();

        if (!addv.isChangedClass())
            throw new IllegalArgumentException("Cannot find class: " + className);

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

        RemoveAnnotationToClassVisitor remv = new RemoveAnnotationToClassVisitor(this.previousAnnotations);

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
