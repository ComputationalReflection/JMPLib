package jmplib.primitives.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
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

/**
 * This visitor class adds the corresponding annotations to the specified class.
 * It does detect and ignore repeated annotations.
 * 
 * @author Jose Manuel Redondo Lopez
 *
 */
@ExcludeFromJMPLib
final class AddAnnotationToMethodVisitor extends VoidVisitorAdapter<Void> {
	private Class<?>[] annotations;
	private java.lang.reflect.Method method;
	private StructuralIntercessionException error = null;
	private List<AnnotationExpr> originalIdList;
	private boolean methodChanged = false;
	
	public AddAnnotationToMethodVisitor(java.lang.reflect.Method method, Class<?>[] annotations) {
		this.method = method;
		this.annotations = annotations;
	}

	public StructuralIntercessionException getError() {
		return error;
	}

	public List<AnnotationExpr> getPreviousAnnotations() {
		return originalIdList;
	}

	private boolean isMethodToChange(MethodDeclaration n) {
		if (method.getName().equals(n.getName()))
		{
			Class<?>[] searchedMethodParameters = method.getParameterTypes();
			List<Parameter> parameters = n.getParameters();
			if (searchedMethodParameters.length != parameters.size())
				return false;
			for (int i = 0;i<searchedMethodParameters.length; i++) {
				String cl1 = VisitorUtil.parseVersionClassName(VisitorUtil.getClassName(searchedMethodParameters[i].getName()));
				String cl2 = VisitorUtil.parseVersionClassName(VisitorUtil.getClassName(parameters.get(i).getType().toString()));
				if (!cl1.equals(cl2))
					return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {

		if (isMethodToChange(n)) {
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
			this.methodChanged = true;
		}
		super.visit(n, arg);
	}

	public boolean isMethodChanged() {
		return methodChanged;
	}

}

/**
 * This visitor class undos the operations performed by the previous visitor.
 * 
 * @author redon
 *
 */
final class RemoveAnnotationToMethodVisitor extends VoidVisitorAdapter<Void> {
	private List<AnnotationExpr> annotationsToRestore;

	public RemoveAnnotationToMethodVisitor(List<AnnotationExpr> annotationsToRestore) {
		this.annotationsToRestore = annotationsToRestore;
	}

	@Override
	public void visit(MethodDeclaration n, Void arg) {
		n.setAnnotations(annotationsToRestore);
		super.visit(n, arg);
	}
}

/**
 * This class adds new annotations to existing classes. This class doesn't
 * compile this code, only generates it.
 * 
 * @author José Manuel Redondo, Ignacio Lagartos
 * 
 */
public class AddAnnotationToMethodPrimitive extends AbstractPrimitive {
	private Class<?>[] annotations;
	private java.lang.reflect.Method method;
	private List<AnnotationExpr> previousAnnotations;

	public AddAnnotationToMethodPrimitive(java.lang.reflect.Method met, ClassContent classContent, Class<?>[] annotations) {
		super(classContent);
		this.method = met;
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

		AddAnnotationToMethodVisitor addv = new AddAnnotationToMethodVisitor(method, this.annotations);

		// Visit class declaration and add the corresponding elements
		unit.accept(addv, null);
		if (addv.getError() != null)
			throw addv.getError();

		if (!addv.isMethodChanged())
			throw new IllegalArgumentException("Cannot find method: " + method.toString());
		
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

		RemoveAnnotationToMethodVisitor remv = new RemoveAnnotationToMethodVisitor(this.previousAnnotations);

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
