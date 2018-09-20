package es.uniovi.jmplib.testing.times.binarytrees;

//package addmethod;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;

class Car {

    public void run(int i) {
    }
}
public class AddMethodMain {

    public static void main(String[] args) {
        Car car = new Car();
        System.out.println("======== INITIAL STATE =======");
        System.out.println(car);
        try {
            car.run(250);
            System.out.println("======== 250km Later =======");
            System.out.println(car);
            // Method added to know the remaining kilometres
            IIntercessor transaction = new TransactionalIntercessor().createIntercessor();
            transaction.addMethod(
                    Car.class, new jmplib.reflect.Method(
                    "kilometresToRunOutOfFuel",
                    MethodType.methodType(int.class),
                    "return (fuel < 0) ? 0 : (int)(fuel * consumptionAverage);"));
            transaction.replaceImplementation(
                    Car.class, new jmplib.reflect.Method(
                    "toString",
                    "return String.format(\"Car [km=%d, fuel=%d, consumptionAverage=%f, kilometersLeft=%d]\""
                            + ", km, fuel, consumptionAverage, kilometresToRunOutOfFuel());"));
            transaction.commit();
            System.out.println("- Method added to see remaining km and toString modified");
            car.run(250);
            System.out.println("======== 500km Later =======");
            System.out.println(car);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
