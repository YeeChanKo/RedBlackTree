package rbtree;

public class RBTree {
	Node NIL = new Node(Integer.MIN_VALUE, "black", null, null, null);
	Node ROOT = NIL;

	public void insert(Node newNode) {
		Node parentNode = NIL;
		Node currentNode = ROOT;

		while (currentNode != NIL) {
			parentNode = currentNode;

			if (newNode.key < currentNode.key)
				currentNode = currentNode.left;
			else
				currentNode = currentNode.right;
		} // 이 부분은 일반적인 binary tree의 insert, 찾아가는 부분

		newNode.parent = parentNode;
		if (parentNode == NIL) // 트리에 아무것도 없을 때
			ROOT = newNode;
		else if (newNode.key < parentNode.key)
			parentNode.left = newNode;
		else
			parentNode.right = newNode;

		newNode.left = NIL;
		newNode.right = NIL;
		newNode.color = "red"; // 새로 삽입한 node는 red로 초기화
		insertFixup(newNode); // newNode 삽입으로 생긴 violation 해결
	}

	public void insertFixup(Node fixupNode) {
		// parentNode와 fixupNode의 색깔이 모두 red라서 violation이 일어날 때
		while (fixupNode.parent.color == "red") {

			// parent가 왼쪽 child일 때
			if (fixupNode.parent == fixupNode.parent.parent.left) {
				Node uncle = fixupNode.parent.parent.right;

				// uncle 색깔이 red일 때 - case 1
				if (uncle.color == "red") {
					fixupNode.parent.parent.color = "red";
					fixupNode.parent.color = "black";
					uncle.color = "black";
					fixupNode = fixupNode.parent.parent;
				}
				// uncle 색깔이 black일 때 - case 2 & case 3
				else {
					// fixupNode가 right child일 때, left child인 케이스로 변환시켜줌
					// case 3 -> case 2
					if (fixupNode == fixupNode.parent.right) {
						fixupNode = fixupNode.parent;
						leftRotate(fixupNode);
					}

					// case 2
					fixupNode.parent.color = "black";
					fixupNode.parent.parent.color = "red";
					rightRotate(fixupNode.parent.parent);
				}
			}

			// parent가 오른쪽 child일 때
			else {
				Node uncle = fixupNode.parent.parent.left;

				// uncle 색깔이 red일 때 - case 4
				if (uncle.color == "red") {
					fixupNode.parent.parent.color = "red";
					fixupNode.parent.color = "black";
					uncle.color = "black";
					fixupNode = fixupNode.parent.parent;
				}
				// uncle 색깔이 black일 때 - case 4 & case 5
				else {
					// fixupNode가 left child일 때, right child인 케이스로 변환시켜줌
					// case 5 -> case 3
					if (fixupNode == fixupNode.parent.left) {
						fixupNode = fixupNode.parent;
						rightRotate(fixupNode);
					}

					// case 3
					fixupNode.parent.color = "black";
					fixupNode.parent.parent.color = "red";
					leftRotate(fixupNode.parent.parent);
				}
			}
		}

		ROOT.color = "black";
	}

	public void leftRotate(Node x) {
		Node y = x.right;

		x.right = y.left;
		// y.left에 노드가 있으면
		if (y.left != NIL)
			y.left.parent = x;

		y.parent = x.parent;
		// x가 root인 경우
		if (x.parent == NIL)
			ROOT = y;
		// x가 parent의 left child인 경우
		else if (x == x.parent.left)
			x.parent.left = y;
		// x가 parent의 right child인 경우
		else
			x.parent.right = y;

		y.left = x;
		x.parent = y;
	}

	public void rightRotate(Node x) {
		Node y = x.left;

		x.left = y.right;
		// y.right에 노드가 있으면
		if (y.right != NIL)
			y.right.parent = x;

		y.parent = x.parent;
		// x가 root인 경우
		if (x.parent == NIL)
			ROOT = y;
		// x가 parent의 left child인 경우
		else if (x == x.parent.left)
			x.parent.left = y;
		// x가 parent의 right child인 경우
		else
			x.parent.right = y;

		y.right = x;
		x.parent = y;
	}

	public void delete(Node targetNode) {
		String erasedColor = targetNode.color; // 삭제되는 노드의 색깔을 저장해둠
		Node fixupNode;

		// 왼쪽 노드가 비어있을 때 - 삭제되는 노드의 child 개수가 1개
		if (targetNode.left == NIL) {
			fixupNode = targetNode.right;
			transplant(targetNode, targetNode.right);
		}
		// 오른쪽 노드가 비어있을 때 - 삭제되는 노드의 child 개수가 1개
		else if (targetNode.right == NIL) {
			fixupNode = targetNode.left;
			transplant(targetNode, targetNode.left);
		}
		// 삭제되는 노드의 child 개수가 2개
		else {
			// right subtree에서 가장 왼쪽에 있는 노드를 successor로 지정한다
			// 가장 왼쪽에 있는 노드이기 때문에 successor는 left child가 없다!
			Node successor = getMinimumNode(targetNode.right);

			// successor를 삭제 노드 색깔로 변경한 후 삭제 노드 자리에 이식한다
			erasedColor = successor.color; // 사라질 successor의 색깔을 erased color로 넣어줌
			successor.color = targetNode.color;

			fixupNode = successor.right; // successor에 right child가 없으면 fixupNode는 NIL

			// (getMinimumNode를 통해 구한) successor가 targetNode.right일 경우
			if (successor.parent == targetNode)
				fixupNode.parent = successor; // fixupNode가 NIL일 수도 있으므로 parent를 successor로 지정해줌
												// 나중에 deleteFixup에서 쓰인다

			// (getMinimumNode를 통해 구한) successor가 targetNode.right가 아닐 경우
			// targetNode.right는 left child가 있다는 의미
			else {
				transplant(successor, successor.right);
				// successor의 right child를 successor와 바꿔치기 해준다
				// successor는 left child가 없기 때문에 이 부분은 고려하지 않아도 된다

				// 따로 떼어낸 successor의 child를 설정
				successor.right = targetNode.right;
				successor.right.parent = successor;
			}

			// targetNode를 successor로 바꿔치기 해주고 successor의 left를 targetNode의 left로 지정해준다
			transplant(targetNode, successor);
			successor.left = targetNode.left;
			successor.left.parent = successor;
		}

		// black 노드가 삭제되었을 경우, "동일 black node 개수 property"에 위배되므로 처리해준다
		// black 노드가 삭제되었으므로, black height를 1 증가시켜줘야 한다
		if (erasedColor == "black")
			deleteFixup(fixupNode);
	}

	public void deleteFixup(Node fixupNode) {
		// 삭제된 node의 색깔이 black일 때, 삭제된 해당 node의 위치에 fixupNode로 들어간다
		// 이 경우 fixupNode가 black이면 처리를 해줘야 한다
		// fixupNode가 ROOT이거나 색깔이 black이 아닌 red이면 색깔만 바꿔주고 끝내면 된다
		while (fixupNode != ROOT && fixupNode.color == "black") {
			// fixupNode가 left child일 경우
			if (fixupNode == fixupNode.parent.left) {
				Node sibling = fixupNode.parent.right;

				// case 5
				if (sibling.color == "red") {
					sibling.color = "black";
					fixupNode.parent.color = "red";
					leftRotate(fixupNode.parent);
					sibling = fixupNode.parent.right;
				}

				// case 1
				if (sibling.left.color == "black" && sibling.right.color == "black") {
					sibling.color = "red";
					fixupNode = fixupNode.parent;
				}
				// case 2 & case 3
				else {
					// case 3일 경우, case 3를 case 2로 변환시켜줌
					if (sibling.right.color == "black") {
						sibling.left.color = "black";
						sibling.color = "red";
						rightRotate(sibling);
						sibling = fixupNode.parent.right;
					}

					// case 2
					sibling.color = fixupNode.parent.color;
					fixupNode.parent.color = "black";
					sibling.right.color = "black";
					leftRotate(fixupNode.parent);
					fixupNode = ROOT; // fixupNode가 root에 도달하어 종료된다
				}
			}
			// fixupNode가 right child일 경우
			else {
				Node sibling = fixupNode.parent.left;

				// case 24
				if (sibling.color == "red") {
					sibling.color = "black";
					fixupNode.parent.color = "red";
					rightRotate(fixupNode.parent);
					sibling = fixupNode.parent.left;
				}

				// case 17
				if (sibling.left.color == "black" && sibling.right.color == "black") {
					sibling.color = "red";
					fixupNode = fixupNode.parent;
				}
				// case 18 & case 19
				else {
					// case 19일 경우, case 18로 변환시켜줌
					if (sibling.left.color == "black") {
						sibling.right.color = "black";
						sibling.color = "red";
						leftRotate(sibling);
						sibling = fixupNode.parent.left;
					}

					// case 18
					sibling.color = fixupNode.parent.color;
					fixupNode.parent.color = "black";
					sibling.left.color = "black";
					rightRotate(fixupNode.parent);
					fixupNode = ROOT; // fixupNode가 root에 도달하어 종료된다
				}
			}
		}

		fixupNode.color = "black";
	}

	public Node getMinimumNode(Node n) {
		while (n.left != NIL) {
			n = n.left;
		}

		return n;
	}

	// 이 transplant는 oldNode의 부모와의 관계만에 대해서 다룰 뿐
	// oldNode의 자식들에 대해서 다루지는 않는다
	public void transplant(Node oldNode, Node plantNode) {
		// oldNode가 root일 경우
		if (oldNode.parent == NIL)
			ROOT = plantNode;
		// oldNode가 left child일 경우
		else if (oldNode == oldNode.parent.left)
			oldNode.parent.left = plantNode;
		// oldNode가 right child일 경우
		else
			oldNode.parent.right = plantNode;

		// 공통 부분
		plantNode.parent = oldNode.parent;
	}

	public void printSubTree(Node n, int depth) {
		if (n == NIL) {
			System.out.println("the tree is empty!");
			return;
		}

		if (n.left != NIL)
			printSubTree(n.left, depth + 1);

		for (int i = 0; i < depth; i++)
			System.out.print("   ");
		System.out.println(n.key + ", " + n.color);

		if (n.right != NIL)
			printSubTree(n.right, depth + 1);
	}

	public int CheckBlackHeight(Node n) {
		if (n == NIL)
			return 0; // 종료 조건

		int leftNum = CheckBlackHeight(n.left); // 왼쪽 자식 블랙수
		if (leftNum == -1)
			return -1; // 에러

		int rightNum = CheckBlackHeight(n.right);
		if (rightNum == -1)
			return -1; // 에러

		if (n.left == NIL || n.left.color == "black")
			leftNum += 1; // 검정색 수 증가

		if (n.right == NIL || n.right.color == "black")
			rightNum += 1; // 검정색 수 증가

		if (leftNum != rightNum)
			return -1; // 에러
		else
			return leftNum; // 제대로 되었을 때 최종으로 보냄 ( 왼쪽이든 오른쪽이든 상관없음)
	}
}