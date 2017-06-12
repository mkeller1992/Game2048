package ch.bfh.game2048.ai.strategies;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.SerializationUtils;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class RecursiveStrategy extends BaseAIStrategy {

	private int finalDepth;
	private int[][] weights;
	
	double valueChangeAfterUP;
	double valueChangeAfterDOWN;
	double valueChangeAfterLEFT;
	double valueChangeAfterRIGHT;


	public RecursiveStrategy(AIGameEngine engine) {
		super(engine);

	}

	@Override
	public boolean initializeAI() {

		finalDepth = 2;

		// the weights to compute the value of a certain board-constellation:
		weights = new int[][] { new int[] { 21, 20, 19, 15 }, new int[] { 18, 17, 15, 14 }, new int[] { 13, 12, 10, 9 }, new int[] { 9, 8, 6, 5 } };
		return true;

	}

	@Override
	public Direction getMove(Tile[][] board) {

		engine.setGameBoard(board);
		
		// Reset values from previous move
		resetStats();

		// Get the direction with the highest expected increase of board-value
		double maxVal = -1000000;

		// if move up is a valid move
		if (engine.isMoveValid(Direction.UP)) {

			valueChangeAfterUP = calculateExpectedValue(0, cloneBoard(engine.getBoard()), Direction.UP, 1, 0);
			
			maxVal = Math.max(valueChangeAfterUP,maxVal);

		}

		// if move down is a valid move
		if (engine.isMoveValid(Direction.DOWN)) {
			valueChangeAfterDOWN = calculateExpectedValue(0, cloneBoard(engine.getBoard()), Direction.DOWN, 1, 0);

			maxVal = Math.max(valueChangeAfterDOWN,maxVal);

		}


		// if move left is a valid move
		if (engine.isMoveValid(Direction.LEFT)) {
		
		valueChangeAfterLEFT = calculateExpectedValue(0, cloneBoard(engine.getBoard()), Direction.LEFT, 1, 0);
		
		maxVal = Math.max(valueChangeAfterLEFT,maxVal);
		
		}
		
		
		// if move right is a valid move
		if (engine.isMoveValid(Direction.RIGHT)) {
		
		valueChangeAfterRIGHT = calculateExpectedValue(0, cloneBoard(engine.getBoard()), Direction.RIGHT, 1, 0);
		
		maxVal = Math.max(valueChangeAfterRIGHT,maxVal);
		
		}

		// Apply suitable move according to expected increases of board-value

		if (valueChangeAfterUP == maxVal && engine.isMoveValid(Direction.UP)) {
			return Direction.UP;
		}

		if (valueChangeAfterLEFT == maxVal && engine.isMoveValid(Direction.LEFT)) {
			return Direction.LEFT;
		}

		if (valueChangeAfterRIGHT == maxVal && engine.isMoveValid(Direction.RIGHT)) {
			return Direction.RIGHT;
		}

		if (valueChangeAfterDOWN == maxVal && engine.isMoveValid(Direction.DOWN)) {
			return Direction.DOWN;
		}

		System.out.println("Shouldnt be here **************************");
		
		
		return null;
	}
	
	
	

	/**
	 * 
	 * Simulates an initial move + the possible follow-up moves up to a certain depth:
	 * 
	 * - Considers all possible cases of spawned tiles
	 * - For each possible case count only the move with the highest expected "score" (NOT all 4 possible moves)
	 * - "Score" = Increases/ decreases of "board-values" weighted by the probability of their occurrence
	 * - "Board value" = Based on tile-value weighed according to its position on the board
	 * - Invalid moves result in a board-value of -1
	 * 
	 * 
	 * @param reachedDepth
	 *            the depth of the recursion
	 * @param clonedGameBoard
	 *            the cloned initial board or the board after the previous simulated move
	 * @param direction
	 *            the next direction to simulate
	 * @param weight
	 *            the probability of the move to simulate
	 * @param score
	 *            the "score" from the previous simulated move
	 * @return the summarized expected/ weighted board-value changes
	 */

	public double calculateExpectedValue(int reachedDepth, Tile[][] clonedGameBoard, Direction direction, double weight, double score) {

		Tile[][] board = clonedGameBoard;
		double totalScore = 0;

		// if move to simulate is invalid return 0...
		// ... and terminate the current trace of the recursion
		if (engine.isMoveValid(direction) == false) {
			return 0;
		}

		// ... else get the current board's weighted value:
		double boardValueBeforeMove = getBoardValue(board);

		// Simulate the next move...
		board = getBoardAfterSimulatedMove(board, direction);

		// ...and get the board-value after the move:
		double boardValueAfterMove = getBoardValue(board);

		// Get the increase / decrease of board-value following the simulated move
		double boardValueChangeFromLastMove = boardValueAfterMove - boardValueBeforeMove;

		// add new score to current temp score
		double tempScore = score + boardValueChangeFromLastMove;

		// if final depth is reached, return weighted score
		if (reachedDepth == finalDepth) {
			return tempScore * weight;
		}

		// get number of empty tiles on board after simulated move
		int numbOfZeros = getAmountOfEmptyTiles(board);

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
					// In both cases only the best out of 4 possible moves will be counted
					// Therefore these move's weight is:

					double weightOfMoveAfter4 = (weight / (numbOfZeros)) * 0.1;
					double weightOfMoveAfter2 = (weight / (numbOfZeros)) * 0.9;

					ArrayList<Double> scoresAfter4Spawned = new ArrayList<>();
					ArrayList<Double> scoresAfter2Spawned = new ArrayList<>();

					// Simulate a move to all 4 directions
					// once with a 2 spawned on the empty tile, once with a 4 spawned
					// Finally only the best out of 4 directions will be counted

					for (Direction dir : Direction.values()) {
						// Compute expected sum of board-value-change...
						// weighted by the probability of its occurrence and assuming a 4 resp. a 2 was spawned
						double scoreFromMoveAfter4 = calculateExpectedValue((reachedDepth + 1), cloneBoard(boardWith4), dir, weightOfMoveAfter4, tempScore);
						double scoreFromMoveAfter2 = calculateExpectedValue((reachedDepth + 1), cloneBoard(boardWith2), dir, weightOfMoveAfter2, tempScore);

						// add the scores for cases that a 4 resp. a 2 was spawned to a list...
						// ... so that the max. score out of these four values can be computed
						scoresAfter4Spawned.add(scoreFromMoveAfter4);
						scoresAfter2Spawned.add(scoreFromMoveAfter2);
					}

					/*
					 * For each possible board-situation count only the
					 * expected "score" of the best out of the 4 possible moves.
					 * "Score" = the sum of the expected weighted board-value changes				
					 */

					// count the best score for the case "a 4 was spawned"
					totalScore += Collections.max(scoresAfter4Spawned);

					// count the best score for the case "a 2 was spawned"
					totalScore += Collections.max(scoresAfter2Spawned);

				}
			}
		}
		// returns the expected total "score" based on the selected cases + weighted by the case's probability
		return totalScore;
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
	public int getAmountOfEmptyTiles(Tile[][] gameBoard) {
		int c = 0;

		for (Tile[] row : gameBoard) {
			for (Tile t : row) {
				if (t.getValue() == 0) {
					c++;
				}
			}
		}
		return c;
	}
	
	
	/**
	 * Reset statistics (move-values) from previous moves
	 */
	
	private void resetStats() {

		valueChangeAfterUP =  -1000000;
		valueChangeAfterDOWN =  -1000000;
		valueChangeAfterLEFT =  -1000000;
		valueChangeAfterRIGHT =  -1000000;

	}
	

	/**
	 * a board with Tile-Objects
	 * 
	 * @param gameBoard
	 *            a tile-board
	 * @return a of the tile-board
	 */

	private Tile[][] cloneBoard(Tile[][] gameBoard) {

		Tile[][]testBoard = SerializationUtils.clone(gameBoard);
		
//		Tile[][] testBoard = new Tile[gameBoard.length][gameBoard.length];
//		for (int i = 0; i < gameBoard.length; i++) {
//			for (int j = 0; j < testBoard.length; j++) {
//				testBoard[i][j] = SerializationUtils.clone(gameBoard[i][j]); ///// TODO Possibly whole arry can be cloned at once
//			}
//		}
		return testBoard;
	}
}
