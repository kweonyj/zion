import java.util.Random;
import java.math.BigInteger;

public class BigInteger_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger maxint = new BigInteger("3").pow(5);
		
		BigInteger mok = maxint.divide(BigInteger.valueOf(2));

		System.out.println("maxint=" + maxint);
		System.out.println("mok=" + mok);
	}
}
