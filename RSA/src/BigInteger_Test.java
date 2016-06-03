import java.util.Random;
import java.math.BigInteger;

public class BigInteger_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger maxint = new BigInteger("2").pow(5);
		
		BigInteger randomint = new BigInteger(3, new Random());

		System.out.println("random=" + randomint);
		System.out.println("maxint=" + maxint);
		System.out.println("plus=" + maxint.add(randomint));
	}

}
