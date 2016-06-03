import java.util.Scanner;
import java.util.Random;
import java.math.BigInteger;

public class RSA_4096 {

	public static void main(String[] args) {

		// 변수 초기값 설정
		int M = 0;									// 메세지 평문, M < n 의 조건을 만족해야 함. 테스트로 2-100 사이 정수로 함
		int C = 0;									// 암호문
		BigInteger n = new BigInteger("0");		// 4096bits 크기를 갖는 변수 n
		BigInteger p = new BigInteger("0");	// 임의의 소수 변수 p
		BigInteger q = new BigInteger("0");	// 임의의 소수 변수 q
		BigInteger pi = new BigInteger("0");	// 오일러 파이함수 pi
		BigInteger e = new BigInteger("0");		// 공개키
		int d = 0;									// 개인키
		

		Scanner scan = new Scanner(System.in); 

		// 사용자로 부터 평문 숫자 입력을 받는다.
		while(true)
		{
			System.out.println("암호화할 숫자(메세지평문)을 입력하시오.  (종료하려면 0 입력)");

			M = scan.nextInt();		// 평문읽기

			if (M == 0)						// 프로그램 종료 조건
			{
				System.out.println("프로그램을 종료합니다.");
				break;
			}
			
			if(M < 2 || M > 100)		// 메세지 조건범위 검사
			{
				System.out.println("평문메세지 크기가 범위를 벗어났습니다. 다시 입력해 주세요.");
				continue;				
			}
	
			while(true)
			{
				// 서로 다른 소수 p, q를 생성한다
				p = getRandomPrimeNum();
				q = getRandomPrimeNum();
				
				n = p.multiply(q);
				pi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
				
				System.out.println("p = " + p);
				System.out.println("q = " + q);
				System.out.println("n = " + n);
				System.out.println("pi = " + pi);

				/*
				 * < 소수 p, q 조건 >
				 * 1. 두 소수는 서로 같지 않아야 함
				 * 2. 입력된 메세지 M은 두 소수의 곱 n 보다 작아야 함
				 * 3. 오일러 파이함수 pi는 개인키의 조건 1< e < pi 이므로, pi는 2 보다 커야함
				 *     왜냐하면 정수e의 최소값은 2이기 때문에 pi 는 2보다 커야한다
				 *     (compareTo 함수는 less than 조건에서 -1를 return)
				 */
				if(p.equals(q) || pi.compareTo(BigInteger.valueOf(2)) == -1)		// p, q가 비정상인 경우
					continue;
				else
					break;
			}
			

			
			// 2. 공개키 생성함수 호출
			e = getPublicKey(pi);
			System.out.println("e = " + e);		
/*
			// 3. 개인키 생성함수 호출
			d = getPrivateKey(e, pi);
			System.out.println("d = " + d);
		
			// 4. 암호화 과정
			C = getCrypto(M, e, n);
			System.out.println("암호문 C = " + C);
			
			// 5. 복호화 과정
			M = getCrypto(C, d, n);
			System.out.println("복호문 M = " + M);
			*/
		}
		
		scan.close();
	}
	
	/*
	 * < 임의의 소수를 구하는 함수 >
	 * n의 크기가 4096bit 이므로 p, q의 최대값은 각각 2048bits 이다
	 * 랜덤값을 2부터 시작해서 랜덤값까지 차례로 1씩 더하면서
	 * 나눗셈을 시도하여 나머지가 0이 되면 소수가 아니며,
	 * 0이 아니면서 최종값k와 랜덤값이 같으면 소수가 된다.
	 */
	public static BigInteger getRandomPrimeNum()
	{
		while(true)
		{
			// random 값 선택
			BigInteger randombigint = new BigInteger(7, new Random());		// 테스트를 위해 2048bits 대신 16bits 사용
			BigInteger k = new BigInteger("2");
			BigInteger bigint0 = new BigInteger("0");
			BigInteger bigint1 = new BigInteger("1");

			// 랜덤값이 0, 1인 경우는 다시 while로 감
			if( randombigint.equals(bigint0) || randombigint.equals(bigint1))
				continue;

			System.out.println("랜덤값 = " + randombigint);
				
			// 소수판별
			while(true)
			{
				// 임의의 수가 k로 나누어진 경우, 즉 나머지가 0인 경우
				if( randombigint.remainder(k).equals(bigint0) )
					break;
				else		// 나누어 지지 않는 경우 1을 더해서 다시 나눗셈을 한다.
					k=k.add(bigint1);
			}
			
			if(randombigint.equals(k))			// 소수일 조건. 랜덤값이 k와 같은 경우
				return randombigint;
			else
				continue;
		}
	}
	
	/*
	 * < 공개키 e 생성함수 >
	 * 공개키 e의 조건은 1 < e < pi 이며 pi 와 서로소인 정수
	 * 임의의 값을 가지고 서로소인지 확인한 후
	 * 서로소가 아니면 정수 random_e 를 하나씩 줄여가면 확인
	 */
	public static BigInteger getPublicKey(BigInteger pi)
	{
		// pi 보다 작으면서 pi와 서로 소인 정수 e 선택
		while(true)
		{
			BigInteger temp_e = new BigInteger(pi.bitLength()-1, new Random());			// pi 보다 작은 임의의 정수 선택

			while(true)
			{
				// temp_e와 pi 가 서소로인지 판별
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
	 * < 임의의 자연수 생성함수 >
	 * pi 를 입력받아 그보다 작은 임의의 정수를 return
	 * 나중에 삭제해도 될것 같음
	 */
	public static BigInteger getRandomNum(BigInteger pi)
	{
		int bitlength = pi.bitLength();
		
		System.out.println("bitlength= " + bitlength);
		return BigInteger.valueOf(bitlength);
	}	

	/*
	 * 임의의 두 수를 받아 최대공약수를 찾는 함수
	 * 두 수(a, b)가 같으면 최대공약수는 a
	 * GCD를 찾는 구조는 재귀적함수 호출
	 * 큰수를 작은 수로 나눌때 나머지가 0이면 그 수가 최대공약수가 되며,
	 * 나머지를 가지고 반복 실행한다.
	 * 즉, a mod b 연산을 반복한다.
	 */
	public static BigInteger getGCD(BigInteger a, BigInteger b)
	{
		BigInteger temp = new BigInteger("0");
		if(a.equals(b))
			return a;
		else if(a.compareTo(b) == -1)		// b 가 큰 경우 나누기를 하기 위해 자리 바꿈
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
