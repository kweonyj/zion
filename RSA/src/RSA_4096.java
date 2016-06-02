import java.util.Scanner;
import java.util.Random;

public class RSA_4096 {

	public static void main(String[] args) {

		// ���� �ʱⰪ ����
		int M = 0;			// �޼��� ��, M < n �� ������ �����ؾ� ��.
		int C = 0;			// ��ȣ��
		int n = 0;			// * 4096bits ũ�⸦ ���� n, n=p*q. biginteger�� ��ȯ�ؾ� �� *
		int p = 0;			// ������ �Ҽ�
		int q = 0;			// ������ �Ҽ�
		int pi = 0;			// ���Ϸ� �����Լ� pi = (p-1)*(q-1)
		int e = 0;			// ����Ű
		int d = 0;			// ����Ű
		

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
	
			// 1. ���� �ٸ� �Ҽ� p, q�� �����Ѵ�
			while(true)
			{
				p = getRandomPrimeNum(2, 100);
				q = getRandomPrimeNum(2, 100);
				
				n = p * q;
				pi = (p-1)*(q-1);
				
				/*
				 * �Ҽ� �˻� ����
				 * 1. �� �Ҽ��� ���� ���� �ʾƾ� ��
				 * 2. �Էµ� �޼��� M�� �� �Ҽ��� �� n ���� �۾ƾ� ��
				 * 3. ���Ϸ� �����Լ� pi�� ����Ű�� ���� 1< e < pi �̹Ƿ�, pi�� 2 ���� Ŀ����
				 *     �ֳ��ϸ� ����e�� �ּҰ��� 2�̱� ������ pi �� 2���� Ŀ���Ѵ�
				 */
				if(p != q && M < n && pi > 2)
				{
					System.out.println("p=" + p + ", q=" + q);
					break;
				}
			}
			
			System.out.println("n = " + n);
			System.out.println("pi = " + pi);
	
			// 2. ����Ű �����Լ� ȣ��
			e = getPublicKey(pi);
			System.out.println("e = " + e);		

			// 3. ����Ű �����Լ� ȣ��
			d = getPrivateKey(e, pi);
			System.out.println("d = " + d);
		
			// 4. ��ȣȭ ����
			C = getCrypto(M, e, n);
			System.out.println("��ȣ�� C = " + C);
			
			// 5. ��ȣȭ ����
			M = getCrypto(C, d, n);
			System.out.println("��ȣ�� M = " + M);
		}
		
		scan.close();
	}
	
	/*
	 * �� ���� �Է¹޾� �� ���̿� �ִ� random ���� return �ϴ� �Լ�
	 * ū ���� random ���� nextInt()�� �ذ� �� �� ���� ������ nextDouble()�� �̿��Ѵ�.
	 * ���� ���, random ���� �˻������� 2 ~ 100 �϶�
	 * random �� result = 0.2*(100-2) + 2 �� ���·� ���ȴ�.
	 */
	public static int getRandomPrimeNum(int startNum, int endNum)
	{
		Random rn = new Random();
		int intRange = endNum - startNum;
		int k;

		// �Ҽ��� ã�� ������ �ݺ�
		while(true)
		{
			// random �� ����
			int result = (int)(rn.nextDouble() * intRange + startNum);

			// 2���� ���ʷ� ++ �Ͽ� ������ �õ�, �������� 0�� ���� �Ҽ��� �ƴ�
			for(k=2; k <= result-1; k++)
			{
				if(result%k == 0)		//�Ҽ��� �ƴ� ��� for �ݺ��� ��ġ��, while�� ��
					break;
			}
			
			if(k == result)					// �Ҽ��� ��� return
				return result;
		}
	}

	/*
	 * ����Ű e �����Լ�
	 * ����Ű e�� ������ 1 < e < pi �̸� pi �� ���μ��� ����
	 * ������ ���� ������ ���μ����� Ȯ���� ��
	 * ���μҰ� �ƴϸ� ���� random_e �� �ϳ��� �ٿ����� Ȯ��
	 */
	public static int getPublicKey(int pi)
	{
		// pi ���� �����鼭 pi�� ���� ���� ���� e ����
		while(true)
		{
			int temp_e = getRandomNum(2, pi);			// ������ ���� ����
			while(true)
			{
				// temp_e�� pi �� ���ҷ����� �Ǻ�
				if(getGCD(pi, temp_e) == 1)
					break;
				else
					temp_e--;
			}
			if(temp_e > 2)
				return temp_e;
			else
				continue;
		}
	}

	/*
	 * ������ �ڿ��� �����Լ�
	 * �� ���� �Է¹޾� �� ���̿� �ִ� ������ ������ ���ϴ� �Լ�
	 * getRandomPrimeNum �Լ��� �����ϸ�, �Ҽ� Ȯ�� ���Ǹ� ����
	 */
	public static int getRandomNum(int startNum, int endNum)
	{
		Random rn = new Random();
		int intRange = endNum - startNum;
		
		int result = (int)(rn.nextDouble() * intRange + startNum);					// �����ϰ� �ſ� ū���� ������ ���� ���ϱ�
		return result;
	}	

	/*
	 * ������ �� ���� �޾� �ִ������� ã�� �Լ�
	 * �� ��(a, b)�� ������ �ִ������� a
	 * GCD�� ã�� ������ ������Լ� ȣ��
	 * ū���� ���� ���� ������ �������� 0�̸� �� ���� �ִ������� �Ǹ�,
	 * �������� ������ �ݺ� �����Ѵ�.
	 * ��, a mod b ������ �ݺ��Ѵ�.
	 */
	public static int getGCD(int a, int b)
	{
		int temp;
		if(a==b)
			return a;
		else if(a<b)		// b �� ū ��� �����⸦ �ϱ� ���� �ڸ� �ٲ�
		{
			temp = a;
			a = b;
			b = temp;
		}
		
		if(b == 0)
			return a;
		return getGCD(b, a%b);		
	}

	
	/*
	 * ����Ű �����Լ�
	 * e*d=1 mod n ���
	 * �־��� e �� ���ؼ� ���� 2���� ���ʷ� �÷����� mod n �� ����Ͽ� 1�� �Ǵ� ��� return
	 */
	public static int getPrivateKey(int e, int pi)
	{
		int temp_d;
		
		for(temp_d=2; temp_d<pi; temp_d++)
		{
			if((temp_d*e)%pi == 1)
				break;
			else
				continue;
		}
		return temp_d;
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
	public static int getCrypto(int Text, int key, int n)
	{
		int result = Text;
		int temp = Text;
		int i;
		
		for(i=1; i< key; i++)
			result = (result*temp) %n;
		
		return result;
	}
}
