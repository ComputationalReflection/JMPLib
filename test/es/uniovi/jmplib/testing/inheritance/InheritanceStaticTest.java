package es.uniovi.jmplib.testing.inheritance;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.Test;

import jmplib.DefaultEvaluator;
import jmplib.DefaultIntercessor;
import jmplib.IEvaluator;
import jmplib.IIntercessor;
import jmplib.annotations.ExcludeFromJMPLib;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.InvokerData;
import jmplib.invokers.MemberInvokerData;

@ExcludeFromJMPLib
public class InheritanceStaticTest {
	private static IIntercessor Intercessor = DefaultIntercessor.getInstance();
	private static IEvaluator Evaluator = DefaultEvaluator.getInstance();

	/*
	 * @Before public void init() { IntercessorTransaction.resetLibraryState(); }
	 */

	@Test
	public void testAddMethod_InstanceMethod_AbstractClass() throws StructuralIntercessionException {
		Func_String_LivingBeing_String toString = null;
		String name = "Bilbo";
		String body = "return \"I am a living being! My name is \" + name + \"!\";";
		String expected = "I am a living being! My name is " + name + "!";
		Intercessor.addMethod(LivingBeing.class, new jmplib.reflect.Method("toString",
				MethodType.methodType(String.class, String.class), body, Modifier.PUBLIC, "name"));
		toString = Evaluator.getMethodInvoker(LivingBeing.class, "toString",
				new MemberInvokerData<>(Func_String_LivingBeing_String.class));
		assertNotNull(toString);
		String actual = toString.invoke(new Animal(), name);
		assertThat(actual, equalTo(expected));
		actual = toString.invoke(new Mammal(), name);
		assertThat(actual, equalTo(expected));
		actual = toString.invoke(new Dog(), name);
		assertThat(actual, equalTo(expected));
		actual = toString.invoke(new Dalmatian(), name);
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testAddMethod_Override_NonEditableClass() throws StructuralIntercessionException {
		Dog dog = new Dog();
		Dog dalmatian = new Dalmatian();
		Dog dog2 = new Dog();
		dog.setChipNumber(3);
		dog2.setChipNumber(5);
		dalmatian.setChipNumber(3);
		String body = "if(o instanceof Dog) return this.getChipNumber() == ((Dog)o).getChipNumber();"
				+ "else return false;";
		Intercessor.addMethod(Dog.class, new jmplib.reflect.Method("equals",
				MethodType.methodType(boolean.class, Object.class), body, Modifier.PUBLIC, "o"));
		Func_boolean_Dog_Object equals = Evaluator.getMethodInvoker(Dog.class, "equals",
				new MemberInvokerData<>(Func_boolean_Dog_Object.class));
		assertNotNull(equals);
		boolean actual = equals.invoke(dog, dalmatian);
		assertThat(actual, equalTo(true));
		actual = equals.invoke(dog, dog2);
		assertThat(actual, equalTo(false));
		actual = dog.equals(dalmatian);
		assertThat(actual, equalTo(true));
		actual = dog.equals(dog2);
		assertThat(actual, equalTo(false));
	}

	@Test
	public void testAddMethod_Override() throws StructuralIntercessionException {
		String expected = "I am an Animal!";
		Func_String_Animal overrideToString = null;
		// Overriding toString on Animal
		Intercessor.addMethod(Animal.class, new jmplib.reflect.Method("toString", MethodType.methodType(String.class),
				"return \"" + expected + "\";", Modifier.PUBLIC));
		overrideToString = Evaluator.getMethodInvoker(Animal.class, "toString",
				new MemberInvokerData<>(Func_String_Animal.class));
		assertNotNull(overrideToString);
		String actual = overrideToString.invoke(new Animal());
		assertThat(actual, equalTo(expected));
		actual = overrideToString.invoke(new Mammal());
		assertThat(actual, equalTo(expected));
		actual = overrideToString.invoke(new Dog());
		assertThat(actual, equalTo(expected));
		actual = overrideToString.invoke(new Dalmatian());
		assertThat(actual, equalTo(expected));
		actual = new Animal().toString();
		assertThat(actual, equalTo(expected));
		actual = new Mammal().toString();
		assertThat(actual, equalTo(expected));
		actual = new Dog().toString();
		assertThat(actual, equalTo(expected));
		actual = new Dalmatian().toString();
		assertThat(actual, equalTo(expected));
		LivingBeing livingBeing = new Dalmatian();
		actual = livingBeing.toString();
		assertThat(actual, equalTo(expected));
		expected = "I am a living being!";
		actual = new Plant().toString();
		assertThat(actual, equalTo(expected));
		actual = new Oak().toString();
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testAddMethod_Abstract() throws StructuralIntercessionException {
		// Abstract method
		// Firstly, add the implementation to subclasses
		String animalNutriments = "Animals and/or plants";
		String plantNutriments = "Mineral nutrients, oxigen, hidroxigen and carbon";
		Intercessor.addMethod(Animal.class, new jmplib.reflect.Method("getNutriment",
				MethodType.methodType(String.class), "return \"" + animalNutriments + "\";", Modifier.PUBLIC));
		Intercessor.addMethod(Plant.class, new jmplib.reflect.Method("getNutriment",
				MethodType.methodType(String.class), "return \"" + plantNutriments + "\";", Modifier.PUBLIC));
		// All subclasses have the method implementation so add the abstract one
		Intercessor.addMethod(LivingBeing.class, new jmplib.reflect.Method("getNutriment",
				MethodType.methodType(String.class), "", Modifier.ABSTRACT | Modifier.PUBLIC));
		Func_String_LivingBeing getNutriment = Evaluator.getMethodInvoker(LivingBeing.class, "getNutriment",
				new MemberInvokerData<>(Func_String_LivingBeing.class));
		assertNotNull(getNutriment);
		String actual = getNutriment.invoke(new Plant());
		assertThat(actual, equalTo(plantNutriments));
		actual = getNutriment.invoke(new Animal());
		assertThat(actual, equalTo(animalNutriments));
	}

	@Test
	public void testReplaceImplementation() throws StructuralIntercessionException {
		Plant plant = new Plant();
		String expected = "Nothing! I am a boring plant!";
		String actual = plant.getFunctions();
		assertThat(actual, equalTo(expected));
		String newFunctions = "My functions are the same as every "
				+ "living being: nutrition, interaction and reproduction";
		Intercessor.replaceImplementation(Plant.class, new jmplib.reflect.Method("getFunctions",
				MethodType.methodType(String.class), "return \"" + newFunctions + "\";"));
		Plant oak = new Oak();
		actual = oak.getFunctions();
		assertThat(actual, equalTo(newFunctions));
		actual = plant.getFunctions();
		assertThat(actual, equalTo(newFunctions));
	}

	@Test
	public void testDeleteMethod() throws StructuralIntercessionException {
		// Delete override method
		Intercessor.removeMethod(Dalmatian.class, new jmplib.reflect.Method("bark"));
		Dalmatian dalmatian = new Dalmatian();
		String expected = "bark!";
		// must call super
		String actual = dalmatian.bark();
		assertThat(actual, equalTo(expected));
		// delete parent class method
		try {
			Intercessor.removeMethod(Dog.class, new jmplib.reflect.Method("bark"));
			// fail("The method should rise an exception");
		} catch (Exception e) {
			fail("The method should not rise an exception");
			/*
			 * assertThat(e, instanceOf(StructuralIntercessionException.class));
			 * assertThat(e.getCause(), instanceOf(CompilationFailedException.class));
			 * assertNotNull(e.getCause()); assertNotNull(e.getCause().getMessage());
			 */
		}
	}

	@Test
	public void testAddField() throws StructuralIntercessionException {
		Oak oak = new Oak();
		int actual, expected = 7;
		Intercessor.addField(Plant.class, new jmplib.reflect.Field(Modifier.PUBLIC, int.class, "flowers", "0"));
		Intercessor.addMethod(Oak.class,
				new jmplib.reflect.Method("flourish", MethodType.methodType(void.class, int.class),
						"this.flowers += buds;", Modifier.PUBLIC, new String[] { "buds" }));
		Action_Oak_int flourish = Evaluator.getMethodInvoker(Oak.class, "flourish",
				new MemberInvokerData<>(Action_Oak_int.class));
		assertNotNull(flourish);
		flourish.invoke(oak, expected);
		Intercessor.addMethod(Plant.class, new jmplib.reflect.Method("getFlowers", MethodType.methodType(int.class),
				"return flowers;", Modifier.PUBLIC));
		Func_int_Plant getFlowers = Evaluator.getMethodInvoker(Plant.class, "getFlowers",
				new MemberInvokerData<>(Func_int_Plant.class));
		assertNotNull(getFlowers);
		actual = getFlowers.invoke(oak);
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void testDeleteField() throws StructuralIntercessionException {
		Mother m = new Son();
		assertThat(m.getText(), equalTo("Son"));
		Intercessor.removeField(Son.class, new jmplib.reflect.Field("text"));
		assertThat(m.getText(), equalTo("Mother"));
	}

	@Test
	public void testPolimorphicReferences() {
		Parent parent = new Parent();
		Child child = new Child();
		Parent childPoly = new Child();
		assertTrue(parent.a == 1);
		assertThat(child.a, equalTo("hi"));
		assertTrue(childPoly.a == 1);
	}

	@Test
	public void testDefaultMethodAttribute() {
		Person p = new Employee();
		p.name = "Pepe";
		assertThat(p.name, equalTo("Pepe"));
		Employee p2 = new Employee();
		p2.name = "Pepe";
		assertThat(p2.name, equalTo("Pepe"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDefaultMethodAttributeVersions() throws StructuralIntercessionException {
		PersonToModify p = new EmployeeToModify();
		p.name = "Pepe";
		assertThat(p.name, equalTo("Pepe"));
		EmployeeToModify p2 = new EmployeeToModify();
		p2.name = "Pepe";
		assertThat(p2.name, equalTo("Pepe"));
		Intercessor.addField(PersonToModify.class, new jmplib.reflect.Field(Modifier.PUBLIC, String.class, "lastName"));
		BiConsumer<EmployeeToModify, String> set = Evaluator.setFieldInvoker(EmployeeToModify.class, "lastName",
				new MemberInvokerData<>(BiConsumer.class, EmployeeToModify.class, String.class));
		set.accept(p2, "Perez");
		Function<EmployeeToModify, String> get = Evaluator.getFieldInvoker(EmployeeToModify.class, "lastName",
				new MemberInvokerData<>(Function.class, EmployeeToModify.class, String.class));
		assertThat(get.apply(p2), equalTo("Perez"));
	}

}
