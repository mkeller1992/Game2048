package ch.bfh.game2048.view.model;

/**
 * Board-Sizes Enum:
 * 
 * - Stores the allowed board-sizes and their description
 * 
 * - boardSize specifies the number of rows (== number of columns)
 * - toString() specifies that the first param of the enum will be displayed as text
 * 
 */
public enum BoardSizes {
	SIZE_3("Board-Size 3x3", 3),
	SIZE_4("Board-Size 4x4", 4),
	SIZE_5("Board-Size 5x5", 5),
	SIZE_6("Board-Size 6x6", 6),
	SIZE_7("Board-Size 7x7", 7),
	SIZE_8("Board-Size 8x8", 8);
	
	
	String description;
	int boardSize;
	
	private BoardSizes(String description, int boardSize){
		
		this.description =description;
		this.boardSize=boardSize;
		
	}
	
	public int getBoardSize(){
		return boardSize;
	}
	
	
	public static BoardSizes findStateByBoardSize(int boardSize){
	    for(BoardSizes b : values()){
	        if(b.getBoardSize()==boardSize){
	            return b;
	        }
	    }
	    return null;
	}
	
	/**
	 * Causes the first enum-param to be displayed as text
	 * e.g. text showed in a combobox that was populated by this enum
	 */
	public String toString(){
		return description;
	}
	
}
