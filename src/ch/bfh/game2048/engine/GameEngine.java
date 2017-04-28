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

		switch (dir) {
		case RIGHT:			
			moveHorizontal(1, 0, dir);
			break;
		case LEFT:
			moveHorizontal(-1, boardSize-1, dir);
			break;
		case UP:
			moveVertical(-1, boardSize-1, dir);
			break;
		case DOWN:
			moveVertical(1, 0, dir);
			break;
		}

		return false;
	}

	private boolean moveHorizontal(int step, int start, Direction dir) {
		System.out.println("moving horizontal: step +"+ step);
		
		// loop through rows/columns
		for (int i = 0; i < boardSize; i++) {

			// loop through Tiles
			for (int j = start; j < boardSize && j >= 0; j += step) {
				// board[i][j]

				moveTile(i, j, dir);
				// merge();

			}
		}

		return false;
	}
	
	private boolean moveVertical(int step, int start, Direction dir) {
		System.out.println("moving horizontal: step +"+ step);
		
		// loop through rows/columns
		for (int i = 0; i < boardSize; i++) {

			// loop through Tiles
			for (int j = start; j < boardSize && j >= 0; j += step) {
				// board[i][j]

				moveTile(j, i, dir);
				// merge();

			}
		}

		return false;
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
	private boolean moveTile(int row, int col, Direction dir) {

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
			return false;
		// throw new Exception?

		}

	}

	private boolean moveTileHorizontal(int row, int col, int step) {
		boolean moved = false;

		if (col + step == boardSize || col + step < 0)
			return false;

		if (board[row][col + step].getValue() == 0) {
			Tile tmp = board[row][col + step];
			board[row][col + step] = board[row][col];
			board[row][col] = tmp;

			moved = true;

			moveTileHorizontal(row, col + step, step);
		}

		return moved;
	}

	private boolean moveTileVertical(int row, int col, int step) {
		boolean moved = false;

		if (row + step == boardSize || row + step < 0)
			return false;

		if (board[row + step][col].getValue() == 0) {
			Tile tmp = board[row + step][col];
			board[row + step][col] = board[row][col];
			board[row][col] = tmp;

			moved = true;

			moveTileVertical(row + step, col, step);
		}

		return moved;
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
