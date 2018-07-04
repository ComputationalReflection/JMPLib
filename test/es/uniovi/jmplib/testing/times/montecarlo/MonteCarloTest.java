package es.uniovi.jmplib.testing.times.montecarlo;

/**
 Estimate Pi by approximating the area of a circle.

 How: generate N random numbers in the unit square, (0,0) to (1,1)
 and see how are within a radius of 1 or less, i.e.
 <pre>  

 sqrt(x^2 + y^2) < r

 </pre>
  since the radius is 1.0, we can square both sides
  and avoid a sqrt() computation:
  <pre>

    x^2 + y^2 <= 1.0

  </pre>
  this area under the curve is (Pi * r^2)/ 4.0,
  and the area of the unit of square is 1.0,
  so Pi can be approximated by 
  <pre>
		        # points with x^2+y^2 < 1
     Pi =~ 		--------------------------  * 4.0
		             total # points

  </pre>

*/

public class MonteCarloTest extends Test
{
	



	@Override
	public void test() {
	}


}
