package ch.bfh.game2048.engine;

import java.util.Comparator;

import ch.bfh.game2048.model.GameStatistics;

/**
 * 
 * Score Comparator
 * 
 * Sets the rules for the sorting the game-score / make a ranking
 * 
 * The score-ranking is made based on the following criteria
 * (ordered by priority):
 * 
 * 1.) Score-Points
 * 2.) Value of highest Tile
 * 3.) Duration of game (the less the better)
 * 4.) Number of moves needed (the less the better)
 * 
 */

public class ScoreComparator implements Comparator<GameStatistics> {

	@Override
	public int compare(GameStatistics p1, GameStatistics p2) {

		// 1.) Score-Points:
		int points1 = p1.getScore();
		int points2 = p2.getScore();

		// 2.) Value of highest Tile on Board:
		int highestTile1 = p1.getHighestValue();
		int highestTile2 = p2.getHighestValue();

		// 3.) Duration of the game (less is better):
		long duration1 = p1.getEndMil() - p1.getStartMil();
		long duration2 = p2.getEndMil() - p2.getStartMil();

		// 3.) Number of moves needed (less is better):
		int numbOfMoves1 = p1.getAmountOfMoves();
		int numbOfMoves2 = p2.getAmountOfMoves();

		if (points1 > points2) {
			return -1;
		}
		if (points1 < points2) {
			return 1;
		}

		if (highestTile1 > highestTile2) {
			return -1;
		}
		if (highestTile1 < highestTile2) {
			return 1;
		}

		if (duration1 > duration2) {
			return -1;
		}
		if (duration1 < duration2) {
			return 1;
		}

		if (numbOfMoves1 > numbOfMoves2) {
			return -1;
		}
		if (numbOfMoves1 < numbOfMoves2) {
			return 1;
		}
		return 0;
	}
}
