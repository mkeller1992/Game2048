package ch.bfh.game2048.view.model;

/**
 * Scenes Enum:
 * 
 * Stores the names (keys) of the available scenes
 *
 */
public enum Scene {

	MAINSCENE("Game"),
	HIGHSCORE("Highscore"),
	SINGLEAI("Single AI"),
	MULTIAI("Multi AI"),
	SETTINGS("Settings");
	
	String description;

	/**
	 * @param description
	 */
	private Scene(String description) {
		this.description = description;
	}
	
	
	public String toString(){
		return description;
	}
	
	
	
}
