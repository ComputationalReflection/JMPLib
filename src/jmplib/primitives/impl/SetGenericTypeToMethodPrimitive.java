package jmplib.primitives.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.reflect.TypeVariable;
import jmplib.sourcecode.ClassContent;

/**
 * This visitor class sets the corresponding annotations to the specified class.
 * It does detect and ignore repeated annotations.
 * 
 * @author Jose Manuel Redondo Lopez
 *
 */
@ExcludeFromJMPLib
final class SetGenericTypeToMethodVisitor extends VoidVisitorAdapter<Void> {
	private TypeVariable<?>[] tvs;
	private String methodName;
	private StructuralIntercessionException error = null;
	private List<TypeParameter> originalIdList;

	public SetGenericTypeToMethodVisitor(String methodName, TypeVariable<?>[] tvs) {
		this.methodName = methodName;
		this.tvs = tvs;
	}

	public StructuralIntercessionException getError() {
		return error;
	}

	public List<TypeParameter> getPreviousTypeParameters() {
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
			originalIdList = n.getTypeParameters();
			List<String> idListStr = new ArrayList<String>();

			for (TypeVariable<?> id : tvs) {
				idListStr.add(id.getName());
			}

			List<TypeParameter> addedIds = new ArrayList<TypeParameter>();

			for (String str : idListStr) {
				TypeParameter id = new TypeParameter();
				id.setName(str);
				addedIds.add(id);
			}

			n.setTypeParameters(addedIds);
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
final class UnsetGenericTypeToMethodVisitor extends VoidVisitorAdapter<Void> {
	private List<TypeParameter> typesToRestore;

	public UnsetGenericTypeToMethodVisitor(List<TypeParameter> typesToRestore) {
		this.typesToRestore = typesToRestore;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		n.setTypeParameters(typesToRestore);
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
public class SetGenericTypeToMethodPrimitive extends AbstractPrimitive {
	private jmplib.reflect.TypeVariable<?>[] typesToSet;
	private String methodName;
	private List<TypeParameter> previousTypeParameters;

	public SetGenericTypeToMethodPrimitive(java.lang.reflect.Method met, ClassContent classContent,
			jmplib.reflect.TypeVariable<?>[] typesToSet) {
		super(classContent);
		this.methodName = met.getName();
		this.typesToSet = typesToSet;
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

		SetGenericTypeToMethodVisitor addv = new SetGenericTypeToMethodVisitor(methodName, this.typesToSet);

		// Visit class declaration and add the corresponding imports
		unit.accept(addv, null);
		if (addv.getError() != null)
			throw addv.getError();

		// Update class contents
		classContent.setContent(unit.toString());
		// Save the imports that were really added
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

		UnsetGenericTypeToMethodVisitor remv = new UnsetGenericTypeToMethodVisitor(this.previousTypeParameters);

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
