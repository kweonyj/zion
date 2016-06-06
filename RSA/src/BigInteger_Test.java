import java.util.Random;
import java.math.BigInteger;

public class BigInteger_Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigInteger maxint = new BigInteger("37");
		
		BigInteger moduloint = maxint.remainder(BigInteger.valueOf(10));

		System.out.println("maxint=" + maxint);
		System.out.println("moduloint=" + moduloint);
		System.out.println("bitlength=" + maxint.bitLength());
		
		
		int [] bitarray = new int[2048];
		int k =0;
		int testint = maxint.intValue(); 
		for(k=0; k<5; k++)
		{
			bitarray[k] = testint%2;
			testint = testint/2;
			//System.out.println(bitarray[k]);
		}
		
		
	}
}
