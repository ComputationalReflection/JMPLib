package jmplib.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import jmplib.annotations.ExcludeFromJMPLib;

/**
 * Contains all the auxiliary code used by the Evaluator
 *
 * @author Jose Manuel Redondo Lopez
 */
@ExcludeFromJMPLib
public class EvaluatorUtils {
    /**
     * Changes all method bodies of the {@link CompilationUnit} for empty bodies.
     * Each method that returns any value is changed to return the default value.
     *
     * @param unit The {@link CompilationUnit} that contains the methods
     */
    public static void setEmptyBodies(CompilationUnit unit) {
        TypeDeclaration type = unit.getTypes().get(0);
        for (BodyDeclaration decl : type.getMembers()) {
            if (decl instanceof MethodDeclaration) {
                MethodDeclaration m = (MethodDeclaration) decl;
                String typeName = m.getType().toString();
                String defaultBody = "{return " + getDefaultValue(typeName) + ";}";
                try {
                    BlockStmt defaultParsedBody = JavaParser.parseBlock(defaultBody);
                    m.setBody(defaultParsedBody);
                } catch (ParseException e) {
                }
            }
        }
    }

    /**
     * Return the default value for the provided type. The value is returned as
     * String to append to the method body.
     *
     * @param type The type to check the default value
     * @return The String with the default value
     */
    public static String getDefaultValue(String type) {
        switch (type.toLowerCase()) {
            case "int":
            case "short":
            case "byte":
            case "char":
            case "long":
                return "0";
            case "float":
            case "double":
                return "0.0";
            case "void":
                return "";
            default:
                return "null";
        }
    }
}
