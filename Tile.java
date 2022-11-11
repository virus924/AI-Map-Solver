package gameModulized;

public class Tile {
	String floorType; //what type of floor this tile has - plain, hazard, block (cant walk on), goal (ends level once stepped on, gives points), death (ends game)
	String intType; //what type of interactable, if any, this tile has - none, floor lever "fl,row-row,col-col,new tile" (changes floor type for the area), lootable (gives points), used,
	String changeType; //which tile will change how if it is a lever tile
	boolean discovered;
	
	/*
	*	public Tile(String floorType, String intType) {
	*		this.floorType = floorType;
	*		this.intType = intType;
	*	}
	*/
	public Tile(String floorType, String intType, String changeType) {
		this.floorType = floorType;
		this.intType = intType;
		this.changeType = changeType;
		this.discovered = false;
	}
	
	public String getIntType() {
		return intType;
	}
	public String getFloorType() {
		return floorType;
	}
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String type) {
		this.changeType = type;
	}
	
	public void removeChangeType() {
		this.changeType = "";
	}
	
	public String[] getTileData() {
		//this one is used for the spot she is on
		String[] tileData = {floorType, intType};
		//discovered = true;
		return tileData;
	}
	public String[] getAdjTileData() {
		//this one is used for adjacent tiles she is NOT on
		String[] tileData = {floorType, intType};
		//discovered = true;
		return tileData;
	}
	
	public void setTileData(String[] data) {
		floorType = data[0];
		intType = data[1];
	}
	
	public void changeFloor(String floorType) {
		this.floorType = floorType;
		this.changeType = "changed";
	}
	
	public void useInt() {
		this.intType = "used";
	}
	
	public void discover() {
		discovered = true;
		
	}
	
	public int registerTileValue(Player p1) {
		
		//this method is where you change values for AI
		
		//change base value to -1 for slowly degrading points
		int value = 0;
		switch(floorType) {
			case "plain":
				value += p1.getPlain();
				break;
			case "lava":
				value -= p1.getLava();//was -1000
				discover();
				break;
			case "block":
				//-1 so it wont be chosen over an already visited tile (hopefully)
				//-2 so it can counteract the discovered factor
				//back to -1 because i now discover the tile if i cant traverse on it
				value -= p1.getBlock();//was -1
				discover();
				break;
		}
		
		switch(intType) {
			case "":
				value += p1.getEmpty();
				break;
			
			//this one will need to become a universal one!!!! might change how the system itself works
			case "fl":
				value += p1.getFl();
				break;
			case "loot":
				value += p1.getLoot();
				break;
			case "used":
				value += p1.getUsed();
				break;
			case "goal":
				value += p1.getGoal();
				break;
		}
		
		switch(changeType) {
			case "changed":
				value += p1.getChanged();
				break;
		}
		
		if(!discovered) {
			//System.out.println("Tile has not been discovered yet");
			value += p1.getBaseUndiscovered();
		}
			
			
		return value;
	}
}
