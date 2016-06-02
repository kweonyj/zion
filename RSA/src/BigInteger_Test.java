import java.math.BigDecimal;
import java.math.BigInteger;

public class BigInteger_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger result = new BigInteger("2");
		System.out.println("result = " + result);
		result = result.pow(4096);
		System.out.println("result = " + result.bitLength());
	}

}
