package dict;

public class EntryNode extends Entry{
	
	public EntryNode next;
	
	public EntryNode(Object keyNumber,int codeNumber, EntryNode alpha){
		key = keyNumber;
		value = codeNumber;
	    next = alpha;
	}
	
	public String toString(){
		String s = "<"+key+","+value+">";
		return s;
	}
}
