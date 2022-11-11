package gameModulized;

public class Player {
	Tile[][] privMap;
	//score is the personal score of the bot, not a big deal
	private int score = 0;
	private int[] location = new int[2];
	private String moves = "START TEST";
	private int lastMove;//1 = up, 2 = down, 3 = left, 4 = right
	
	//modularization is here
		//values are set in the player itself, so I can use a central array in GameClient to change everything
	private int plain;				//located in Tile.RegisterTileValue
	private int lava;
	private int block;
	private int empty;
	private int fl;
	private int loot;
	private int used;
	private int goal;
	private int changed;
	private int baseUndiscovered;
	private int hillValue;			//located in Player.setLocation
	private int undiscoveredValue;	//located in Player.searchAlg1
	private int streak;				//located in GameClient.getAreaData
	
	public Player(int mapRow, int mapCol, int startRow, int startCol, int[] values) {
		privMap = new Tile[mapRow][mapCol];
		initiateMap();
		location[0] = startRow;
		location[1] = startCol;
		moves += " - Location - [" + startRow + "," + startCol + "]";
		plain = values[0];
		lava = values[1];
		block = values[2];
		empty = values[3];
		fl = values[4];
		loot = values[5];
		used = values[6];
		goal = values[7];
		changed = values[8];
		baseUndiscovered = values[9];
		hillValue = values[10];
		undiscoveredValue = values[11];
		streak = values[12];
		
		
	}
	
	public Player() {
		
	}
	
	private void initiateMap() {
		for (int i = 0; i < privMap.length; i++) {
			for (int j = 0; j < privMap[i].length; j++) {
				if(i == 0 || i == privMap.length || j == 0 || j == privMap[i].length) {
					//privMap[i][j] = new Tile("block","none","0");
					privMap[i][j] = new Tile("?","?","0");
				} else {
					privMap[i][j] = new Tile("?","?","0");
				}
				//if I give the player the outside edges as all blocks, the search algorithm that I am working on
					//might be made that much better
			}
		}
	}
	
	public String[] getLocalData() {
		return privMap[location[0]][location[1]].getTileData();
	}
	public void setLocalData(String[] data) {
		privMap[location[0]][location[1]].setTileData(data);
	}
	
	public void changeScore(int value) {
		//System.out.println("Adding " + value + " to score");
		score += value;
	}
	
	public int getPlain() {
		return plain;
	}
	public int getLava() {
		return lava;
	}
	public int getBlock() {
		return block;
	}
	public int getEmpty() {
		return empty;
	}
	public int getFl() {
		return fl;
	}
	public int getLoot() {
		return loot;
	}
	public int getUsed() {
		return used;
	}
	public int getGoal() {
		return goal;
	}
	public int getChanged() {
		return changed;
	}
	public int getBaseUndiscovered() {
		return baseUndiscovered;
	}
	public int getHillValue() {
		return hillValue;
	}
	public int getUndiscoveredValue() {
		return undiscoveredValue;
	}
	public int getStreak() {
		return streak;
	}
	
	
	public int[] getLocation() {
		return location;
	}
	public int getScore() {
		return score;
	}
	public String getMoves() {
		return moves;
	}
	public int getLastMove() {
		return lastMove;
	}
	
	public void setLocation(int row, int col, int lastMove) {
		//this will be called from the moving or interact functions, will allow teleport pads
		location[0] = row;
		location[1] = col;
		
		this.lastMove = lastMove;
		
		//deadZoneTracker
		///* this is the hill method penalty increase
		int value = Integer.parseInt(privMap[row][col].getChangeType());
		//value += 1;
		value += hillValue;
		privMap[row][col].setChangeType("" + value);
		//*/
	}
	
	public void findDeadArea(Player p1, Tile[][] map, int[] location) {
		//this is my "balloon method". will take a rectangle and determine if it is a dead zone.
		//while(need to change to move on) {
			/*
			start point - find nearest wall, set that as min or max
			continue expanding, get the next limit (next closest wall)
			get the 3rd limit using other values
			find the 4th limit
			then, analyze the surrounding blocks
			more than 1 exit (exit is keyword) without being a dead end and it will cancel
			lets see
			
			*/
			
			//move out in each direction (main 4) until a wall is hit
			boolean wall = false;
			int wallInt = 0;
			int iMax = 0;
			int iMin = 0;
			int jMax = 0;
			int jMin = 0;
			int test = 0;
			while(!wall) {
				test += 1;
				if(privMap[location[0] - test][location[1]].getFloorType() == "block" || privMap[location[0]][location[1]].getFloorType() == "lava" || privMap[location[0]][location[1]].getFloorType() == "?") {
					iMin = location[0] - test;
					wall = true;
				}
				if(privMap[location[0] + test][location[1]].getFloorType() == "block" || privMap[location[0]][location[1]].getFloorType() == "lava" || privMap[location[0]][location[1]].getFloorType() == "?") {
					iMax = location[0] + test;
					wall = true;
				}
				if(privMap[location[0]][location[1] - test].getFloorType() == "block" || privMap[location[0]][location[1]].getFloorType() == "lava" || privMap[location[0]][location[1]].getFloorType() == "?") {
					jMin = location[1] - test;
					wall = true;
				}
				if(privMap[location[0]][location[1] - test].getFloorType() == "block" || privMap[location[0]][location[1]].getFloorType() == "lava" || privMap[location[0]][location[1]].getFloorType() == "?") {
					jMin = location[1] + test;
					wall = true;
				}
			}
			//so it found a wall, now to find the two corners
			//can i make this so that it is universal? Dont think so ATM
			
			//this section only works if all map edges are blocks or lava
			if(iMin != 0) {
				//find j min and max
				for(int j = location[1]; j > map[0].length; j++) {
					if(map[iMin][j].getFloorType() == "block" || map[iMin][j].getFloorType() == "lava") {
						jMax = j - 1;
						break;
					} else if (map[iMin][j].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
						break;
					}
				}
				for(int j = location[1]; j <= 0; j--) {
					if(map[iMin][j].getFloorType() == "block" || map[iMin][j].getFloorType() == "lava") {
						jMin = j + 1;
					} else if (map[iMin][j].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
					}
				}
				
			} else if(iMax != 0){
				for(int j = location[1]; j > map[0].length; j++) {
					if(map[iMax][j].getFloorType() == "block" || map[iMax][j].getFloorType() == "lava") {
						jMax = j - 1;
						break;
					} else if (map[iMax][j].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
						break;
					}
				}
				for(int j = location[1]; j <= 0; j--) {
					if(map[iMax][j].getFloorType() == "block" || map[iMax][j].getFloorType() == "lava") {
						jMin = j + 1;
					} else if (map[iMax][j].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
					}
				}
				
			} else if (jMin != 0) {
				for(int i = location[0]; i > map.length; i++) {
					if(map[jMin][i].getFloorType() == "block" || map[jMin][i].getFloorType() == "lava") {
						iMax = i - 1;
						break;
					} else if (map[jMin][i].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
						break;
					}
				}
				for(int i = location[0]; i <= 0; i--) {
					if(map[jMin][i].getFloorType() == "block" || map[jMin][i].getFloorType() == "lava") {
						iMin = i + 1;
					} else if (map[jMin][i].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
					}
				}
				
			} else if(jMax != 0){
				for(int i = location[0]; i > map.length; i++) {
					if(map[jMax][i].getFloorType() == "block" || map[jMax][i].getFloorType() == "lava") {
						iMax = i - 1;
						break;
					} else if (map[jMax][i].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
						break;
					}
				}
				for(int i = location[0]; i <= 0; i--) {
					if(map[jMax][i].getFloorType() == "block" || map[jMax][i].getFloorType() == "lava") {
						iMin = i + 1;
					} else if (map[jMax][i].getFloorType() == "?") {
						System.out.println("Early Termination - Undiscovered Block Detected");
					}
				}
				
			} else {
				System.out.println("THIS SECTION SHOULD NOT RUN");
				//return void.class;
			}
			
			
			
			//now I need to get it moving properly
			//have it take the values and move until it reaches a block, and mark all those blocks as a deadzone IF AND ONLY IF the process completes.
			//it should terminate if it finds a ? block, or if it is connected to NON dead blocks in more than one area.
			if(iMin != 0) {
				//use a nested for loop, the other i value is unknown
				for(int i = iMin; i < map.length; i++) {
					for(int j = jMin; j < jMax; j++) {
						//this is checking the tiles
						if(privMap[i][j].getFloorType() == "?") {
							System.out.println("Undiscovered Tile Found Within Range");
							break;
						}
						
						//need to check if it gets through fully to mark the area as dead, need to check nearby tiles too
					}
				}
			} else if (iMax != 0) {
				for(int i = iMax; i >= 0; i--) {
					for(int j = jMin; j < jMax; j++) {
						
					}
				}
			} else if (jMin != 0) {
				for(int i = iMin; i < iMax; i++) {
					for(int j = jMin; j < map[i].length; j++) {
						
					}
				}
			} else if (jMax != 0) {
				for(int i = iMin; i < iMax; i++) {
					for(int j = jMax; j >= 0; j--) {
						
					}
				}
			} else {
				System.out.println("ANOTHER SECTION THAT SHOULDNT RUN");
			}
			
			//i can sort of implement the hill method, once it determines the "exit" point, then the further away a block it from that point then the higher its value it (i+j, put it in changeType)
		//}
	}
	public void hillMethod() {
		//DEAD ZONE TRACKER
		//this is method 1, will track dead zones by how many times you have been on a tile since the last map change
		//im thinking this method will be inserted into the value determining methods to adjust the values, might use a second method???
		//ok, so it gets inserted into the map method of registerTileValue(), where it will subtract the 3rd value for this map from that value.
			//minor change with p1.setLocation(), 
		
		//p1.setLocation(); - Section Marked
		//p1.initiateMap(); - 3rd "?" changed to "0"
		//GameClient.getAreaData(); - added <- Integer.parseInt(p1.privMap[location[0] - 1][location[1]].getChangeType())> to the end of 4 lines
	}
	
	public int searchAlg1(int row, int col) {
		//ok, so what I need to be able to do is snag all the adjacent tiles that have not been discovered.
		//valueAdjust is to easily change all the change values at once
		int value = 0;
		//int valueAdjust = 1;
		
		try {
			//up
			if(privMap[row - 1][col].getFloorType() == "?") {
				value += undiscoveredValue;//valueAdjust
			}
		} catch (IndexOutOfBoundsException e) {
			
		}
		catch (Exception e) {
			System.out.println("Error in searchAlg - " + e);
		}
		try {
			//down
			if(privMap[row + 1][col].getFloorType() == "?") {
				value += undiscoveredValue;//valueAdjust
			}
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			System.out.println("Error in searchAlg - " + e);
		}
		try {
			//left
			if(privMap[row][col - 1].getFloorType() == "?") {
				value += undiscoveredValue;//valueAdjust
			}
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			System.out.println("Error in searchAlg - " + e);
		}
		try {
			//right
			if(privMap[row][col + 1].getFloorType() == "?") {
				value += undiscoveredValue;//valueAdjust
			}
		} catch (IndexOutOfBoundsException e) {
			
		} catch (Exception e) {
			System.out.println("Error in searchAlg - " + e);
		}
		
		
		
		return value;
	}
	
	
	
	//player cannot move into blocked or lethal tiles - lethal is lava rn
	public void moveDown() {
		//row -1
		moves += "\nMoved Down a Tile";
	}
	public void moveUp() {
		//row +1
		moves += "\nMoved Up a Tile";
	}
	public void moveLeft() {
		//col -1
		moves += "\nMoved Left a Tile";
	}
	public void moveRight() {
		//col +1
		moves += "\nMoved Right a Tile";
	}
	public void interact(String type) {
		moves += " - Interacted with a " + type;
	}
}
