/* DList.java */
package list;
/**
 *  A DList is a mutable doubly-linked list ADT.  Its implementation is
 *  circularly-linked and employs a sentinel node at the head of the list.
 *
 **/

public class DList{

	DListNode head;
	int size;

	/* DList invariants:
	 *  1)  head != null.
	 *  2)  For every DListNode x in a DList, x.next != null.
	 *  3)  For every DListNode x in a DList, x.prev != null.
	 *  4)  For every DListNode x in a DList, if x.next == y, then y.prev == x.
	 *  5)  For every DListNode x in a DList, if x.prev == y, then y.next == x.
	 *  6)  For every DList l, l.head.myList = null.  (Note that l.head is the
	 *      sentinel.)
	 *  7)  For every DListNode x in a DList l EXCEPT l.head (the sentinel),
	 *      x.myList = l.
	 *  8)  size is the number of DListNodes, NOT COUNTING the sentinel,
	 *      that can be accessed from the sentinel (head) by a sequence of
	 *      "next" references.
	 **/

	/**
	 *  newNode() calls the DListNode constructor.  Use this method to allocate
	 *  new DListNodes rather than calling the DListNode constructor directly.
	 *  That way, only this method need be overridden if a subclass of DList
	 *  wants to use a different kind of node.
	 *
	 *  @param item the item to store in the node.
	 *  @param list the list that owns this node.  (null for sentinels.)
	 *  @param prev the node previous to this node.
	 *  @param next the node following this node.
	 **/
	private DListNode newNode(Object item, DList list,
			DListNode prev, DListNode next) {
		return new DListNode(item, list, prev, next);
	}

	/**
	 *  isEmpty() returns true if this List is empty, false otherwise.
	 *  @return true if this List is empty, false otherwise
	 **/

	/** 
	 *  length() returns the length of this List. 
	 *  @return the length of this List.
	 **/
	public boolean isEmpty() { 
		return size == 0;
	}

	public int length() { 
		return size; 
	}


	/**
	 *  DList() constructs for an empty DList.
	 **/
	public DList() {
		// Your solution here.  Similar to Homework 4, but now you need to specify
		//   the `list' field (second parameter) as well.
		head = new DListNode(null, null, head, head);
		size = 0;
		head.next = head;
		head.prev = head;
	}

	/**
	 *  insertFront() inserts an item at the front of this DList.
	 *  @param item is the item to be inserted.
	 **/
	public void insertFront(Object item) {
		// Your solution here.  Similar to Homework 4, but now you need to specify
		//   the `list' field (second parameter) as well.
		if(head.next == null){
			head.next = newNode(item, this, head, head);
			head.prev = head.next;
		}
		else{
			head.next = newNode(item, this, head, head.next);
			head.next.next.prev = head.next;
		}
		size++;
	}

	/**
	 *  insertBack() inserts an item at the back of this DList.
	 *  @param item is the item to be inserted.
	 **/
	public void insertBack(Object item) {
		// Your solution here.  Similar to Homework 4, but now you need to specify
		//   the `list' field (second parameter) as well.
		if(head.next == null){
			head.next = newNode(item, this, head, head);
			head.prev = head.next;
		}
		else{
			head.prev = newNode(item, this, head.prev, head);
			head.prev.prev.next = head.prev;
		}
		size++;
	}

	/**
	 *  front() returns the node at the front of this DList.  If the DList is
	 *  empty, return an "invalid" node--a node with the property that any
	 *  attempt to use it will cause an exception.  (The sentinel is "invalid".)
	 *  @return a ListNode at the front of this DList.
	 */
	public DListNode front() {
		return head.next;
	}

	/**
	 *  back() returns the node at the back of this DList.  If the DList is
	 *  empty, return an "invalid" node--a node with the property that any
	 *  attempt to use it will cause an exception.  (The sentinel is "invalid".)
	 *  @return a ListNode at the back of this DList.
	 */
	public DListNode back() {
		return head.prev;
	}

	/**
	 *  toString() returns a String representation of this DList.
	 *  @return a String representation of this DList.
	 */
	public String toString() {
		String result = "[  ";
		DListNode current = head.next;
		while (current != head) {
			try {
				result = result + current.item() + "  ";
				current = current.next;
			} catch (InvalidNodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result + "]";
	}
}
