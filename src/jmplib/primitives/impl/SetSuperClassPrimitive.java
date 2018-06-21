package jmplib.primitives.impl;

import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.javaparser.util.JavaParserUtils;
import jmplib.primitives.AbstractPrimitive;
import jmplib.sourcecode.ClassContent;
import jmplib.sourcecode.SourceCodeCache;
import jmplib.util.InheritanceTables;

import java.util.ArrayList;
import java.util.List;

/**
 * This visitor class adds the corresponding superclass to the class contents.
 *
 * @author redon
 */
@ExcludeFromJMPLib
final class SetSuperClassVisitor extends VoidVisitorAdapter<Void> {
    private Class<?> superclassToAdd;
    private StructuralIntercessionException error = null;
    private Class<?>[] typeParameters;
    private SetSuperClassPrimitive primitive;

    public SetSuperClassVisitor(SetSuperClassPrimitive primitive, Class<?> interfaceToAdd, Class<?>... typeParameters) {
        this.superclassToAdd = interfaceToAdd;
        this.typeParameters = typeParameters;
        this.primitive = primitive;
    }

    public StructuralIntercessionException getError() {
        return error;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        List<ClassOrInterfaceType> superclasses = n.getExtends();
        ClassOrInterfaceType superClassType = null;

        for (ClassOrInterfaceType cl : superclasses) {
            if ((cl.getName().equals(superclassToAdd.getName()))
                    || (cl.toString().startsWith(superclassToAdd.getName()))) {
                error = new StructuralIntercessionException(
                        "The class " + n.getName() + " already extends from " + superclassToAdd.getName());
                return;
            }
        }

		/*if (!VersionTables.hasNewVersion(superclassToAdd)) {
			try {
				String evolvedSuperClassName = this.primitive.updateVersionRetStr(superclassToAdd, superclassToAdd.getSuperclass().getName());
				superClassType = new ClassOrInterfaceType(evolvedSuperClassName);
			} catch (StructuralIntercessionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {*/
        //superclassToAdd = VersionTables.getNewVersion(superclassToAdd);
        superClassType = new ClassOrInterfaceType(superclassToAdd.getName());
        //}

        if ((typeParameters != null) && (typeParameters.length > 0)) {
            List<Type> typeParams = new ArrayList<Type>();
            for (java.lang.Class<?> tp : typeParameters) {
                typeParams.add(JavaParserUtils.transform(tp));
            }
            superClassType.setTypeArgs(typeParams);
        }
        superclasses.clear();
        superclasses.add(superClassType);
        n.setExtends(superclasses);
        super.visit(n, arg);
    }
}

/**
 * This visitor class undos the operations performed by the previous visitor.
 *
 * @author redon
 */
final class RemoveSuperClassVisitor extends VoidVisitorAdapter<Void> {
    private Class<?> superclassToRemove;

    public RemoveSuperClassVisitor(Class<?> interfaceToRemove) {
        this.superclassToRemove = interfaceToRemove;
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        List<ClassOrInterfaceType> superclasses = n.getExtends();
        List<ClassOrInterfaceType> newExtends = new ArrayList<ClassOrInterfaceType>();

        for (ClassOrInterfaceType cl : superclasses) {
            if (!(cl.getName().equals(superclassToRemove.getName())
                    || (cl.toString().startsWith(superclassToRemove.getName())))) {
                newExtends.add(cl);
            }
        }

        n.setExtends(newExtends);
        super.visit(n, arg);
    }
}

/**
 * This class adds modifies the superclass of existing classes. This class
 * doesn't compile this code, only generates it.
 *
 * @author Jos� Manuel Redondo, Ignacio Lagartos
 */
public class SetSuperClassPrimitive extends AbstractPrimitive {
    private Class<?> newSuperClass;
    private Class<?>[] typeParameters;

    public SetSuperClassPrimitive(ClassContent classContent, Class<?> newSuperClass, Class<?>... typeParameters) {
        super(classContent);
        this.newSuperClass = newSuperClass;
        this.typeParameters = typeParameters;
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

        SetSuperClassVisitor addv = new SetSuperClassVisitor(this, this.newSuperClass, this.typeParameters);

        // Visit class declaration and add the corresponding interface
        unit.accept(addv, null);
        if (addv.getError() != null)
            throw addv.getError();

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

        RemoveSuperClassVisitor remv = new RemoveSuperClassVisitor(this.newSuperClass);

        // Visit class declaration and remove the corresponding interface
        unit.accept(remv, null);

        // Update class contents
        classContent.setContent(unit.toString());
    }

    public String changeVersion(String content, Class<?> clazz, int from,
                                int to) {
        return super.changeVersion(content, clazz, from, to);
    }

    public String updateVersionRetStr(Class<?> clazz, String superclassName)
            throws StructuralIntercessionException {
        SourceCodeCache classEditor = SourceCodeCache.getInstance();
        ClassContent classContent = classEditor.getClassContent(clazz);
        if (classContent.isUpdated()) {
            // Update the superclass when the classContent is already updated
            String content = classContent.getContent();
            content = classContent.getContent().replaceFirst(
                    "extends(\\s+)((\\w|\\.)*)(\\s*)",
                    "extends " + superclassName + " ");
            classContent.setContent(content);
            return superclassName;
        }
        modifiedClasses.add(classContent);
        // Update the version number
        int oldVersionNumber = classContent.getVersion();
        int newVersionNumber = oldVersionNumber + 1;
        classContent.setVersion(newVersionNumber);
        String content = classContent.getContent();
        // Update the className
        content = this.changeVersion(content, clazz, oldVersionNumber,
                newVersionNumber);
        // Update the superclass
        content = content.replaceFirst("extends(\\s+)((\\w|\\.)*)(\\s*)",
                "extends " + superclassName + " ");
        classContent.setContent(content);
        // Update the path
        String path = classContent.getPath();
        path = path.replaceAll("(_)(\\d+)(.java)", "_" + newVersionNumber
                + ".java");
        classContent.setPath(path);
        // Update subclasses
        for (Class<?> subClass : InheritanceTables.getSubclasses(clazz)) {
            updateVersion(subClass, clazz.getSimpleName() + "_NewVersion_"
                    + newVersionNumber);
        }
        // ClassContent updated
        classContent.setUpdated(true);
        return clazz.getSimpleName() + "_NewVersion_" + newVersionNumber;
    }
}
