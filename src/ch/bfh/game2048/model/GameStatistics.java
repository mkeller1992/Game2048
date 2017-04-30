package ch.bfh.game2048.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Observable;


public class GameStatistics extends Observable implements Serializable{
	private static final long serialVersionUID = -591812298242721335L;

	Player player;
	int score;
	int amountOfMoves;
	int highestValue;
	Date date;
	long startMil;
	long endMil;
	boolean gameOver;
	
	public GameStatistics(Player player){
		
		this.player = player;
		this.score = 0;
		this.amountOfMoves = 0;
		this.highestValue =0;
		this.startMil = 0;
		this.endMil = 0;
		this.gameOver = false;
	}
	

	public GameStatistics(Player player, int score, int amountOfMoves, int highestValue, Date date, long startMil, long endMil, boolean gameOver) {
		super();
		this.player = player;
		this.score = score;
		this.amountOfMoves = amountOfMoves;
		this.highestValue = highestValue;
		this.date = date;
		this.startMil = startMil;
		this.endMil = endMil;
		this.gameOver = gameOver;
	}



	public String getPlayerNickname(){
		return player.getNickName();
	}
	
	private long getDuration() {
		long tDelta = endMil - startMil;
		long elapsedSeconds = tDelta / 1000;

		return elapsedSeconds;
	}
	
	public int getScore() {
		return score;
	}
	public void addScore(int score){
		this.score += score;
	}
	public int getAmountOfMoves() {
		return amountOfMoves;
	}
	public void incrementMoves() {
		this.amountOfMoves++;
	}
	public int getHighestValue() {
		return highestValue;
	}
	public void setHighestValue(int highestValue) {
		this.highestValue = highestValue;
		setChanged();
		notifyObservers();
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public long getStartMil() {
		return startMil;
	}
	public void setStartMil(long startMil) {
		this.startMil = startMil;
	}
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
	public boolean isGameOver() {
		return gameOver;
	}
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
		setChanged();
		notifyObservers();
	}
}
