import java.util.NoSuchElementException;

public class BacktrackingArray implements Array<Integer>, Backtrack {
	private Stack stack;
	private int[] arr;
	private int numOfElements;
	
	// Do not change the constructor's signature
	public BacktrackingArray(Stack stack, int size) {
		this.stack = stack;
		arr = new int[size];
		numOfElements = 0;
	}

	public boolean inRange(int index) {  //check if index is in the current array
		if (index > numOfElements-1 || index < 0)
			return false;
		return true;
	}

	@Override
	public Integer get(int index) { 
		if (inRange(index)) //the index is in the array
			return arr[index];
		throw new NoSuchElementException("not in range");

	}

	@Override
	public Integer search(int k) {
		if (arr == null) //no elements
			throw new IllegalArgumentException();
		for (int i = 0; i < numOfElements; i = i + 1) {
			if (k == arr[i]) //the value at index i is k
				return i;
		}
		return -1; //k not found in array
	}

	@Override
	public void insert(Integer x) {
		if (numOfElements >= arr.length) //no free space
			throw new IllegalArgumentException("the array is full");
		arr[numOfElements] = x; //add x to the first index with no element
		stack.push("insertion"); //for backtrack
		numOfElements = numOfElements + 1; //one element was added

	}

	@Override
	public void delete(Integer index) {
		if (numOfElements < 1) //no elelment to delete
			throw new IllegalArgumentException("the array is empty");
		if (inRange(index)) { //the index is in the array
			stack.push(index); //for backtrack
			stack.push(arr[index]); //for backtrack
			arr[index] = arr[numOfElements-1]; //the last element at array will move to the index we want to delete
			stack.push("deletion");//for backtrack
			numOfElements = numOfElements - 1; //one element was deleted
		}
	}

	@Override
	public Integer minimum() {
		if (numOfElements < 1)//no elelments
			throw new IllegalArgumentException("the array is empty");
		Integer min = 0; //start from the first index
		for (int i = 1; i < numOfElements; i = i + 1) { 
			if (arr[i] < arr[min]) //if element at index i is smaller than min
				min = i; //update min
		}
		return min;
	}

	@Override
	public Integer maximum() { //no elelments
		if (numOfElements < 1)
			throw new IllegalArgumentException("the array is empty");
		Integer max = 0; //start from the first index
		for (int i = 1; i < numOfElements; i = i + 1) { 
			if (arr[i] > arr[max]) //if element at index i is bigger than min
				max = i; //update max
		}
		return max;
	}

	@Override
	public Integer successor(Integer index) {
		if (numOfElements < 1)  //no elelments
			throw new IllegalArgumentException("the array is empty");
		if (inRange(index)) { //the index is in the array
			int max = maximum();
			if (max == index) //maximum has no successor 
				throw new NoSuchElementException("no successor for the requested index");
			int suc = max; //for compering all elements to the maximum 
			for (int i = 0; i < numOfElements; i = i + 1) { 
				if (arr[i] > arr[index] && arr[i] < arr[suc]) //if element at i is bigger than element at index and smaller than current suc
					suc = i; //update suc
			}
			return suc;  //the smallest element that bigger than element at index
		} else
			throw new NoSuchElementException("no element in this index");
	}

	@Override
	public Integer predecessor(Integer index) {
		if (numOfElements < 1) //no elelments
			throw new IllegalArgumentException("the array is empty");
		if (inRange(index)) { //the index is in the array
			int min = minimum(); 
			if (min == index) //the minimum has no predecessor 
				throw new NoSuchElementException("no predecessor for the requested index");
			int pre = min; //for compering all elements to the minimum
			for (int i = 0; i < numOfElements; i = i + 1) { 
				if (arr[i] < arr[index] && arr[i] > arr[pre]) //if element at i is smaller than element at index and bigger than current pre
					pre = i; //update pre
			}
			return pre; //the biggest element that smaller than element at index
		} else
			throw new NoSuchElementException("no element in this index");
	}

	@Override
	public void backtrack() {
		if (!stack.isEmpty()) { //have action to cancle
			String lastact = (String)stack.pop(); //the last action
			if (lastact == "insertion") { //last action was insert
				numOfElements = numOfElements-1; //element can be insertsd only to the last index, so we want to delete the last element at array -we can just update the number of elements reduce by 1 
			} else { //last action was delete
				Integer value = (Integer) stack.pop(); //take out the value we deleted
				Integer index = (Integer) stack.pop(); //take out the index we deleted from
				arr[numOfElements] = arr[index]; //moving the value  at the index we want to insert to, to the end of array
				arr[index]=value; //insert value to the right index
				numOfElements = numOfElements + 1; //one element was added
			}

		}

	}

	@Override
	public void retrack() {
		/////////////////////////////////////
		// Do not implement anything here! //
		/////////////////////////////////////
	}

	@Override
	public void print() {
		if (numOfElements >= 1) { //there are elements to print
			for (int i = 0; i < numOfElements; i = i + 1) {
				if (i < numOfElements - 1) //add space after all elements except the last ekement
					System.out.print(arr[i] + " ");
				else //last elelment-no space
					System.out.print(arr[i]);
			}

		}
	}

}
