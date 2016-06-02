import java.util.Random;
import java.math.BigInteger;

public class BigInteger_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger result = new BigInteger("2");
		Random rn = new Random();
		System.out.println("result = " + result + "rn = " + rn);
		//result = result.pow(4096);
		result = BigInteger.TEN;
		result = result.multiply(BigInteger.valueOf(rn.nextLong()));
		System.out.println("result = " + result);
	}

}
