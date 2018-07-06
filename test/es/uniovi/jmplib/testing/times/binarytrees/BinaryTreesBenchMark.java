package es.uniovi.jmplib.testing.times.binarytrees;

import es.uniovi.jmplib.testing.times.BenchMark;
import es.uniovi.jmplib.testing.times.Test;
import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;
import jmplib.exceptions.StructuralIntercessionException;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class BinaryTreesBenchMark extends BenchMark {
    public BinaryTreesBenchMark(Test test) {
        super(test);
    }

    @Override
    public void prepare() {
        IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
        try {
            List<jmplib.reflect.Field> fields = new ArrayList<jmplib.reflect.Field>();
            List<jmplib.reflect.Method> methods = new ArrayList<jmplib.reflect.Method>();

            // TreeNode
            fields.add(new jmplib.reflect.Field(Modifier.PRIVATE, int.class, "item"));
            fields.add(new jmplib.reflect.Field(Modifier.PRIVATE, TreeNode.class, "left"));
            fields.add(new jmplib.reflect.Field(Modifier.PRIVATE, TreeNode.class, "right"));
            fields.add(new jmplib.reflect.Field(Modifier.PRIVATE, String.class, "nodeId"));

            methods.add(new jmplib.reflect.Method("getItem", MethodType.methodType(int.class), "return item;"));
            methods.add(new jmplib.reflect.Method("setItem", MethodType.methodType(void.class, int.class),
                    "item = value;", "value"));
            methods.add(new jmplib.reflect.Method("getLeft", MethodType.methodType(TreeNode.class), "return left;"));
            methods.add(new jmplib.reflect.Method("setLeft", MethodType.methodType(void.class, TreeNode.class),
                    "left = value;", "value"));
            methods.add(new jmplib.reflect.Method("getRight", MethodType.methodType(TreeNode.class), "return right;"));
            methods.add(new jmplib.reflect.Method("setRight", MethodType.methodType(void.class, TreeNode.class),
                    "right = value;", "value"));
            methods.add(new jmplib.reflect.Method("buildId", MethodType.methodType(String.class),
                    "if ((left == null) && (right == null)) " + "return \"(empty node)\";"
                            + "String temp = left == null ? \"(left null)\" : left.buildId() + \" < { \"+ nodeId + \" }\";"
                            + "temp += right == null ? \"(right null)\" : \" > \" + right.buildId();"
                            + "return temp;"));
            methods.add(new jmplib.reflect.Method("check", MethodType.methodType(int.class),
                    "nodeId = buildId();" + "return left == null ? item : left.check() - right.check() + item;"));
            methods.add(new jmplib.reflect.Method("create", MethodType.methodType(TreeNode.class, int.class, int.class),
                    "return ChildTreeNodes(item, depth);", Modifier.STATIC | Modifier.PUBLIC, "item", "depth"));
            methods.add(new jmplib.reflect.Method("ChildTreeNodes",
                    MethodType.methodType(TreeNode.class, int.class, int.class),
                    "TreeNode node = new TreeNode(item);" + "if (depth > 0) {" +
                            // "node.setLeft(ChildTreeNodes(2 * item - 1, depth - 1));" +
                            // "node.setRight(ChildTreeNodes(2 * item, depth - 1));" +
                            "node.left = ChildTreeNodes(2 * item - 1, depth - 1);"
                            + "node.right = ChildTreeNodes(2 * item, depth - 1);" + "}" + "return node;",
                    Modifier.STATIC | Modifier.PUBLIC, "item", "depth"));

            transaction.addField(TreeNode.class, fields.toArray(new jmplib.reflect.Field[0]));
            transaction.addMethod(TreeNode.class, methods.toArray(new jmplib.reflect.Method[0]));

            transaction.replaceImplementation(TreeNode.class,
                    new jmplib.reflect.Method("initialize", "nodeId = Integer.toString(item);" + "this.item = item;"));

            // BinaryTreesTest
            transaction.replaceImplementation(BinaryTrees.class, new jmplib.reflect.Method("test", "int minDepth = 4;"
                    + "int maxDepth = Math.max(minDepth + 2, BenchMark.ITERATIONS);"
                    + "int stretchDepth = maxDepth + 1;" + "int check = (TreeNode.create(0, stretchDepth)).check();"
                    + "TreeNode longLivedTree = TreeNode.create(0, maxDepth);"
                    + "for (int depth = minDepth; depth <= maxDepth; depth += 2)" + "{"
                    + "int iterations = 1 << (maxDepth - depth + minDepth);" + "check = 0;"
                    + "for (int i = 1; i <= iterations; i++)" + "{" + "check += (TreeNode.create(i, depth)).check();"
                    + "check += (TreeNode.create(-i, depth)).check();" + "}" + "}"));

            transaction.commit();
        } catch (StructuralIntercessionException e) {
            e.printStackTrace();
        }
    }

}
