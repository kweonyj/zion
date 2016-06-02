import java.util.Scanner;
import java.util.Random;

public class RSA_4096 {

	public static void main(String[] args) {

		// 변수 초기값 설정
		int M = 0;			// 메세지 평문, M < n 의 조건을 만족해야 함.
		int C = 0;			// 암호문
		int n = 0;			// * 4096bits 크기를 갖는 n, n=p*q. biginteger로 변환해야 함 *
		int p = 0;			// 임의의 소수
		int q = 0;			// 임의의 소수
		int pi = 0;			// 오일러 파이함수 pi = (p-1)*(q-1)
		int e = 0;			// 공개키
		int d = 0;			// 개인키
		

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
	
			// 1. 서로 다른 소수 p, q를 생성한다
			while(true)
			{
				p = getRandomPrimeNum(2, 100);
				q = getRandomPrimeNum(2, 100);
				
				n = p * q;
				pi = (p-1)*(q-1);
				
				/*
				 * 소수 검색 조건
				 * 1. 두 소수는 서로 같지 않아야 함
				 * 2. 입력된 메세지 M은 두 소수의 곱 n 보다 작아야 함
				 * 3. 오일러 파이함수 pi는 개인키의 조건 1< e < pi 이므로, pi는 2 보다 커야함
				 *     왜냐하면 정수e의 최소값은 2이기 때문에 pi 는 2보다 커야한다
				 */
				if(p != q && M < n && pi > 2)
				{
					System.out.println("p=" + p + ", q=" + q);
					break;
				}
			}
			
			System.out.println("n = " + n);
			System.out.println("pi = " + pi);
	
			// 2. 공개키 생성함수 호출
			e = getPublicKey(pi);
			System.out.println("e = " + e);		

			// 3. 개인키 생성함수 호출
			d = getPrivateKey(e, pi);
			System.out.println("d = " + d);
		
			// 4. 암호화 과정
			C = getCrypto(M, e, n);
			System.out.println("암호문 C = " + C);
			
			// 5. 복호화 과정
			M = getCrypto(C, d, n);
			System.out.println("복호문 M = " + M);
		}
		
		scan.close();
	}
	
	/*
	 * 두 수를 입력받아 그 사이에 있는 random 값을 return 하는 함수
	 * 큰 수의 random 값은 nextInt()로 해결 할 수 없기 때문에 nextDouble()을 이용한다.
	 * 예를 들어, random 값의 검색범위가 2 ~ 100 일때
	 * random 값 result = 0.2*(100-2) + 2 의 형태로 계산된다.
	 */
	public static int getRandomPrimeNum(int startNum, int endNum)
	{
		Random rn = new Random();
		int intRange = endNum - startNum;
		int k;

		// 소수를 찾을 때까지 반복
		while(true)
		{
			// random 값 선택
			int result = (int)(rn.nextDouble() * intRange + startNum);

			// 2부터 차례로 ++ 하여 나누기 시도, 나머지가 0인 경우는 소수가 아님
			for(k=2; k <= result-1; k++)
			{
				if(result%k == 0)		//소수가 아닌 경우 for 반복을 마치고, while로 감
					break;
			}
			
			if(k == result)					// 소수일 경우 return
				return result;
		}
	}

	/*
	 * 공개키 e 생성함수
	 * 공개키 e의 조건은 1 < e < pi 이며 pi 와 서로소인 정수
	 * 임의의 값을 가지고 서로소인지 확인한 후
	 * 서로소가 아니면 정수 random_e 를 하나씩 줄여가면 확인
	 */
	public static int getPublicKey(int pi)
	{
		// pi 보다 작으면서 pi와 서로 소인 정수 e 선택
		while(true)
		{
			int temp_e = getRandomNum(2, pi);			// 임의의 정수 선택
			while(true)
			{
				// temp_e와 pi 가 서소로인지 판별
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
	 * 임의의 자연수 생성함수
	 * 두 수를 입력받아 그 사이에 있는 임의의 정수를 구하는 함수
	 * getRandomPrimeNum 함수와 동일하며, 소수 확인 조건만 없음
	 */
	public static int getRandomNum(int startNum, int endNum)
	{
		Random rn = new Random();
		int intRange = endNum - startNum;
		
		int result = (int)(rn.nextDouble() * intRange + startNum);					// 랜덤하게 매우 큰수의 임의의 값을 구하기
		return result;
	}	

	/*
	 * 임의의 두 수를 받아 최대공약수를 찾는 함수
	 * 두 수(a, b)가 같으면 최대공약수는 a
	 * GCD를 찾는 구조는 재귀적함수 호출
	 * 큰수를 작은 수로 나눌때 나머지가 0이면 그 수가 최대공약수가 되며,
	 * 나머지를 가지고 반복 실행한다.
	 * 즉, a mod b 연산을 반복한다.
	 */
	public static int getGCD(int a, int b)
	{
		int temp;
		if(a==b)
			return a;
		else if(a<b)		// b 가 큰 경우 나누기를 하기 위해 자리 바꿈
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
	 * 개인키 생성함수
	 * e*d=1 mod n 계산
	 * 주어진 e 에 대해서 정수 2부터 차례로 올려가며 mod n 을 계산하여 1이 되는 경우 return
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
	 * 암호화(지수계산) 함수
	 * 
	 * 암호화와 복호화가 같은 과정으로 지수-법 계산을 인수만 바꾸어서 사용
	 * C=M^e mod n
	 * M=C^d mod n
	 * 
	 * result = (result*temp) % n 을 사용한 이유는 modulo 연산에서
	 * (a*b) mod n = (a mod n * b) mod n = (a * b mod n) mod n = (a mod n * b mod n) mod n 모두 성립하기 때문임
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
