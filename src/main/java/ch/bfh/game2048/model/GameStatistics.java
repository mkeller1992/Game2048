package ch.bfh.game2048.model;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import ch.bfh.game2048.persistence.Config;

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
@XmlType(propOrder = { "playerName", "score", "highestValue", "amountOfMoves", "startMil", "endMil", "boardSize" })
public class GameStatistics extends Observable implements Comparable<GameStatistics>{

	private String playerName;

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
	
	NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
	String timeFormat = Config.getInstance().getPropertyAsString("timerTimeFormat");

	public GameStatistics() {

	}

	public GameStatistics(String playerName, int boardSize) {

		this.playerName = playerName;
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

	@XmlElement(name = "PlayerName")
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
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

	public Integer getScore() {
		return score;
	}

	public String getFormattedScore() {
		return numberFormat.format(score);
	}

	public void addScore(int score) {
		this.score += score;
	}

	public Integer getAmountOfMoves() {
		return amountOfMoves;
	}

	public void incrementMoves() {
		this.amountOfMoves++;
	}

	@XmlElement(name = "HighestTile")
	public Integer getHighestValue() {
		return highestValue;
	}

	public void setHighestValue(int highestValue) {
		this.highestValue = highestValue;
		setChanged();
		notifyObservers();
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
	
	public Long getDuration(){
		return endMil-startMil;
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
		setEndMil(System.currentTimeMillis() - pauseTimeMil);
		setChanged();
		notifyObservers();
	}

	/**
	 * Set to true when player wants to continue playing after reaching game-winning tile
	 * --> To prevent that the victory alert is displayed multiple times
	 */

	public void setGameContinue(boolean gameContinue) {
		this.gameContinue = gameContinue;
	}

	/**
	 * Returns true when game-winner wants to continue playing
	 * --> To prevent that the victory alert is displayed multiple times
	 */

	@XmlTransient
	public boolean isGameContinue() {
		return gameContinue;
	}

	/**
	 * Set endMil = time at beginning of the pause -->
	 * This is only temporary so that at the end of the pause
	 * the pause-duration can be computed by deducting "endTime"
	 * 
	 */

	public void pauseTime() {
		setEndMil(System.currentTimeMillis());
	}

	/**
	 * Previously endMil was set = time at beginning of the pause
	 * Therefore the pause-duration can be computed by computing
	 * "current time at resumption" minus "endMil"
	 * 
	 */
	public void resumeTime() {
		this.pauseTimeMil += System.currentTimeMillis() - endMil;
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
