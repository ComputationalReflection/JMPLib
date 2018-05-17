package jmplib.primitives.impl;

import java.util.ArrayList;
import java.util.List;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.sourcecode.ClassContent;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * This visitor class removes the corresponding interface to the class contents.
 * 
 * @author redon
 *
 */
@ExcludeFromJMPLib
final class UndoableRemoveInterfaceVisitor extends VoidVisitorAdapter<Void> {
	private Class<?> interfaceToRemove;
	private List<Type> typeParameters;
	private StructuralIntercessionException error = null;

	public UndoableRemoveInterfaceVisitor(Class<?> interfaceToRemove) {
		this.interfaceToRemove = interfaceToRemove;
	}

	public List<Type> getRemovedInterfaceTypeParameters() {
		return typeParameters;
	}

	public StructuralIntercessionException getError() {
		return error;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		List<ClassOrInterfaceType> interfaces = n.getImplements();
		List<ClassOrInterfaceType> newImplements = new ArrayList<ClassOrInterfaceType>();
		boolean found = false;

		for (ClassOrInterfaceType cl : interfaces) {
			if (!(cl.toString().startsWith(interfaceToRemove.getName())
					|| (cl.getName().equals(interfaceToRemove.getName())))) {
				newImplements.add(cl);
			} else {
				this.typeParameters = cl.getTypeArgs();
				found = true;
			}
		}

		if (!found)
			error = new StructuralIntercessionException(
					"The class " + n.getName() + " do not implement the interface " + interfaceToRemove.getName());

		n.setImplements(newImplements);
		super.visit(n, arg);
	}
}

/**
 * This visitor class undos the operations performed by the previous visitor.
 * 
 * @author redon
 *
 */
final class UndoableAddInterfaceVisitor extends VoidVisitorAdapter<Void> {
	private Class<?> interfaceToAdd;
	private StructuralIntercessionException error = null;
	private List<Type> typeParameters;

	public UndoableAddInterfaceVisitor(Class<?> interfaceToAdd) {
		this.interfaceToAdd = interfaceToAdd;
	}

	public List<Type> getInterfaceTypeParameters() {
		return typeParameters;
	}

	public void setInterfaceTypeParameters(List<Type> tp) {
		typeParameters = tp;
	}

	public StructuralIntercessionException getError() {
		return error;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		List<ClassOrInterfaceType> interfaces = n.getImplements();
		for (ClassOrInterfaceType cl : interfaces)
			if ((cl.getName().equals(interfaceToAdd.getName()))
					|| (cl.toString().startsWith(interfaceToAdd.getName()))) {
				error = new StructuralIntercessionException(
						"The class " + n.getName() + " already implements the interface " + interfaceToAdd.getName());
				return;
			}

		ClassOrInterfaceType interfaceType = new ClassOrInterfaceType(interfaceToAdd.getName());
		if (this.typeParameters.size() > 0) {
			List<Type> typeParams = new ArrayList<Type>();
			for (Type tp : this.typeParameters) {
				typeParams.add(tp);
			}
			interfaceType.setTypeArgs(typeParams);
		}
		interfaces.add(interfaceType);
		n.setImplements(interfaces);
		super.visit(n, arg);
	}
}

/**
 * This class adds new interfaces to existing classes. This class doesn't
 * compile this code, only generates it.
 * 
 * @author José Manuel Redondo, Ignacio Lagartos
 * 
 */
public class RemoveInterfacePrimitive extends AbstractPrimitive {
	private Class<?> interfaceToAdd;
	private UndoableAddInterfaceVisitor addv;
	private UndoableRemoveInterfaceVisitor remv;

	public RemoveInterfacePrimitive(ClassContent classContent, Class<?> interf) {
		super(classContent);
		this.interfaceToAdd = interf;
		this.addv = new UndoableAddInterfaceVisitor(this.interfaceToAdd);
		this.remv = new UndoableRemoveInterfaceVisitor(this.interfaceToAdd);
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
		// Visit class declaration and add the corresponding interface
		unit.accept(remv, null);
		if (remv.getError() != null)
			throw remv.getError();

		// Preserve removed interface type parameters for possible undo operations
		addv.setInterfaceTypeParameters(remv.getRemovedInterfaceTypeParameters());

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

		// Visit class declaration and remove the corresponding interface
		unit.accept(addv, null);

		// Update class contents
		classContent.setContent(unit.toString());
	}

	@Override
	public boolean isSafe() {
		return true;
	}

}
