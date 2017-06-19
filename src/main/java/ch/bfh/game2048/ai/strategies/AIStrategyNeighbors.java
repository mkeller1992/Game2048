package ch.bfh.game2048.ai.strategies;

import org.apache.commons.lang3.SerializationUtils;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

/**
*
* Strategy for 3x3 boards
* 
* - computes 2 moves ahead
* - tries to minimize value-difference between adjacent tiles...
* - ... while playing upwards
*
*/


public class AIStrategyNeighbors extends BaseAIStrategy {

	private int[][] weights;

	private int[][] weightsHighLeft;
	private int[][] weightsHighRight;

	Double[] neighborScoreChanges;

	public AIStrategyNeighbors(AIGameEngine engine) {
		super(engine);
	}

	/**
	 * Initialize weight-arrays:
	 */

	@Override
	public boolean initializeAI() {
		initializeWeights();
		return true;
	}

	/**
	 * Initializes the weight-patterns:
	 */
	private void initializeWeights() {

		// Generate the weight-pattern for board-constellations where the highest tile is on the LEFT side:
		// { 0,1,2,3 } { 4,5,6,7 } etc.
		
		weightsHighLeft = new int[8][8];
		int c = 0;
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				weightsHighLeft[row][column] = c;
				c++;
			}
		}

		// Generate the weight-pattern for board-constellations where the highest tile is on the RIGHT side:
		// { 3,2,1,0 } { 7,6,5,4 } etc.
		
		weightsHighRight = new int[8][8];
		c = 0;
		for (int row = 0; row < 8; row++) {
			for (int column = 7; column >= 0; column--) {
				weightsHighRight[row][column] = c;
				c++;
			}
		}
	}

	/**
	 * This strategy is based on "neighbor-scores" which depend on:
	 * - value differences between adjacent tiles
	 * - the position of the value-differences on the board
	 * 
	 * The strategy strives for:
	 * - ...minimizing the absolute value differences between adjacent tiles
	 * - ...keeping the highest tiles in the first row
	 * 
	 * - Weight-pattern get adjusted based on the position of the first row's highest tile
	 * - Positions with lower weight == incentive to place high-value tiles there, since...
	 * ... their *absolute* value-difference to the adjacent tiles is naturally large
	 * 
	 * Strategy picks the direction with the smallest expected neighbor-score increase
	 * 
	 */

	@Override
	public Direction getMove(Tile[][] board) {

		engine.setGameBoard(board);

		// Choose suitable weight-pattern based on board-size / board-constellation
		selectWeightPattern(board);		

		// Reset values from previous move
		neighborScoreChanges = new Double[4];

		// Check if move is valid and set the corresponding board-value-change:
		int i = 0;
		for (Direction dir : Direction.values()) {
			if (engine.isMoveValid(dir)) {
				neighborScoreChanges[i] = calculateExpectedValue(engine.getBoard(), dir);
			}
			i++;
		}

		// Check if in case of moving up there is the risk of being forced to move "down" subsequently
		// if risk exists --> set neighbor-score-change of "up-move" to a very high (unattractive) number (only for 4x4 boards)
		if (board.length == 4 && neighborScoreChanges[0] != null) {
			Tile[][] boardAfterUp = getBoardAfterSimulatedMove(engine.getBoard(), Direction.UP);

			if (riskOfDeadLockAfterUP(boardAfterUp, 2) | riskOfDeadLockAfterUP(boardAfterUp, 1)) {
				neighborScoreChanges[0] = 10000d;
			}
		}

		// Get the lowest expected neighbor-score-increase from all directions
		double minVal = 1000000;
		for (Double v : neighborScoreChanges) {
			if (v != null && v < minVal) {
				minVal = v;
			}
		}

		// Return the direction with the lowest expected neighbor-score increase:
		i = 0;
		for (Direction dir : Direction.values()) {
			if (neighborScoreChanges[i] != null && neighborScoreChanges[i] == minVal) {
				return dir;
			}
			i++;
		}
		return null;
	}

	/**
	 * Computes the average expected neighbor-score-increase (or decrease) of the next 2 moves
	 * That means the method returns a "total-score" consisting of
	 * 
	 * - the increase of the weighted tile-value-differences (=neighbor-score-increase) induced by @param direction...
	 * - ... PLUS for each possible scenario of tile spawned...
	 * ... the neighbor-score-increase of the best follow-up-move, multiplied with the scenario's probability
	 * 
	 * @param clonedGameBoard
	 *            a clone of the current board
	 * @param direction
	 *            initial direction to test
	 * @return the average expected neighbor-score increase induced by 2 moves ahead
	 */

	public double calculateExpectedValue(Tile[][] clonedGameBoard, Direction direction) {

		Tile[][] board = clonedGameBoard;
		double totalScore = 0;

		// Get get the current board's weighted neighbor-score:
		double boardValueBeforeFirstMove = getNeighborScore(board);

		// Simulate the next move...
		board = getBoardAfterSimulatedMove(board, direction);

		// ...and get the neighbor-score after the move:
		double boardValueAfterFirstMove = getNeighborScore(board);

		// Get the increase / decrease of neighbor-score following the simulated move
		double boardValueChangeFromFirstMove = boardValueAfterFirstMove - boardValueBeforeFirstMove;

		totalScore += boardValueChangeFromFirstMove;

		// get number of empty tiles on board after simulated move
		int numbOfZeros = getAmountOfEmptyTiles(board, 0, board.length - 1);

		// for each empty tile on current board compute the case that:
		// - a 4 will be spawned
		// - a 2 will be spawned
		for (int i = 0; i < clonedGameBoard.length; i++) {

			for (int j = 0; j < clonedGameBoard.length; j++) {

				if (board[i][j].getValue() == 0) {

					// clone the board and set a 4 resp. a 2 at the position of the empty tile

					Tile[][] boardWith4 = cloneBoard(board);
					boardWith4[i][j].setValue(4);

					Tile[][] boardWith2 = cloneBoard(board);
					boardWith2[i][j].setValue(2);

					// Given a 2 or a 4 was spawned on the empty tile:
					// We assume that after the spawning of the tile the move with the lowest neighbor-score-increase will be executed
					// Therefore the probability for a specific neighbor-score-increase is:

					double probabilityOfSpawning4AtThisPosition = (0.1 / (numbOfZeros));
					double probabilityOfSpawning2AtThisPosition = (0.9 / (numbOfZeros));

					// Simulate a move to all 4 directions
					// once with a 2 spawned on the empty tile, once with a 4 spawned
					// Finally only the best out of 4 directions will be counted

					double scoreFromBestOfAllDirectionsAfterSpawning2 = 10000000;
					double scoreFromBestOfAllDirectionsAfterSpawning4 = 10000000;

					for (Direction dir : Direction.values()) {

						Tile[][] boardAfterSpawning2AndMovingAgain = getBoardAfterSimulatedMove(cloneBoard(boardWith2), dir);
						Tile[][] boardAfterSpawning4AndMovingAgain = getBoardAfterSimulatedMove(cloneBoard(boardWith4), dir);

						// if move after spawning 2 resp. 4 was invalid, neighbor-score will be 100000 (very unattractive)...
						double boardValueChangeFromSecondMoveAfter2 = 100000;
						double boardValueChangeFromSecondMoveAfter4 = 100000;

						// if move was valid calculate "neighbor-score after move" minus "neighbor-score before move"

						if (boardAfterSpawning2AndMovingAgain != null) {
							// The neighbor-score-increase induced by the second move (in case a 2 was spawned)
							boardValueChangeFromSecondMoveAfter2 = getNeighborScore(boardAfterSpawning2AndMovingAgain) - boardValueAfterFirstMove;
						}

						if (boardAfterSpawning4AndMovingAgain != null) {
							// The neighbor-score-increase induced by the second move (in case a 4 was spawned)
							boardValueChangeFromSecondMoveAfter4 = getNeighborScore(boardAfterSpawning4AndMovingAgain) - boardValueAfterFirstMove;
						}

						// Get only the lowest (=the best) neighbor-score-increase from all 4 directions
						scoreFromBestOfAllDirectionsAfterSpawning2 = Math.min(boardValueChangeFromSecondMoveAfter2, scoreFromBestOfAllDirectionsAfterSpawning2);
						scoreFromBestOfAllDirectionsAfterSpawning4 = Math.min(boardValueChangeFromSecondMoveAfter4, scoreFromBestOfAllDirectionsAfterSpawning4);
					}

					// multiply the lowest neighbor-score-increase of all 4 directions with the probability of its occurrence
					// and add it to the total score
					totalScore += (probabilityOfSpawning2AtThisPosition * scoreFromBestOfAllDirectionsAfterSpawning2);
					totalScore += (probabilityOfSpawning4AtThisPosition * scoreFromBestOfAllDirectionsAfterSpawning4);
				}
			}
		}
		// returns the average expected neighbor-score-increase induced by @param direction + the best follow-up move
		return totalScore;
	}
	
	
	/**
	 * Selects suitable weight-pattern...
	 * ... based on the position of the first row's highest tile:
	 * - highest weights are on the same side as the first row's highest tile
	 * 
	 * @param board
	 *            current game-board
	 */
	
	private void selectWeightPattern(Tile[][] board){
		// get position of highest tile of first row
		int max = 0;
		for (Tile t : board[0]) {
			max = Math.max(t.getValue(), max);
		}

		for (int column = 0; column < board.length; column++) {

			if (column < (board.length / 2) && board[0][column].getValue() == max) {
					weights = weightsHighLeft;
				return;
			}
			else if (column >= (board.length / 2) && board[0][column].getValue() == max) {
					weights = weightsHighRight;
				return;
			}
		}
	}
	

	/**
	 * 	- Multiplies the average weight of two adjacent tiles with their (positive) value-difference
	 * - a tile's weight depends on its position within the board
	 * - the weights are increasing towards the boards bottom
	 * 
	 * @param board
	 * @return
	 */
	
	public double getNeighborScore(Tile[][] board) {

		double neighbourScore = 0;

		// summarize weighted tile differences horizontally
		for (int row = 0; row < board.length; row++) {
			for (int column = 0; column < board.length - 1; column++) {

				// if the tiles that are compared are not 2-0 and not 0-2 > compute neighbor-score
				if (!(board[row][column].getValue() == 0 && board[row][column + 1].getValue() == 2) && !(board[row][column].getValue() == 2 && board[row][column + 1].getValue() == 0)) {
					neighbourScore += ((weights[row][column] + weights[row][column + 1]) / 2)
							* (Math.abs(board[row][column].getValue() - board[row][column + 1].getValue()));
				}
			}
		}

		// summarize weighted tile-differences vertically
		for (int column = 0; column < board.length; column++) {
			for (int row = 0; row < board.length - 1; row++) {

				// if the tiles that are compared are not 2-0 and not 0-2 > compute neighbor-score
				if (!(board[row][column].getValue() == 0 && board[row + 1][column].getValue() == 2) && !(board[row][column].getValue() == 2 && board[row + 1][column].getValue() == 0)) {
					neighbourScore += ((weights[row][column] + weights[row + 1][column]) / 2)
							* (Math.abs(board[row][column].getValue() - board[row + 1][column].getValue()));
				}
			}
		}
		return neighbourScore;
	}

	/**
	 * - Checks if there is the risk of being forced to "move down" (= risk of deadlock)
	 * 
	 * - returns true if all of the following conditions are given:
	 * ...there are no adjacent equal tiles in the upper rows
	 * ...there is only one empty tile in the upper rows
	 * ...the lower rows are completely empty
	 * 
	 * @param boardAfterMove
	 *            the game-board after a certain simulated move
	 * @param indexOflastFullRow
	 *            number of upper rows to be checked if they are full
	 * @return true if there is a risk of deadlock
	 */

	public boolean riskOfDeadLockAfterUP(Tile[][] boardAfterMove, int indexOflastFullRow) {

		int emptyTilesInUpperRows = getAmountOfEmptyTiles(boardAfterMove, 0, indexOflastFullRow);
		int emptyTilesInLowerRows = getAmountOfEmptyTiles(boardAfterMove, 1 + indexOflastFullRow, boardAfterMove.length - 1);

		// checks if there are two adjacent equal tiles horizontally:
		// if so --> return false because there is no risk of deadlock

		for (int i = 0; i < boardAfterMove.length; i++) {

			for (int j = 0; j < boardAfterMove[0].length - 1; j++) {

				if (boardAfterMove[i][j].getValue() != 0 && boardAfterMove[i][j].getValue() == boardAfterMove[i][j + 1].getValue()) {
					return false;
				}
			}
		}

		// checks if there are two adjacent equal tiles vertically:
		// if so --> return false because there is no risk of deadlock

		for (int i = 0; i < boardAfterMove.length; i++) {

			for (int j = 0; j < boardAfterMove[0].length - 1; j++) {

				if (boardAfterMove[j][i].getValue() != 0 && boardAfterMove[j][i].getValue() == boardAfterMove[j + 1][i].getValue()) {
					return false;
				}
			}
		}

		// returns true if there is only one empty tile in the upper rows and the lower rows are empty
		if (emptyTilesInUpperRows == 1 && emptyTilesInLowerRows == (boardAfterMove.length - (indexOflastFullRow + 1)) * boardAfterMove[0].length) {
			return true;
		}

		// return false if there is no deadlock-risk
		return false;
	}

	/**
	 * - Simulates a move to a given direction
	 * - Return the board-constellation after that move or null if move was invalid
	 * 
	 * @param board
	 *            an already cloned board
	 * @param dir
	 *            direction of the move to simulate
	 * @return the board constellation after the move or null if move was invalid
	 */

	private Tile[][] getBoardAfterSimulatedMove(Tile[][] board, Direction dir) {

		boolean moveValid = false;
		Tile[][] tmpBoard = engine.getBoard();
		Tile[][] retVal;

		engine.setGameBoard(board);
		moveValid = engine.move(dir, true);

		retVal = engine.getBoard();

		engine.revertMove();
		engine.setGameBoard(tmpBoard);

		return moveValid ? retVal : null;
	}

	/**
	 * A helper method counting the empty tiles on the board...
	 * ...from indexOfFirstRow to indexOfEndRow (inclusive)
	 * 
	 * @param gameBoard
	 * @param indexOfFirstRow
	 *            first row to count empty tiles
	 * @param indexOfEndRow
	 *            last row to count empty tiles
	 * @return amount of empty tiles within specified rows
	 */
	public int getAmountOfEmptyTiles(Tile[][] gameBoard, int indexOfFirstRow, int indexOfEndRow) {
		int c = 0;

		for (int i = indexOfFirstRow; i <= indexOfEndRow; i++) {
			for (int j = 0; j < gameBoard.length; j++) {
				if (gameBoard[i][j].getValue() == 0) {
					c++;
				}
			}
		}

		return c;
	}

	/**
	 * Clones a board with Tile-Objects
	 * 
	 * @param gameBoard
	 *            a tile-board
	 * @return a clone of the tile-board
	 */

	private Tile[][] cloneBoard(Tile[][] gameBoard) {

		Tile[][] testBoard = SerializationUtils.clone(gameBoard);

		return testBoard;
	}
}
