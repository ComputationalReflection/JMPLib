package jmplib.primitives.impl;

import java.util.List;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractReadPrimitive;
import jmplib.sourcecode.ClassContent;

/**
 * This visitor class gets the imports of the compilation unit contents.
 * 
 * @author Jose Manuel Redondo Lopez
 *
 */
@ExcludeFromJMPLib
final class GetImportVisitor extends VoidVisitorAdapter<Void> {
	private String[] imports;
	private StructuralIntercessionException error = null;

	public StructuralIntercessionException getError() {
		return error;
	}

	public String[] getImports() {
		return imports;
	}

	@Override
	public void visit(CompilationUnit n, Void arg) {
		List<ImportDeclaration> existingImports = n.getImports();
		if (existingImports == null)
			return;

		imports = new String[existingImports.size()];
		int counter = 0;
		for (ImportDeclaration str : existingImports) {
			imports[counter++] = str.toString();
		}
		super.visit(n, arg);
	}
}

/**
 * This class gets imports from the files of existing classes. This class
 * doesn't compile this code, only generates it.
 * 
 * @author José Manuel Redondo, Ignacio Lagartos
 * 
 */
public class GetImportPrimitive extends AbstractReadPrimitive<String[]> {
	public GetImportPrimitive(ClassContent classContent) {
		super(classContent);
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

		GetImportVisitor addv = new GetImportVisitor();

		// Visit class declaration and add the corresponding imports
		unit.accept(addv, null);
		if (addv.getError() != null)
			throw addv.getError();

		setReadValue(addv.getImports());
	}

	@Override
	public boolean isSafe() {
		return true;
	}

	@Override
	protected void undoPrimitive() throws StructuralIntercessionException {
		// No changes are done by this primitive, so nothing is undone
	}

}
