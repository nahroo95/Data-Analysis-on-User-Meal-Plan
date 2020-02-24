package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to many different
 * indexes of a large data set. BPTree objects are created for each type of
 * index needed by the program. BPTrees provide an efficient range search as
 * compared to other types of data structures due to the ability to perform
 * log_m N lookups and linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K>
 *            key - expect a string that is the type of id for each item
 * @param <V>
 *            value - expect a user-defined type that stores all data for a food
 *            item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

	// Root of the tree
	private Node root;

	// Branching factor is the number of children nodes
	// for internal nodes of the tree
	private int branchingFactor;

	/**
	 * Public constructor
	 * 
	 * @param branchingFactor
	 */
	public BPTree(int branchingFactor) {
		if (branchingFactor <= 2) {
			throw new IllegalArgumentException("Illegal branching factor: " + branchingFactor);
		}
		this.root = new LeafNode();
		this.branchingFactor = branchingFactor;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void insert(K key, V value) {
		// TODO : Complete
		root.insert(key, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
	 */
	@Override
	public List<V> rangeSearch(K key, String comparator) {
		if (!comparator.contentEquals(">=") && !comparator.contentEquals("==") && !comparator.contentEquals("<="))
			return new ArrayList<V>();
		// TODO : Complete
		return root.rangeSearch(key, comparator);
	}

	// public List<V> searchRange(K key1, RangePolicy policy1, K key2,
	// RangePolicy policy2) {
	// return root.getRange(key1, policy1, key2, policy2);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Queue<List<Node>> queue = new LinkedList<List<Node>>();
		queue.add(Arrays.asList(root));
		StringBuilder sb = new StringBuilder();
		while (!queue.isEmpty()) {
			Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
			while (!queue.isEmpty()) {
				List<Node> nodes = queue.remove();
				sb.append('{');
				Iterator<Node> it = nodes.iterator();
				while (it.hasNext()) {
					Node node = it.next();
					sb.append(node.toString());
					if (it.hasNext())
						sb.append(", ");
					if (node instanceof BPTree.InternalNode)
						nextQueue.add(((InternalNode) node).children);
				}
				sb.append('}');
				if (!queue.isEmpty())
					sb.append(", ");
				else {
					sb.append('\n');
				}
			}
			queue = nextQueue;
		}
		return sb.toString();
	}

	/**
	 * This abstract class represents any type of node in the tree This class is a
	 * super class of the LeafNode and InternalNode types.
	 * 
	 * @author sapan
	 */
	private abstract class Node {

		// List of keys
		List<K> keys;

		/**
		 * Package constructor
		 */
		Node() {
			this.keys = new ArrayList<K>();
		}

		/**
		 * Inserts key and value in the appropriate leaf node and balances the tree if
		 * required by splitting
		 * 
		 * @param key
		 * @param value
		 */
		abstract void insert(K key, V value);

		/**
		 * Gets the first leaf key of the tree
		 * 
		 * @return key
		 */
		abstract K getFirstLeafKey();

		/**
		 * Gets the new sibling created after splitting the node
		 * 
		 * @return Node
		 */
		abstract Node split();

		/*
		 * (non-Javadoc)
		 * 
		 * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
		 */
		abstract List<V> rangeSearch(K key, String comparator);

		/**
		 * 
		 * @return boolean
		 */
		abstract boolean isOverflow();

		public String toString() {
			return keys.toString();
		}

	} // End of abstract class Node

	/**
	 * This class represents an internal node of the tree. This class is a concrete
	 * sub class of the abstract Node class and provides implementation of the
	 * operations required for internal (non-leaf) nodes.
	 * 
	 * @author sapan
	 */
	private class InternalNode extends Node {

		// List of children nodes
		List<Node> children;

		/**
		 * Package constructor
		 */
		InternalNode() {

			// TODO : Complete
			// super();
			this.keys = new ArrayList<K>();
			this.children = new ArrayList<Node>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			// TODO : Complete
			// why do we need this?
			return children.get(0).getFirstLeafKey();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			// TODO : Complete
			return children.size() > branchingFactor;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
		 */
		void insert(K key, V value) {
			// TODO : Complete
			Node child = getChild(key);
			child.insert(key, value);
			if (child.isOverflow()) {
				Node sibling = child.split();
				insertChild(sibling.getFirstLeafKey(), sibling);
			}
			if (root.isOverflow()) {
				Node sibling = split();
				InternalNode newRoot = new InternalNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		/**
		 * Helper method to find where to insert a new node with given key and value
		 * fair
		 * 
		 * @param key
		 * @return Node
		 */
		Node getChild(K key) {
			// 'binarySearch' returns index of key in sorted list sorted in ascending order
			// Returns the index of the given key.
			// If key is not present, the it returns "(-(insertion point) - 1)".
			int index = Collections.binarySearch(keys, key);
			// ternary conditional operator
			// If index >= 0, then index = index + 1 ( which means the duplicate key is
			// already there)
			// Else, key was not there. Hence, index = -(-index - 1) - 1
			int childIndex = index >= 0 ? index + 1 : -index - 1;
			return children.get(childIndex);
		}

		/**
		 * Helper method for insert when the internal node needs to split.
		 * 
		 * @param key
		 * @param child
		 *            a Node
		 */
		void insertChild(K key, Node child) {
			int index = Collections.binarySearch(keys, key);
			int childIndex = index >= 0 ? index + 1 : -index - 1;

			keys.add(childIndex, key);
			children.add(childIndex + 1, child);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {

			int from = (keys.size()+1) / 2, to = keys.size();
			InternalNode sibling = new InternalNode();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.children.addAll(children.subList(from, to + 1));

			keys.subList(from - 1, to).clear();
			children.subList(from, to + 1).clear();

			return sibling;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			// TODO : Complete
			return getChild(key).rangeSearch(key, comparator);
		}

	} // End of class InternalNode

	/**
	 * This class represents a leaf node of the tree. This class is a concrete sub
	 * class of the abstract Node class and provides implementation of the
	 * operations that required for leaf nodes.
	 * 
	 * @author sapan
	 */
	private class LeafNode extends Node {

		// List of values
		List<V> values;

		// Reference to the next leaf node
		LeafNode next;

		// Reference to the previous leaf node
		LeafNode previous;

		/**
		 * Package constructor
		 */
		LeafNode() {
			super();
			// TODO : Complete
			keys = new ArrayList<K>();
			values = new ArrayList<V>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			// TODO : Complete
			return keys.get(0);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			// TODO : Complete
			return values.size() > branchingFactor - 1;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(Comparable, Object)
		 */
		void insert(K key, V value) {
			// TODO : Complete
			// The id of a food item is unique, while the name can be a duplicate of some
			// other food's name
			int index = Collections.binarySearch(keys, key);
			int valueIndex = index >= 0 ? index+1 : -index - 1;

			/* split 연산
			1. 쪼개는 갯수는 floor ((Fanout + 1) / 2)에 해당함. (변수 split_factor)
			2. 쪼개는 기준값은 entries[split_factor + 1].key에 해당함
			3. 좌, 우 노드의 생성 및 노드를 반으로 쪼개 좌, 우로 입력함
			4. 해당 노드가 루트였나?
			4.1. Yes
			4.1.1. 새 노드 할당(Non-leaf로 할당한다.)
			4.1.2. 새 노드로 루트로 변경함.
			4.1.3. 좌, 우 노드의 부모를 해당 노드로 변경해준다.
			4.2. No
			4.2.1. 해당 노드도 꽉찼나?
			4.2.1.1. Yes
			4.2.1.1.1. 역시 쪼개기 연산 수행
			4.2.1.2. No
			5. 아무튼 부모 노드에 split key를 넣고, 해당 엔트리의 좌우노드 포인터를 좌, 우 노드로 바꿔준다.

			*/
			keys.add(valueIndex, key);
			values.add(valueIndex, value);

			if (root.isOverflow()) {
				Node sibling = split();
				InternalNode newRoot = new InternalNode();
				newRoot.keys.add(sibling.getFirstLeafKey());
				newRoot.children.add(this);
				newRoot.children.add(sibling);
				root = newRoot;
			}
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#split()
		 */
		Node split() {
			// TODO : Complete
			
			LeafNode sibling = new LeafNode();
			int from = (keys.size() + 1) / 2, to = keys.size();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.values.addAll(values.subList(from, to));

			keys.subList(from, to).clear();
			values.subList(from, to).clear();
			
			if (this.next != null) {
				this.next.previous = sibling;
			}
			sibling.next = next;
			this.next = sibling;
			sibling.previous = this;
			return sibling;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#rangeSearch(Comparable, String)
		 */
		List<V> rangeSearch(K key, String comparator) {
			List<V> result = new ArrayList<V>();
			if (key == null) {
				return result;
			}
			
			// comparator.equals("<=")
		/*	if (comparator.equals("==") | comparator.equals(">=")) {
				LeafNode node = this;
				while (node != null) {
					Iterator<K> kIt = node.keys.iterator();
					Iterator<V> vIt = node.values.iterator();
					while (kIt.hasNext()) {
						K key1 = kIt.next();
						V value = vIt.next();
						if (comparator.equals("==")) {
							if (key1.equals(key)) {
								result.add(value);
							}
						}

						if (comparator.equals(">=")) {
							if (key1.compareTo(key) >= 0) {
								result.add(value);
							}
						}
					}
					node = node.next;
				}
			}*/
			if (comparator.equals(">=")) {
				LeafNode node = this;
				LeafNode nextNode = node.next;
				while (nextNode != null) {
					for (int i = 0; i < nextNode.keys.size(); i++) {
						if (nextNode.keys.get(i).compareTo(key) >= 0) {
							result.add(0, nextNode.values.get(i));
						}
					}
					nextNode = nextNode.next;
				}

				while (node != null) {
					for (int i = node.keys.size()-1; i >=0 ; i--) {
						if (node.keys.get(i).compareTo(key) >= 0) {
							result.add(0, node.values.get(i));
						} else {
							break;
						}
					}
					node = node.previous;
				}
			}
			else if (comparator.equals("==")) {
				LeafNode node = this;
				LeafNode nextNode = node.next;
				while (nextNode != null) {
					for (int i = 0; i < nextNode.keys.size(); i++) {
						if (nextNode.keys.get(i).compareTo(key) == 0) {
							result.add(0, nextNode.values.get(i));
						} else {
							break;
						}
					}
					nextNode = nextNode.next;
				}
				while (node != null) {
					for (int i = node.keys.size()-1; i >=0 ; i--) {
						if (node.keys.get(i).compareTo(key) == 0) {
							result.add(0, node.values.get(i));
						} else {
							break;
						}
					}
					node = node.previous;
				}
			}	
			
			else if (comparator.equals("<=")) {
				LeafNode node = this;
				LeafNode prev = node.previous;
				
				//add all values of the previous node
				while (prev!=null){
					result.addAll(prev.values);
					prev = prev.previous;
				}
				
				while (node != null) {
					
					for (int i =0; i<node.keys.size(); i++){
						if(node.keys.get(i).compareTo(key) <= 0){
							result.add(0 , node.values.get(i));
						} else{
							break;
						}
					}
					node = node.next;
				}
			}			
			return result;
		}

	} // End of class LeafNode
	


	/**
	 * Contains a basic test scenario for a BPTree instance. It shows a simple
	 * example of the use of this class and its related types.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create empty BPTree with branching factor of 3
		BPTree<Double, Double> bpTree = new BPTree<>(3);

		// create a pseudo random number generator
		Random rnd1 = new Random();

		// some value to add to the BPTree
		Double[] dd = { 0.0d, 0.5d, 0.2d, 0.8d };

		// build an ArrayList of those value and add to BPTree also
		// allows for comparing the contents of the ArrayList
		// against the contents and functionality of the BPTree
		// does not ensure BPTree is implemented correctly
		// just that it functions as a data structure with
		// insert, rangeSearch, and toString() working.
		List<Double> list = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			Double j = dd[rnd1.nextInt(4)];
			list.add(j);
			bpTree.insert(j, j);
			System.out.println("\n\nTree structure:\n" + bpTree.toString());
		}
		
		// System.out.println(list);
		List<Double> filteredValues = bpTree.rangeSearch(0.4d, ">=");
		System.out.println("Filtered values: " + filteredValues.toString());
		
	}

} // End of class BPTree
