package ch.bfh.game2048.engine;

import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.GameStatistics;
import ch.bfh.game2048.model.Tile;

public class GameEngine {
	int boardSize;
	Tile[][] board;
	GameStatistics stats;

	public GameEngine(int boardSize, GameStatistics stats) {
		this.boardSize = boardSize;
		this.stats = stats;

		board = new Tile[boardSize][boardSize];

		initGameBoard();

	}
	
	

	public GameStatistics getStats() {
		return stats;
	}

	protected void setBoard(Tile[][] board){
		this.board = board;
	}
	private void initGameBoard() {

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = new Tile();
			}
		}
		
		spawnRandomTile();
		spawnRandomTile();
	}

	private void spawnRandomTile() {
		boolean done = false;

		while (!done) {
			int row = (int) (Math.random() * (boardSize - 1));
			int col = (int) (Math.random() * (boardSize - 1));

			if (board[row][col].getValue() == 0) {
				board[row][col].setValue(getRandomValue());
				done = true;
			}
		}
	}

	private int getRandomValue() {
		return Math.random() > 0.9 ? 4 : 2;
	}

	/**
	 * 
	 * @param dir
	 * @return boolean true if something was moved
	 */
	public boolean move(Direction dir) {
		boolean moved = false;
		
		resetMergedInfo();
				
		moveBoard(dir);

		if(moved){
			stats.incrementMoves();
			spawnRandomTile();			
		}
		return moved;
	}

	
	private boolean moveBoard(Direction dir) {
		boolean validMove = false;

		
		int start =0;
		int step = -1 * ((dir.getColStep() != 0) ? dir.getColStep() : dir.getRowStep());
		if(step < 0){
			start = boardSize -1;		
		}
		
		// loop through rows/columns
		for (int i = 0; i < boardSize; i++) {
			// loop through Tiles
			for (int j = start; j < boardSize && j >= 0; j += step) {

				int row;
				int col;
				if(dir.equals(Direction.LEFT) || dir.equals(Direction.RIGHT)){
					row = i;
					col = j;
				} else {
					row = j;
					col = i;
				}
				
				if (board[row][col].getValue() != 0) {
					int moveBy = moveTile(row, col, dir);
					boolean merged = mergeTile(row + (moveBy * dir.getRowStep()), col + (moveBy * dir.getColStep()), dir);

					if (moveBy > 0 || merged) {
						validMove = true;
					}
				}
			}
		}
		return validMove;
	}
	

	/**
	 * Moves the given Tile in the given Direction, until it is next to the
	 * border or to an other Tile.
	 * 
	 * @param row
	 * @param col
	 * @param dir
	 * @return
	 */
	private int moveTile(int row, int col, Direction dir) {
		if (col + dir.getColStep() < boardSize && col + dir.getColStep() >= 0 
				&& row + dir.getRowStep() < boardSize && row + dir.getRowStep() >= 0)
		{
			
			if (board[row + dir.getRowStep()][col + dir.getColStep()].getValue() == 0) {
				Tile tmp = board[row + dir.getRowStep()][col  + dir.getColStep()];
				board[row + dir.getRowStep()][col  + dir.getColStep()] = board[row][col];
				board[row][col] = tmp;

				return 1 + moveTile(row + dir.getRowStep() , col + dir.getColStep(), dir);
			}
			
		}
		return 0;
	}

	public boolean mergeTile(int row, int col, Direction dir) {
		if (row + dir.getRowStep() >= 0 && row + dir.getRowStep() < boardSize && col + dir.getColStep() >= 0 && col + dir.getColStep() < boardSize) {

			Tile tile1 = board[row][col];
			Tile tile2 = board[row + dir.getRowStep()][col + dir.getColStep()];

			if (tile1.isMerged() || tile2.isMerged()) {
				return false;
			}

			if (tile1.getValue() == tile2.getValue()) {
				
				int mergedValue = 2 * tile2.getValue();
				
				board[row][col] = new Tile();
				board[row + dir.getRowStep()][col + dir.getColStep()].setValue(mergedValue);				
				board[row + dir.getRowStep()][col + dir.getColStep()].setMerged(true);
				
				stats.addScore(mergedValue);
				if (mergedValue > stats.getHighestValue()){
					stats.setHighestValue(mergedValue);
				}
				return true;
			}
		}
		return false;
	}
	
	private void resetMergedInfo(){
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j].setMerged(false);
				board[i][j].setSpawned(false);
			}
		}
	}
	

	public void print() {

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				System.out.print(board[i][j].getValue() + "   ");
			}
			System.out.println();
		}
	}
}
