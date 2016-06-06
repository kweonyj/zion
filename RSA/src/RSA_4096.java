import java.util.Scanner;
import java.util.Random;
import java.math.BigInteger;

import java.text.SimpleDateFormat;
import java.sql.Date;

public class RSA_4096 {
	
	final static int bitlength = 2048;					// p, q�� bit ũ�⸦ �����ϴ� ���. **�׽�Ʈ�� 32�� �ϰ� ���߿� 2048�� ����

	public static void main(String[] args) {

		// ���� �ʱⰪ ����
		BigInteger M = new BigInteger("0");		// �޼��� ��, M < n �� ������ �����ؾ� ��. �׽�Ʈ�� 2-100 ���� ������ ��
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
			
			if(M.compareTo(BigInteger.valueOf(2)) == -1 || M.compareTo(BigInteger.valueOf(100)) == 1)		// �޼��� ���ǹ��� �˻�
			{
				System.out.println("�򹮸޼��� ũ�Ⱑ ������ ������ϴ�. �ٽ� �Է��� �ּ���.");
				continue;				
			}
	
			// ����ð��� �����ϱ� ���� �ý��� �ð��� ���
			printsystime("���۽ð�");

			while(true)
			{
				// ���� �ٸ� �Ҽ� p, q�� �����Ѵ�
				p = getRandomPrimeNum();
				q = getRandomPrimeNum();
				
				n = p.multiply(q);
				pi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));			// pi=(p-1)*(q-1)
				
				/*
				 * < �Ҽ� p, q ���� >
				 * 1. �� �Ҽ��� ���� ���� �ʾƾ� ��
				 * 2. �Էµ� �޼��� M�� �� �Ҽ��� �� n ���� �۾ƾ� ��
				 * 3. ���Ϸ� �����Լ� pi�� ����Ű�� ���� 1< e < pi �̹Ƿ�, pi�� 2 ���� Ŀ����
				 *     �ֳ��ϸ� ����e�� �ּҰ��� 2�̱� ������ pi �� 2���� Ŀ���Ѵ�
				 *     (compareTo �Լ��� less than ���ǿ��� -1�� return)
				 */
				if(p.equals(q) || pi.compareTo(BigInteger.valueOf(2)) == -1)		// p, q�� ������ �������� ���ϴ� ���
					continue;
				else
					break;
			}
			
			System.out.println("p = " + p);
			System.out.println("q = " + q);
			System.out.println("n = " + n);
			System.out.println("pi = " + pi);

			// p, q, n, pi �� ���� �ð� ���
			printsystime("�Ҽ��� ���� �ð�");

			// ����Ű �����Լ� ȣ��
			e = getPublicKey(pi);
			System.out.println("e = " + e);		

			// ����Ű�� ���� �ð� ���
			printsystime("����Ű ���ð�");

			// ����Ű �����Լ� ȣ��
			d = getPrivateKey(e, pi);
			//d = Extended_Euclidean(e, pi);
			System.out.println("d = " + d);

			// ����Ű�� ���� �ð� ���
			printsystime("����Ű ���ð�");

			// ��ȣȭ ����
			C = getCrypto(M, e, n);
			System.out.println("�޼��� M = " + M);
			System.out.println("��ȣ�� C = " + C);

			// ��ȣȭ �Ϸ� �ð� ���
			printsystime("��ȣȭ �ð�");

			// ��ȣȭ ����
			M = getCrypto(C, d, n);
			System.out.println("��ȣ�� M = " + M);
			
			// ��ȣȭ �Ϸ� �ð� ���
			printsystime("��ȣȭ �ð�");
		}
		
		scan.close();
	}
	

	/*
	 * < ������ �Ҽ��� ���ϴ� �Լ� >
	 * n�� ũ�Ⱑ 4096bit �̹Ƿ� p, q�� �ִ밪�� ���� 2048bits �̴�
	 * �������� 2���� �����ؼ� ������/2 ���� ���ʷ� 1�� ���ϸ鼭
	 * �������� �õ��Ͽ� �������� 0�� �Ǹ� �Ҽ��� �ƴϸ�,
	 * ���������� ������ �Ҽ��� �ȴ�.
	 */
	public static BigInteger getRandomPrimeNum()
	{
		while(true)
		{
			// random �� ����
			BigInteger rndbig = new BigInteger(bitlength, 16, new Random());		// bitlength bits ũ�⸦ ���� ������ �Ҽ� ����
			BigInteger k = new BigInteger("2");
			BigInteger bigint0 = new BigInteger("0");
			BigInteger bigint1 = new BigInteger("1");
			BigInteger bigint2 = new BigInteger("2");
System.out.println("rndbig = " + rndbig);
			// �Ҽ� �Ǻ��� ���� ������/2 ������ �����Ѵ�. Ȧ���� ��� ��+1���� ����
			BigInteger rndhalf = rndbig.divide(bigint2).add(bigint1);
System.out.println("rndhalf = " + rndhalf);
			// �������� 0 or 1�� ���� �ٽ� while�� ��
			if( rndbig.equals(bigint0) || rndbig.equals(bigint1))
				continue;
			// for 4096
			else
				return rndbig;


			/* �Ҽ� �Ǻ� - for 32bits
			while(!rndhalf.equals(k))
			{
				// rndhalf�� k�� �������� ���, �� �������� 0�� ���
				if( rndbig.remainder(k).equals(bigint0) )
					break;
				else		// ������ ���� �ʴ� ��� 1�� ���ؼ� �ٽ� �������� �Ѵ�.
					k = k.add(bigint1);
			}
System.out.println("k = " + k);
System.out.println("-------------------------------------------------");
			if(rndhalf.equals(k))			// �Ҽ��� ����. �������� k�� ���� ���
				return rndbig;
			else			
				continue;
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
			BigInteger temp_e = new BigInteger(pi.bitLength()-1, new Random());			// pi ���� ���� ������ ���� ����. �Ѻ�Ʈ ���� ���� �Ѵ�

			while(temp_e.compareTo(BigInteger.valueOf(2)) == 1)								// e �� 2���� ū ���̾�� ��
			{
				// temp_e�� pi �� ���ҷ����� �Ǻ�
				if(getGCD(pi, temp_e).equals(BigInteger.valueOf(1)))
					break;
				else
					temp_e = temp_e.subtract(BigInteger.valueOf(1));
			}
			if(temp_e.compareTo(BigInteger.valueOf(2)) == 1)
				return temp_e;
			else
				continue;
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
		else if(a.compareTo(b) == -1)		// b �� ū ��� �����⸦ �ϱ� ���� �ڸ� �ٲ�
		{
			temp = a;
			a = b;
			b = temp;
		}
		
		if(b.equals(BigInteger.valueOf(0)))
			return a;
		return getGCD(b, a.remainder(b));		
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
		BigInteger mok = new BigInteger("0");
		BigInteger remainder = new BigInteger("0");
		BigInteger S0 = new BigInteger("1");
		BigInteger S1 = new BigInteger("0");
		BigInteger S2 = new BigInteger("0");
		BigInteger T0 = new BigInteger("0");
		BigInteger T1 = new BigInteger("1");
		BigInteger T2 = new BigInteger("0");
		
		while(true)
		{
			mok = maxint.divide(minint);
			remainder = maxint.remainder(minint);

			if(remainder.equals(BigInteger.valueOf(0)))
			{
				if(T2.compareTo(BigInteger.valueOf(0)) == -1)
					T2 = pi.add(T2);

				return T2;
			}
			else
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
		
		/* �ڹ� �Լ� ���
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
	 */
	public static BigInteger getCrypto(BigInteger Text, BigInteger key, BigInteger n)
	{
		/* for 32bits
		BigInteger result = Text;
		BigInteger temp = Text;
		BigInteger i = new BigInteger("1");
				
		while(i.compareTo(key) == -1)
		{
			result = result.multiply(temp).remainder(n);
			i = i.add(BigInteger.valueOf(1));
		}
		*/
		
		// for 4096bits
		BigInteger result = new BigInteger("0");
		result = Text.modPow(key, n);
		
		return result;
	}
	
	
	// �ӵ� ������ ���� �ý��� �ð��� ����ϴ� �Լ�
	public static void printsystime(String msg)
	{
		long time = System.currentTimeMillis();
		SimpleDateFormat ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String CurrentTime = ctime.format(new Date(time));
		System.out.println(msg + " : " + CurrentTime);
	}
	
}