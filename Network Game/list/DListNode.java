/* DListNode.java */

//package hw5.list;
package list;

/**
 *  A DListNode is a mutable node in a DList (doubly-linked list).
 **/

public class DListNode{

	private Object item;
	private DList myList;

	/**
	 *  (inherited)  item references the item stored in the current node.
	 *  (inherited)  myList references the List that contains this node.
	 *  prev references the previous node in the DList.
	 *  next references the next node in the DList.
	 *
	 *  DO NOT CHANGE THE FOLLOWING FIELD DECLARATIONS.
	 **/
	DListNode prev;
	DListNode next;

	/**
	 *  DListNode() constructor.
	 *  @param i the item to store in the node.
	 *  @param l the list this node is in.
	 *  @param p the node previous to this node.
	 *  @param n the node following this node.
	 */
	DListNode(Object i, DList l, DListNode p, DListNode n) {
		item = i;
		myList = l;
		prev = p;
		next = n;
	}

	/**
	 *  isValidNode returns true if this node is valid; false otherwise.
	 *  By default, an invalid node is one that doesn't belong to a list (myList
	 *  is null), but subclasses can override this definition.
	 *  @return true if this node is valid; false otherwise.
	 */
	public boolean isValidNode() {
		return myList != null;
	}

	/**
	 *  item() returns this node's item.  If this node is invalid,
	 *  throws an exception.
	 *  @return the item stored in this node.
	 */
	
	public Object item() throws InvalidNodeException {
		if (!isValidNode()) {
			throw new InvalidNodeException();
		}
		return item;
	}

	/**
	 *  setItem() sets this node's item to "item".  If this node is invalid,
	 *  throws an exception.
	 *
	 *  Performance:  runs in O(1) time.
	 */
	public void setItem(Object item) throws InvalidNodeException {
		if (!isValidNode()) {
			throw new InvalidNodeException();
		}
		this.item = item;
	}
	
	/**
	 *  next() returns the node following this node.  If this node is invalid,
	 *  throws an exception.
	 *  @return the node following this node.
	 *  @exception InvalidNodeException if this node is not valid.
	 */
	public DListNode next() throws InvalidNodeException {
		if (!isValidNode()) {
			throw new InvalidNodeException("next() called on invalid node");
		}
		return next;
	}

	/**
	 *  prev() returns the node preceding this node.  If this node is invalid,
	 *  throws an exception.
	 *  @param node the node whose predecessor is sought.
	 *  @return the node preceding this node.
	 *  @exception InvalidNodeException if this node is not valid.
	 */
	public DListNode prev() throws InvalidNodeException {
		if (!isValidNode()) {
			throw new InvalidNodeException("prev() called on invalid node");
		}
		return prev;
	}

	/**
	 *  insertAfter() inserts an item immediately following this node.  If this
	 *  node is invalid, throws an exception.
	 *  @param item the item to be inserted.
	 *  @exception InvalidNodeException if this node is not valid.
	 */
	public void insertAfter(Object item) throws InvalidNodeException {
		if (!isValidNode()) {
			throw new InvalidNodeException("insertAfter() called on invalid node");
		}
		// Your solution here.  Will look something like your Homework 4 solution,
		//   but changes are necessary.  For instance, there is no need to check if
		//   "this" is null.  Remember that this node's "myList" field tells you
		//   what DList it's in.  You should use myList.newNode() to create the
		//   new node.
		DListNode n = new DListNode(item, (DList)this.myList, this, this.next);
		this.next = n;
		this.next.next.prev = this.next;
		this.myList.size++;
	}

	/**
	 *  insertBefore() inserts an item immediately preceding this node.  If this
	 *  node is invalid, throws an exception.
	 *
	 *  @param item the item to be inserted.
	 *  @exception InvalidNodeException if this node is not valid.
	 *
	 *  Performance:  runs in O(1) time.
	 */
	public void insertBefore(Object item) throws InvalidNodeException {
		if (!isValidNode()) {
			throw new InvalidNodeException("insertBefore() called on invalid node");
		}
		// Your solution here.  Will look something like your Homework 4 solution,
		//   but changes are necessary.  For instance, there is no need to check if
		//   "this" is null.  Remember that this node's "myList" field tells you
		//   what DList it's in.  You should use myList.newNode() to create the
		//   new node.
		DListNode n = new DListNode(item, (DList)this.myList, this.prev, this);
		this.prev = n;
		this.prev.prev.next = this.prev;
		this.myList.size++;
	}

	/**
	 *  remove() removes this node from its DList.  If this node is invalid,
	 *  throws an exception.
	 *  @exception InvalidNodeException if this node is not valid.
	 */
	public void remove() throws InvalidNodeException {
		if (!isValidNode()) {
			throw new InvalidNodeException("remove() called on invalid node");
		}
		// Your solution here.  Will look something like your Homework 4 solution,
		//   but changes are necessary.  For instance, there is no need to check if
		//   "this" is null.  Remember that this node's "myList" field tells you
		//   what DList it's in.
		this.prev.next = this.next;
		this.next.prev = this.prev;
		myList.size--;



		// Make this node an invalid node, so it cannot be used to corrupt myList.
		myList = null;
		// Set other references to null to improve garbage collection.
		next = null;
		prev = null;
	}
}
