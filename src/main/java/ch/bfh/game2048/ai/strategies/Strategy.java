package ch.bfh.game2048.ai.strategies;

public enum Strategy {
	SIMPLEUPLEFT("Simple UP/Left", SimpleUpLeftStrategy.class),
	RANDOM("Random moves", RandomStrategy.class);
	
	
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
	
}
