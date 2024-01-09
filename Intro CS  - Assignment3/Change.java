
class Change {

	// Task 2.1
	public static boolean change(int[] coins, int n) {
		boolean ans = change2(coins, n, 0);
		return ans;
	}

	public static boolean change2(int[] coins, int n, int i) {
		boolean ans;
		if (i == coins.length & n == 0) // stop condition
			ans = true;
		else {
			if (i > coins.length - 1 | n < 0) // stop condition
				ans = false;
			else
				ans = change2(coins, n - coins[i], i) | change2(coins, n, i + 1); // use the coin in coins[i] or dont
																					// use and continue checking
		}
		return ans;
	}

	// Task 2.2
	public static boolean changeLimited(int[] coins, int n, int numOfCoinsToUse) {
		boolean ans = changeLimited2(coins, n, numOfCoinsToUse, 0);
		return ans;
	}

	public static boolean changeLimited2(int[] coins, int n, int numOfCoinsToUse, int i) {
		boolean ans;
		if (i < coins.length & n == 0 & numOfCoinsToUse >= 0) // stop condition
			ans = true;
		else {
			if (i > coins.length - 1 | n < 0 | numOfCoinsToUse < 0) // stop condition
				ans = false;
			else
				ans = changeLimited2(coins, n - coins[i], numOfCoinsToUse - 1, i) // use the coins in coins[i]
						| changeLimited2(coins, n, numOfCoinsToUse, i + 1); // dont use the coins in coins[i]
		}
		return ans;
	}

	// Task 2.3
	public static void printChangeLimited(int[] coins, int n, int numOfCoinsToUse) {
		printChangeLimited2(coins, n, numOfCoinsToUse, 0, "");
	}

	public static void printChangeLimited2(int[] coins, int n, int numOfCoinsToUse, int i, String acc) {
		boolean forOnePrint = changeLimited(coins, n, numOfCoinsToUse);
		if (i < coins.length & n == 0 & numOfCoinsToUse >= 0 & forOnePrint) { // stop condition
			forOnePrint = false;
			System.out.println(acc.substring(0, acc.length() - 1));
		} else {
			if (i > coins.length - 1 | n < 0 | numOfCoinsToUse < 0) // stop condition
				forOnePrint = false;
			else {
				if (forOnePrint = changeLimited(coins, n - coins[i], numOfCoinsToUse - 1)) // if possible to change
																							// continue with using
																							// coins[i]
					printChangeLimited2(coins, n - coins[i], numOfCoinsToUse - 1, i, acc + coins[i] + ",");
				else {
					printChangeLimited2(coins, n, numOfCoinsToUse, i + 1, acc); // if impossible continue checking
																				// without coins[i]
				}
			}
		}
	}

	// Task 2.4
	public static int countChangeLimited(int[] coins, int n, int numOfCoinsToUse) {
		int ans = countChangeLimited2(coins, n, numOfCoinsToUse, 0);
		return ans;
	}

	public static int countChangeLimited2(int[] coins, int n, int numOfCoinsToUse, int i) {
		int ans = 0;
		if (i < coins.length & n == 0 & numOfCoinsToUse >= 0) { // stop condition
			ans = ans + 1;
		} else {
			if (i > coins.length - 1 | n < 0 | numOfCoinsToUse < 0) // stop condition
				ans = 0;
			else
				ans = countChangeLimited2(coins, n - coins[i], numOfCoinsToUse - 1, i) // count the options with
																						// coins[i]
						+ countChangeLimited2(coins, n, numOfCoinsToUse, i + 1); // count the options without coins[i]
		}
		return ans;
	}

	// Task 2.5
	public static void printAllChangeLimited(int[] coins, int n, int numOfCoinsToUse) {
		printAllChangeLimited2(coins, n, numOfCoinsToUse, 0, "");
	}

	public static void printAllChangeLimited2(int[] coins, int n, int numOfCoinsToUse, int i, String acc) {
		if (i < coins.length & n == 0 & numOfCoinsToUse >= 0) {
			if (acc.length() == 0) // stop condition
				System.out.println("");
			else // stop condition
				System.out.println(acc.substring(0, acc.length() - 1));
		} else {
			if (i <= coins.length - 1 & n > 0 & numOfCoinsToUse > 0) {
				printAllChangeLimited2(coins, n - coins[i], numOfCoinsToUse - 1, i, acc + coins[i] + ","); // use
																											// coins[i]
				printAllChangeLimited2(coins, n, numOfCoinsToUse, i + 1, acc); // dont use coins[i]
			}
		}
	}

}
