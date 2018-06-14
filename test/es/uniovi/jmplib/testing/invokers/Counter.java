package es.uniovi.jmplib.testing.invokers;

public class Counter {
	
	public int counter;
	public static int COUNTERS = 10;
	
	public void increment(){
		counter++;
	}
	
	public void decrement(){
		counter--;
	}
	
	public double getCounter(){
		return counter;
	}

}
