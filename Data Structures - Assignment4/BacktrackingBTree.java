import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BacktrackingBTree<T extends Comparable<T>> extends BTree<T> {
	// For clarity only, this is the default ctor created implicitly.
	public BacktrackingBTree() {
		super();
	}

	public BacktrackingBTree(int order) {
		super(order);
	}

	// You are to implement the function Backtrack.
	public void Backtrack() {
		if (!backtrackDeque.isEmpty()) {
			Deque<T> back = (Deque<T>) backtrackDeque.pop();
			T toDelete = (T) back.pop(); //the key that was inserted
			Node<T> insertedNode = getNode(toDelete); //search the node of the key
			if (insertedNode.parent == null && insertedNode.numOfChildren == 0 && insertedNode.numOfKeys == 1) { //its the only key in the tree
				root = null;
			    size =0;
			}
			else {
				insertedNode.removeKey(toDelete);
			}
			Node<T> node = insertedNode.parent;
			while (!back.isEmpty()) { 
				T poppedUp = back.pop(); //the key that was popped up while spliting
				boolean merged = false;
				while (!merged) {
					if (node.indexOf(poppedUp) != -1) { //search the key in the node
						int index = node.indexOf(poppedUp);
						if (node.numOfKeys > 1)
							node.removeKey(index);
						else { // there is only one key in the node
							if (node.parent == null) { //is root

								root = node.children[index]; //update the root field
								node.children[index].parent = null; //update the child his parent is null
							} else { //is not root
								node.children[index].parent = node.parent;
								node.parent.removeChild(node); //remove node from the children array of his parent
								node.parent.addChild(node.children[index]); //add the child of node to his parent children array
							}
						}
						node.children[index].addKey(poppedUp); //add the poppedUp key to his child node
						for (int i = 0; i < node.children[index + 1].numOfKeys; i = i + 1) {
							T toAdd = node.children[index + 1].keys[i]; 
							node.children[index].addKey(toAdd); //move all keys of the right part of spliting to the left
							if (node.children[index + 1].numOfChildren != 0) {
								if (node.children[index + 1].children[i] != null)
									node.children[index].addChild(node.children[index + 1].children[i]); //move all children of the right part of spliting to the left
							}	
						}
						if (node.children[index + 1].children[node.children[index + 1].numOfKeys] != null)
							node.children[index]
									.addChild(node.children[index + 1].children[node.children[index + 1].numOfKeys]); //move the last child of the right to the left
						node.removeChild(node.children[index + 1]); //remove the right child of the split
						merged = true;
					} else //the poppedUp key is not in the node, go up and search it in the uncestor
						node = node.parent; 
				}
			}
		}

	}

	// Change the list returned to a list of integers answering the requirements
	public static List<Integer> BTreeBacktrackingCounterExample() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(20);
		list.add(30);
		list.add(40);
		list.add(50);
		list.add(60);
		list.add(10);
		return list;
	}
}
