package es.uniovi.jmplib.testing.state;

import java.util.ArrayList;
import java.util.List;

import jmplib.classversions.VersionTables;

public class Person {
	
	public String name;
	public String surname;
	public Gender gender;
	public int zipcode;
	public List<Person> descendants = new ArrayList<Person>();
	private Car car = null;
	
	public Person(){
		this.name = "Juan";
		this.surname = "Garcia";
		this.gender = Gender.MALE;
		this.zipcode = 33005;
	}
	
	public Person(String name, String surname, Gender gender, int zipcode){
		this.name = name;
		this.surname = surname;
		this.gender = gender;
		this.zipcode = zipcode;
	}

	public Car getCar() {
		return car;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public int getZipcode() {
		return zipcode;
	}
	public void setZipcode(int zipcode) {
		this.zipcode = zipcode;
	}
	public List<Person> getDescendants() {
		return descendants;
	}
	public Gender getGender() {
		return gender;
	}

	public void addDescendant(Person descendant){
		if(descendant == null)
			throw new IllegalArgumentException();
		descendants.add(descendant);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getGender() == null) ? 0 : getGender().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getSurname() == null) ? 0 : getSurname().hashCode());
		result = prime * result + getZipcode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (VersionTables.getNewVersion(getClass()) != 
				VersionTables.getNewVersion(obj.getClass()))
			return false;
		Person other = (Person) obj;
		if (name == null) {
			if (other.getName() != null)
				return false;
		} else if (!name.equals(other.getName()))
			return false;
		if (surname == null) {
			if (other.getSurname() != null)
				return false;
		} else if (!surname.equals(other.getSurname()))
			return false;
		if (zipcode != other.getZipcode())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", surname=" + surname + ", gender="
				+ gender + ", zipcode=" + zipcode + ", class=" + getClass().getName() + "]";
	}
	
	

}
