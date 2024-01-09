import java.util.ArrayList;
import java.util.List;

public class BacktrackingAVL extends AVLTree {
	// For clarity only, this is the default ctor created implicitly.
	public BacktrackingAVL() {
		super();
	}

	// You are to implement the function Backtrack.
	public void Backtrack() {
		if (!backtrackDeque.isEmpty()) { 
			if (backtrackDeque.peek() instanceof String) { //there was rotation during the insertion
				String rotation = (String) backtrackDeque.pop();
				Node unbalanced = (Node) backtrackDeque.pop();
				Backtrack2(rotation, unbalanced); //rotate back the rotation occurred during insert
				if (backtrackDeque.peek() instanceof String) { //there was 2 rotations during the insertion
					String rotation2 = (String) backtrackDeque.pop();
					Node unbalanced2 = (Node) backtrackDeque.pop();
					Backtrack2(rotation2, unbalanced2);//rotate back the rotation occurred during insert
				}
			}
			Node toDelete = (Node) backtrackDeque.pop(); // toDelete is the inserted node
			if (toDelete.parent == null) //toDelete is root
				root = null;
			else {
				int value = toDelete.value;
				updateSizeLeft(value); //update size left tree for all nodes fron toDelete to root
				updateSizeRight(value);//update size right tree for all nodes fron toDelete to root
				if (toDelete.parent.left == toDelete) //toDelete is left child
					toDelete.parent.left = null; //delete toDelete
				else //toDelete is right child
					toDelete.parent.right = null; //delete toDelete
				//update hight of all nodes from toDelete to root
				Node nodeCopy = toDelete.parent;
				while (nodeCopy != null) {
					nodeCopy.updateHeight();
					nodeCopy = nodeCopy.parent;
				}
			}
		}
	}

	public void Backtrack2(String rotation, Node unbalanced) {
		boolean isRoot = unbalanced == root; 
		if (rotation.equals("R")) {
			unbalanced = rotateLeft(unbalanced); //perform the opposit rotation to the rotation accured at insertion
		} else {
			unbalanced = rotateRight(unbalanced); //perform the opposit rotation to the rotation accured at insertion
		}
		if (isRoot) //the rotate node is the root
			root = unbalanced.parent; //update root after rotation
		if (unbalanced.parent != null) { //is not root
			if (unbalanced.parent.value > unbalanced.value) //is left child
				unbalanced.parent.left = unbalanced;
			else //is right child
				unbalanced.parent.right = unbalanced;
		} else //is root
			root = unbalanced;

	}

	public void updateSizeLeft(int value) {
		Node node = root;
		updateSizeLeft2(node, value);
	}

	public void updateSizeLeft2(Node node, int value) {
		if (node != null) { 
			if (value < node.value) { //the value of deleted node is at left tree of node
				node.sizeLeftTree = node.sizeLeftTree - 1; //update size left tree after deletion
				updateSizeLeft2(node.left, value); //go left and continue the updates
			} else
				updateSizeLeft2(node.right, value); //no update is needed

		}
	}

	public void updateSizeRight(int value) {
		Node node = root;
		updateSizeRight2(node, value);
	}

	public void updateSizeRight2(Node node, int value) {
		if (node != null) {
			if (value > node.value) { //the value of deleted node is at right tree of node
				node.sizeRightTree = node.sizeRightTree - 1;//update size right tree after deletion
				updateSizeRight2(node.right, value); //go right and continue the updates
			} else
				updateSizeRight2(node.left, value); //no update is needed

		}
	}

	// Change the list returned to a list of integers answering the requirements
	public static List<Integer> AVLTreeBacktrackingCounterExample() {
		// You should remove the next two lines, after double-checking that the
		// signature is valid!
		List<Integer> list = new ArrayList<Integer>();
		list.add(7);
		list.add(32);
		list.add(41);
		return list;
	}

	public int Select(int index) {
		return select(root, index);
	}

	private int select(Node node, int index) { //assuming index is in range
		int curRank = node.sizeLeftTree + 1; 
		if (curRank == index) {
			return node.value;
		} else {
			if (index < curRank)
				return select(node.left, index);
			else
				return select(node.right, index - curRank);

		}

	}

	public int Rank(int value) {
		if (root != null)
			return Rank(root, value, 0);
		else
			return 0;
	}

	public int Rank(Node node, int value, int sum) {
		if (node != null) {
			if (node.value == value) { //the node was found
				int sizeLeft = node.sizeLeftTree; 
				return sizeLeft + sum; //sum the left values and the num of the smaller values we count while recursing
			} else if (value < node.value)
				return Rank(node.left, value, sum); //search the key at left
			else { //need to search the key at right
				int sizeLeft = node.sizeLeftTree; //left nodes of key that is smaller than our value are smaller than our value
				return Rank(node.right, value, sum + sizeLeft + 1); 
			}
		} else //not found
			return sum;
	}

}
