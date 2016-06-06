import java.util.Scanner;
import java.util.Random;
import java.math.BigInteger;

import java.text.SimpleDateFormat;
import java.sql.Date;

public class RSA_4096 {
	
	final static int bitlength = 2048;					// p, q의 bit 크기를 결정하는 상수. **테스트는 32로 하고 나중에 2048로 변경

	public static void main(String[] args) {

		// 변수 초기값 설정
		BigInteger M = new BigInteger("0");		// 메세지 평문, M < n 의 조건을 만족해야 함. 테스트로 2-100 사이 정수로 함
		BigInteger C = new BigInteger("0");		// 암호문
		BigInteger n = new BigInteger("0");			// 4096bits 크기를 갖는 변수 n
		BigInteger p = new BigInteger("0");		// 2048bits 크기를 갖는 임의의 소수 변수 p
		BigInteger q = new BigInteger("0");		// 2048bits 크기를 갖는 임의의 소수 변수 q
		BigInteger pi = new BigInteger("0");		// 오일러 파이함수 pi
		BigInteger e = new BigInteger("0");			// 공개키
		BigInteger d = new BigInteger("0");		// 개인키

		// 사용자로 부터 평문 숫자 입력을 받는다.
		Scanner scan = new Scanner(System.in); 

		while(true)
		{
			System.out.println("암호화할 숫자(메세지평문)을 입력하시오.  (종료하려면 0 입력)");

			M = BigInteger.valueOf(scan.nextInt());			// 평문읽기

			if (M.equals(BigInteger.valueOf(0)))						// 프로그램 종료 조건
			{
				System.out.println("프로그램을 종료합니다.");
				break;
			}
			
			if(M.compareTo(BigInteger.valueOf(2)) == -1 || M.compareTo(BigInteger.valueOf(100)) == 1)		// 메세지 조건범위 검사
			{
				System.out.println("평문메세지 크기가 범위를 벗어났습니다. 다시 입력해 주세요.");
				continue;				
			}
	
			// 수행시간을 측정하기 위해 시스템 시간을 출력
			printsystime("시작시간");

			while(true)
			{
				// 서로 다른 소수 p, q를 생성한다
				p = getRandomPrimeNum();
				q = getRandomPrimeNum();
				
				n = p.multiply(q);
				pi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));			// pi=(p-1)*(q-1)
				
				/*
				 * < 소수 p, q 조건 >
				 * 1. 두 소수는 서로 같지 않아야 함
				 * 2. 입력된 메세지 M은 두 소수의 곱 n 보다 작아야 함
				 * 3. 오일러 파이함수 pi는 개인키의 조건 1< e < pi 이므로, pi는 2 보다 커야함
				 *     왜냐하면 정수e의 최소값은 2이기 때문에 pi 는 2보다 커야한다
				 *     (compareTo 함수는 less than 조건에서 -1를 return)
				 */
				if(p.equals(q) || pi.compareTo(BigInteger.valueOf(2)) == -1)		// p, q가 조건을 만족하지 못하는 경우
					continue;
				else
					break;
			}
			
			System.out.println("p = " + p);
			System.out.println("q = " + q);
			System.out.println("n = " + n);
			System.out.println("pi = " + pi);

			// p, q, n, pi 를 구한 시간 출력
			printsystime("소수를 구한 시간");

			// 공개키 생성함수 호출
			e = getPublicKey(pi);
			System.out.println("e = " + e);		

			// 공개키를 구한 시간 출력
			printsystime("공개키 계산시간");

			// 개인키 생성함수 호출
			d = getPrivateKey(e, pi);
			//d = Extended_Euclidean(e, pi);
			System.out.println("d = " + d);

			// 개인키를 구한 시간 출력
			printsystime("개인키 계산시간");

			// 암호화 과정
			C = getCrypto(M, e, n);
			System.out.println("메세지 M = " + M);
			System.out.println("암호문 C = " + C);

			// 암호화 완료 시간 출력
			printsystime("암호화 시간");

			// 복호화 과정
			M = getCrypto(C, d, n);
			System.out.println("복호문 M = " + M);
			
			// 복호화 완료 시간 출력
			printsystime("복호화 시간");
		}
		
		scan.close();
	}
	

	/*
	 * < 임의의 소수를 구하는 함수 >
	 * n의 크기가 4096bit 이므로 p, q의 최대값은 각각 2048bits 이다
	 * 랜덤값을 2부터 시작해서 랜덤값/2 까지 차례로 1씩 더하면서
	 * 나눗셈을 시도하여 나머지가 0이 되면 소수가 아니며,
	 * 나누어지지 않으면 소수가 된다.
	 */
	public static BigInteger getRandomPrimeNum()
	{
		while(true)
		{
			// random 값 선택
			BigInteger rndbig = new BigInteger(bitlength, 16, new Random());		// bitlength bits 크기를 갖는 임의의 소수 생성
			BigInteger k = new BigInteger("2");
			BigInteger bigint0 = new BigInteger("0");
			BigInteger bigint1 = new BigInteger("1");
			BigInteger bigint2 = new BigInteger("2");
System.out.println("rndbig = " + rndbig);
			// 소수 판별을 위해 랜덤값/2 까지만 조사한다. 홀수인 경우 몫+1까지 조사
			BigInteger rndhalf = rndbig.divide(bigint2).add(bigint1);
System.out.println("rndhalf = " + rndhalf);
			// 랜덤값이 0 or 1인 경우는 다시 while로 감
			if( rndbig.equals(bigint0) || rndbig.equals(bigint1))
				continue;
			// for 4096
			else
				return rndbig;


			/* 소수 판별 - for 32bits
			while(!rndhalf.equals(k))
			{
				// rndhalf가 k로 나누어진 경우, 즉 나머지가 0인 경우
				if( rndbig.remainder(k).equals(bigint0) )
					break;
				else		// 나누어 지지 않는 경우 1을 더해서 다시 나눗셈을 한다.
					k = k.add(bigint1);
			}
System.out.println("k = " + k);
System.out.println("-------------------------------------------------");
			if(rndhalf.equals(k))			// 소수일 조건. 랜덤값이 k와 같은 경우
				return rndbig;
			else			
				continue;
			*/
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
			BigInteger temp_e = new BigInteger(pi.bitLength()-1, new Random());			// pi 보다 작은 임의의 정수 선택. 한비트 적은 수로 한다

			while(temp_e.compareTo(BigInteger.valueOf(2)) == 1)								// e 는 2보다 큰 수이어야 함
			{
				// temp_e와 pi 가 서소로인지 판별
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
	 * 임의의 두 수를 받아 최대공약수를 찾는 함수
	 * 유클리드 알고리즘을 이용한다
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

	
	/*
	 * 개인키 생성함수
	 * e*d = 1 mod pi 계산
	 * 주어진 e 와 pi 를 확장 유클리드 알고리즘을 적용해서 linear combination 값을 구한다.
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
		
		/* 자바 함수 사용
		BigInteger temp_d = new BigInteger("2");
		temp_d = e.modInverse(pi);
		return temp_d;
		*/
	}
	
	
	/*
	 * 암호화(지수계산) 함수
	 * 
	 * 암호화와 복호화가 같은 과정으로 지수-법 계산을 인수만 바꾸어서 사용
	 * C=M^e mod n
	 * M=C^d mod n
	 * 
	 * result = (result*temp) % n 을 사용한 이유는 modulo 연산에서
	 * (a*b) mod n = (a mod n * b) mod n = (a * b mod n) mod n = (a mod n * b mod n) mod n 모두 성립하기 때문임
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
	
	
	// 속도 측정을 위해 시스템 시간을 출력하는 함수
	public static void printsystime(String msg)
	{
		long time = System.currentTimeMillis();
		SimpleDateFormat ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String CurrentTime = ctime.format(new Date(time));
		System.out.println(msg + " : " + CurrentTime);
	}
	
}