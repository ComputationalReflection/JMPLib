package es.uniovi.jmplib.testing.state;

import jmplib.*;
import jmplib.exceptions.StructuralIntercessionException;
import jmplib.invokers.MemberInvokerData;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class StateTest {
    static Func_Person_List listSons = null;
    static Person father, son;
    private static IIntercessor Intercessor = new SimpleIntercessor().createIntercessor();
    private static IEvaluator Evaluator = new SimpleEvaluator().createEvaluator();

    @BeforeClass
    public static void initialize() throws StructuralIntercessionException {
        father = new Person("John", "Doe", Gender.MALE, 33333);
        son = new Person("Jack", "Doe", Gender.MALE, 33333);
        father.addDescendant(son);
        MethodType type = MethodType.methodType(List.class);
        Intercessor.addMethod(Person.class,
                new jmplib.reflect.Method("listOfSons", type,
                        "List<Person> sons = new ArrayList<Person>();" + "\nfor(Person person : descendants){"
                                + "\n\tif(person.getGender().equals(Gender.MALE)){" + "\n\t\tsons.add(person);" + "\n}"
                                + "\n}" + "\nreturn sons;",
                        Modifier.PUBLIC));
        listSons = Evaluator.getMethodInvoker(Person.class, "listOfSons", new MemberInvokerData<>(Func_Person_List.class));
    }

    @Test
    public void externalAccessGet() throws StructuralIntercessionException {
        Person p = new Person("John", "Doe", Gender.MALE, 33333);
        assertThat(p.name, equalTo(p.getName()));
        p.setName("Jack");
        assertThat(p.name, equalTo(p.getName()));
        assertThat(p.name, equalTo("Jack"));
    }

    @Test
    public void externalAccessSet() throws StructuralIntercessionException {
        Person p = new Person("John", "Doe", Gender.MALE, 33333);
        assertThat(p.getName(), equalTo("John"));
        p.name = "Jack";
        assertThat(p.getName(), equalTo("Jack"));
    }

    @Test
    public void basicType() throws StructuralIntercessionException {
        assertThat(father.getName(), equalTo("John"));
        assertThat(father.getSurname(), equalTo("Doe"));
        assertTrue(father.getZipcode() == 33333);
    }

    @Test
    public void stringType() throws StructuralIntercessionException {
        assertThat(father.getName(), equalTo("John"));
        assertThat(father.getSurname(), equalTo("Doe"));
        assertThat(son.getName(), equalTo("Jack"));
        assertThat(son.getSurname(), equalTo("Doe"));
    }

    @Test
    public void defaultEqualsWithGetClassComparation() throws StructuralIntercessionException {
        assertTrue(father.getDescendants().size() == 1);
        assertThat(father.getDescendants().get(0), equalTo(son));
        assertTrue(listSons.invoke(father).size() == 1);
        assertThat(listSons.invoke(father).get(0), equalTo(son));
    }

    @Test
    public void enumType() throws StructuralIntercessionException {
        assertThat(father.getGender(), equalTo(Gender.MALE));
        assertThat(son.getGender(), equalTo(Gender.MALE));
    }

    @Test
    public void testReplaceExternalAccess() {
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        c1.increment();
        assertTrue(c1.counter == 1);
        assertTrue(c2.counter == 0);
        assertTrue((c1.counter > 0 ? c1 : c2).counter == 1);
        assertTrue((c1.counter == 0 ? c1 : c2).counter == 0);
    }

    @Test
    public void testCrossReferences()
            throws StructuralIntercessionException, InstantiationException, IllegalAccessException {
        Person p = new Person();
        Car c = new Car();
        IIntercessor transaction = new TransactionalIntercessor();
        transaction.addMethod(Person.class,
                new jmplib.reflect.Method("setCar", MethodType.methodType(void.class, Car.class),
                        "this.car = car; car.setOwner(this);", Modifier.PUBLIC, "car"));
        transaction.addMethod(Car.class, new jmplib.reflect.Method("setOwner",
                MethodType.methodType(void.class, Person.class), "this.owner = owner;", Modifier.PUBLIC, "owner"));
        transaction.commit();

        Func_void_Person_Car setCar = Evaluator.getMethodInvoker(Person.class, "setCar",
                new MemberInvokerData<>(Func_void_Person_Car.class));

        assertNull(p.getCar());
        assertNull(c.getOwner());
        setCar.invoke(p, c);
        assertThat(p.getCar(), equalTo(c));
        assertThat(c.getOwner(), equalTo(p));
    }

}
