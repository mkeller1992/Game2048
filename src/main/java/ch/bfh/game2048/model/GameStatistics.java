package ch.bfh.game2048.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Game-Statistics
 * 
 * Stores the following statistics for one single game:
 * 
 * - player-name
 * - number of moves needed
 * - value of highest tile on board
 * - start-time of the game (in milliseconds)
 * - end-time of the game (in milliseconds)
 * - board-size used (number of rows == number of columns)
 * 
 * - boolean gameOver: True when user has lost the game
 * - boolean continue: True when game-winner wants to continue playing
 * 
 * - Game-Statistics are stored to an xml-file when user exits program
 * 
 */
@XmlType(propOrder = { "playerName", "score", "amountOfMoves", "highestValue", "startTimestamp", "duration", "boardSize" })
public class GameStatistics implements Comparable<GameStatistics>, Serializable{
	private static final long serialVersionUID = 5027351164259846728L;

	private String playerName;

	private Long score;

	private Integer amountOfMoves;
		
	private Integer highestValue;

	private Long startTimestamp;

	private Long duration; // milliseconds
	
	private Integer boardSize;
	
	public GameStatistics() {

	}

	public GameStatistics(int boardSize) {
		this.score = 0l;
		this.amountOfMoves = 0;
		this.highestValue = 0;
		this.startTimestamp = System.currentTimeMillis();
		this.boardSize = boardSize;
	}
	
	public GameStatistics(String playerName, int boardSize) {

		this.playerName = playerName;
		this.score = 0l;
		this.amountOfMoves = 0;
		this.highestValue = 0;
		this.startTimestamp = System.currentTimeMillis();
		this.boardSize = boardSize;

	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Long getScore() {
		return score;
	}

	public void addScore(Long score) {
		this.score += score;
	}	

	public void setScore(Long score) {
		this.score = score;
	}

	public Integer getAmountOfMoves() {
		return amountOfMoves;
	}

	public void incrementAmountOfMoves() {
		this.amountOfMoves++;
	}
	
	public void setAmountOfMoves(Integer amountOfMoves) {
		this.amountOfMoves = amountOfMoves;
	}

	public Integer getHighestValue() {
		return highestValue;
	}

	public void setHighestValue(Integer highestValue) {
		this.highestValue = highestValue;
	}

	public Long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Integer getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(Integer boardSize) {
		this.boardSize = boardSize;
	}

	/**
	 * 
	 * Game Statistic Comparator
	 * 
	 * Sets the rules for the sorting the game-score / make a ranking
	 * 
	 * The score-ranking is made based on the following criteria (ordered by
	 * priority):
	 * 
	 * 1.) Score-Points 2.) Value of highest Tile 3.) Duration of game (the less the
	 * better) 4.) Number of moves needed (the less the better)
	 * 
	 */
	@Override
	public int compareTo(GameStatistics p2) {

		int ret = this.getScore().compareTo(p2.getScore());
		if (ret != 0)
			return ret;

		ret = this.getHighestValue().compareTo(p2.getHighestValue());
		if (ret != 0)
			return ret;

		ret = p2.getDuration().compareTo(this.getDuration());
		if (ret != 0)
			return ret;

		ret = p2.getAmountOfMoves().compareTo(this.getAmountOfMoves());
		if (ret != 0)
			return ret;
		
		return ret;
	}
}
