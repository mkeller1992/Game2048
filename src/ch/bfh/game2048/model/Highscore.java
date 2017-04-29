package ch.bfh.game2048.model;

import java.util.ArrayList;

public class Highscore {
	
	ArrayList<GameStatistics> highscore;
	
	public Highscore(){	
		
	}

	public ArrayList<GameStatistics> getHighscore() {
		
		return highscore;
	}

	public void setHighscore(ArrayList<GameStatistics> highscore) {
		this.highscore = highscore;
	}
	

	
	
}


