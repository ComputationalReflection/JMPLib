package es.uniovi.jmplib.testing.combined;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import jmplib.*;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.EvalInvokerData;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;
import java.util.function.Function;

import static junit.framework.TestCase.assertEquals;

public class CombinedFeaturesTest {

    @Test
    public void paperExample1() throws StructuralIntercessionException, NoSuchMethodException {
        // Values asked to the user: method name and method body (given the parameter "x")
        String methodName = "compute", methodBody = "Math.sqrt(x)";

        Calculator calc = new Calculator();
        //System.out.printf("1.1 + 2.2 = %f.\n", calc.add(1.1, 2.2)); // original method
        assertEquals(3.3, calc.add(1.1, 2.2), 0.1);

        // Transaction with different modifications
        IIntercessor intercessor = new TransactionalIntercessor().createIntercessor();
        // Adds one "lastResult" field
        intercessor.addField(Calculator.class, new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "lastResult"));
        // Changes the implementation of "add" to consider "lastResult"
        intercessor.replaceImplementation(
                Calculator.class, new jmplib.reflect.Method("add", "this.lastResult = a + b; return this.lastResult;"));
        // Also changes "sub", but modifying the existing AST
        MethodDeclaration md = Introspector.decorateClass(Calculator.class)
                .getMethod("sub", double.class, double.class)
                .getMethodDeclaration();
        ExpressionStmt stmt = new ExpressionStmt(new AssignExpr(
                new NameExpr("lastResult"),
                new BinaryExpr(
                        new NameExpr(md.getParameters().get(0).getId().getName()),
                        new NameExpr(md.getParameters().get(1).getId().getName()),
                        BinaryExpr.Operator.minus)
                , AssignExpr.Operator.assign));
        md.getBody().getStmts().add(0, stmt);
        intercessor.replaceImplementation(Calculator.class, new Method(md));
        // Adds getLastResult
        intercessor.addMethod(Calculator.class, new Method("getLastResult",
                MethodType.methodType(double.class),
                "return this.lastResult;"));
        // Executes all the changes at once
        intercessor.commit();

        //System.out.printf("2.2 + 3.3 = %f.\n", calc.add(2.2, 3.3)); //TYPO IN THE ORIGINAL EXAMPLE
        assertEquals(5.5, calc.add(2.2, 3.3), 0.1);

        // Gets a Java 8 standard functional interface to invoke getLastResult
        IEvaluator evaluator = new SimpleEvaluator().createEvaluator();
        Function<Calculator, Double> getLastResult = evaluator.getMethodInvoker(Calculator.class, "getLastResult",
                new MemberInvokerData<>(Function.class, Calculator.class, double.class));

        //System.out.printf("Last result: %f.\n", getLastResult.apply(calc));
        assertEquals(5.5,  getLastResult.apply(calc), 0.1);

        //System.out.printf("3.3 - 4.4 = %f.\n", calc.sub(3.3, 4.4));//TYPO IN THE ORIGINAL EXAMPLE
        assertEquals(-1.1,  calc.sub(3.3, 4.4));

        // Uses dynamic code evaluation (i.e., eval) to run "calc.getLastResult()"
       /*System.out.printf("Last result: %f.\n",
                evaluator.generateEvalInvoker("calc.getLastResult()",
                        new EvalInvokerData<>(Function.class, new String[]{"calc"}, new Class<?>[]{Calculator.class, double.class})
                ).apply(calc)
        );*/
        assertEquals(-1.1,  evaluator.generateEvalInvoker("calc.getLastResult()",
                new EvalInvokerData<>(Function.class, new String[]{"calc"}, new Class<?>[]{Calculator.class, double.class})
        ).apply(calc));


        // Adds a new method, taking its name and body as variables (e.g., asked to the user at runtime)
        // OJO, ESTO HABRIA QUE HACERLO CON LA FACHADA THREAD-SAFE PARA MOSTRAR SU FUNCIONAMIENTO
        intercessor = new SimpleIntercessor().createIntercessor();
        intercessor.addMethod(Calculator.class, new Method(methodName, MethodType.methodType(double.class, double.class),
                "this.lastResult = " + methodBody + ";" + "return this.lastResult;",
                Modifier.PUBLIC, "x"));
        // Takes the new method as a standard functional interface
        BiFunction<Calculator, Double, Double> userMethod = evaluator.getMethodInvoker(Calculator.class, methodName,
                new MemberInvokerData<>(BiFunction.class, Calculator.class, double.class, double.class));

        //System.out.printf("%s(5.5) = %f.\n", methodBody, userMethod.apply(calc, 5.5));
        assertEquals(Math.sqrt(5.5), userMethod.apply(calc, 5.5), 0.1);

        //System.out.printf("Last result: %f.\n", getLastResult.apply(calc));
        assertEquals(Math.sqrt(5.5), getLastResult.apply(calc), 0.1);
    }
}
