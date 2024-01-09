
public class Warmup {
	
	public static int backtrackingSearch(int[] arr, int x, int forward, int back, Stack myStack) {
		int i = 0;
		int counter = 0; //counter of forward steps
		while (i < arr.length && arr[i] != x ) {
			if (counter < forward) { //able to do more steps forward
				myStack.push(arr[i]);
				i = i + 1;
				counter = counter + 1; //step forward is done
			} else { //need to go steps backwards
				for (int j = 1; j <= back; j = j + 1) {
					myStack.pop(); //pop the steps backwards
				}
				i = i - back; //move 'i' to the current index after back steps
				counter = 0; //Initialise counter of forward steps
			}
		}
		if (i < arr.length && arr[i] == x) //found
			return i;
		return -1; //not found
	}

	public static int consistentBinSearch(int[] arr, int x, Stack myStack) {
		if (arr.length<=0)
			return -1;
		return binarySearch(arr, x, 0, arr.length-1, myStack,0);
	}

	public static int binarySearch(int[] arr, int x, int from, int to, Stack myStack, int inconsistencies) {
		if (from>arr.length-1 || to<0) //not found
			return -1;
		myStack.push(from);
		myStack.push(to);
		int middle = (to + from) / 2;
		if (inconsistencies == 0) { //no need to step back
			if (to >= from) { //binary search as we learned in class
				if (arr[middle] < x)
					return binarySearch(arr, x, middle + 1, to, myStack, Consistency.isConsistent(arr));
				else if (arr[middle] > x)
					return binarySearch(arr, x, from, middle - 1, myStack, Consistency.isConsistent(arr));
				else 
					return middle;
			}
			else 
			    return -1;
		}
		else { //need to step backwards
			for(int i = 0; i <= inconsistencies; i = i+1) {
				to = (int)myStack.pop();
				from = (int)myStack.pop();
			}
			return binarySearch(arr, x, from, to, myStack,0); //do binary search
		}
	}

}
