package ch.bfh.game2048.ai.strategies;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class UpUpLeftStrategy extends BaseAIStrategy {			
	public UpUpLeftStrategy(AIGameEngine engine) {
		super(engine);
	}
	
	@Override
	public Direction getMove(Tile[][] board) {			
		engine.setGameBoard(board);
		
		engine.setGameBoard(board);
					
		if(engine.isMoveValid(Direction.UP)){
			return Direction.UP;			
		} else if(engine.isMoveValid(Direction.LEFT)) {
			return Direction.LEFT;
		} else if(engine.isMoveValid(Direction.RIGHT)){
			return Direction.RIGHT;
		} else {
			return Direction.DOWN;
		}		

	}

	@Override
	public boolean initializeAI() {		
		return true;
	}


}
