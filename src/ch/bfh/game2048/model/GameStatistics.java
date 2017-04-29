package ch.bfh.game2048.model;

import java.io.Serializable;
import java.util.Date;

public class GameStatistics implements Serializable{
	private static final long serialVersionUID = -591812298242721335L;
	Player player;
	int score;
	int amountOfMoves;
	int highestValue;
	Date date;
	long startMil;
	long endMil;
	
	public GameStatistics(Player player){
		
		this.player = player;
		score = 0;
		amountOfMoves = 0;
		this.highestValue =0;
		this.startMil = 0;
		this.endMil = 0;
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
	
	
}
