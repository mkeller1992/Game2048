package ch.bfh.game2048.ai.strategies;

import java.util.Random;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.model.Direction;
import ch.bfh.game2048.model.Tile;

public class RandomStrategy extends BaseAIStrategy {
	Random rand;

	public RandomStrategy(AIGameEngine engine) {
		super(engine);
	}

	@Override
	public boolean initializeAI() {
		rand = new Random();
		return true;
	}

	@Override
	public Direction getMove(Tile[][] board) {
		
		engine.setGameBoard(board);
		
		Direction dir;
		do {
			dir = Direction.values()[rand.nextInt(3)];

		} while (!engine.isMoveValid(dir));

		return dir;
	}

}
