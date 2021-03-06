package jmplib.primitives;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.sourcecode.ClassContent;

import java.lang.reflect.Modifier;

/**
 * Superclass of all field primitives.
 *
 * @author Ignacio Lagartos
 */
@ExcludeFromJMPLib
public abstract class FieldPrimitive extends AbstractPrimitive {

    protected String name;

    protected FieldDeclaration declaration;
    protected MethodDeclaration getter, setter, unary;

    public FieldPrimitive(ClassContent classContent, String name) {
        super(classContent);
        this.name = name;
    }

    /**
     * Obtains the visibility of the auxiliary methods
     *
     * @param fieldModifiers The field modifiers
     * @return The visibility of the methodsS
     */
    protected int getAuxiliaryMethodVisibility(int fieldModifiers) {
        int mask = Modifier.PRIVATE | Modifier.PROTECTED | Modifier.PUBLIC;
        return fieldModifiers & mask;
    }

}
