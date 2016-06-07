import java.util.Scanner;
import java.util.Random;
import java.math.BigInteger;

import java.text.SimpleDateFormat;
import java.sql.Date;

public class RSA_4096 {
	
	final static int bitlength = 2048;					// p, q�� bits ũ�⸦ �����ϴ� ���. n�� ũ�Ⱑ 4096�̹Ƿ� p, q ������ ���� 2048�� �ȴ�.

	public static void main(String[] args) {

		// ���� �ʱⰪ ����
		BigInteger M = new BigInteger("0");		// �޼��� ��, M < n �� ������ �����ؾ� ��. �Է��׽�Ʈ�� 2-1000 ���� ������ ��
		BigInteger C = new BigInteger("0");		// ��ȣ��
		BigInteger n = new BigInteger("0");			// 4096bits ũ�⸦ ���� ���� n
		BigInteger p = new BigInteger("0");		// 2048bits ũ�⸦ ���� ������ �Ҽ� ���� p
		BigInteger q = new BigInteger("0");		// 2048bits ũ�⸦ ���� ������ �Ҽ� ���� q
		BigInteger pi = new BigInteger("0");		// ���Ϸ� �����Լ� pi
		BigInteger e = new BigInteger("0");			// ����Ű
		BigInteger d = new BigInteger("0");		// ����Ű

		// ����ڷ� ���� �� ���� �Է��� �޴´�.
		Scanner scan = new Scanner(System.in); 

		while(true)
		{
			System.out.println("��ȣȭ�� ����(�޼�����)�� �Է��Ͻÿ�.  (�����Ϸ��� 0 �Է�)");

			M = BigInteger.valueOf(scan.nextInt());			// ���б�

			if (M.equals(BigInteger.valueOf(0)))						// ���α׷� ���� ����
			{
				System.out.println("���α׷��� �����մϴ�.");
				break;
			}
			
			if(M.compareTo(BigInteger.valueOf(2)) == -1 || M.compareTo(BigInteger.valueOf(1000)) == 1)		// �޼��� ���ǹ��� �˻�
			{
				System.out.println("�򹮸޼��� ũ�Ⱑ ������ ������ϴ�. �ٽ� �Է��� �ּ���.");
				continue;				
			}
	
			// ����ð��� �����ϱ� ���� �ý��� �ð��� ���
			print_systime("���۽ð�");

			while(true)
			{
				// ���� �ٸ� �Ҽ� p, q�� �����Ѵ�
				p = getRandomPrimeNum();
				q = getRandomPrimeNum();
				
				pi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));			// pi=(p-1)*(q-1)
				
				/*
				 * < �Ҽ� p, q ���� >
				 * 1. �� �Ҽ��� ���� ���� �ʾƾ� ��
				 * 2. �Էµ� �޼��� M�� �� �Ҽ��� �� n ���� �۾ƾ� ��
				 * 3. ���Ϸ� �����Լ� pi�� ����Ű�� ���� 1< e < pi �̹Ƿ�, pi�� 2 ���� Ŀ����
				 *     �ֳ��ϸ� ����e�� �ּҰ��� 2�̱� ������ pi �� 2���� Ŀ���Ѵ�.
				 *     (compareTo �Լ��� less than ���ǿ��� -1�� return)
				 */
				if(p.equals(q) || pi.compareTo(BigInteger.valueOf(2)) == -1)		// p, q�� ������ �������� ���ϴ� ���
					continue;
				else
					break;
			}
			
			n = p.multiply(q);				// p, q �� ���������� �������� n ���� ����Ѵ�.

			System.out.println("p = " + p);
			System.out.println("q = " + q);
			System.out.println("n = " + n);
			System.out.println("pi = " + pi);

			// p, q, n, pi �� ���� �ð� ���
			print_systime("�Ҽ��� ���� �ð�");

			// ����Ű �����Լ� ȣ��
			e = getPublicKey(pi);
			System.out.println("e = " + e);		

			// ����Ű�� ���� �ð� ���
			print_systime("����Ű ���ð�");

			// ����Ű �����Լ� ȣ��
			d = getPrivateKey(e, pi);
			System.out.println("d = " + d);

			// ����Ű�� ���� �ð� ���
			print_systime("����Ű ���ð�");

			// ��ȣȭ ����
			C = getCrypto(M, e, n);
			System.out.println("��ȣ�� C = " + C);

			// ��ȣȭ �Ϸ� �ð� ���
			print_systime("��ȣȭ �ð�");

			// ��ȣȭ ����
			M = getCrypto(C, d, n);
			System.out.println("��ȣ�� M = " + M);
			
			// ��ȣȭ �Ϸ� �ð� ���
			print_systime("��ȣȭ �ð�");
		}
		
		scan.close();					// �Է� ��ũ���� �ݴ´�.
	}
	

	/*
	 * < ������ �Ҽ��� ���ϴ� �Լ� >
	 * n�� ũ�Ⱑ 4096bit �̹Ƿ� p, q�� �ִ밪�� ���� 2048bits �̴�
	 * �������� 2���� �����ؼ� ������/2 ���� ���ʷ� 1�� ���ϸ鼭
	 * �������� �õ��Ͽ� �������� 0�� �Ǹ� �Ҽ��� �ƴϸ�,
	 * ���������� ������ �Ҽ��� �ȴ�.
	 * 
	 * ���α׷� �� �Ҽ� �Ǻ� ���μ����� �ۼ�������, �Ҽ��� Ŀ�� ���� ���ð��� ���� �ɷ���
	 * �ڹٿ��� �����ϴ� BigInteger �����Լ��� ����ߴ�.
	 * new BigInteger(int bitlength, int certainty, Random rnd) �� �̿�
	 * ���⿡�� bitlength�� BigInteger�� bits ũ��, certainty�� �Ҽ��� Ȯ���μ� ��ġ�� �������� �Ҽ��� Ȯ���� ������,
	 * ���ð��� ���� �ɸ��� ������ �ִ�.(1-(1/2)^certainty)
	 */
	public static BigInteger getRandomPrimeNum()
	{
		while(true)
		{
			// random �� ����
			BigInteger rndbig = new BigInteger(bitlength, 16, new Random());		// bitlength bits ũ�⸦ ���� ������ �Ҽ� ����
			return rndbig;

			/* �Ҽ� �Ǻ�
			BigInteger k = new BigInteger("2");
			BigInteger bigint0 = new BigInteger("0");
			BigInteger bigint1 = new BigInteger("1");
			BigInteger bigint2 = new BigInteger("2");

			// �Ҽ� �Ǻ��� ���� ������/2 ������ �����Ѵ�. rndbig ���� Ȧ���� ��� rndhalf+1���� ����
			BigInteger rndhalf = rndbig.divide(bigint2).add(bigint1);

			// �������� 0 or 1�� ���� �ٽ� while�� ��
			if( rndbig.equals(bigint0) || rndbig.equals(bigint1))
				continue;

			while(!rndhalf.equals(k))			// k�� ���� 2���� rndhalf ���� �ݺ�����
			{
				// rndhalf�� k�� �������� ���, �� �������� 0�� ���
				if( rndbig.remainder(k).equals(bigint0) )
					break;
				else		// ������ ���� �ʴ� ��� 1�� ���ؼ� �ٽ� �������� �Ѵ�.
					k = k.add(bigint1);
			}

			if(rndhalf.equals(k))			// �Ҽ��� ����. rndhalf �� k�� ���� ���
				return rndbig;
			else			
				continue;					// �Ҽ��� �ƴϸ� �ٽ� �������� ���� ���ؼ� ����Ѵ�.
			*/
		}
	}
	

	/*
	 * < ����Ű e �����Լ� >
	 * ����Ű e�� ������ 1 < e < pi �̸� pi �� ���μ��� ����
	 * ������ ���� ������ ���μ����� Ȯ���� ��
	 * ���μҰ� �ƴϸ� ���� random_e �� �ϳ��� �ٿ����� Ȯ��
	 */
	public static BigInteger getPublicKey(BigInteger pi)
	{
		// pi ���� �����鼭 pi�� ���� ���� ���� e ����
		while(true)
		{
			// ������ �� e �� pi ���� �۾ƾ��ϹǷ� pi ���� 10 bits ũ�Ⱑ ���� �������� ���Ѵ�.
			BigInteger temp_e = new BigInteger(pi.bitLength()-1, new Random());

			while(temp_e.compareTo(BigInteger.valueOf(2)) == 1)			// temp_e�� 2���� ū ���� ��쿡 �ݺ�
			{
				// temp_e�� pi �� ���� ��(�ִ������� 1)���� �Ǻ�
				if(getGCD(pi, temp_e).equals(BigInteger.valueOf(1)))
					return temp_e;
				else
					temp_e = temp_e.subtract(BigInteger.valueOf(1));		// temp_e�� ���� 1�� �ٿ����� ���� �� Ȯ�� 
			}
		}
	}


	/*
	 * ������ �� ���� �޾� �ִ������� ã�� �Լ�
	 * ��Ŭ���� �˰����� �̿��Ѵ�
	 * �� ��(a, b)�� ������ �ִ������� a
	 * GCD�� ã�� ������ ������Լ� ȣ��
	 * ū���� ���� ���� ������ �������� 0�̸� �� ���� �ִ������� �Ǹ�,
	 * �������� ������ �ݺ� �����Ѵ�.
	 * ��, a mod b ������ �ݺ��Ѵ�.
	 */
	public static BigInteger getGCD(BigInteger a, BigInteger b)
	{
		BigInteger temp = new BigInteger("0");

		if(a.equals(b))
			return a;
		else if(a.compareTo(b) == -1)				// �Էµ� ���� b > a �� ��� �����⸦ �ϱ� ���� �ڸ� �ٲ�
		{
			temp = a;
			a = b;
			b = temp;
		}
		
		if(b.equals(BigInteger.valueOf(0)))			// �������� 0�� ��� �׶��� a ���� �ִ�����
			return a;
		
		return getGCD(b, a.remainder(b));			// ����Լ�		
	}

	
	/*
	 * ����Ű �����Լ�
	 * e*d = 1 mod pi ���
	 * �־��� e �� pi �� Ȯ�� ��Ŭ���� �˰����� �����ؼ� linear combination ���� ���Ѵ�.
	 */
	public static BigInteger getPrivateKey(BigInteger e, BigInteger pi)
	{
		BigInteger maxint = pi;
		BigInteger minint = e;
		BigInteger mok = new BigInteger("0");					// ��
		BigInteger remainder = new BigInteger("0");			// ������
		BigInteger S0 = new BigInteger("1");					// linear combination ����� ���� ���� S0
		BigInteger S1 = new BigInteger("0");					// linear combination ����� ���� ���� S1
		BigInteger S2 = new BigInteger("0");					// linear combination ����� ���� ���� S2. S2 = S0 - S1*mok
		BigInteger T0 = new BigInteger("0");					// linear combination ����� ���� ���� T0
		BigInteger T1 = new BigInteger("1");					// linear combination ����� ���� ���� T1
		BigInteger T2 = new BigInteger("0");					// linear combination ����� ���� ���� T2. T2 = T0- T1*mok
		
		while(true)
		{
			mok = maxint.divide(minint);
			remainder = maxint.remainder(minint);

			if(remainder.equals(BigInteger.valueOf(0)))				// �������� 0�϶� T2�� return
			{
				if(T2.compareTo(BigInteger.valueOf(0)) == -1)	// T2�� ���� ������ �� mod pi ������ pi+T2�� �ȴ�. 
					T2 = pi.add(T2);

				return T2;
			}
			else			// S2, T2�� ����ϰ�, ���� ������ ���� �� �������� ġȯ�Ѵ�
			{
				S2 = S0.subtract(mok.multiply(S1));
				T2 = T0.subtract(mok.multiply(T1));
				maxint = minint;
				minint = remainder;
				S0=S1;
				S1=S2;
				T0=T1;
				T1=T2;
			}
		}

		/* �ڹ� modInverse �Լ��� ����� ��
		BigInteger temp_d = new BigInteger("2");
		temp_d = e.modInverse(pi);
		return temp_d;
		*/
	}
	
	
	/*
	 * ��ȣȭ(�������) �Լ�
	 * 
	 * ��ȣȭ�� ��ȣȭ�� ���� �������� ����-�� ����� �μ��� �ٲپ ���
	 * C=M^e mod n
	 * M=C^d mod n
	 * 
	 * result = (result*temp) % n �� ����� ������ modulo ���꿡��
	 * (a*b) mod n = (a mod n * b) mod n = (a * b mod n) mod n = (a mod n * b mod n) mod n ��� �����ϱ� ������
	 * 
	 */
	public static BigInteger getCrypto(BigInteger msg, BigInteger key, BigInteger n)
	{
		int [] bitarray = new int[key.bitLength()];			// �� ��Ʈ���� �ֱ� ���� �迭
		int k;														// �ݺ����� ���� ����
		BigInteger input_msg = msg;						// original input msg
		BigInteger return_msg = new BigInteger("1");	// i��° ���� ������ ���� mod ��. ���������� ��ȯ�� ��
		BigInteger i_mod = new BigInteger("1");			// i ��° mod ��갪
		BigInteger i_prev_mod = new BigInteger("1");	// i-1 ��° mod ��갪
		BigInteger mok = key;									// �־��� key ���� �������� ǥ���ϱ� ���� ���� �������� �����ϴ� ����
		i_mod = input_msg;									// i=0 �϶� �ʱⰪ ����
		i_prev_mod = input_msg;								// i=0 �϶� �ʱⰪ ����

		for(k=0; k<key.bitLength(); k++)
		{
			// �� ��Ʈ�� ���
			bitarray[k] = mok.remainder(BigInteger.valueOf(2)).intValue();
			mok = mok.divide(BigInteger.valueOf(2));

			// i��° mod ���� ��� ����� �ϰ�, i��° ��Ʈ���� 1�̶�� ������ mod ���� ����Ѵ�.			
			if(k>0)												// bitarray[1] ���ʹ� bitarray[0](mod n)^2 (mod n)
			{
				// i��° mod �� ��� 
				i_mod = i_prev_mod.pow(2).remainder(n);
				i_prev_mod = i_mod;
			}
			
			if(bitarray[k] == 1)											// ��Ʈ���� 1�� ��� �������� mod ��� 
				return_msg= return_msg.multiply(i_mod).remainder(n);
		}
		
		/* java �Լ� modPow�� ����ϴ� ��� 
		BigInteger result_msg = new BigInteger("0");
		result_msg = Text.modPow(key, n);
		*/
		
		return return_msg;
	}
	
	
	// �ӵ� ������ ���� �ý��� �ð��� ����ϴ� �Լ�
	public static void print_systime(String msg)
	{
		long time = System.currentTimeMillis();
		SimpleDateFormat ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String CurrentTime = ctime.format(new Date(time));
		System.out.println(msg + " : " + CurrentTime);
	}
}