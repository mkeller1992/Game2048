package ch.bfh.game2048.model;

import java.util.ArrayList;
import java.util.Collections;

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

	@XmlTransient
	public ArrayList<GameStatistics> getHighscore() {		
		Collections.sort(highscores, comparator);
		setCutListAndSetRanks();
		return highscores;
	}

	public void addHighscore(GameStatistics highscore) {
		highscores.add(highscore);
		Collections.sort(highscores, comparator);
		setCutListAndSetRanks();
	}

	// Cut Highscore-List to the number of allowed entries (specified in Properties)
	// Set a rank for each score according to the criteria in "ScoreComparator"
	
	private void setCutListAndSetRanks() {

		int maxNumberOfScores = Config.getInstance().getPropertyAsInt("maxNumberOfScores");
		
		if(highscores.size() > maxNumberOfScores){
		highscores = new ArrayList<GameStatistics>(highscores.subList(0, maxNumberOfScores));
		}

		for (int i = 0; i < highscores.size(); i++) {
			highscores.get(i).setRank(i + 1);
		}
	}
}
