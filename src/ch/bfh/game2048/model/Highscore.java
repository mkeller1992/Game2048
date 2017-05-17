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

	public Highscore() {
		comparator = new ScoreComparator();
		highscores = new ArrayList<GameStatistics>();
	}


	public List<GameStatistics> getFilteredHighscoreList(int boardSize) {		
				
		List<GameStatistics> filteredList = highscores.stream().filter(h -> h.getBoardSize() == boardSize).collect(Collectors.toList()); 
		Collections.sort(filteredList, comparator);
		setCutListAndSetRanks(filteredList);	
		return filteredList;
	}


	public void addHighscore(GameStatistics highscore) {
		highscores.add(highscore);
	}
	
	
	public ArrayList<GameStatistics> getCompleteHighscoreList() {
		return highscores;
	}
	
	
	// Cut Highscore-List to the number of allowed entries (specified in Properties)
	// Set a rank for each score according to the criteria in "ScoreComparator"

	private void setCutListAndSetRanks(List<GameStatistics> filteredList) {

		int maxNumberOfScores = Config.getInstance().getPropertyAsInt("maxNumberOfScores");
		
		if(filteredList.size() > maxNumberOfScores){
			filteredList = new ArrayList<GameStatistics>(highscores.subList(0, maxNumberOfScores));
		}

		for (int i = 0; i < filteredList.size(); i++) {
			filteredList.get(i).setRank(i + 1);
		}
	}
}
