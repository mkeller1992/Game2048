package ch.bfh.game2048.ai.strategies;

import java.lang.reflect.Constructor;

import ch.bfh.game2048.ai.AIGameEngine;
import ch.bfh.game2048.persistence.Config;

public enum Strategy {
	SIMPLEUPLEFT("Simple UP/Left", SimpleUpLeftStrategy.class),
	RANDOM("Random moves", RandomStrategy.class),
	MATTHIASSUPERSTRAT("SUPER FANCY SHIT", AIStrategyMatthias.class);	
	
	private String description;
	private Class<? extends BaseAIStrategy> strategy;

	
	/**
	 * @param description
	 * @param strategy
	 */
	private Strategy(String description, Class<? extends BaseAIStrategy> strategy) {
		this.description = description;
		this.strategy = strategy;
				
	}

	
	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}



	public Class<? extends BaseAIStrategy> getStrategy() {
		return strategy;
	}


	public void setStrategy(Class<? extends BaseAIStrategy> strategy) {
		this.strategy = strategy;
	}


	public static Strategy findStateByDescription(String description){
	    for(Strategy s : values()){
	        if(s.getDescription().equals(description)){
	            return s;
	        }
	    }
	    return null;
	}
	

	public String toString(){
		return description;
	}
	
/**
 * - Returns Strategy.class which corresponds to @param strategy
 * - initializes GameEngine with provided board-size
 * 
 * @param strategy
 * @param boardSize
 * @return
 */
	public static BaseAIStrategy getAIStrategy(Strategy strategy, int boardSize){
		Constructor<? extends BaseAIStrategy> constructor;

		Class<? extends BaseAIStrategy> strategyClazz = strategy.getStrategy();

		try {
			constructor = strategyClazz.getConstructor(AIGameEngine.class);

			BaseAIStrategy aiStrategy = constructor.newInstance(new AIGameEngine(boardSize, Config.getInstance().getPropertyAsInt("winningNumber")));
			aiStrategy.initializeAI();
			aiStrategy.getEngine().startGame();
			return aiStrategy;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
