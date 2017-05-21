package ch.bfh.game2048.model;

/**
 * Tile
 * 
 * Stores the following information about the state of a single board-tile:
 * 
 * - its value (number which will be displayed on it)
 * - whether it's just been merged with another tile
 * - whether it's just been spawned
 * 
 * The information whether a tile has just been merged or spawned is used to
 * apply certain visual effects on the corresponding tile-label
 * 
 * 
 */
public class Tile {
	private int value;
	private boolean merged;
	private boolean spawned;
	
	public Tile() {
		value = 0;
		merged = false;
		spawned = false;
	}	
	
	public int getValue() {		
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean isMerged() {
		return merged;
	}
	public void setMerged(boolean merged) {
		this.merged = merged;
	}
	public boolean isSpawned() {
		return spawned;
	}
	public void setSpawned(boolean spawned) {
		this.spawned = spawned;
	}
}
