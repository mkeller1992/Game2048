package ch.bfh.game2048.ai.strategies;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public abstract class BaseAIStrategy {	
	AIGameEngine engine;	


	public BaseAIStrategy(AIGameEngine engine) {
		super();
		this.engine = engine;
	}

	/**
	 * returns false if something went wrong...
	 * @return
	 */
	public abstract boolean initializeAI();
	
	/**
	 * needed for hint in player GUI and for the ai-controller to play...
	 * @param board
	 * @return
	 */
	public abstract Direction getMove(Tile[][] board);


	public AIGameEngine getEngine() {
		return engine;
	}


	

	

	
}
