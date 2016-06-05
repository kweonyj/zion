import java.util.Random;
import java.math.BigInteger;

public class BigInteger_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger maxint = new BigInteger("137").pow(540);
		
		BigInteger moduloint = maxint.remainder(BigInteger.valueOf(540));

		System.out.println("maxint=" + maxint);
		System.out.println("moduloint=" + moduloint);
	}
}
