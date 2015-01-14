package rbtree;

public class Node {
	int key;
	String color;

	Node parent;
	Node left;
	Node right;
	
	public Node(int key){
		this.key = key;
	}

	public Node(int key, String color, Node parent, Node left, Node right) {
		this.key = key;
		this.color = color;
		this.parent = parent;
		this.left = left;
		this.right = right;
	}
}