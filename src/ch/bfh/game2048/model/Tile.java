package ch.bfh.game2048.model;

public class Tile {
	int value;
	boolean merged;
	boolean spawned;
	
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
