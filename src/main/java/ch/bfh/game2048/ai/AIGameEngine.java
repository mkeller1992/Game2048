package ch.bfh.game2048.ai;

import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class AIGameEngine extends GameEngine {

	public AIGameEngine(int boardSize, int tileValueToWin) {
		super(boardSize, tileValueToWin);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * checks if the move is valid without executing it
	 * @param dir
	 * @return
	 */
	public boolean isMoveValid(Direction dir){
		boolean validMove = false;
		
		validMove = move(dir, true);
		if(validMove){
			revertMove();
		}
		
		return validMove;
	}
	
	public boolean move(Direction dir, boolean simulate){
		return super.move(dir, simulate);
	}
	
	/**
	 * more or less just a helper method counting 0 on the board
	 * @return
	 */
	public int getAmountOfEmptyTiles(){
		int c = 0;
		
		for(Tile[] row : getBoard()){
			for(Tile t : row){
				if(t.getValue() == 0){
					c++;
				}
			}
		}
		
		return c;		
	}
	
	public void setBoard(Tile[][] board){
		this.setBoard(board);
	}
	

}
