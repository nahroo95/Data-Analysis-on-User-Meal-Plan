/**
 * Filename:   BPTree.java
 * Project:    Milestone3
 * Authors:    D-team 85 
 *             Sukyoung Cho, Nahroo Yun, Yeeun Lim, Yongsang Park
 *
 * Semester:   Fall 2018
 * Course:     CS400
 *
 * Due Date:   December 12th,2018
 * Version:    1.0
 *
 * Credits:    none
 *
 * Bugs:       no bugs
 */ 
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
		// if branching factor is less or equal to 2, throws an error.
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
		// insert from the root and assigns its position based on the given key
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
		if (key == null)
			return new ArrayList<V>();
		// search from the root
		return root.rangeSearch(key, comparator);
	}

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
			// super();
			// Internal node only stores the information of keys and its children
			this.keys = new ArrayList<K>();
			this.children = new ArrayList<Node>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			// returns the key of the first leaf
			return children.get(0).getFirstLeafKey();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			// if the node has more children than it can, splits
			return children.size() > branchingFactor;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
		 */
		void insert(K key, V value) {
			// insert to its child
			Node child = getChild(key);
			child.insert(key, value);
			if (child.isOverflow()) {
				// if overflows splits
				Node sibling = child.split();
				// create a linke between the parent and a new splited child
				insertChild(sibling.getFirstLeafKey(), sibling);
			}
			if (root.isOverflow()) {
				Node sibling = split();
				InternalNode newRoot = new InternalNode();
				// new root should be an internal node
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
			// calculate index based on the given key
			// and add child to that index
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
			// split in internal node creates a sibling as an internal node
			InternalNode sibling = new InternalNode();
			// split keys and children
			sibling.keys.addAll(keys.subList(from, to));
			sibling.children.addAll(children.subList(from, to + 1));
			// delete keys from the old node
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
			// range search on the internal node
			// keep going down to access to the value
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
			// the leaf node has both keys and values
			keys = new ArrayList<K>();
			values = new ArrayList<V>();
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#getFirstLeafKey()
		 */
		K getFirstLeafKey() {
			// returns the key of the first leaf node
			return keys.get(0);
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#isOverflow()
		 */
		boolean isOverflow() {
			// check if the node is overflow
			return values.size() > branchingFactor - 1;
		}

		/**
		 * (non-Javadoc)
		 * 
		 * @see BPTree.Node#insert(Comparable, Object)
		 */
		void insert(K key, V value) {
			// The id of a food item is unique, while the name can be a duplicate of some
			// other food's name
			int index = Collections.binarySearch(keys, key);
			int valueIndex = index >= 0 ? index+1 : -index - 1;

			// add both key and value
			keys.add(valueIndex, key);
			values.add(valueIndex, value);

			//if the node was the root
			if (root.isOverflow()) {
				Node sibling = split();
				// new root should be an internal node
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
			// sibling is another leaf node
			LeafNode sibling = new LeafNode();
			int from = (keys.size() + 1) / 2, to = keys.size();
			sibling.keys.addAll(keys.subList(from, to));
			sibling.values.addAll(values.subList(from, to));

			keys.subList(from, to).clear();
			values.subList(from, to).clear();
			
			// creates a link between the leaf node
			// This split methods creates a right sibling (next), so the old
			// leaf node should have a link referring to the new created sibling
			// and the new created sibling should have a link to the old node (previous)
			if (this.next != null) {
				// if it already have a right sibling,
				// insert the splited sibling in between
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
			// Since the binary search does not guarantee which index of the duplicate keys
			// it would return, we should check both right sibling and left sibling
			// once we get to the leaf. And break the loop if we find the left-most or
			// right-most key in order to maintain the runnig time of O(logm(N))
			if (comparator.equals(">=")) {
				LeafNode node = this;
				LeafNode previousNode = node.previous;
				LeafNode nextNode = node;
				// and check if there are values with duplicate keys on the left side
				while (previousNode != null) {
					for (int i = previousNode.keys.size() - 1; i >= 0; i--) {
						if (previousNode.keys.get(i).compareTo(key) >= 0) {
							result.add(0, previousNode.values.get(i));
						} else {
							break;
						}
					}
					previousNode = previousNode.previous;
				}
				// add all value on the right side as they are greater
				while (nextNode != null) {
					for (int i = 0; i < nextNode.keys.size(); i++) {
						if (nextNode.keys.get(i).compareTo(key) == 0) {
							result.add(0, nextNode.values.get(i));
						}
						// if it is greater add on the right side to maintain the order
						if (nextNode.keys.get(i).compareTo(key) > 0) {
							result.add(result.size(), nextNode.values.get(i));
						}
					}
					nextNode = nextNode.next;
				}
			} else if (comparator.equals("==")) {
				// if equals check both the right side and left side and only add same values
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
					for (int i = node.keys.size() - 1; i >= 0; i--) {
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

				// add all values of the previous node
				while (prev != null) {
					result.addAll(prev.values);
					prev = prev.previous;
				}

				while (node != null) {
					// check values of that node from the right-most value
					// as the values are sorted in order, if we check from the
					// left-most child, it would break the loop although there
					// is a greater value at the right-most
					for (int i = 0; i < node.keys.size(); i++) {
						if (node.keys.get(i).compareTo(key) <= 0) {
							result.add(0, node.values.get(i));
						} else {
							break;
						}
					}
					node = node.next;
				}

				// Reverse the order of the result to return in ascending order
				List<V> temp = new ArrayList<V>();
				// index of temp list
				for (int i = result.size() - 1; i >= 0; i--) {
					// insert elements from the last index
					temp.add(temp.size(), result.get(i));
				}
				result = temp;

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
		List<Double> filteredValues = bpTree.rangeSearch(0.5d, ">=");
		System.out.println("Filtered values: " + filteredValues.toString());
		
	}

} // End of class BPTree
