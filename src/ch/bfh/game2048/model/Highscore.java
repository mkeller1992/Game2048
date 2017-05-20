package ch.bfh.game2048.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ch.bfh.game2048.engine.ScoreComparator;

/**
 * 
 * Highscore:
 * 
 * Stores the GameStatistics-objects and comes with tools for editing score-list
 * 
 * Includes:
 * 
 * > Getting full score-list or just an extract based on board-size
 * > Methods to sort, resize and set ranks on given score-lists
 *
 */




@XmlRootElement(name = "HighscoreList")
public class Highscore {

	private ScoreComparator comparator;

	@XmlElementWrapper(name = "Highscores")
	@XmlElement(name = "PlayerScore")
	private ArrayList<GameStatistics> highscores;

	public Highscore() {
		comparator = new ScoreComparator();
		highscores = new ArrayList<GameStatistics>();
	}
	
	/**
	 * Add a single GameStatistics object (== a single game-score)
	 * 
	 * @param highscore
	 */
	 
	

	public void addHighscore(GameStatistics highscore) {
		highscores.add(highscore);
	}

	/**
	 * get the score-list including the scores of ALL board-sizes
	 * 
	 * > returned list is sorted according to the criteria in "ScoreComparator"-Class
	 * 
	 * @return a list including all available scores
	 */
	
	public ArrayList<GameStatistics> getCompleteScoreList() {
		sortList(highscores);
		return highscores;
	}

	/**
	 * 
	 * Get the score-list for a certain board-size
	 * 
	 * > returned list is sorted according to the criteria in "ScoreComparator"-Class
	 * 
	 * @param boardSize : Board-size for which a score-list is to be generated
	 * @return a list including scores of a given @param boardSize
	 */
	
	public List<GameStatistics> getFilteredScoreList(int boardSize) {

		List<GameStatistics> filteredList = highscores.stream().filter(h -> h.getBoardSize() == boardSize)
				.collect(Collectors.toList());
		sortList(filteredList);
		return filteredList;
	}

	
	/**
	 * 
	 * Sorts highscore-list, sets ranks of the scores and resizes list to a given number of entries
	 * 
	 * > sorting according to criteria in "ScoreComparator"-Class
	 * > Resizing cuts maximally the first x entries of the list (x = @param maxNumbOfScoresToInclude)
	 * 
	 * @param filteredList : base-list with GameStatistics-objects
	 * @param maxNumbOfScoresToInclude : number of scores to be included in list
	 * @return
	 */


	public List<GameStatistics> sortSetRanksAndResizeList(List<GameStatistics> filteredList, int maxNumbOfScoresToInclude) {

		setRanks(filteredList);
		List<GameStatistics> list = resizeList(filteredList, 1, maxNumbOfScoresToInclude);
		return list;

	}

	/**
	 * 
	 * Sort given score-list according to criteria in "ScoreComparator"-Class
	 * 
	 * @param filteredList : list with GameStatistics-Objects to be sorted
	 */
	
	
	private void sortList(List<GameStatistics> filteredList) {
		Collections.sort(filteredList, comparator);
	}

	
	/**
	 * Resize score-list to a given number of GameStatistics-objects
	 * 
	 * 
	 * @param filteredList : base-list with GameStatistics-objects
	 * @param firstRank : first rank of base-list to be included in resulting list
	 * @param lastRank : last rank of base-list to be included in resulting list
	 * @return
	 */
	
	public List<GameStatistics> resizeList(List<GameStatistics> filteredList, int firstRank, int lastRank) {

		// Checks first if the list contains the number of requested ranks:
		if (filteredList.size() > (lastRank - firstRank + 1)) {
			filteredList = new ArrayList<GameStatistics>(filteredList.subList(firstRank - 1, lastRank));
		}
		return filteredList;
	}
	

	/**
	 * Sets the score-ranks in a given list of GameStatistics-objects
	 * 
	 * > 1.) Sorts the list according to criteria in "ScoreComparator"-Class
	 * > 2.) Sets the ranks of the GameStatistics-objects --> rank = 1 + index in sorted list
	 * 
	 * @param filteredList : relevant score-list for setting the ranks
	 */
	
	public void setRanks(List<GameStatistics> filteredList) {

		sortList(filteredList);
		for (int i = 0; i < filteredList.size(); i++) {
			filteredList.get(i).setRank(i + 1);
		}
	}

	
	/**
	 * Gets a GameStatistics-object's current score-rank in the given highscore-list
	 * 
	 * > 1.) Sorts list according to criteria in "ScoreComparator"-Class
	 * > 2.) Computes rank of given GameStatistics-object (== its index+1 in sorted list)
	 * 
	 * @param filteredList : relevant score-list for computing the rank
	 * @param scorelistEntry : score whose rank is to be computed
	 * @return current rank of provided scorelistEntry
	 */
	
	public int getRankOfListEntry(List<GameStatistics> filteredList, GameStatistics scorelistEntry) {

		sortList(filteredList);

		int i = 0;

		for (GameStatistics s : filteredList) {
			if (s.equals(scorelistEntry)) {
				return i + 1;
			} else {
				i++;
			}
		}
		return 0;
	}
}
