package ch.bfh.game2048.ai.strategies;

import org.apache.commons.lang3.SerializationUtils;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

/**
 *
 * Advanced strategy:
 * 
 * - computes 2 moves ahead
 * - is based on board-value-change and its probability
 * - each tile-position has a different weight
 *
 */

public class AIStrategyMatthias extends BaseAIStrategy {

	private int[][] weights;

	private int[][] weights4x4;
	private int[][] weightsDefault;

	Double[] valueChanges;

	public AIStrategyMatthias(AIGameEngine engine) {
		super(engine);
	}

	/**
	 * Initialize weight-arrays:
	 * - for 4x4 board
	 * - for remaining board-sizes
	 */
	
	@Override
	public boolean initializeAI() {
		set4x4Weights();
		setDefaultWeights();
		return true;
	}
	
	// Sets the weights for board-size 4x4
	private void set4x4Weights() {

		weights4x4 = new int[][] { new int[] { 21, 20, 19, 15 }, new int[] { 18, 17, 15, 14 }, new int[] { 13, 12, 10, 9 }, new int[] { 9, 8, 6, 5 } };
	}

	// Sets the weights for board-sizes != 4x4
	private void setDefaultWeights() {

		weightsDefault = new int[8][8];
		int c = 1;
		for (int i = 7; i >= 0; i--) {
			for (int j = 7; j >= 0; j--) {
				weightsDefault[i][j] = c;
				c++;
			}
		}
	}

	@Override
	public Direction getMove(Tile[][] board) {

		engine.setGameBoard(board);
		
		// Choose relevant tile-weights based on the size of the active board:
		if (board.length == 4) {
			weights = weights4x4;
		} else {
			weights = weightsDefault;
		}
		
		// Reset values from previous move
		valueChanges = new Double[4];

		// Check if move is valid and set the corresponding board-value-change:
		int i = 0;
		for (Direction dir : Direction.values()) {
			if (engine.isMoveValid(dir)) {
				valueChanges[i] = calculateExpectedValue(engine.getBoard(), dir);
			}
			i++;
		}

		// Check if in case of moving up there is the risk of being forced to move "down" subsequently
		// if risk exists --> set board-value-change of "up-move" to a very low number (only for 4x4 boards)
		if (board.length == 4 &&valueChanges[0] != null) {
			Tile[][] boardAfterUp = getBoardAfterSimulatedMove(engine.getBoard(), Direction.UP);

			if (riskOfDeadLockAfterUP(boardAfterUp, 2) | riskOfDeadLockAfterUP(boardAfterUp, 1)) {
				valueChanges[0] = -10000d;
			}
		}

		// Get the highest expected board-value-change from all directions
		double maxVal = -1000000;
		for (Double v : valueChanges) {
			if (v != null && v > maxVal) {
				maxVal = v;
			}
		}

		// Return the direction with the highest expected value:
		i = 0;
		for (Direction dir : Direction.values()) {
			if (valueChanges[i] != null && valueChanges[i] == maxVal) {
				return dir;
			}
			i++;
		}
		return null;
	}

	/**
	 * 
	 * Computes the average expected board-value-change of the next 2 moves
	 * That means the method returns a "total-score" consisting of
	 * 
	 * - the board-value-change induced by @param direction PLUS...
	 * - ...for each possible scenario of tile spawned...
	 * ... the board-value-change of the best follow-up-move, multiplied with the scenario's probability 
	 * 
	 * @param clonedGameBoard
	 *            a clone of the current board
	 * @param direction
	 *            initial direction to test
	 * @return the average expected board-value-change induced by 2 moves ahead
	 */

	public double calculateExpectedValue(Tile[][] clonedGameBoard, Direction direction) {

		Tile[][] board = clonedGameBoard;
		double totalScore = 0;

		// Get get the current board's weighted value:
		double boardValueBeforeFirstMove = getBoardValue(board);

		// Simulate the next move...
		board = getBoardAfterSimulatedMove(board, direction);

		// ...and get the board-value after the move:
		double boardValueAfterFirstMove = getBoardValue(board);

		// Get the increase / decrease of board-value following the simulated move
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
					// We assume that after the spawning of the tile the move with the highest board-value change will be executed
					// Therefore the probability for a specific board-value change is:

					double probabilityOfSpawning4AtThisPosition = (0.1 / (numbOfZeros));
					double probabilityOfSpawning2AtThisPosition = (0.9 / (numbOfZeros));

					// Simulate a move to all 4 directions
					// once with a 2 spawned on the empty tile, once with a 4 spawned
					// Finally only the best out of 4 directions will be counted

					double scoreFromBestOfAllDirectionsAfterSpawning2 = -10000000;
					double scoreFromBestOfAllDirectionsAfterSpawning4 = -10000000;

					for (Direction dir : Direction.values()) {

						Tile[][] boardAfterSpawning2AndMovingAgain = getBoardAfterSimulatedMove(cloneBoard(boardWith2), dir);
						Tile[][] boardAfterSpawning4AndMovingAgain = getBoardAfterSimulatedMove(cloneBoard(boardWith4), dir);

						// if move after spawning 2 resp. 4 was invalid, board-value-change will be zero...	
						double boardValueChangeFromSecondMoveAfter2 = 0;
						double boardValueChangeFromSecondMoveAfter4 = 0;
						
						// if move was valid calculate "board-value after move" minus "board-value before move"
						
						if(boardAfterSpawning2AndMovingAgain != null){
							// The board-value-change induced by the second move (in case a 2 was spawned)
							boardValueChangeFromSecondMoveAfter2 = getBoardValue(boardAfterSpawning2AndMovingAgain) - boardValueAfterFirstMove;												
						}	

						if(boardAfterSpawning4AndMovingAgain != null){
							// The board-value-change induced by the second move (in case a 4 was spawned)
							 boardValueChangeFromSecondMoveAfter4 = getBoardValue(boardAfterSpawning4AndMovingAgain) - boardValueAfterFirstMove;
						}

						// Get only the highest board-value-change from all 4 directions
						scoreFromBestOfAllDirectionsAfterSpawning2 = Math.max(boardValueChangeFromSecondMoveAfter2, scoreFromBestOfAllDirectionsAfterSpawning2);
						scoreFromBestOfAllDirectionsAfterSpawning4 = Math.max(boardValueChangeFromSecondMoveAfter4, scoreFromBestOfAllDirectionsAfterSpawning4);
					}

					
					// multiply the highest board-value change of all 4 directions with the probability of its occurrence
					// and add it to the total score
					totalScore += (probabilityOfSpawning2AtThisPosition * scoreFromBestOfAllDirectionsAfterSpawning2);
					totalScore += (probabilityOfSpawning4AtThisPosition * scoreFromBestOfAllDirectionsAfterSpawning4);
				}
			}
		}
		// returns the average expected board-value-change induced by @param direction + the best follow-up move
		return totalScore;
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
	 * - Multiplies each board-tile's value with a weight and returns the sum:
	 * - a tile's weight depends on its position within the board
	 * - the weights are increasing from right to left / from down to up
	 * 
	 * @param gameBoard
	 *            board which was cloned before
	 * @return weighted board-value
	 */

	private double getBoardValue(Tile[][] gameBoard) {

		double total = 0;

		for (int i = 0; i < gameBoard.length; i++) {

			for (int j = 0; j < gameBoard.length; j++) {
				total += (weights[i][j] * gameBoard[i][j].getValue());
			}
		}
		return total;
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
	 * a board with Tile-Objects
	 * 
	 * @param gameBoard
	 *            a tile-board
	 * @return a of the tile-board
	 */

	private Tile[][] cloneBoard(Tile[][] gameBoard) {

		Tile[][] testBoard = SerializationUtils.clone(gameBoard);

		return testBoard;
	}
}