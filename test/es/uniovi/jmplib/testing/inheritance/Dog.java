package es.uniovi.jmplib.testing.inheritance;

public class Dog extends Mammal{
	
	private int chipNumber;
	
	public int getChipNumber() {
		return chipNumber;
	}

	public void setChipNumber(int chipNumber) {
		this.chipNumber = chipNumber;
	}

	public String bark(){
		return "bark!";
	}

}
