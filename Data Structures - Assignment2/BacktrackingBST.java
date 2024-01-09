
import java.util.NoSuchElementException;

public class BacktrackingBST implements Backtrack, ADTSet<BacktrackingBST.Node> {
	private Stack stack;
	private Stack redoStack;
	private BacktrackingBST.Node root = null;
	private Integer canRedoCounter = 0;

	// Do not change the constructor's signature
	public BacktrackingBST(Stack stack, Stack redoStack) {
		this.stack = stack;
		this.redoStack = redoStack;
	}

	public Node getRoot() {
		if (root == null) {
			throw new NoSuchElementException("empty tree has no root");
		}
		return root;
	}

	private boolean isEmpty() {
		return root == null;
	}

	public Node search(int k) {
		if (isEmpty())
			return null;
		Node find = root;
		while (find.getKey() != k) {
			if (k > find.getKey()) // k may be at right
				find = find.right;
			else
				find = find.left; // k may be at left
			if (find == null)
				return null; // not found
		}
		return find;
	}

	public void insert(Node node) {
		if (isEmpty())
			root = node; // insert node in the root
		else {
			boolean inserted = false;
			Node cur = root;
			while (!inserted) {
				if (node.getKey() < cur.getKey()) { // insert at left
					if (cur.left == null) {
						cur.left = node; // put node at left
						cur.left.parent = cur; // update parent field
						inserted = true;
					} else
						cur = cur.left;
				} else { // insert at right
					if (cur.right == null) {
						cur.right = node; // put node at right
						cur.right.parent = cur; // update parent field
						inserted = true;
					} else
						cur = cur.right;
				}
			}
		}
		stack.push(node); // able backtracking
		canRedoCounter = 0; // unable retracking
	}

	public void delete(Node node) {
		if (!isEmpty()) {
			canRedoCounter = 0; // unable retracking
			Object[] deletedtedNode = new Object[5]; // able backtracking (saves the information)
			deletedtedNode[0] = node.getKey();
			deletedtedNode[1] = node.parent;
			deletedtedNode[2] = node.left;
			deletedtedNode[3] = node.right;
			deletedtedNode[4] = node;
			boolean isRoot = (node.parent == null); // we want to delete the root
			if (node.left == null || node.right == null) { // parent of one node or is a leaf
				if (node.right != null) { // left is null
					if (isRoot) {
						node.right.parent = null;
						root = node.right; // update root field
					} else {
						newChildForNodesParent(node, node.right); // update the parent of node- the right is his child
						node.right.parent = node.parent;
						node = node.right;
					}
				} else { // right is null
					if (node.left == null) { // node is a leaf
						if (isRoot)
							root = null; // update root field
						else {
							newChildForNodesParent(node, null); // update the parent of node- his child is null
							node = null;
						}
					} else { // node has only left child, right is null
						if (isRoot) {
							node.left.parent = null;
							root = node.left; // update root field
						} else {
							newChildForNodesParent(node, node.left); // update the parent of node- the left is his child
							node.left.parent = node.parent;
							node = node.left;
						}
					}
				}
				stack.push(deletedtedNode); // able backtracking
			} else { // node has two children
				Node suc = successor(node);
				Boolean hasBigRightTree = (node.right != suc); // check if there are more nodes at right except of suc
				int newKey = suc.getKey();
				delete(suc); // deleting leaf
				node = suc;
				node.left = (Node) deletedtedNode[2]; // update left field
				node.left.parent = node; // update the parent field of left child
				if (hasBigRightTree) { // need to restore the nodes at right tree (except the suc)
					node.right = (Node) deletedtedNode[3];// update right field
					node.right.parent = node;// update the parent field of right child
				}
				if (isRoot) {
					root = node; // update root field
					node.parent = null; // update parent field
				} else {
					node.parent = (Node) deletedtedNode[1]; // update parent field
					newChildForNodesParent(node, node); // update the parent of node- the suc is his child
				}
				node.key = newKey;
				stack.push(deletedtedNode); // able backtracking
			}
		} else
			throw new NoSuchElementException("the tree is empty");
	}

	public Node minimum() {
		if (isEmpty())
			throw new NoSuchElementException("the tree is empty");
		Node min = root;
		while (min.left != null) {
			min = min.left;
		}
		return min; // returns the 'leftest' node
	}

	public Node maximum() {
		if (isEmpty())
			throw new NoSuchElementException("the tree is empty");
		Node max = root;
		while (max.right != null) {
			max = max.right;
		}
		return max; // returns the 'rightest' node
	}

	public Node successor(Node node) {
		if (isEmpty())
			throw new NoSuchElementException("the tree is empty");
		else {
			Node suc = node;
			if (suc.right == null) { // no right tree
				while (suc != root && suc.parent.right == suc) { // current suc is the right child of his parent
					suc = suc.parent; // up in the tree till root or till step up-left once
				}
				if (suc != root)
					return suc.parent;
				else
					throw new NoSuchElementException("not exists");
			} else { // successor at right tree
				suc = node.right;
				while (suc.left != null) {
					suc = suc.left; // search for the minimum of right tree
				}
				return suc;
			}
		}
	}

	public Node predecessor(Node node) {
		if (isEmpty())
			throw new NoSuchElementException("the tree is empty");
		else {
			Node pre = node;
			if (pre.left == null) { // no left tree
				while (pre != root && pre.parent.left == pre) { // current pre is the left child of his parent
					pre = pre.parent; // up in the tree till root or till step up-right once
				}
				if (pre != root)
					return pre.parent;
				else
					throw new NoSuchElementException("not exists");
			} else { // predecessor at left tree
				pre = node.left;
				while (pre.right != null) {
					pre = pre.right; // search for the maximum of left tree
				}
			}
			return pre;
		}
	}

	// help function for updating parent of node on his new child:
	private void newChildForNodesParent(Node curChild, Node newChild) {
		if (curChild.getKey() < curChild.parent.getKey()) // curChild is at left of his parent
			curChild.parent.left = newChild;
		else // curChild is at right of his parent
			curChild.parent.right = newChild;
	}

	@Override
	public void backtrack() {
		if (!stack.isEmpty()) {
			Object action = stack.pop();
			if (action instanceof Node) { // if last action was insert (insertion of leaf)
				Integer counter = canRedoCounter + 1; // able retracking
				delete((Node) action);
				canRedoCounter = counter; // delete function reset it, restore the counter
				redoStack.push(stack.pop()); // the delete function pushing the node back, pop it and push to retracking
												// stack
				redoStack.push("deletion"); // able retracking
			} else { // if last action was delete
				Object[] arrayAction = (Object[]) action; // array of the information of the node
				Node toInsert = (Node) arrayAction[4];// restore the node
				toInsert.key = (Integer) arrayAction[0];
				toInsert.parent = (Node) arrayAction[1];
				toInsert.left = (Node) arrayAction[2];
				toInsert.right = (Node) arrayAction[3];
				Boolean isRoot = (arrayAction[1] == null); // root was deleted
				if (arrayAction[2] == null || arrayAction[3] == null) { // has one child or is a leaf
					if (isRoot) {
						root.parent = toInsert;
						root = toInsert; // update the root field
					} else { // node (not the root) was deleted
						newChildForNodesParent(toInsert, toInsert); // update the parent of toInsert- toInsert is his
																	// child
						if (arrayAction[2] == null) { // left child is null
							if (arrayAction[3] != null) // right child isn't null
								toInsert.right.parent = toInsert; // update right's parent
						} else // left is not null
							toInsert.left.parent = toInsert;
					}
				} else { // has two children
					Object[] sucArray = (Object[]) stack.pop(); // pop the successor's information from the stack
					// update all the relevant fields:
					toInsert.left.parent = toInsert;
					toInsert.right.parent = toInsert;
					Node suc = (Node) sucArray[4]; // restore the successor
					suc.parent = (Node) sucArray[1];
					suc.key = (Integer) sucArray[0];
					suc.right = (Node) sucArray[3];
					suc.left = (Node) sucArray[2];
					newChildForNodesParent(suc, suc); // update the parent of suc his child is suc
					if (isRoot)
						root = toInsert; // update the root field
					else
						newChildForNodesParent(toInsert, toInsert); // update the parent of toInsert-toInsert is his
																	// child
				}
				redoStack.push(toInsert); // able retracking
				redoStack.push("insertion"); // able retracking
				canRedoCounter = canRedoCounter + 1; // able retracking
			}

		}
	}

	@Override
	public void retrack() {
		if (canRedoCounter > 0) { // if last action was backtracking
			Object action = redoStack.pop();
			if (action.equals("deletion")) { // cancelled action was insertion
				Object[] nodeRestore = (Object[]) redoStack.pop(); // array of the information of node
				Node toInsert = (Node) nodeRestore[4]; // update fields
				toInsert.parent = (Node) nodeRestore[1];
				if (toInsert.parent != null)
					newChildForNodesParent(toInsert, toInsert); // update the parent of toInsert- toInsert is his child
				else // is root
					root = toInsert;
				stack.push(toInsert); // able backtracking again
				canRedoCounter = canRedoCounter - 1;
			} else { // cancelled action was deletion
				Integer counter = canRedoCounter - 1;
				delete((Node) redoStack.pop());
				canRedoCounter = counter; // delete function reset it, restore the counter
			}

		}
	}

	public void printPreOrder() {
		if (!isEmpty()) {
			Node last = maximum();
			if (last.left == null && last.right == null) // to check which node will be print last (without space after
															// printing)
				root.printPreOrder(last); // if the maximum is a leaf it will be print last
			else // the predecessor of the maximum needs to be print last
				root.printPreOrder(predecessor(last));
		}
	}

	@Override
	public void print() {
		printPreOrder();
	}

	public static class Node {
		// These fields are public for grading purposes. By coding conventions and best
		// practice they should be private.
		public BacktrackingBST.Node left;
		public BacktrackingBST.Node right;

		private BacktrackingBST.Node parent;
		private int key;
		private Object value;

		public Node(int key, Object value) {
			this.key = key;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public void printPreOrder(Node last) {
			if (getKey() == last.getKey())
				System.out.print(getKey()); // print without space
			else
				System.out.print(getKey() + " "); // print node's key with space
			if (left != null)
				left.printPreOrder(last); // go left
			if (right != null)
				right.printPreOrder(last); // go right
		}
	}

}
