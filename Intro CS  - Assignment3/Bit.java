
public class Bit {

	private boolean value;

	// Task 4.1
	public Bit(boolean value) {
		this.value = value;
	}

	// Task 4.2
	public int toInt() {
		int ans = 0;
		if (value == true)
			ans = 1; // change ans value
		return ans;
	}

	// Task 4.3
	public String toString() {
		String ans = "" + toInt(); // make it string
		return ans;
	}
}
