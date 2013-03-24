/* HashTableChained.java */

package dict;
import java.math.*;

/**
 *  HashTableChained implements a Dictionary as a hash table with chaining.
 *  All objects used as keys must have a valid hashCode() method, which is
 *  used to determine which bucket of the hash table an entry is stored in.
 *  Each object's hashCode() is presumed to return an int between
 *  Integer.MIN_VALUE and Integer.MAX_VALUE.  The HashTableChained class
 *  implements only the compression function, which maps the hash code to
 *  a bucket in the table's range.
 *
 *  DO NOT CHANGE ANY PROTOTYPES IN THIS FILE.
 **/

public class HashTableChained implements Dictionary {

	/**
	 *  Place any data fields here.
	 **/
	public EntryNode[] platte;
	public final static double LOADFACTOR = .75;

	/** 
	 *  Construct a new empty hash table intended to hold roughly sizeEstimate
	 *  entries.  (The precise number of buckets is up to you, but we recommend
	 *  you use a prime number, and shoot for a load factor between 0.5 and 1.)
	 **/

	public HashTableChained(int sizeEstimate) {
		// Your solution here.
		double N = sizeEstimate/LOADFACTOR;
		N = Math.floor(N);
		int modulus = this.nearestPrime((int) N);
		platte = new EntryNode[modulus]; //change this to make sure N is a prime number
	}

	/** 
	 *  Construct a new empty hash table with a default size.  Say, a prime in
	 *  the neighborhood of 100.
	 **/

	public HashTableChained() {
		// Your solution here.
		platte = new EntryNode[97];
	}

	/**
	 *  Converts a hash code in the range Integer.MIN_VALUE...Integer.MAX_VALUE
	 *  to a value in the range 0...(size of hash table) - 1.
	 *
	 *  This function should have package protection (so we can test it), and
	 *  should be used by insert, find, and remove.
	 **/

	int compFunction(int code) {
		// Replace the following line with your solution.
		int N = this.platte.length;
		return Math.abs(code)%N;
	}
	
	public int indexOf(Object h){
		return compFunction(h.hashCode());
	}
	
	public int tableLength(){
		return platte.length;
	}

	/** 
	 *  Returns the number of entries stored in the dictionary.  Entries with
	 *  the same key (or even the same key and value) each still count as
	 *  a separate entry.
	 *  @return number of entries in the dictionary.
	 **/

	public int size() {
		// Replace the following line with your solution.
		int i = 0, j = 0; //is the index of the list, j is the number of entries.
		for (i = 0;i<platte.length;i++){
			EntryNode temp = platte[i];
			while (temp!=null){
				j++;
				temp = temp.next;
			}
		}
		return j;
	}

	/** 
	 *  Tests if the dictionary is empty.
	 *
	 *  @return true if the dictionary has no entries; false otherwise.
	 **/

	public boolean isEmpty() {
		// Replace the following line with your solution.
		return this.size()==0;
	}

	/**
	 *  Create a new Entry object referencing the input key and associated value,
	 *  and insert the entry into the dictionary.  Return a reference to the new
	 *  entry.  Multiple entries with the same key (or even the same key and
	 *  value) can coexist in the dictionary.
	 *
	 *  This method should run in O(1) time if the number of collisions is small.
	 *
	 *  @param key the key by which the entry can be retrieved.
	 *  @param value an arbitrary object.
	 *  @return an entry containing the key and value.
	 **/

	public Entry insert(Object key, int value) {
		// Replace the following line with your solution.
		int hash = this.compFunction(key.hashCode());
		EntryNode temp = platte[hash];
		if (temp==null){
			platte[hash] = new EntryNode(key,value,null);
			return platte[hash];
		}else{
			while (temp.next!=null){
				temp = temp.next;
			}
			temp.next = new EntryNode(key,value,null);
			return temp.next;
		}
	}

	/** 
	 *  Search for an entry with the specified key.  If such an entry is found,
	 *  return it; otherwise return null.  If several entries have the specified
	 *  key, choose one arbitrarily and return it.
	 *
	 *  This method should run in O(1) time if the number of collisions is small.
	 *
	 *  @param key the search key.
	 *  @return an entry containing the key and an associated value, or null if
	 *          no entry contains the specified key.
	 **/

	public Entry find(Object key) {
		// Replace the following line with your solution.
		int hash = compFunction(key.hashCode());
		EntryNode temp = platte[hash];
		if (temp==null){
			return null;
		}else{
			while (temp!=null){
				if (temp.key().equals(key)){
					return temp;
				}else{
					temp = temp.next;
				}
			}
		}
		return null; ///this case should not be reached.
	}

	/** 
	 *  Remove an entry with the specified key.  If such an entry is found,
	 *  remove it from the table and return it; otherwise return null.
	 *  If several entries have the specified key, choose one arbitrarily, then
	 *  remove and return it.
	 *
	 *  This method should run in O(1) time if the number of collisions is small.
	 *
	 *  @param key the search key.
	 *  @return an entry containing the key and an associated value, or null if
	 *          no entry contains the specified key.
	 */

	public Entry remove(Object key) {
		// Replace the following line with your solution.
		int hash = compFunction(key.hashCode());
		EntryNode temp = platte[hash];
		if (temp==null){
			return null;
		}else if(temp.key().equals(key)){
			platte[hash] = platte[hash].next;
			return temp;
		}else{
			EntryNode beta = temp.next;
			while (beta!=null){
				if (beta.key().equals(key)){
					temp.next = beta.next;
					return beta;
				}else{
					temp = temp.next;
					beta = beta.next; //likely spot to cause null pointer exception
				}
			}
		}
		return null; ///this case should not be reached.
	}

	/**
	 *  Remove all entries from the dictionary.
	 */
	public void makeEmpty() {
		// Your solution here.
		for (int i = 0;i<platte.length;i++){
			platte[i] = null;
		}
	}

	public int numberCollisions(){
		int collisions = 0;
		int i = 0;
		for (i=0;i<platte.length;i++){
			EntryNode temp = platte[i];
			while (temp!=null){
				if (temp.next!=null){
					collisions++;
					temp = temp.next;
				}else{
					temp = null;
				}
			}
		}
		return collisions;
	}

	//This method is used by the constructor to determine a good number of buckets which are prime.
	public int nearestPrime(int n) {
		boolean[] prime = new boolean[n + 1];
		int i;
		for (i = 2; i <= n; i++) {
			prime[i] = true;
		}
		for (int divisor = 2; divisor * divisor <= n; divisor++) {
			if (prime[divisor]) {
				for (i = 2 * divisor; i <= n; i = i + divisor) {
					prime[i] = false;
				}
			}
		}
		for (int j = n;j>1;j--){
			if (prime[j]==true){
				return j;
			}
		}
		return 597;
	}
	
	public String toString(){
		String s = "[ ";
		int i = 0;
		EntryNode temp;
		for (i=0;i<platte.length;i++){
			temp = platte[i];
			if (temp==null){
				s = s+"( )";
			}else{
				s = s+"( ";
				while (temp!=null){
					s = s+temp+" ";
					temp = temp.next;
				}
				s = s+")";
			}
		}
		return s+" ]";
	}
}
