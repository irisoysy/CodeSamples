/* MachinePlayer.java */

package player;
import list.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
	static final int BLACK = 0;
	static final int WHITE = 1;
	private static final int ADD = 3; //default search depth for add moves.
	private static final int STEP = 2; //default search depth for step moves.

	static final double MAXSCORE = 1000; //score given for a network.
	private int myColor; //chip color that MachinePlayer 
	private int enemyColor; //chip color that the enemy player uses.
	private Board currentBoard; //the internal representation of the 8x8 grid and the chips on the board.
	private int depthOfSearch; //the search depth that MachinePlayer uses at any given time.
	private boolean constructorOne = false; //tells use which constructor was called, so we know to decrement depthOfSearch
	//when we get to Step moves.

	/**
	 * Creates a machine player with the given color. White has the first move.
	 * 
	 * @param color Either 0(Black) or 1(White)
	 */
	public MachinePlayer(int color) {
		if (color==BLACK){
			myColor = BLACK;
			enemyColor = WHITE;
		}else{
			myColor = WHITE;
			enemyColor = BLACK;
		}
		depthOfSearch = ADD;
		constructorOne = true;
		myName = "GammaDelta";
		currentBoard = new Board();
	}

	/**
	 * Creates a machine player with the given color and desired depth search. White has the first move.
	 * 
	 * @param color Either 0(Black) or 1(White)
	 * @param searchDepth How deep the searching will go in game tree search
	 */
	public MachinePlayer(int color, int searchDepth) {
		this(color);
		constructorOne = false;
		depthOfSearch = searchDepth;
	}

	/**
	 * For the first two moves, it puts one piece in each goal zone for "this" player.
	 * After the first two moves, it uses alphaBeta to determine a high scoring board.
	 * If alphaBeta fails to find a move for any reason, then chooseMove just returns whatever
	 * is the first valid move it can find for "this" player. This is put in as a safety measure
	 * to prevent chooseMove from ever returning null.
	 * 
	 * @return a new move by "this" player. 
	 * Internally records the move (updates the internal game board) as a move by "this" player.
	 */
	public Move chooseMove(){

		//FIRST TWO MOVES JUST PUTS ONE PIECE IN EACH OF ITS GOALS
		Move first;
		if (myColor==BLACK){
			if (currentBoard.blackChips.length()<1){
				first  = new Move(3,0);
				currentBoard.performMove(first,BLACK);
				return first;
			}else if(currentBoard.blackChips.length()<2){
				first = new Move(3,7);
				currentBoard.performMove(first,BLACK);
				return first;
			}
		} else{
			if (currentBoard.whiteChips.length()<1){
				first = new Move(0,3);
				currentBoard.performMove(first,WHITE);
				return first;
			}else if(currentBoard.whiteChips.length()<2){
				first = new Move(7,3);
				currentBoard.performMove(first,WHITE);
				return first;
			}
		}

		//resets the search depth if we are in step moves
		if ((constructorOne==true) && (currentBoard.blackChips.length()>9) || currentBoard.whiteChips.length()>9){
			//meaning, the constructor without the search depth parameter was called, and either player is out of
			//add moves for any level of the tree in alphaBeta.
			depthOfSearch = STEP;
			//leaves constructorOne true for readability and testing. Could set it to false here and it would bypass this
			//if statement from now on, but then we would lose the information that constructor one was called, so we decided
			//to leave it as true.
		}

		//then calls alpha beta search to find the maximum move.
		Move notFirst;
		Best bestMove = new Best();
		try{
			bestMove = alphaBeta(true,-100000000,100000000,depthOfSearch);
		}catch(InvalidNodeException ine){
			System.out.println(ine);
		}
		notFirst = bestMove.move;
		if (notFirst!=null){
			currentBoard.performMove(notFirst,myColor);
			return notFirst;
		}

		//last resort, just in case alpha beta failed for some reason, we have to return a non null move.
		DList allMoves;
		Move notGood = new Move();
		try{
			allMoves = currentBoard.validMoves(myColor);;
			notGood = (Move) allMoves.front().item();
		}catch(InvalidNodeException ine){
			System.out.println(ine);
		}
		return notGood;
	} 

	/**
	 * @param m is the Move that is proposed to be performed.
	 * 
	 * Informs "this" player of the opponents move.
	 * If the Move m is legal, records the move as a move by the opponent (updates the internal game board) and returns true.  
	 * If the move is illegal, returns false without modifying the internal state of "this" player.
	 * My implementation 
	 */
	public boolean opponentMove(Move m) {
		if (myColor==BLACK){
			myColor = WHITE; //resets my color to the enemies color to check if forceMove is valid for the opponent.
			boolean opponent = forceMove(m);
			myColor = BLACK;
			return opponent;
		}else{
			myColor = BLACK;
			boolean opponent = forceMove(m);
			myColor = WHITE;
			return opponent;
		}
	}

	//This method is used to help set up "Network problems" for your
	// player to solve.
	/**
	 * @param m is the Move that is proposed to be performed.
	 * If the Move m is legal, returns true and records the move by "this" player by updating the internal game board.
	 * If the Move m is is illegal, returns false without modifying the internal state of "this" and the game board.
	 */
	public boolean forceMove(Move m) {
		boolean validness = false;
		if (m.moveKind==Move.STEP){
			Chip oldChip = this.currentBoard.getChip(m.x2,m.y2);
			currentBoard.removeChip(oldChip);
			try{
				validness = currentBoard.isValid(m.x1,m.y1,myColor);
			}catch(InvalidNodeException ine){
				return false;
			}
			if (validness){
				currentBoard.addChip(m.x1,m.y1,myColor);
			}else{
				currentBoard.addChip(m.x2, m.y2, myColor);
			}
			return validness;
		}else{
			try{
				validness = currentBoard.isValid(m.x1,m.y1,myColor);
			}catch(InvalidNodeException ine){
				return false;
			}
			if (validness){
				currentBoard.performMove(m,myColor);
			}
			return validness;		
		}
	}


	/**
	 * Evaluates the board for the player and returns a double that represents their chances of winning.
	 * -1000 represents a definite loss for this player, and 1000 is a definite win.
	 * It works by finding the sum of the number of connections that each "this" chip makes, and subtracting the sum
	 * of the number of connections that each enemy chip can make. If player is myColor, then it would return high,
	 * else it would return low.
	 * 
	 * @param player For which player the board will be evaluated
	 * @param side True if it called for machine player, false if it is called for the other player
	 * @return A double in the range -1000 to 1000
	 * @throws InvalidNodeException
	 */
	private double boardEvaluation(int player) throws InvalidNodeException {
		//this finds the difference between the number of chips of white that are connected and the number
		//of chips of black that are connected, and returns high or low depending on if MachinePlayer is black
		//or white
		int sumConn = 0, sumBadConn = 0;
		DList alpha;
		for (int i = 0;i<8;i++){
			for (int j = 0;j<8;j++){
				if (!currentBoard.isEmpty(i,j) && currentBoard.getChip(i, j).color()==player){
					alpha = currentBoard.findConnections(currentBoard.getChip(i, j),null);
					sumConn = sumConn+alpha.length();
				}else if(!currentBoard.isEmpty(i,j)){
					alpha = currentBoard.findConnections(currentBoard.getChip(i, j),null);
					sumBadConn = sumBadConn+alpha.length();
				}
			}
		}

		if (player==myColor){
			return sumConn-sumBadConn;
		}else{
			return sumBadConn-sumConn;
		}
	}


	/**
	 * Performs alphaBeta pruning on the players currentBoard, as per lecture 17 notes. If it finds a network it returns
	 * the max or min score, depending on side, and if the level of the tree is 0, it evaluates the board and returns 
	 * that score. If it finds two networks, as per the readme, it returns min for side true, since that is a loss.
	 * 
	 * @param side is true when performing moves for the machine, false when performing moves for the enemy.
	 * @param alpha parameter for alpha beta pruning
	 * @param beta parameter for alpha beta pruning
	 * @param level represents which level in the tree search. When level is 0, the boardEvaluation method is run.
	 * @return The best move
	 * @throws InvalidNodeException
	 */
	private Best alphaBeta(boolean side, int alpha, int beta, int level) throws InvalidNodeException{
		Best reply, myBest = new Best();
		DList network1,network2;
		int player;
		if (side==true){
			player = myColor;
			network1 = currentBoard.findNetworks(null,player);
			network2 = currentBoard.findNetworks(null, enemyColor);
		}else{
			player = enemyColor;
			network1 = currentBoard.findNetworks(null,player);
			network2 = currentBoard.findNetworks(null, myColor);
		}

		if (network1!=null){//meaning someone, either black or white has a network.
			Best bottomBest = new Best();
			if (side==true){
				if (network2==null){
					//makes sure network2 is null because if both sides have networks, the enemy gets the win.
					bottomBest.score = MAXSCORE+level;
				}else{
					bottomBest.score = -1*MAXSCORE-level;
				}
			}else{
				if (network2==null){
					bottomBest.score = -1*MAXSCORE-level;
				}else{
					bottomBest.score = MAXSCORE+level;
				}
			}
			return bottomBest;
		}else if(network2!=null){
			Best bottomBest = new Best();
			if(side==true){
				bottomBest.score = -1*MAXSCORE-level;
			}else{
				bottomBest.score = MAXSCORE+level;
			}
			return bottomBest;
		}else if(level==0){
			Best bottomBest = new Best();
			bottomBest.score = boardEvaluation(player);
			return bottomBest;
		}

		if (side==true){
			//this is a Machine move
			myBest.score = alpha;
		}else{
			myBest.score = beta;
		}

		DList allMoves = currentBoard.validMoves(player);
		DListNode temp = allMoves.front();

		while (temp.isValidNode()){
			try{
				currentBoard.performMove((Move)temp.item(),player);
				reply = alphaBeta(!side,alpha,beta,level-1);
				currentBoard.revertMove((Move)temp.item(),player);
				if ((side==true) && (reply.score>myBest.score)){
					myBest.move = (Move)temp.item();
					myBest.score = reply.score;
					alpha = (int)reply.score;
				}else if((side==false)&&(reply.score<myBest.score)){
					myBest.move = (Move) temp.item();
					myBest.score = reply.score;
					beta = (int) reply.score;
				}
				if (alpha>=beta){
					return myBest;
				}
				temp = temp.next();
			}catch(InvalidNodeException ine){
				System.out.println(ine);
			}
		}
		return myBest;
	}
}