import java.util.NoSuchElementException;

public class BacktrackingSortedArray implements Array<Integer>, Backtrack {
	private Stack stack;
	public int[] arr;
	private int numOfElements;

	// Do not change the constructor's signature
	public BacktrackingSortedArray(Stack stack, int size) {
		this.stack = stack;
		arr = new int[size];
		numOfElements = 0;
	}

	public boolean inRange(int index) { //check if index is in the current array
		if (index > numOfElements || index < 0)
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
		if (arr == null)
			throw new IllegalArgumentException();
		return binarySearch(k, 0, numOfElements - 1);
	}

	public Integer binarySearch(int k, int from, int to) {
		if (from > numOfElements - 1 || to < 0) //out of array
			return -1;
		int middle = (to + from) / 2; //cut array by two
		if (to >= from) { 
			if (arr[middle] < k) //k is in the right half
				return binarySearch(k, middle + 1, to);
			else if (arr[middle] > k)  //k is in the left half
				return binarySearch(k, from, middle - 1);
			else
				return middle;
		} else
			return -1;
	}

	@Override
	public void insert(Integer x) {
		if (numOfElements >= arr.length)
			throw new IllegalArgumentException("the array is full");
		arr[numOfElements] = x; //insert element to the end of array
		insert2(numOfElements); //sort the array
		stack.push(1); //for backtracking
		numOfElements = numOfElements + 1; //one element was added

	}

	public void insert2(int i) {
		int value = arr[i]; 
		while (i > 0 && arr[i - 1] > value) { //if value is smaller from the element befor
			arr[i] = arr[i - 1]; //move the element one step right
			i = i - 1;
		}
		arr[i] = value; //insert the value to his place
		stack.push(i); //for backtrack
	}

	@Override
	public void delete(Integer index) {
		if (numOfElements < 1) //no elements to delete
			throw new IllegalArgumentException("the array is empty");
		if (inRange(index)) {
			stack.push(arr[index]); //for backtrack
			for (int i = index; i < numOfElements - 1; i = i + 1) {
				arr[i] = arr[i + 1]; //move all alements after the one we deleted one index left
			}
			stack.push(-1);
			numOfElements = numOfElements - 1;//one element was deleted
		} else
			throw new NoSuchElementException("no element in this index");

	}

	@Override
	public Integer minimum() {
		if (numOfElements < 1)
			throw new IllegalArgumentException("the array is empty");
		return 0; //the minimum is at first index
	}

	@Override
	public Integer maximum() {
		if (numOfElements < 1)
			throw new IllegalArgumentException("the array is empty");
		return numOfElements - 1;//the maximum is at last index
	}

	@Override
	public Integer successor(Integer index) {
		if (numOfElements < 1)
			throw new IllegalArgumentException("the array is empty");
		if (inRange(index) && inRange(index + 1)) { //index in array and not the maximum
			return index + 1; //because array is sorted
		} else
			throw new NoSuchElementException("no element in this index");
	}

	@Override
	public Integer predecessor(Integer index) {
		if (numOfElements < 1)
			throw new IllegalArgumentException("the array is empty");
		if (inRange(index) && inRange(index - 1)) { //index in array and not the minimum
			return index - 1; //because array is sorted
		} else
			throw new NoSuchElementException("no element in this index");
	}

	@Override
	public void backtrack() {
		if (!stack.isEmpty()) { //have action to cancle
			Integer lastact = (Integer) stack.pop();
			if (lastact == 1) //lasr action was insert
				delete((Integer) stack.pop()); //delete from index we saved
			else { //last action was delete
				insert((Integer) stack.pop()); //insert the value we saved
			}
			stack.pop(); //we used the functions insert/delete and dont want to backtrack them
			stack.pop();
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
