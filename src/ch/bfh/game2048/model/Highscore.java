package ch.bfh.game2048.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import ch.bfh.game2048.engine.ScoreComparator;
import ch.bfh.game2048.persistence.Config;

@XmlRootElement(name = "HighscoreList")
public class Highscore {

	private ScoreComparator comparator;

	@XmlElementWrapper(name = "Highscores")
	@XmlElement(name = "PlayerScore")
	private ArrayList<GameStatistics> highscores;	
	
	@XmlTransient
	private List<GameStatistics> filteredList;

	public Highscore() {
		comparator = new ScoreComparator();
		highscores = new ArrayList<GameStatistics>();
		filteredList = new ArrayList<GameStatistics>();
	}

	@XmlTransient
	public List<GameStatistics> getHighscore() {		
		return filteredList;
	}

	public void addHighscore(GameStatistics highscore) {
		highscores.add(highscore);
	}
	
	// Filter scores with certain boardSize, rank them
	// cut them down to a certain amount of scores
	public void prepareScoreList(int boardSize){
		updateFilteredList(boardSize);
		Collections.sort(filteredList, comparator);
		setCutListAndSetRanks();		
	}
	
	
	// Filter scores with certain boardSize and put it in new List
	public void updateFilteredList(int boardSize){	

		filteredList = highscores.stream().filter(h -> h.getBoardSize() == boardSize).collect(Collectors.toList()); 

	}
	

	// Cut Highscore-List to the number of allowed entries (specified in Properties)
	// Set a rank for each score according to the criteria in "ScoreComparator"
	
	private void setCutListAndSetRanks() {

		int maxNumberOfScores = Config.getInstance().getPropertyAsInt("maxNumberOfScores");
		
		if(filteredList.size() > maxNumberOfScores){
			filteredList = new ArrayList<GameStatistics>(highscores.subList(0, maxNumberOfScores));
		}

		for (int i = 0; i < filteredList.size(); i++) {
			filteredList.get(i).setRank(i + 1);
		}
	}
}
