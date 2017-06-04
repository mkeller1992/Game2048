package ch.bfh.game2048.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

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

	@XmlElementWrapper(name = "Highscores")
	@XmlElement(name = "PlayerScore")
	private ArrayList<GameStatistics> highscores;

	public Highscore() {
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


	public ArrayList<GameStatistics> getHighscoreList() {
		return highscores;
	}


}
