
import java.math.BigInteger;
import java.util.Random;

class Assignment3BigInteger {

	// Task 1.1
	public static BigInteger sumSmaller(BigInteger n) {
		BigInteger sum = new BigInteger("0");
		BigInteger curNum = new BigInteger("1");
		final BigInteger one = BigInteger.ONE; // final BigInteger so it can't change
		while (curNum.compareTo(n) == -1) { // while curNum smaller than n
			sum = sum.add(curNum); // add curNum to sum
			curNum = curNum.add(one); // add +1 to curNum
		}
		return sum;
	}

	// Task 1.2
	public static void printRandoms(int n) {
		Random rand = new Random(); // create random number
		for (int count = 0; count < n; count = count + 1) { // running n times to print n random numbers
			System.out.println(rand.nextInt());
		}
	}

	// Task 1.3
	public static boolean isPrime(BigInteger n) {
		boolean ans = true;
		BigInteger val = new BigInteger("2"); // the smallest prime number
		final BigInteger one = BigInteger.ONE;
		final BigInteger zero = BigInteger.ZERO;
		// like the algorithm we learned in class:
		while (val.multiply(val).compareTo(n) == -1 | val.multiply(val).equals(n) & ans) { // while (val^2 <= n) and
																							// (ans(is prime) is true)
			if (n.mod(val) == zero)
				ans = false; // if n%val=0 : n is not prime
			val = val.add(one); // add +1 to val
		}
		return ans;
	}

	// Task 1.4
	public static BigInteger randomPrime(int n) {
		BigInteger randBig = new BigInteger("0");
		Random rand = new Random(); // create a new random number
		randBig = BigInteger.probablePrime(n, rand); // Returns a positive BigInteger that is probably prime, in range
														// of maximum 2^n-1
		while (isPrime(randBig) == false) { // make sure that is prime
			randBig = BigInteger.probablePrime(n, rand); // if its not prime get another random number till its prime
		}
		return randBig;
	}

}