package player;
import list.*;

public class Chip {
	//Static Final Fields for Players, Black is 0, White is 1
	static final int BLACK = 0;
	static final int WHITE = 1;
	private int x, y; //the (x,y) position of the Chip
	private int color; //the color of the chip
	Board board; //each chip knows which board it is in.

	Chip(int xLoc, int yLoc, int c, Board b){
		x = xLoc;
		y = yLoc;
		color = c;
		board = b;
	}

	/**
	 * Gets the neighbors of a specific chip
	 * @return A Dlist of all the chips that are neighbors of the chip
	 */
	DList getNeighbors(){
		return board.getNeighbors(x, y, color);
	}

	/**
	 * Returns whether a chip is in valid bounds of the board.
	 * This does NOT return whether it is a legal move or not.
	 * 
	 * @return Whether a chip is in bounds or not
	 */
	boolean inBounds(){
		if(x > 7 || x < 0 || y > 7 || y < 0){
			return false;
		}else if((x == 0 && y == 0) || (x == 7 && y == 0) || (x == 0 && y == 7) || (x == 7 && y == 7)){
			return false;
		}
		return true;

	}

	/**
	 * Tests if two chips are equal to each other
	 * Assumes that they are both in the same board.
	 * 
	 * @param c The other chip that is being compared to the origian chip.
	 * @return Whether a chip is equal to another chip. If c is null, it returns false as well.
	 */
	boolean equals(Chip c){
		if (c==null || x!=c.x || y!=c.y || color!=c.color){ //short circuit logic to prevent null pointer exception
			return false;
		}else{
			return true;
		}
	}

	/**
	 *  toString() returns a String representation of this Chip.
	 *  
	 *  @return a String representation of this Chip.
	 */
	public String toString(){
		String s;
		s = "("+this.x+","+this.y+")"+" "+this.color;
		return s;
	}

	/**
	 * @return the color of the "this" chip.
	 */
	public int color(){
		return this.color;
	}

	/**
	 * 
	 * @return the x position of "this" chip
	 */
	public int xPos(){
		return this.x;
	}

	/**
	 * 
	 * @return the y position of "this" chip
	 */
	public int yPos(){
		return this.y;
	}
}
