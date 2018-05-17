package jmplib.primitives.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.sourcecode.ClassContent;

/**
 * This visitor class adds the removed superclass to the class contents. Undoing
 * the following visitor
 * 
 * @author redon
 *
 */
@ExcludeFromJMPLib
final class UndoableSetSuperClassVisitor extends VoidVisitorAdapter<Void> {
	private ClassOrInterfaceType superclassToAdd;

	public UndoableSetSuperClassVisitor() {
	}

	public ClassOrInterfaceType getSuperclassToAdd() {
		return superclassToAdd;
	}

	public void setSuperclassToAdd(ClassOrInterfaceType superclassToAdd) {
		this.superclassToAdd = superclassToAdd;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		List<ClassOrInterfaceType> superclasses = n.getExtends();

		superclasses.add(superclassToAdd);
		n.setExtends(superclasses);
		super.visit(n, arg);
	}
}

/**
 * This visitor removes the superclass of a class.
 * 
 * @author redon
 *
 */
final class UndoableRemoveSuperClassVisitor extends VoidVisitorAdapter<Void> {
	private ClassOrInterfaceType removedSuperclass;

	public UndoableRemoveSuperClassVisitor() {
	}

	public ClassOrInterfaceType getRemovedSuperClass() {
		return removedSuperclass;
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, Void arg) {
		List<ClassOrInterfaceType> superclasses = n.getExtends();
		List<ClassOrInterfaceType> newExtends = new ArrayList<ClassOrInterfaceType>();

		removedSuperclass = superclasses.get(0);

		n.setExtends(newExtends);
		super.visit(n, arg);
	}
}

/**
 * This class removes the superclass of existing classes. This class doesn't
 * compile this code, only generates it.
 * 
 * @author José Manuel Redondo, Ignacio Lagartos
 * 
 */
public class RemoveSuperClassPrimitive extends AbstractPrimitive {
	private UndoableRemoveSuperClassVisitor addv;
	private UndoableSetSuperClassVisitor remv;

	public RemoveSuperClassPrimitive(ClassContent classContent) {
		super(classContent);
		addv = new UndoableRemoveSuperClassVisitor();
		remv = new UndoableSetSuperClassVisitor();
	}

	/**
	 * Adds the method and the invoker to the source code of the class
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
		unit.accept(addv, null);

		remv.setSuperclassToAdd(addv.getRemovedSuperClass());

		// Update class contents
		classContent.setContent(unit.toString());
		changesClassHierarchy = true;
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
		unit.accept(remv, null);

		// Update class contents
		classContent.setContent(unit.toString());
	}
}
