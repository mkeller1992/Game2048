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

	protected void setBoard(Tile[][] board){
		this.board = board;
	}
	protected Tile[][] getBoard(){
		return board;
	}
	
	private void initGameBoard() {

		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = new Tile();
			}
		}
		
		spawnRandomNumber();
		spawnRandomNumber();
	}

	private void spawnRandomNumber() {
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
		resetMergedInfo();
		
		switch (dir) {
		case RIGHT:
			moveHorizontal(-1, boardSize - 1, dir);
			break;
		case LEFT:
			moveHorizontal(1, 0, dir);			
			break;
		case UP:
			moveVertical(1, 0, dir);
			break;
		case DOWN:
			moveVertical(-1, boardSize - 1, dir);			
			break;
		}

		return false;
	}

	private boolean moveHorizontal(int step, int start, Direction dir) {
		System.out.println("moving horizontal: step +" + step);
		boolean validMove = false;
		// loop through rows/columns
		for (int i = 0; i < boardSize; i++) {
			// loop through Tiles
			for (int j = start; j < boardSize && j >= 0; j += step) {			
				
				if (board[i][j].getValue() != 0) {
					int moveBy = moveTile(i, j, dir);					
					boolean merged = mergeTile(i, j + (moveBy*dir.getColStep()), dir);
					
					if(moveBy > 0 || merged){
						validMove = true;	
					}					
				}
			}
		}
		return validMove;
	}

	private boolean moveVertical(int step, int start, Direction dir) {
		System.out.println("moving Vertical: step +" + step);
		boolean validMove = false;
		
		// loop through rows/columns
		for (int i = 0; i < boardSize; i++) {
			// loop through Tiles
			for (int j = start; j < boardSize && j >= 0; j += step) {

				if (board[j][i].getValue() != 0) {
					int moveBy = moveTile(j, i, dir);					
					boolean merged = mergeTile(j + (moveBy*dir.getRowStep()), i, dir);
					
					if(moveBy > 0 || merged){
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

		int step = 0;

		switch (dir) {
		case RIGHT:
			step = 1;
			return moveTileHorizontal(row, col, step);
		case LEFT:
			step = -1;
			return moveTileHorizontal(row, col, step);
		case DOWN:
			step = 1;
			return moveTileVertical(row, col, step);
		case UP:
			step = -1;
			return moveTileVertical(row, col, step);
		default:
			return 0;
		// throw new Exception?

		}

	}

	private int moveTileHorizontal(int row, int col, int step) {
		if (col + step == boardSize || col + step < 0)
			return 0;

		if (board[row][col + step].getValue() == 0) {
			Tile tmp = board[row][col + step];
			board[row][col + step] = board[row][col];
			board[row][col] = tmp;

			return 1 + moveTileHorizontal(row, col + step, step);
		}

		return 0;
	}

	private int moveTileVertical(int row, int col, int step) {
		if (row + step == boardSize || row + step < 0)
			return 0;

		if (board[row + step][col].getValue() == 0) {
			Tile tmp = board[row + step][col];
			board[row + step][col] = board[row][col];
			board[row][col] = tmp;

			return 1 + moveTileVertical(row + step, col, step);
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
				board[row][col] = new Tile();
				board[row + dir.getRowStep()][col + dir.getColStep()].setValue(2 * tile2.getValue());				
				board[row + dir.getRowStep()][col + dir.getColStep()].setMerged(true);
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
