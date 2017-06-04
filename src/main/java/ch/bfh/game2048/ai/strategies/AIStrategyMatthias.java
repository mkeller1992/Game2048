package ch.bfh.game2048.ai.strategies;

import java.util.ArrayList;
import java.util.Collections;

import ch.bfh.game2048.engine.GameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class AIStrategyMatthias extends BaseAIStrategy {
	
	int finalDepth = 2;

	// the weights to compute the value of a certain board-constellation:
	int[][] weights = new int[][] { new int[] { 21, 20, 19, 15 }, new int[] { 18, 17, 15, 14 }, new int[] { 13, 12, 10, 9 }, new int[] { 9, 8, 6, 5 } };



	/**
	 * 
	 * Simulates an initial move + the possible follow-up moves up to a certain depth:
	 * 
	 * - Considers all possible cases of spawned tiles
	 * - For each possible case count only the move with the highest expected "score" (NOT all 4 possible moves)
	 * - "Score" =  Increases/ decreases of "board-values" weighted by the probability of their occurrence
	 * - "Board value" = Based on tile-value weighed according to its position on the board
	 * - Invalid moves result in a board-value of -1
	 * 
	 * 
	 * @param reachedDepth
	 *            the depth of the recursion
	 * @param gameBoard
	 *            the game-board after the previous simulated move
	 * @param direction
	 *            the next direction to simulate
	 * @param weight
	 *            the probability of the move to simulate
	 * @param score
	 *            the "score" from the previous simulated move
	 * @return the summarized expected/ weighted board-value changes
	 */

	public double calculateExpectedValue(int reachedDepth, int[][] gameBoard, String direction, double weight, double score) {

		int[][] board = gameBoard;
		double totalScore = 0;

		// Simulate a move and retrieve the corresponding statistics-object
		MoveStats moveStats = simulateMove(board, direction);

		// Get the increase / decrease of board-value following the simulated move
		Double boardValueChangeFromLastMove = moveStats.getBoardValueChange();

		// if last simulated move was invalid (-> board-value change == null) return 0...
		// ... and hereby terminate this trace of the recursion
		if (boardValueChangeFromLastMove == null) {
			return 0;
		}	
		
		// add new score to current temp score
		double tempScore = score + boardValueChangeFromLastMove;


		// if final depth is reached, return weighted score
		if (reachedDepth == finalDepth) {
			return tempScore * weight;
		}

		// get number of empty tiles on board after simulated move
		int numbOfZeros = moveStats.getNumbOfZerosAfterMove();

		// for each empty tile on current board compute the case that:
		// - a 4 will be spawned
		// - a 2 will be spawned
		for (int i = 0; i < 4; i++) {

			for (int j = 0; j < 4; j++) {

				if (board[i][j] == 0) {

					// Clone a board and set a 4 resp. a 2 at the position of the empty tile

					int[][] boardWith4 = cloneBoard(board);
					boardWith4[i][j] = 4;

					int[][] boardWith2 = cloneBoard(board);
					boardWith2[i][j] = 2;

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
					 * 
					 */

					// count the best score for the case "a 4 was spawned"
					totalScore +=Collections.max(scoresAfter4Spawned);
					
					// count the best score for the case "a 2 was spawned"				
					totalScore += Collections.max(scoresAfter2Spawned);				
					
				}
			}
		}
		// returns the expected total "score" based on the selected cases + weighted by the case's probability
		return totalScore;
	}



	@Override
	public boolean initializeAI() {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public Direction getMove(Tile[][] board) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
