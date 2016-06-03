import java.util.Scanner;
import java.util.Random;
import java.math.BigInteger;

public class RSA_4096 {

	public static void main(String[] args) {

		// ���� �ʱⰪ ����
		int M = 0;									// �޼��� ��, M < n �� ������ �����ؾ� ��. �׽�Ʈ�� 2-100 ���� ������ ��
		int C = 0;									// ��ȣ��
		BigInteger n = new BigInteger("0");		// 4096bits ũ�⸦ ���� ���� n
		BigInteger p = new BigInteger("0");	// ������ �Ҽ� ���� p
		BigInteger q = new BigInteger("0");	// ������ �Ҽ� ���� q
		BigInteger pi = new BigInteger("0");	// ���Ϸ� �����Լ� pi
		BigInteger e = new BigInteger("0");		// ����Ű
		int d = 0;									// ����Ű
		

		Scanner scan = new Scanner(System.in); 

		// ����ڷ� ���� �� ���� �Է��� �޴´�.
		while(true)
		{
			System.out.println("��ȣȭ�� ����(�޼�����)�� �Է��Ͻÿ�.  (�����Ϸ��� 0 �Է�)");

			M = scan.nextInt();		// ���б�

			if (M == 0)						// ���α׷� ���� ����
			{
				System.out.println("���α׷��� �����մϴ�.");
				break;
			}
			
			if(M < 2 || M > 100)		// �޼��� ���ǹ��� �˻�
			{
				System.out.println("�򹮸޼��� ũ�Ⱑ ������ ������ϴ�. �ٽ� �Է��� �ּ���.");
				continue;				
			}
	
			while(true)
			{
				// ���� �ٸ� �Ҽ� p, q�� �����Ѵ�
				p = getRandomPrimeNum();
				q = getRandomPrimeNum();
				
				n = p.multiply(q);
				pi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
				
				System.out.println("p = " + p);
				System.out.println("q = " + q);
				System.out.println("n = " + n);
				System.out.println("pi = " + pi);

				/*
				 * < �Ҽ� p, q ���� >
				 * 1. �� �Ҽ��� ���� ���� �ʾƾ� ��
				 * 2. �Էµ� �޼��� M�� �� �Ҽ��� �� n ���� �۾ƾ� ��
				 * 3. ���Ϸ� �����Լ� pi�� ����Ű�� ���� 1< e < pi �̹Ƿ�, pi�� 2 ���� Ŀ����
				 *     �ֳ��ϸ� ����e�� �ּҰ��� 2�̱� ������ pi �� 2���� Ŀ���Ѵ�
				 *     (compareTo �Լ��� less than ���ǿ��� -1�� return)
				 */
				if(p.equals(q) || pi.compareTo(BigInteger.valueOf(2)) == -1)		// p, q�� �������� ���
					continue;
				else
					break;
			}
			

			
			// 2. ����Ű �����Լ� ȣ��
			e = getPublicKey(pi);
			System.out.println("e = " + e);		
/*
			// 3. ����Ű �����Լ� ȣ��
			d = getPrivateKey(e, pi);
			System.out.println("d = " + d);
		
			// 4. ��ȣȭ ����
			C = getCrypto(M, e, n);
			System.out.println("��ȣ�� C = " + C);
			
			// 5. ��ȣȭ ����
			M = getCrypto(C, d, n);
			System.out.println("��ȣ�� M = " + M);
			*/
		}
		
		scan.close();
	}
	
	/*
	 * < ������ �Ҽ��� ���ϴ� �Լ� >
	 * n�� ũ�Ⱑ 4096bit �̹Ƿ� p, q�� �ִ밪�� ���� 2048bits �̴�
	 * �������� 2���� �����ؼ� ���������� ���ʷ� 1�� ���ϸ鼭
	 * �������� �õ��Ͽ� �������� 0�� �Ǹ� �Ҽ��� �ƴϸ�,
	 * 0�� �ƴϸ鼭 ������k�� �������� ������ �Ҽ��� �ȴ�.
	 */
	public static BigInteger getRandomPrimeNum()
	{
		while(true)
		{
			// random �� ����
			BigInteger randombigint = new BigInteger(7, new Random());		// �׽�Ʈ�� ���� 2048bits ��� 16bits ���
			BigInteger k = new BigInteger("2");
			BigInteger bigint0 = new BigInteger("0");
			BigInteger bigint1 = new BigInteger("1");

			// �������� 0, 1�� ���� �ٽ� while�� ��
			if( randombigint.equals(bigint0) || randombigint.equals(bigint1))
				continue;

			System.out.println("������ = " + randombigint);
				
			// �Ҽ��Ǻ�
			while(true)
			{
				// ������ ���� k�� �������� ���, �� �������� 0�� ���
				if( randombigint.remainder(k).equals(bigint0) )
					break;
				else		// ������ ���� �ʴ� ��� 1�� ���ؼ� �ٽ� �������� �Ѵ�.
					k=k.add(bigint1);
			}
			
			if(randombigint.equals(k))			// �Ҽ��� ����. �������� k�� ���� ���
				return randombigint;
			else
				continue;
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
			BigInteger temp_e = new BigInteger(pi.bitLength()-1, new Random());			// pi ���� ���� ������ ���� ����

			while(true)
			{
				// temp_e�� pi �� ���ҷ����� �Ǻ�
				if(getGCD(pi, temp_e).equals(BigInteger.valueOf(1)))
					break;
				else
					temp_e = temp_e.subtract(BigInteger.valueOf(1));
			}
			if(temp_e.compareTo(BigInteger.valueOf(2)) == 1)
			{
				System.out.println("temp_e = " + temp_e);
				return temp_e;
			}
			else
				continue;
		}
	}

	/*
	 * < ������ �ڿ��� �����Լ� >
	 * pi �� �Է¹޾� �׺��� ���� ������ ������ return
	 * ���߿� �����ص� �ɰ� ����
	 */
	public static BigInteger getRandomNum(BigInteger pi)
	{
		int bitlength = pi.bitLength();
		
		System.out.println("bitlength= " + bitlength);
		return BigInteger.valueOf(bitlength);
	}	

	/*
	 * ������ �� ���� �޾� �ִ������� ã�� �Լ�
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

	
	
	
	
	
}
