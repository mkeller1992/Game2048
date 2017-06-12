package ch.bfh.game2048.ai.strategies;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class SimpleUpLeftStrategy extends BaseAIStrategy {
	Direction lastMove = Direction.UP;		
		
	public SimpleUpLeftStrategy(AIGameEngine engine) {
		super(engine);
	}


	
	@Override
	public Direction getMove(Tile[][] board) {			
		Direction moveDir;
		
		engine.setGameBoard(board);
		
		// TODO Auto-generated method stub				
		if(lastMove.equals(Direction.UP)){
			lastMove = Direction.LEFT;
			moveDir = Direction.LEFT;
		} else {
			lastMove = Direction.UP;
			moveDir = Direction.UP;
		}
		
		if(engine.isMoveValid(moveDir)){
//			System.out.println("suggesting moving in direction: " + moveDir);
			return moveDir;			
		} else {
			for(Direction d : Direction.values()){
				if(engine.isMoveValid(d)){
//					System.out.println("moving in alternative direction: " + d);
					return d;
				}
			}
		}
		
		return null;	
	}

	@Override
	public boolean initializeAI() {		
		return true;
	}


}
