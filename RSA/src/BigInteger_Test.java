import java.util.Random;
import java.math.BigInteger;

public class BigInteger_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger maxint = new BigInteger("151").pow(240);
		
		BigInteger moduloint = maxint.remainder(BigInteger.valueOf(240));

		System.out.println("maxint=" + maxint);
		System.out.println("moduloint=" + moduloint);
	}
}
