package jmplib.primitives.impl;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.sourcecode.ClassContent;

import java.util.ArrayList;
import java.util.List;

/**
 * This visitor class adds the corresponding imports to the compilation unit
 * contents. It does detect and ignore repeated imports.
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
final class AddImportVisitor extends VoidVisitorAdapter<Void> {
    private String[] imports;
    private StructuralIntercessionException error = null;
    private List<ImportDeclaration> originalIdList;

    public AddImportVisitor(String[] imports) {
        this.imports = imports;
    }

    public StructuralIntercessionException getError() {
        return error;
    }

    public List<ImportDeclaration> getPreviousImports() {
        return originalIdList;
    }

    @Override
    public void visit(CompilationUnit n, Void arg) {
        originalIdList = n.getImports();
        List<String> idListStr = new ArrayList<String>();
        boolean existingImport = false;

        for (String id : imports) {
            existingImport = false;
            if (originalIdList != null) {
                for (ImportDeclaration originalId : originalIdList) {
                    if (id.equals(originalId.getName().getName())) {
                        existingImport = true;
                        break;
                    }
                }
            }
            // Add only non-existing import
            if (!existingImport)
                idListStr.add(id);
        }

        List<ImportDeclaration> addedIds = new ArrayList<ImportDeclaration>();

        for (String str : idListStr) {
            ImportDeclaration id = new ImportDeclaration();
            id.setName(new NameExpr(str));
            addedIds.add(id);
        }

        n.setImports(addedIds);
        super.visit(n, arg);
    }
}

/**
 * This visitor class undos the operations performed by the previous visitor.
 *
 * @author redon
 */
final class RemoveImportVisitor extends VoidVisitorAdapter<Void> {
    private List<ImportDeclaration> importsToRestore;

    public RemoveImportVisitor(List<ImportDeclaration> importsToRestore) {
        this.importsToRestore = importsToRestore;
    }

    @Override
    public void visit(CompilationUnit n, Void arg) {
        n.setImports(importsToRestore);
        super.visit(n, arg);
    }
}

/**
 * This class adds new imports to the files of existing classes. This class
 * doesn't compile this code, only generates it.
 *
 * @author Jos� Manuel Redondo, Ignacio Lagartos
 */
public class AddImportPrimitive extends AbstractPrimitive {
    private String[] imports;
    private List<ImportDeclaration> addedImports;

    public AddImportPrimitive(ClassContent classContent, String[] imports) {
        super(classContent);
        this.imports = imports;
    }

    /**
     * Adds the specified import to the source code file of the class
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

        AddImportVisitor addv = new AddImportVisitor(this.imports);

        // Visit class declaration and add the corresponding imports
        unit.accept(addv, null);
        if (addv.getError() != null)
            throw addv.getError();

        // Update class contents
        classContent.setContent(unit.toString());
        // Save the imports that were really added
        addedImports = addv.getPreviousImports();
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

        RemoveImportVisitor remv = new RemoveImportVisitor(this.addedImports);

        // Visit class declaration and remove the corresponding imports
        unit.accept(remv, null);

        // Update class contents
        classContent.setContent(unit.toString());
    }

    @Override
    public boolean isSafe() {
        return true;
    }

}
