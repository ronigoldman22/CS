public class NumericalString {

	// Task 3.1
	public static boolean legalNumericString(String s, int b) {
		boolean ans = true;
		if (s == null | b < 2 | b > 10 | s.length()==0)
			throw new IllegalArgumentException("input is illegal");
		for (int i = 0; i < s.length() & ans; i = i + 1) {
			if (s.charAt(i)<'0' | s.charAt(i)>'9') //check if s is numeric String
				throw new IllegalArgumentException("input is illegal"); 
			int c = (int) s.charAt(i) - '0'; // change to int value
			if (c < 0 | c >= b) // if c is not in range - false
				ans = false;
		}
		return ans;
	}

	// Task 3.2
	public static String decimalIncrement(String s) {
		String ans = decimalIncrement2(s, 0, true);
		return ans;
	}

	public static String decimalIncrement2(String s, int i, boolean add) {
		String ans = "";
		if (legalNumericString(s, 10) == false) // if not decimal string
			throw new IllegalArgumentException("input is illegal");
		if (0 < s.length() & add == true) { // stop condition
			if (i < s.length()) { // stop condition
				if (s.charAt(i) == '9') {
					ans = "0" + decimalIncrement2(s, i + 1, true); // add "0" and continue to the next index
				} else {
					int t = s.charAt(i) + 1; // add +1 to the int value
					char T = (char) t; // change it to char
					ans = T + s.substring(i + 1) + decimalIncrement2(s, i, false); // add T and the rest of s string,
																					// call the function with no need to
																					// add anymore
				}
			} else
				ans = "1";
		}
		return ans;
	}

	// Task 3.3
	public static String decimalDouble(String s) {
		String ans = decimalDouble2(s, 0, 0);
		return ans;
	}

	public static String decimalDouble2(String s, int i, int carry) {
		String ans = "";
		if (legalNumericString(s, 10) == false)
			throw new IllegalArgumentException("input is illegal");
		if (i < s.length()) { // stop condition
			if (s.charAt(i) <= '4') {
				int t = s.charAt(i) - '0'; // change it to int value
				int tt = t * 2 + '0' + carry; // duplicate, return to the int values of char and add carry
				char T = (char) tt; // change to char
				ans = T + decimalDouble2(s, i + 1, 0); // continue to next index
			} else {
				int t = (s.charAt(i) - '5') * 2 + carry + '0'; // calculate the unity digit
				char T = (char) t; // change to char
				ans = T + decimalDouble2(s, i + 1, 1); // continue to next index with carry=1
			}
		} else {
			if (carry > 0) { // if we still have carry after we finish add it to ans
				int add = carry + '0'; // return to the int values of char
				char addcarry = (char) add; // change to char
				ans = addcarry + "";
			}
		}
		return ans;
	}

	// Task 3.4
	public static String binary2Decimal(String s) {
		String ans = binary2Decimal2(s, 0);
		return ans;
	}

	public static String binary2Decimal2(String s, int index) {
		String ans = "0";
		if (legalNumericString(s, 2) == false)
			throw new IllegalArgumentException("input is illegal");
		if (s.length() == index) // stop condition
			return ans;
		else {
			if (s.charAt(index) == '1') {
				ans = decimalIncrement(decimalDouble(binary2Decimal2(s, index + 1))); // duplicate and add +1 to string
			} else {
				ans = decimalDouble(binary2Decimal2(s, index + 1)); // duplicate string
			}
		}
		return ans;
	}

	public static void main(String[] args) {
		System.out.println("Good Luck! :)");
	}

}
