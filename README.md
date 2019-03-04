# JMPlib v1.1.0

Dynamic languages are widely used due to the flexibility needed in some applications or systems. Therefore, dynamic language metaprogramming features have been incorporated gradually to statically-typed languages. Our work is aimed to improve the flexibility of Java language without modifying the Java Virtual Machine. We developed a library that allows Java language to support two types of metaprogramming features: 1) structural intercession y 2) dynamic code evaluation. This was achieved using class versioning, code instrumentation and Hot-Swapping. In conclusion, the library allows programmers to use these two functionalities in new or legacy code to improve its runtime flexibility.

###V1.1.1

This minor revision adds a new agent parameter enabling the replacement of IL instructions that use the isinstance operators and perform casts. This way, dynamic changes to the implemented interfaces of a class can be taken into account when performing these operations. This feature is enabled by default. To disable it, pass the following parameter to the agent: disable_is_instance.

The feature automatically converts code like: ```java object instanceof Comparable```

In: ```java Introspector.instanceOf(object, Comparable.class);```

Additionally, code like: ```java (Comparable)object;```
Now is turned into: ```java (Comparable)Introspector.cast(Comparable.class, object);```

This feaure works both in user supplied code and also in Java API classes, as it is implemented with the ASM library and therefore source code is not needed. However, when trying to apply these features with Java classes the command line must be modified, as JMPLib classes are loaded with a different ClassLoader than the classes in the Java API. This modification loads a JMPLib functionality wrapper with the same ClassLoader as the Java API classes, enabling the modified code to call the JMPLib operations. The wrapper just calls the proper JMPLib class with the correct classloader so the functionality is accessible from them. Therefore, the command line options to use this functionality from the Java API is:

```java -Xbootclasspath/a:./lib/jmplib-reflect.jar -javaagent:./lib/jmplib.jar```

The wrapper jar file is in the /lib directory of the sources. The source code of the wrapper is in the JMPLib source wrapper/ folder.

## Sources and binaries

The last stable version of the source code and its binaries are available in the [releases](https://github.com/ComputationalReflection/JMPLib/releases) section. The previous version of this project is available in this [previous release](https://github.com/ilagartos/jmplib/releases/) repository.

## How to use it!

### Starting

For this example, we have created a `Car` class that simulates the fuel consumption when the car travels:

```java
package addmethod;

public class Car {
	
	private int km = 0;
	private int fuel = 70;
	private double consumptionAverage = 5.7; // 100km
	
	public void run(int kilometres) throws Exception{
		km += kilometres;
		fuel -= kilometres * consumptionAverage/100;
		if(fuel < 0)
			throw new Exception("Out of fuel");
	}
	
	public void refill(int litres){
		fuel += litres;
	}

	@Override
	public String toString() {
		return "Car [km=" + km + ", fuel=" + fuel + ", consumptionAverage=" + consumptionAverage + "]";
	}
}
```

### Using meta-programming

Additionally, we have created a main class with some meta-programming:

```java
package addmethod;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;

import jmplib.IIntercessor;
import jmplib.TransactionalIntercessor;

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
```

First of all, we have created a car and travelled 250km with it. Later, we have applied a couple of meta-programming primitives to improve the car class. In one hand, we have added a new method that calculates how many kilometres left until the car runs out of fuel. On the other hand, we have modified the toString method to show this new information. Finally, we have run 250km with the same car.

### Little config

We have created a file called `config.properties` in the root folder. This file have two lines, one specifying the path to the jre inside the JDK and other specifying the path to our source folder.

```
java.home=C:\\Program Files\\Java\\jdk1.8.0_45\\jre
source.path=src\\
```

### Compile

```bash
javac -cp jmplib-with-dependencies.jar -d bin/ src\addmethod\*
```

### Program structure

After compiling the code, the proyect have the next structure:

```bash
├── bin/
│   ├── addmethod/
│   │   ├── AddMethodMain.class
│   │   ├── Car.class
├── src/
│   ├── addmethod/
│   │   ├── AddMethodMain.java
│   │   ├── Car.java
├── config.properties
├── jmplib-with-dependencies.jar
```

### Execute

Using the library requires specifying the `-javaagent` parameter. 

```bash
java -javaagent:jmplib-with-dependencies.jar -cp .;bin/ addmethod.AddMethodMain
```

The console output is the following one:  

```
======== INITIAL STATE =======
Car [km=0, fuel=70, consumptionAverage=5,700000]
======== 250km Later =======
Car [km=250, fuel=55, consumptionAverage=5,700000]
- Method added to see remaining km and toString modified
======== 500km Later =======
Car [km=500, fuel=40, consumptionAverage=5,700000, kilometersLeft=228]
```

As you can see, the last line shows the new information due to the car instance that we had created has obtained the new functionallity.

## More info

See [javadoc](https://computationalreflection.github.io/JMPLib/) of the current version of the project (1.1.0)

See [javadoc](https://cdn.rawgit.com/ilagartos/jmplib/master/docs/index.html) of the 1.0.0 version 
