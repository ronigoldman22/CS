public class BitVector {
	private Bit[] bits;

	// Task 4.4
	public BitVector(String s) {
		if (s == null || s.equals(""))
			throw new IllegalArgumentException("s is null");
		for (int i = 0; i < s.length(); i = i + 1) {
			if (s.charAt(i) != '1' & s.charAt(i) != '0') // check if all chars are legal
				throw new IllegalArgumentException("s contains characters that are not '1' or '0'");
		}
		bits = new Bit[s.length()];
		// fill the array:
		for (int i = 0; i < s.length(); i = i + 1) {
			if (s.charAt(i) == '0')
				bits[i] = new Bit(false); // if its 0 create false
			else
				bits[i] = new Bit(true); // if its 1 create true
		}
	}

	// Task 4.5
	public String toString() {
		String ans = "";
		String temp = "";
		for (int i = 0; i < bits.length; i = i + 1) {
			String c = bits[i].toString(); // create string with the Bit at bits[i]
			temp = temp + c; // add c to temp string
		}
		temp = NumericalString.binary2Decimal(temp); // using the class NumericalString to convert binary string to
													// decimal
		while (temp!="") {
		ans = temp.charAt(0) + ans; //reverse temp to get the answer
		temp = temp.substring(1);
	}
		return ans;
	}

}
