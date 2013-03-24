/* InvalidNodeException.java */
package list;
/**
 *  Implements an Exception that signals an attempt to use an invalid ListNode.
 */
public class InvalidNodeException extends Exception {

	public InvalidNodeException() {
		super();
	}
	public InvalidNodeException(String s) {
		super(s);
	}
}
