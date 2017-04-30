package ch.bfh.game2048.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "HighscoreList")
public class Highscore {

	@XmlElementWrapper(name = "Highscores")
	@XmlElement(name = "PlayerScore")
	private ArrayList<GameStatistics> highscores;

	public Highscore() {

		highscores = new ArrayList<GameStatistics>();
	}

	@XmlTransient
	public ArrayList<GameStatistics> getHighscore() {
		setRanks(highscores);
		return highscores;
	}

	public void setHighscore(ArrayList<GameStatistics> highscores) {
		this.highscores = highscores;
	}

	public void setRanks(ArrayList<GameStatistics> highscores) {

		for (GameStatistics scoreObject : highscores) {

			int numbOfBetterScores = 0;

			for (GameStatistics e : highscores) {

				if (e.getScore() > scoreObject.getScore()) {
					numbOfBetterScores++;
				}
			}
			scoreObject.setRank(numbOfBetterScores + 1);
		}
	}

}
