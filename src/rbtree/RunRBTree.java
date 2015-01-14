package rbtree;

public class RunRBTree {
	public static void main(String[] args) {
		RBTree t = new RBTree();

		boolean checkBlackHeight = true;

		Node[] nodeList = new Node[] { new Node(7), new Node(11), new Node(2), new Node(8),
				new Node(5), new Node(14), new Node(1), new Node(15), new Node(4) };

		// insert test
		for (int i = 0; i < nodeList.length; i++) {
			t.insert(nodeList[i]);
			t.printSubTree(t.ROOT, 0);
			System.out.println();

			if (t.CheckBlackHeight(t.ROOT) < 0)
				checkBlackHeight = false;
		}

		// delete test
		for (int i = 0; i < nodeList.length; i++) {
			t.delete(nodeList[i]);
			t.printSubTree(t.ROOT, 0);
			System.out.println();

			if (t.CheckBlackHeight(t.ROOT) < 0)
				checkBlackHeight = false;
		}

		System.out.println("check black height result : " + checkBlackHeight);
	}
}