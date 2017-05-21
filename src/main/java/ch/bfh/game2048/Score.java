package ch.bfh.game2048;

public class Score {
	int score;
	String name;
	int time;
	

	public Score() {
		super();
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	/**
	 * @param score
	 * @param name
	 * @param time
	 */
	public Score(int score, String name, int time) {
		super();
		this.score = score;
		this.name = name;
		this.time = time;
	}
}
