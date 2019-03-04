package es.uniovi.jmplib.testing.times.binarytrees;

//package addmethod;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.SimpleEvaluator;
import jmplib.TransactionalIntercessor;
import jmplib.invokers.EvalInvokerData;
import jmplib.invokers.MemberInvokerData;
import jmplib.reflect.Introspector;
import jmplib.reflect.Method;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.function.Function;





public class AddMethodMain {

    public static void main(String[] args) {
        try { //REDONDO: EXCEPTION HANDLING (ENTIENDO QUE OMITIDA A PROPOSITO POR CLARIDAD)
            Calculator calc = new Calculator();
            // Transaction with different modifications
            IIntercessor intercessor = new TransactionalIntercessor().createIntercessor(); //REDONDO: CREACION DEL INTERCESSOR
            // Adds one "lastResult" field
            intercessor.addField(Calculator.class,
                    new jmplib.reflect.Field(Modifier.PRIVATE, double.class, "lastResult"));
            // Changes the implementation of "add" to consider "lastResult"
            intercessor.replaceImplementation(Calculator.class, new jmplib.reflect.Method("add", //REDONDO: CREACION DEL METHOD (FALTABA NOMBRE DE PARAMETROS Y TIPO DEL METODO)
                    MethodType.methodType(double.class, double.class, double.class),
                    "this.lastResult = a + b; return this.lastResult;",  "a", "b"));
            // Also changes "sub", but modifying the existing AST
            MethodDeclaration md = Introspector.decorateClass(Calculator.class)
                    .getMethod("sub", double.class, double.class).getMethodDeclaration();
            ExpressionStmt stmt = new ExpressionStmt(new AssignExpr(
                    new NameExpr("lastResult"),
                    new BinaryExpr(new NameExpr(md.getParameters().get(0).getId().getName()),
                            new NameExpr(md.getParameters().get(1).getId().getName()),
                            BinaryExpr.Operator.minus)
                    , AssignExpr.Operator.assign));
            md.getBody().getStmts().add(0, stmt);
            intercessor.replaceImplementation(Calculator.class, new Method(md));
            // Adds getLastResult
            intercessor.addMethod(Calculator.class, new Method("getLastResult",
                    MethodType.methodType(double.class), "return this.lastResult;"));
            // Executes all the changes at once
            intercessor.commit();
            // Calls the new "sub" method
            calc.sub(3.3, 4.4);
            // Gets a Java 8 standard functional interface to invoke getLastResult
            IEvaluator evaluator = new SimpleEvaluator().createEvaluator(); //REDONDO: CREACION DEL EVALUATOR
            Function<Calculator, Double> getLastResult = evaluator.getMethodInvoker(
                    Calculator.class, "getLastResult",
                    new MemberInvokerData<Function>(Function.class, Calculator.class,
                            double.class));
            double result = getLastResult.apply(calc); //REDONDO: PARÉNTESIS EXTRA

            String methodName = "sub"; //REDONDO: Nombre del metodo sin declarar.
            // Dynamic code evaluation (i.e., eval) to call one method in calc with 2 params
            result = evaluator.generateEvalInvoker("calculator." + methodName + "(p1, p2)",
                    new EvalInvokerData<>(CalcFunc.class, new String[]{"calculator", "p1", "p2"}) //REDONDO: COMA EXTRA
            ).apply(calc, 3.0, 4.0); //REDONDO: Puse constantes en lugar de nombres de variables que estaban sin declarar

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
