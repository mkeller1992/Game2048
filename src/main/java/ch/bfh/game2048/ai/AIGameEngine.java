package ch.bfh.game2048.ai;

import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class AIGameEngine extends GameEngine {
	private int aiNumber = 0;
	
	public AIGameEngine(int boardSize, int tileValueToWin) {
		super(boardSize, tileValueToWin);
	}
	
	public AIGameEngine(int boardSize, int tileValueToWin, int aiNumber) {
		super(boardSize, tileValueToWin);
		this.aiNumber = aiNumber;
	}
	
	public void setAiNumber(int aiNumber) {
		this.aiNumber = aiNumber;
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
	
	public void setGameBoard(Tile[][] board){
		this.setBoard(board);
	}

	public int getAiNumber() {
		return aiNumber;
	}
	
	@Override
	public void revertMove(){
		super.revertMove();
	}
		
}
