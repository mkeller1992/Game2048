package ch.bfh.game2048.model;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.time.DurationFormatUtils;

@XmlType(propOrder = { "player", "score", "highestValue", "amountOfMoves", /*"date",*/ "startMil" ,"endMil" /*,"durationMil"*/ })
public class GameStatistics extends Observable {
	private Player player;

	@XmlElement(name = "Points")
	private int score;

	@XmlElement(name = "NumberOfMoves")
	private int amountOfMoves;
	private int highestValue;

	private long startMil;
	private long endMil;
	// private Date date;
	private int rank;
//	long durationMil;
	private boolean gameOver;

	// Need to be moved to "general properties" later:
	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
	NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
	String timeFormat = "HH:mm:ss";

	public GameStatistics() {

	}

	public GameStatistics(Player player) {

		this.player = player;
		this.score = 0;
		this.amountOfMoves = 0;
		this.highestValue = 0;
		this.startMil = System.currentTimeMillis();
		this.endMil = 0;
		// this.date = new Date();
//		this.durationMil = 0;
		this.gameOver = false;
	}


	public String getPlayerNickname(){
		return player.getNickName();
	}

	// public long getDuration() {
	// long tDelta = endMil - startMil;
	// long elapsedSeconds = tDelta / 1000;
	//
	// return elapsedSeconds;
	// }

	@XmlTransient
	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getRankAsString() {
		return new String(rank + ".");
	}

	public int getScore() {
		return score;
	}

	public String getFormattedScore() {
		return numberFormat.format(score);
	}

	public void addScore(int score) {
		this.score += score;
	}

	public int getAmountOfMoves() {
		return amountOfMoves;
	}

	public void incrementMoves() {
		this.amountOfMoves++;
	}

	@XmlElement(name = "HighestTile")
	public int getHighestValue() {
		return highestValue;
	}

	public void setHighestValue(int highestValue) {
		this.highestValue = highestValue;
		setChanged();
		notifyObservers();
	}

//	public Date getDate() {
//		return date;
//	}

	public String getFormattedDate() {
		return dateFormat.format(startMil);

		// return df.format(date);
	}

	@XmlElement(name = "StartMillis")
	public long getStartMil() {
		return startMil;
	}

	public void setStartMil(long startMil) {
		this.startMil = startMil;
	}

	@XmlElement(name = "EndMillis")
	public long getEndMil() {
		return endMil;
	}

	public void setEndMil(long endMil) {
		this.endMil = endMil;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@XmlTransient
	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		setEndMil(System.currentTimeMillis());
		setChanged();
		notifyObservers();
	}

//	@XmlElement(name = "Duration")
//	public long getDurationMil() {
//		return durationMil;
//	}
	
	public String getFormattedDuration() {
		return DurationFormatUtils.formatDuration(getEndMil()-getStartMil(), timeFormat);
	}

//	public void setDurationMil(long duration) {
//		this.durationMil = duration;
//	}
	
	
}
