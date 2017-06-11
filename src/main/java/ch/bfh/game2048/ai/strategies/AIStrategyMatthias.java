package ch.bfh.game2048.ai.strategies;

import java.util.Collections;

import org.apache.commons.lang3.SerializationUtils;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class AIStrategyMatthias extends BaseAIStrategy {

	private int[][] weights;

	double valueChangeAfterUP;
	double valueChangeAfterDOWN;
	double valueChangeAfterLEFT;
	double valueChangeAfterRIGHT;

	Double[] valueChanges;

	public AIStrategyMatthias(AIGameEngine engine) {
		super(engine);

	}

	@Override
	public boolean initializeAI() {

		// the weights to compute the value of a certain board-constellation:
		weights = new int[][] { new int[] { 21, 20, 19, 15 }, new int[] { 18, 17, 15, 14 }, new int[] { 13, 12, 10, 9 }, new int[] { 9, 8, 6, 5 } };
		return true;

	}

	@Override
	public Direction getMove(Tile[][] board) {

		// Reset values from previous move
		valueChanges = new Double[board.length];


		// Check if move is valid and set the corresponding board-value-change:
		int i = 0;
		for (Direction dir : Direction.values()) {
			if (engine.isMoveValid(dir)) {
				valueChanges[i] = calculateExpectedValue(cloneBoard(engine.getBoard()), dir);
			}
			i++;
		}
	

		// Check if in case of moving up there is the risk of being forced to move "down" subsequently
		// if risk exists --> set board-value-change of "up-move" to a very low number
		if (valueChanges[0] != null) {
			Tile[][] boardAfterUp = getBoardAfterSimulatedMove(cloneBoard(board), Direction.UP);

			if (riskOfDeadLockAfterUP(boardAfterUp, 2) | riskOfDeadLockAfterUP(boardAfterUp, 1)) {
				
				System.out.println("Up: "+valueChanges[0]);
				
				valueChanges[0] = -10000d;
				

				System.out.println("Left: "+valueChanges[1]);
				System.out.println("Right: "+valueChanges[2]);
				System.out.println("Down: "+valueChanges[3]);
				System.out.println("**********************************************");
				
			}
		}

		// Set max. expected board-change out of all directions
		
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
	 * That means the total score consists of:
	 * 
	 * ... more to come soon...
	 * 
	 * 
	 * @param clonedGameBoard
	 * @param direction
	 * @return
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
		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 4; j++) {

				if (board[i][j].getValue() == 0) {

					// a board and set a 4 resp. a 2 at the position of the empty tile

					Tile[][] boardWith4 = cloneBoard(board);
					boardWith4[i][j].setValue(4);

					Tile[][] boardWith2 = cloneBoard(board);
					boardWith2[i][j].setValue(2);

					// Given a 2 or a 4 was spawned on the empty tile:
					// We assume that after the spawning the move with the highest board-value change will be conducted
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

						// The board-value-change induced by the second move (in case a 2 resp. a 4 was spawned)
						double boardValueChangeFromSecondMoveAfter2 = getBoardValue(boardAfterSpawning2AndMovingAgain) - boardValueAfterFirstMove;
						double boardValueChangeFromSecondMoveAfter4 = getBoardValue(boardAfterSpawning4AndMovingAgain) - boardValueAfterFirstMove;

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

		// counter horizontal:

		for (int i = 0; i < boardAfterMove.length; i++) {

			for (int j = 0; j < boardAfterMove[0].length - 1; j++) {

				if (boardAfterMove[i][j].getValue() != 0 && boardAfterMove[i][j].getValue() == boardAfterMove[i][j + 1].getValue()) {
					return false;
				}
			}
		}

		// counter vertical

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
	 * - Return the board-constellation after that move
	 * 
	 * @param board
	 *            an already cloned board
	 * @param dir
	 *            direction of the move to simulate
	 * @return the board constellation after the move
	 */

	private Tile[][] getBoardAfterSimulatedMove(Tile[][] board, Direction dir) {
		Tile[][] tmpBoard = engine.getBoard();
		Tile[][] retVal;

		engine.setGameBoard(board);
		engine.move(dir, true);

		retVal = engine.getBoard();

		engine.revertMove();
		engine.setGameBoard(tmpBoard);

		return retVal;
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

		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 4; j++) {
				total += (weights[i][j] * gameBoard[i][j].getValue());
			}
		}
		return total;
	}

	/**
	 * A helper method counting the empty tiles on the board
	 * 
	 * @param gameBoard
	 * @return number of zeros resp. empty tiles on the board
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