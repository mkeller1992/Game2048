package ch.bfh.game2048.view.model;

import ch.bfh.game2048.model.GameStatistics;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HighscoreEntry {
	private IntegerProperty rank;
	private StringProperty nickname;
	private LongProperty score;
	private IntegerProperty maxTile;
	private LongProperty duration;
	private IntegerProperty numOfMoves;
	private LongProperty timestamp;
	private IntegerProperty boardsize;
	
	
	
	
	/**
	 * 
	 */
	public HighscoreEntry() {
		super();
	}
	
	public HighscoreEntry(GameStatistics stats){		
		this.nickname.set(stats.getPlayerName());
		this.score.set(stats.getScore());
		this.maxTile.set(stats.getHighestValue());
		this.duration.set(stats.getDuration());
		this.numOfMoves.set(stats.getAmountOfMoves());
		this.timestamp.set(stats.getStartTimestamp());
		this.boardsize.set(stats.getBoardSize());
	}
	
	public HighscoreEntry(int rank, String nickname, long score, int maxtile, long duration, int numOfMoves, long timestamp, int boardsize){
		this.rank = new SimpleIntegerProperty(rank);
		this.nickname = new SimpleStringProperty(nickname);
		this.score = new SimpleLongProperty(score);
		this.maxTile = new SimpleIntegerProperty(maxtile);
		this.duration = new SimpleLongProperty(duration);			
		this.numOfMoves = new SimpleIntegerProperty(numOfMoves);
		this.timestamp = new SimpleLongProperty(timestamp);
		this.boardsize = new SimpleIntegerProperty(boardsize);
	}
	

	/**
	 * @param rank
	 * @param nickname
	 * @param score
	 * @param maxTile
	 * @param duration
	 * @param numOfMoves
	 * @param timestamp
	 * @param boardsize
	 */
	public HighscoreEntry(IntegerProperty rank, StringProperty nickname, LongProperty score, IntegerProperty maxTile, LongProperty duration, IntegerProperty numOfMoves, LongProperty timestamp, IntegerProperty boardsize) {
		super();
		this.rank = rank;
		this.nickname = nickname;
		this.score = score;
		this.maxTile = maxTile;
		this.duration = duration;
		this.numOfMoves = numOfMoves;
		this.timestamp = timestamp;
		this.boardsize = boardsize;
	}

	public Integer getRank() {
		return rank.get();
	}

	public void setRank(Integer rank) {
		this.rank.set(rank);
	}

	public String getNickname() {
		return nickname.get();
	}

	public void setNickname(String nickname) {
		this.nickname.set(nickname);
	}

	public Long getScore() {
		return score.get();
	}

	public void setScore(Long score) {
		this.score.set(score);
	}

	public Integer getMaxTile() {
		return maxTile.get();
	}

	public void setMaxTile(Integer maxTile) {
		this.maxTile.set(maxTile);
	}

	public Long getDuration() {
		return duration.get();
	}

	public void setDuration(Long duration) {
		this.duration.set(duration);
	}

	public Integer getNumOfMoves() {
		return numOfMoves.get();
	}

	public void setNumOfMoves(Integer numOfMoves) {
		this.numOfMoves.set(numOfMoves);
	}

	public Long getTimestamp() {
		return timestamp.get();
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp.set(timestamp);
	}

	public Integer getBoardsize() {
		return boardsize.get();
	}

	public void setBoardsize(Integer boardsize) {
		this.boardsize.set(boardsize);
	}

}
