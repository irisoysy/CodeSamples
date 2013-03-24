//Updated last on 1:16PM on 3/21/12
package player;
import list.*;

class Board {

	//Static Final Fields for Players, Black is 0, White is 1
	public static final int BLACK = 0;
	public static final int WHITE = 1;

	Chip[][] board;	//2D Array Representing the board

	DList whiteChips, blackChips; //List of chips of each color that are currently placed on the board.


	/**
	 * Constructs a new Board object. There is no paramter. It has package protection because the only
	 * class that requires it is MachinePlayer, which is also in the player package.
	 */
	Board(){ 
		board = new Chip[8][8];
		whiteChips = new DList();
		blackChips = new DList();
	}


	/**
	 *  Takes a move and applies it to the board (this class). This does check wheter the move is valid
	 *  because the methods which call it always check validity first.
	 *  
	 *  @param m the move that is applied to the board
	 *  @param player Which player it applies to
	 */
	void performMove(Move m, int player){
		if(m.moveKind == Move.ADD){
			addChip(m.x1, m.y1, player);
		}
		else if(m.moveKind == Move.STEP){
			removeChip(getChip(m.x2, m.y2));
			addChip(m.x1, m.y1, player);
		}
	}

	/**
	 * Reverts the board back to its older version. Again, does not check validity of pieces, since it is 
	 * only ever called after performMove is called, so removing the piece must be valid.
	 * 
	 * @param oldMove The move that was applied most recently to the player
	 * @param oldPlayer The player which did the move
	 */
	void revertMove(Move m, int player){
		if (m.moveKind==Move.ADD){
			removeChip(m.x1,m.y1);
		}else{
			removeChip(m.x1,m.y1);
			addChip(m.x2,m.y2,player);
		}
	}

	/**
	 * Returns the chip at a certain location
	 * 
	 * @param x The x location of the chip
	 * @param y The y location of the chip
	 * @return The chip at the position, or null if there is no chip.
	 */
	Chip getChip(int x, int y) { 
		return board[x][y]; 
	}

	/**
	 * Returns whether a location is in valid bounds of the Board; ie, if it is somewhere in the 8 by 8 grid,
	 * not including the four corners. It does not check if a black piece is trying to be placed in a white goal
	 * zone. This is tested in isValid() instead.
	 * 
	 * Does not return whether it is a legal move or not.
	 * 
	 * @param x The x location of the position
	 * @param y The y location of the position
	 * @return A boolean if the location is in bounds or not
	 */
	private boolean inBounds(int x, int y){
		if(x > 7 || y > 7){
			return false;
		}
		if(x < 0 || y < 0){
			return false;
		}
		else if((x == 0 && y == 0) || (x == 7 && y == 0) || (x == 0 && y == 7) || (x == 7 && y == 7)){
			return false;	
		}
		return true;
	}

	/**
	 * Checks if spot is empty
	 * 
	 * @param x The x location of the spot
	 * @param y THe y location of the spot
	 * @return Whether it is empty or not
	 */
	boolean isEmpty(int x, int y){
		return board[x][y] == null;
	}

	/**
	 * Adds a chip to the board. Assumes the position is empty. This method is only ever
	 * called after the spot has already been teseted for validity, so it does not check
	 * validity itself.
	 * 
	 * @param x The x location of the desired position
	 * @param y The y location of the desired position
	 * @param player What color the chip is (0 for black, 1 for white).
	 */
	void addChip(int x, int y, int player){
		Chip chip = new Chip(x, y, player, this);
		board[x][y] = chip; //updates the board.

		//updates the DList of chips.
		if(player == WHITE){
			whiteChips.insertBack(chip);
		}
		else if(player == BLACK){
			blackChips.insertBack(chip);
		}
	}

	/**
	 * Removes a specific chip from the board and its respective DList.
	 * Assumes the chip is on the board, if the chip is null, returns null.
	 * 
	 * @param c The desired removed chip
	 */
	void removeChip(Chip c){
		//First check if the chip is null
		if(c == null){
			return;
		}

		//updates the board.
		board[c.xPos()][c.yPos()] = null;
		DListNode node = null;

		//updates the DList of chips.
		if(c.color() == WHITE){
			node = whiteChips.front();
		}else if(c.color() == BLACK){
			node = blackChips.front();
		}
		while(node.isValidNode()){
			try{
				DListNode next = node.next();
				if(c.equals(node.item())){
					node.remove();
					break;
				}
				node = next;
			} catch (InvalidNodeException e){
				System.err.println(e);
			}
		}
	}

	/**
	 * Remove a chip at a specific location. This does the same thing as removeChip(Chip c). 
	 * If there is no chip at that location, nothing will happen.
	 * 
	 * @param x The x location of the chip you wish to remove.
	 * @param y The x location of the chip you wish to remove.
	 */
	private void removeChip(int x, int y){
		removeChip(getChip(x, y));
	}

	/**
	 * Returns a list of same colored Chips that are neighbors to a position; ie, it rotates around the 
	 * 3 by 3 square to determine the neighbors of a chip. It only returns neighbors of the same color.
	 * 
	 * @param x The x location where neighbors should be found
	 * @param y The y location where neighbors should be found
	 * @param player 
	 * @return DList of all chips of the same color that are neighbors of position(x,y)
	 */
	DList getNeighbors(int x, int y, int player){
		DList list = new DList();
		//Do a "square loop" check, checking all the positions around it
		for(int a = -1; a < 2; a++){
			for(int b = -1; b < 2; b++){
				if(a != 0 || b != 0){//Avoid the case where it checks itself
					int newX = a + x;
					int newY = b + y;
					if(inBounds((newX), (newY)) && getChip(newX,newY) != null && getChip(newX,newY).color() == player){
						list.insertBack(board[newX][newY]);
					}
				}
			}
		}
		return list;
	}

	/**
	 * Checks if a spot is valid for respective player, according to the rules in the readme.
	 * 
	 * @param x The x location of the spot
	 * @param y The y location of the spot
	 * @param player is which player the spot needs to be valid for (black or whtie).
	 * @return true if the spot is valid, false otherwise.
	 * @throws InvalidNodeException
	 */
	boolean isValid(int x, int y, int player) throws InvalidNodeException{
		//Check if the chip is in valid bounds of the board
		if(!inBounds(x, y) || !isEmpty(x, y)){
			return false;
		}
		//Check the goal/beginning end posts
		if(player == BLACK){
			if(x == 0 || x == 7){
				return false;
			}
		}
		if(player == WHITE){
			if(y == 0 || y == 7){
				return false;
			}
		}

		//Check if the possible chip location does not break any game rules
		DList neighbors = getNeighbors(x, y, player);
		DListNode node = neighbors.front();
		//Check to see if neighbors' neighbors exist; ifthey do, return false
		while(node.isValidNode()){
			Chip c = (Chip)(node.item());
			if(neighbors.length()>1 || !c.getNeighbors().isEmpty()){ //first part of the if statement is for the case where two neighbors of (x,y) are not neighbors of each other.
				return false;
			}
			node = node.next();
		}
		return true;
	}

	/**
	 * Returns a list of all moves that are valid for the respective player (black or white). For add moves, 
	 * it just calls isValid on each position possible. When it gets to step moves (10 chips), it first removes 
	 * each possible chip, then calls itself recursively to check validity of each possible location. Then adds
	 * the chip back and repeats for the other chips.
	 * 
	 * @param player The player which whom you are finding moves for
	 * @return DList of all possible moves, 
	 */
	DList validMoves(int player) throws InvalidNodeException{
		DList list = new DList();
		int size = 0;
		if (player==BLACK){
			size = blackChips.length();
		}else{
			size = whiteChips.length();
		}

		if (size<10){
			for(int a = 0; a < 8; a++){
				for(int b = 0; b < 8; b++){
					try{
						if (isValid(a,b,player)){
							list.insertBack(new Move(a,b));
						}
					}catch(InvalidNodeException ine){
						System.out.println(ine);
					}
				}
			}
		}else{//meaning the size is greater than 9, so we can only do step moves.
			for (int i = 0;i<8;i++){
				for (int j = 0;j<8;j++){
					Chip oldChip = getChip(i,j);
					if (oldChip!=null && oldChip.color()==player){ //meaning this is one chip we could move.
						//remove the chip, and run through all the spots to check if they are now valid.
						//implemented recursively for readability.
						this.removeChip(oldChip);
						DList intermediateListMoves = validMoves(player);
						DListNode temp = intermediateListMoves.front();
						while(temp.isValidNode()){
							//inserts the step move into the list.
							if (((Move)temp.item()).x1!=i || ((Move)temp.item()).y1!=j){
								//This logical is to make sure the chip has actually been stepped from its
								//original position.
								list.insertBack(new Move(((Move)temp.item()).x1,((Move)temp.item()).y1,i,j));
							}
							temp = temp.next();
						}
						this.addChip(i, j, player);
					}
				}
			}
		}
		return list;
	}


	/**
	 * Finds all the chips connected to a chip c, according to the rules of the game(in the readme). As soon
	 * as it finds a chip, of any color, it stops searching in that particular direction. If the chip it finds
	 * is of the same color as c, then it adds that chip to the list. Else, it only breaks that while loop.
	 * Also, if the chip c is in its respective goal zone, it does not find other chips in the same goal zone,
	 * only other chips which are outside the goal zone. The reason for this is that two chips connected in the goal zone
	 * cannot form a valid network, so knowing they are connected doesn't help to evaluate the board.
	 * 
	 * @param c The chip which you are finding connections for
	 * @oldChip the previous chip, so we don't count the same chip twice. It can also be null if we aren't worried about
	 * multiple findConnections calls.
	 * @return The list of chips connected to the chip c.
	 */
	DList findConnections(Chip c, Chip oldChip){
		DList connectedChips = new DList();

		//start on c position, then first go right to left.
		int xLoc = c.xPos()+1; int yLoc = c.yPos();
		if (c.color()!=BLACK || (c.yPos()!=0 && c.yPos()!=7)){
			while (xLoc<8){
				if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color()){
					connectedChips.insertBack(getChip(xLoc,yLoc));
					break;
				}else if(!isEmpty(xLoc,yLoc)){
					break;
				}
				xLoc++;
			}
			xLoc = c.xPos()-1;
			while (xLoc>-1){
				if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color() && !getChip(xLoc,yLoc).equals(oldChip)){
					connectedChips.insertBack(getChip(xLoc,yLoc));
					break;
				}else if(!isEmpty(xLoc,yLoc)){
					break;
				}
				xLoc--;
			}
		}

		xLoc = c.xPos(); yLoc = c.yPos()+1;
		if(c.color()!=WHITE || (c.xPos()!=0 && c.xPos()!=7)){
			//start on c position, then go top to bottom
			while (yLoc<8){
				if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color() && !getChip(xLoc,yLoc).equals(oldChip)){
					connectedChips.insertBack(getChip(xLoc,yLoc));
					break;
				}else if(!isEmpty(xLoc,yLoc)){
					break;
				}
				yLoc++;
			}
			yLoc = c.yPos()-1;
			while (yLoc>-1){
				if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color() && !getChip(xLoc,yLoc).equals(oldChip)){
					connectedChips.insertBack(getChip(xLoc,yLoc));
					break;
				}else if(!isEmpty(xLoc,yLoc)){
					break;
				}
				yLoc--;
			}
		}

		yLoc = c.yPos()+1; xLoc = c.xPos()-1;
		//Star on c location, then go diagonally top left to bottom right
		while (yLoc<8 && xLoc>-1){
			if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color() && !getChip(xLoc,yLoc).equals(oldChip)){
				connectedChips.insertBack(getChip(xLoc,yLoc));
				break;
			}else if(!isEmpty(xLoc,yLoc)){
				break;
			}
			yLoc++;
			xLoc--;
		}		
		yLoc = c.yPos()-1; xLoc = c.xPos()+1;
		while (xLoc<8 && yLoc>-1){
			if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color() && !getChip(xLoc,yLoc).equals(oldChip)){
				connectedChips.insertBack(getChip(xLoc,yLoc));
				break;
			}else if(!isEmpty(xLoc,yLoc)){
				break;
			}
			yLoc--;
			xLoc++;
		}

		yLoc = c.yPos()+1; xLoc = c.xPos()+1;	
		//start on c position, then go diagonlly top right to bottom left
		while (yLoc<8 && xLoc<8){
			if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color() && !getChip(xLoc,yLoc).equals(oldChip)){
				connectedChips.insertBack(getChip(xLoc,yLoc));
				break;
			}else if(!isEmpty(xLoc,yLoc)){
				break;
			}
			yLoc++;
			xLoc++;
		}
		xLoc = c.xPos()-1; yLoc = c.yPos()-1;
		while (xLoc>-1 && yLoc>-1){
			if (!isEmpty(xLoc,yLoc) && getChip(xLoc,yLoc).color()==c.color() && !getChip(xLoc,yLoc).equals(oldChip)){
				connectedChips.insertBack(getChip(xLoc,yLoc));
				break;
			}else if(!isEmpty(xLoc,yLoc)){
				break;
			}
			yLoc--;
			xLoc--;
		}
		return connectedChips;
	}

	/**
	 * Finds networks for the given player. Returns null if there is no network. It works by first finding a chip in
	 * player's goal zone. Then, it finds chips that are connected to it, and adds each one to a list one by one.
	 * For each chip it calls isNetwork() to determine if the chips in the list form a network. If there is no network, it
	 * again adds each chip to the list and calls findNetworks recursively.
	 * 
	 * Also, for white it tries to find a chip first in the left goal, and for black in the top goal. It does not check
	 * the other goal because if it can't find one in any of the two respective goals, then there can't possibly be a network,
	 * so no need to check the other goal.
	 * 
	 * @param descend is a list of chips already checked to prevent infinite loops. It is null on the first iteration.
	 * @param player is the player you are finding connections for
	 * @return The DList of chips in a network if it finds one. Else, it returns null.
	 * @throws InvalidNodeException If a node, which stores chips as its item field, happens to be invalid as per hw5.
	 */
	DList findNetworks(DList descend, int player) throws InvalidNodeException{
		Chip c = null;
		DList network = new DList();
		DList connections;
		if (descend==null){ //meaning this is the first call for findnetworks
			if (player==BLACK){
				for (int i = 0;i<8;i++){
					if (board[i][0]!=null){
						c = board[i][0];
						network.insertBack(c);
						network = findNetworks(network,player);
						if (network!=null){
							return network;
						}else{
							network = new DList();
						}
					}
				}
			}else{
				if (player==WHITE){
					for (int i = 0;i<8;i++){
						if (board[0][i]!=null){
							c = board[0][i];
							network.insertBack(c);
							network = findNetworks(network,player);
							if (network!=null){
								return network;
							}else{
								network = new DList();
							}
						}
					}
				}
			}
		}else{
			//meaning this is not the first recursive call
			c = (Chip) descend.back().item();
			if (descend.back().prev().isValidNode()){
				connections = findConnections(c,(Chip)descend.back().prev().item());
			}else{
				connections = findConnections(c,null);
			}
			DListNode temp = connections.front();
			if (temp.isValidNode()){
				//need to remove all chips fron consideration that are already in the descend DList.
				DListNode alpha = descend.front();
				while (alpha.isValidNode()){
					while (temp.isValidNode()){
						if (((Chip)alpha.item()).equals((Chip)temp.item())){
							DListNode beta = temp;
							temp = temp.next();
							beta.remove();
						}else{
							temp = temp.next();
						}
					}
					temp = connections.front();
					alpha = alpha.next();
				}
			}

			temp = connections.front();
			if (!temp.isValidNode()){
				return null;
			}else{
				while (temp.isValidNode()){
					descend.insertBack(temp.item());
					if (isNetwork(descend,player)){
						return descend;
					}else{
						descend.back().remove();
						temp = temp.next();
					}
				}
			}

			temp = connections.front();;
			//If it has reached this point, then it has gone through all the children, and none form a network. Now, it has to go
			//through all the children's children.
			while (temp.isValidNode()){
				descend.insertBack(temp.item());
				network = findNetworks(descend,player);
				if (network==null){
					descend.back().remove();
					temp = temp.next();
				}else{
					return network;
				}
			}
		}
		return null;
	}

	/**
	 * Checks a "network" by findNetworks to see if it actually is a network, according to the rules in the readme.
	 * It assumes that findConnections() is not returning a chip that is not connected; ie, findNetworks is calling isNetwork
	 * internally after calling findConnections, so it is assumed the DList network is of only connected chips.
	 * Works by checking the size and the end zones, and to make sure no more than two chips are in a row, and that
	 * none of the intermediate pieces in the network are in the goal zones. Again, as per the rules in the readme.
	 * 
	 * @param network is the list of chips that is being tested on its network-ness.
	 * @param player is the player whose network this is testing (0 for BLACK, 1 for WHITE).
	 * @return A boolean returning true if it is a network or not. 
	 * @throws InvalidNodeException If a node happens to be invalid
	 */
	private boolean isNetwork(DList network, int player) throws InvalidNodeException{
		if (network.length()<6){
			return false;
		}

		//checks end pieces in the list to make sure they are both in the goal zone.
		if (player==BLACK){
			if (((Chip) network.front().item()).yPos()!=0 || ((Chip)network.back().item()).yPos()!=7){
				return false;
			}
		}else{
			if (((Chip) network.front().item()).xPos()!=0 || ((Chip)network.back().item()).xPos()!=7){
				return false;
			}
		}

		DListNode temp1,temp2,temp3,temp4;
		temp1 = network.front(); temp2 = temp1.next(); temp3 = temp2.next();
		//checks if too many are in a row horizontally.
		while (temp1.isValidNode() && temp2.isValidNode() && temp3.isValidNode()){
			if (((Chip)temp1.item()).xPos()==((Chip)temp2.item()).xPos() && ((Chip)temp2.item()).xPos()==((Chip)temp3.item()).xPos()){
				return false;
			}else{
				//increment the nodes.
				temp1 = temp1.next(); temp2 = temp2.next(); temp3 = temp3.next();
			}
		}

		temp1 = network.front(); temp2 = temp1.next(); temp3 = temp2.next();
		//checks if too many are in a row vertically
		while (temp1.isValidNode() && temp2.isValidNode() && temp3.isValidNode()){
			if (((Chip)temp1.item()).yPos()==((Chip)temp2.item()).yPos() && ((Chip)temp2.item()).yPos()==((Chip)temp3.item()).yPos()){
				return false;
			}else{
				//increment the nodes.
				temp1 = temp1.next(); temp2 = temp2.next(); temp3 = temp3.next();
			}
		}
		temp1 = network.front(); temp2 = temp1.next(); temp3 = temp2.next();
		//checks if too many are in a diagonal from top left to bottom right
		while (temp1.isValidNode() && temp2.isValidNode() && temp3.isValidNode()){
			//I broke this into multiple if statements to improve readability, but originally I had it all as one line
			if ((((Chip)temp1.item()).yPos()-((Chip)temp1.item()).xPos())==(((Chip)temp2.item()).yPos()-((Chip)temp2.item()).xPos())){
				if((((Chip)temp2.item()).yPos()-((Chip)temp2.item()).xPos())==(((Chip)temp3.item()).yPos()-((Chip)temp3.item()).xPos())){
					return false;
				}else{
					//increment the nodes.
					//the fact that I broke up the if statements forces me to increment in two different places
					//but it improves the readability of the logical, so I decided to do it.
					temp1 = temp1.next(); temp2 = temp2.next(); temp3 = temp3.next();
				}
			}else{
				//increment the nodes.
				temp1 = temp1.next(); temp2 = temp2.next(); temp3 = temp3.next();
			}
		}

		temp1 = network.front(); temp2 = temp1.next(); temp3 = temp2.next();
		//checks if too many are in a diagonal from top right to bottom left
		while (temp1.isValidNode() && temp2.isValidNode() && temp3.isValidNode()){
			//Again, I broke this into multiple two if statements so as to not have one incredibly long logical in the parantheses.
			if ((((Chip)temp1.item()).yPos()+((Chip)temp1.item()).xPos())==(((Chip)temp2.item()).yPos()+((Chip)temp2.item()).xPos())){
				if((((Chip)temp2.item()).yPos()+((Chip)temp2.item()).xPos())==(((Chip)temp3.item()).yPos()+((Chip)temp3.item()).xPos())){
					return false;
				}else{
					//increment the nodes.
					temp1 = temp1.next(); temp2 = temp2.next(); temp3 = temp3.next();
				}
			}else{
				//increment the nodes.
				temp1 = temp1.next(); temp2 = temp2.next(); temp3 = temp3.next();
			}
		}

		temp1 = network.front(); temp2 = temp1.next(); temp4 = network.back(); temp3 = temp4.prev();
		//checks if two connected pieces are in the goal
		if (player==BLACK){
			if ((((Chip)temp1.item()).yPos()==0 && ((Chip)temp2.item()).yPos()==0) || (((Chip)temp3.item()).yPos()==7 && ((Chip)temp4.item()).yPos()==7)){
				return false;
			}
		}else{
			if ((((Chip)temp1.item()).xPos()==0 && ((Chip)temp2.item()).xPos()==0) || (((Chip)temp3.item()).xPos()==7 && ((Chip)temp4.item()).xPos()==7)){
				return false;
			}
		}

		//makes sure no intermediate pieces are in the goal zones.
		temp1 = network.front().next();
		int i = 2;
		while (i<network.length()){
			if (player==BLACK){
				if (((Chip)temp1.item()).yPos()==0 || ((Chip)temp1.item()).yPos()==7){
					return false;
				}else{
					temp1 = temp1.next();
					i++;
				}
			}else{
				if (((Chip)temp1.item()).xPos()==0 || ((Chip)temp1.item()).xPos()==7){
					return false;
				}else{
					temp1 = temp1.next();
					i++;
				}
			}
		}
		return true;
	}
}