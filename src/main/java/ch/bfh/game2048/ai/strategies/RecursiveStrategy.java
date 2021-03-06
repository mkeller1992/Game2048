package ch.bfh.game2048.ai.strategies;

import org.apache.commons.lang3.SerializationUtils;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class RecursiveStrategy extends BaseAIStrategy {

	private int finalDepth;
	private int[][] weights;
	private int[][] weights4x4;
	private int[][] weightsDefault;

	Double[] valueChanges;

	public RecursiveStrategy(AIGameEngine engine) {
		super(engine);
	}

	@Override
	public boolean initializeAI() {

		finalDepth = 2;
		set4x4Weights();
		setDefaultWeights();
		return true;

	}

	private void set4x4Weights() {

		// Set the weights for board-size 4x4
		weights4x4 = new int[][] { new int[] { 21, 20, 19, 15 }, new int[] { 18, 17, 15, 14 }, new int[] { 13, 12, 10, 9 }, new int[] { 9, 8, 6, 5 } };

	}

	private void setDefaultWeights() {

		// Set the weights for board-sizes != 4x4
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

		// Get the direction with the highest expected increase of board-value
		double maxVal = -1000000;

		// Check if move is valid and set the corresponding board-value-change:
		int i = 0;
		for (Direction dir : Direction.values()) {
			if (engine.isMoveValid(dir)) {
				valueChanges[i] = calculateExpectedValue(finalDepth, engine.getBoard(), dir, 1, 0);
				maxVal = Math.max(valueChanges[i], maxVal);
			}
			i++;
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
		for (int i = 0; i < board.length; i++) {

			for (int j = 0; j < board.length; j++) {

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

					// Simulate a move to all 4 directions
					// once with a 2 spawned on the empty tile, once with a 4 spawned
					// Finally only the best out of 4 directions will be counted

					double maxBoardValueChangeAfter4Spawned = -1000000;
					double maxBoardValueChangeAfter2Spawned = -1000000;

					for (Direction dir : Direction.values()) {
						// Compute expected sum of board-value-change...
						// weighted by the probability of its occurrence and assuming a 4 resp. a 2 was spawned
						double scoreFromMoveAfter4 = calculateExpectedValue((reachedDepth + 1), cloneBoard(boardWith4), dir, weightOfMoveAfter4, tempScore);
						double scoreFromMoveAfter2 = calculateExpectedValue((reachedDepth + 1), cloneBoard(boardWith2), dir, weightOfMoveAfter2, tempScore);

						// get the max. board-value-change out of all 4 move-directions...
						// ... for the case that a 4 was spawned resp. a 2 was spawned:
						maxBoardValueChangeAfter4Spawned = Math.max(scoreFromMoveAfter4, maxBoardValueChangeAfter4Spawned);
						maxBoardValueChangeAfter2Spawned = Math.max(scoreFromMoveAfter2, maxBoardValueChangeAfter2Spawned);
					}

					/*
					 * For each possible board-situation count only the
					 * expected "score" of the best out of the 4 possible moves.
					 * "Score" = the sum of the expected weighted board-value changes
					 */

					// count the best score for the case "a 4 was spawned"
					totalScore += maxBoardValueChangeAfter4Spawned;

					// count the best score for the case "a 2 was spawned"
					totalScore += maxBoardValueChangeAfter2Spawned;
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

		for (int i = 0; i < gameBoard.length; i++) {

			for (int j = 0; j < gameBoard.length; j++) {
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
