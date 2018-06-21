package es.uniovi.jmplib.testing.parser_integration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import jmplib.IIntercessor;
import jmplib.SimpleIntercessor;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.reflect.Field;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class JavaParserIntegrationTest {

    @Test
    public void methodCodeParseTest() throws StructuralIntercessionException {
        try {
            jmplib.reflect.Class cl = Introspector.decorateClass(Calculator.class);

            Method m = cl.getMethod("sin", double.class);
            assertEquals("return Math.sin(a);", m.getSourceCode().trim());
            MethodDeclaration md = m.getMethodDeclaration();

            BlockStmt b = md.getBody();
            Statement s = JavaParser.parseStatement("System.out.println(\"Invoking sin\");");

            List<Statement> sts = b.getStmts();
            sts.add(0, s);
            md.setBody(b);
            Method newMethod = new Method(md);

            IIntercessor intercessor = SimpleIntercessor.getInstance();
            intercessor.replaceImplementation(Calculator.class, newMethod);
            assertTrue(m.getSourceCode().contains("System.out.println(\"Invoking sin\");"));
            assertTrue(m.getSourceCode().contains("return Math.sin(a);"));
        } catch (Exception ex) {
            throw new StructuralIntercessionException(ex.getMessage(), ex.getCause());
        }
    }

    @Test
    public void newFieldFromExistingTest() throws StructuralIntercessionException {
        try {
            jmplib.reflect.Class cl = Introspector.decorateClass(Calculator2.class);

            Field m = cl.getField("name");
            FieldDeclaration fd = m.getFieldDeclaration();
            fd.getVariables().get(0).setId(new VariableDeclaratorId("newName"));
            Field f2 = new Field(fd);
            IIntercessor intercessor = SimpleIntercessor.getInstance();
            intercessor.addField(cl, f2);
            String source = cl.getSourceCode().trim();

            assertTrue(source.contains("public class Calculator2"));
            assertTrue(source.contains("public String name;"));
            assertTrue(source.contains("public double add(double a, double b)"));
            assertTrue(source.contains("public double sin(double a)"));
            assertTrue(source.contains("public double cos(double a)"));
            assertTrue(source.contains("public double subtract(double a, double b)"));
            assertTrue(source.contains("public Object getGeneric(T param)"));
            assertTrue(source.contains("return a + b;"));
            assertTrue(source.contains("return Math.sin(a);"));
            assertTrue(source.contains("return Math.cos(a);"));
            assertTrue(source.contains("return a - b;"));
            assertTrue(source.contains("return param;"));

        } catch (Exception ex) {
            throw new StructuralIntercessionException(ex.getMessage(), ex.getCause());
        }
    }

    @Test
    public void classDeclarationTest() throws StructuralIntercessionException {
        jmplib.reflect.Class cl = Introspector.decorateClass(Calculator.class);

        try {
            ClassOrInterfaceDeclaration cd = cl.getClassDeclaration();

            jmplib.reflect.Class cl2 = new jmplib.reflect.Class(cd);
            assertEquals("es.uniovi.jmplib.testing.parser_integration.Calculator", cl2.getName());
        } catch (Exception ex) {
            throw new StructuralIntercessionException(ex.getMessage(), ex.getCause());
        }
    }
}
