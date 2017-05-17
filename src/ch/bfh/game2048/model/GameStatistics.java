package ch.bfh.game2048.model;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.time.DurationFormatUtils;

import ch.bfh.game2048.persistence.Config;

@XmlType(propOrder = { "player", "score", "highestValue", "amountOfMoves", "startMil" ,"endMil","boardSize" })
public class GameStatistics extends Observable {
	private Player player;

	@XmlElement(name = "Points")
	private int score;

	@XmlElement(name = "NumberOfMoves")
	private int amountOfMoves;
	private int highestValue;

	private long startMil;
	private long endMil;
	private long pauseTimeMil;
	private int rank;
	private int boardSize;	
	private boolean gameOver;
	private boolean gameContinue;

	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
	NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
	String timeFormat = Config.getInstance().getPropertyAsString("timerTimeFormat");

	public GameStatistics() {
		
	}

	public GameStatistics(Player player, int boardSize) {

		this.player = player;
		this.score = 0;
		this.amountOfMoves = 0;
		this.highestValue = 0;
		this.startMil = System.currentTimeMillis();
		this.endMil = 0;
		this.pauseTimeMil = 0;
		this.boardSize = boardSize;
		this.gameOver = false;
		this.gameContinue = false;
		
	}


	public String getPlayerNickname(){
		return player.getNickName();
	}

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

	public String getFormattedDate() {
		return dateFormat.format(startMil);
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
	
	public String getFormattedDuration() {
		return DurationFormatUtils.formatDuration(getEndMil()-getStartMil(), timeFormat);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@XmlElement(name = "BoardSize")
	public int getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	}

	@XmlTransient
	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		setEndMil(System.currentTimeMillis()-pauseTimeMil);
		setChanged();
		notifyObservers();
	}

	public void setGameContinue(boolean gameContinue) {
		this.gameContinue = gameContinue;
	}

	@XmlTransient
	public boolean isGameContinue() {
		return gameContinue;
	}
	
	public void pauseTime(){
		setEndMil(System.currentTimeMillis());
	}
	
	public void resumeTime(){
		this.pauseTimeMil += System.currentTimeMillis()-endMil;
		
	}
	
}
