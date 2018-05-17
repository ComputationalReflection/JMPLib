package jmplib.primitives.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.sourcecode.ClassContent;

/**
 * This visitor class sets the corresponding annotations to the specified class.
 * It does detect and ignore repeated annotations.
 * 
 * @author Jose Manuel Redondo Lopez
 *
 */
@ExcludeFromJMPLib
final class SetAnnotationToMethodVisitor extends VoidVisitorAdapter<Void> {
	private Class<?>[] annotations;
	private String methodName;
	private StructuralIntercessionException error = null;
	private List<AnnotationExpr> originalIdList;

	public SetAnnotationToMethodVisitor(String methodName, Class<?>[] annotations) {
		this.methodName = methodName;
		this.annotations = annotations;
	}

	public StructuralIntercessionException getError() {
		return error;
	}

	public List<AnnotationExpr> getPreviousAnnotations() {
		return originalIdList;
	}

	private boolean isMethodToChange(MethodDeclaration n) {
		String nameOnly;

		if (this.methodName.contains(".")) {
			String[] parts = methodName.split(Pattern.quote("."));
			nameOnly = parts[parts.length - 1].trim();
		} else
			nameOnly = this.methodName.trim();

		String className = n.getName().split("_")[0].trim();

		return className.equals(nameOnly);
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {

		if (isMethodToChange(n)) {
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
 *
 */
final class UnsetAnnotationToMethodVisitor extends VoidVisitorAdapter<Void> {
	private List<AnnotationExpr> annotationsToRestore;

	public UnsetAnnotationToMethodVisitor(List<AnnotationExpr> annotationsToRestore) {
		this.annotationsToRestore = annotationsToRestore;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		n.setAnnotations(annotationsToRestore);
		super.visit(n, arg);
	}
}

/**
 * This class sets new annotations to existing methods. This class doesn't
 * compile this code, only generates it.
 * 
 * @author José Manuel Redondo, Ignacio Lagartos
 * 
 */
public class SetAnnotationToMethodPrimitive extends AbstractPrimitive {
	private Class<?>[] annotations;
	private String methodName;
	private List<AnnotationExpr> previousAnnotations;

	public SetAnnotationToMethodPrimitive(java.lang.reflect.Method met, ClassContent classContent,
			Class<?>[] annotations) {
		super(classContent);
		this.methodName = met.getName();
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

		SetAnnotationToMethodVisitor addv = new SetAnnotationToMethodVisitor(methodName, this.annotations);

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

		UnsetAnnotationToMethodVisitor remv = new UnsetAnnotationToMethodVisitor(this.previousAnnotations);

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
