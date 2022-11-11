package gameModulized;

import java.util.Random;

public class GameClient {
	public static void main(String[] args) {
		//create map, populate map for level1 and create player object, get data for start point, and then set up location
		Random rng = new Random();
		Tile[][] map;
		Player p1;
		
		
				//plain, lava, block, empty, fl, loot, used, goal, changed, baseUndiscovered,
				//hill, undiscovered, streak
		//base
		//int[] values = {0, 1000, 1000, 0, 5, 10, 0, 25, 1, 1,
		//		1, 1, 0};
		//adjust each of the values in order to change how much effect each one has on the outcome
		//for another AI part, I can make it select various values for each map and determine which is the best
			//and then it will keep track. I can occasionally have it recheck
		int[] values = {0, 1000, 1000, 0, 5, 10, 0, 25, 1, 1,
				1, 1, 0};
		//need to work out how baseUndiscovered and normal Undiscovered are different
			//I think they are the same, just in different areas for some reason
		
		//System.out.println("Start Run--------------");
		int level = 2;
		int loops = 100000;
		int total = 0;
		double avg = 0.0000000;
		int bestRun = 1000000;
		String[] result = new String[4];
		if (level == 1) {
			map = new Tile[7][7];
			p1 = level1(map, values);
		} else if (level == 2) {
			map = new Tile[10][10];
			p1 = level2(map, values);
		} else if (level == 3) { 
			map = new Tile[15][11];
			p1 = level3(map, values);
		} else {
			p1 = new Player();
			map = new Tile[1][1];
		}
		//p1 = level1(map);
		getLocalData(p1, map);
		int[] location = p1.getLocation();
		for(int i = 0; i < loops; i++) {
			//map = new Tile[10][10];
			if(level == 1) {
				p1 = level1(map, values);
			} else if (level == 2) {
				p1 = level2(map, values);
			} else if (level == 3) {
				p1 = level3(map, values);
			}
			getLocalData(p1, map);
			location = p1.getLocation();
			result = exploreAI(p1, map, location, rng);
			int newRun = Integer.parseInt(result[3]);
			total += newRun;
			if(newRun < bestRun) {
				bestRun = newRun;
			}
			//System.out.println(total);
			//System.out.println(i);
			//try to hard reset it?
			//p1 = new Player();
			//map = new Tile[1][1];
		}
		System.out.println("Total - " + total);
		avg = (double)total/(double)loops;
		System.out.println("Average Number of Turns - " + avg);
		System.out.println("Best Run - " + bestRun);
		//printMap(map);
		
	}
	
	public static String[] exploreAI(Player p1, Tile[][] map, int[] location, Random rng) {
		//System.out.println("Start AI Work");
		String[] moves = {"Starting Location - " + location[0] + "," + location[1],"","",""};
		int moveCount = 0;
		int turnLimit = 1000;//1000;
		while(map[location[0]][location[1]].getIntType() != "goal" && moveCount <= turnLimit) {
			//so they have spawned in. Get area data, analyze.
			moveCount += 1;
			//System.out.println(moveCount);
			int[] areaData = getAreaData(p1, map, location);
			//int count = 0;
			boolean[] values = {false, false, false, false};
			if(areaData[0] >= areaData[1] && areaData[0] >= areaData[2] && areaData[0] >= areaData[3]) {
				//count += 1;
				values[0] = true;
			}
			if(areaData[1] >= areaData[0] && areaData[1] >= areaData[2] && areaData[1] >= areaData[3]) {
				//count += 1;
				values[1] = true;
			}
			if(areaData[2] >= areaData[1] && areaData[2] >= areaData[0] && areaData[2] >= areaData[3]) {
				//count += 1;
				values[2] = true;
			}
			if(areaData[3] >= areaData[1] && areaData[3] >= areaData[2] && areaData[3] >= areaData[0]) {
				//count += 1;
				values[3] = true;
			}
			//System.out.println("Move " + moveCount);
//			for(int i = 0; i < 4; i++) {
//				System.out.println(areaData[i]);
//				System.out.println(values[i]);
//			}
			//System.out.println("---------------------------------");
			
			//ok, need to translate the true/false to functions up, down, left, and right respectively
			//I can use an if/else, but I dont want to keep choosing the wrong choice
				//loop with a failsafe, if the value == false, choose again.
			boolean done = false;
			int choice = -1;
			while(!done) {
				choice = rng.nextInt(4);
				if(values[choice] == true) {
					done = true;
				}
			}
			//System.out.println("Choice - " + choice);
			
			switch(choice) {
				case 0:
					moveUp(p1, map, location);
					break;
				case 1:
					moveDown(p1, map, location);
					break;
				case 2:
					moveLeft(p1, map, location);
					break;
				case 3:
					moveRight(p1, map, location);
					break;
			}
			
			//System.out.println("---------------------------------");
			
			moves[0] += "\n" + moveCount + " - " + location[0] + "," + location[1];
			
		}
		if (moveCount >= turnLimit) {
			//System.out.println("Move Timeout - Moves exceeded Limit");
//			System.out.println(p1.getMoves());
//			System.out.println("Final Score - " + p1.getScore());
		} else {
			//System.out.println(p1.getMoves());
			moves[1] = p1.getMoves();
			//System.out.println("Final Score - " + p1.getScore());
			moves[2] = "Final Score - " + p1.getScore();
		}
		//System.out.println(moves);
		moves[3] = "" + moveCount;
		return moves;
	}
	
	public static Player level1(Tile[][] map, int[] values) {
		//this will change depending on how I want to change the map
		//right now, edges will be walls, col. 4 will be lava, rest will be normal. 1,2 will be a lever to open a walkway at 4,3. 6,5 will be goal
		//this creates player as well as map
		Player p1 = new Player();
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				if(i == 0 || i == map.length - 1 || j == 0 || j == map[i].length - 1) {
					map[i][j] = new Tile("block","","");
				} else if(i == 4) {
					map[i][j] = new Tile("lava", "","");
				} else if (i == 1 && j == 2) {
					map[i][j] = new Tile("plain", "fl", "4,3,plain");
				} else if (i == 5 && j == 5) {
					map[i][j] = new Tile("plain", "goal","");
				} else if(i == 1 && j == 4){
					map[i][j] = new Tile("plain", "start","");
					map[i][j].discover();
					p1 = new Player(map.length, map[i].length, i, j, values);
				} else {
					map[i][j] = new Tile("plain","","");
				}
			}
		}
		
		return p1;
		
		
	}
	
	public static Player level2(Tile[][] map, int[] values) {
		Player p1 = new Player();
		
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				if(i == 0 || i == map.length - 1 || j == 0 || j == map[i].length - 1) {
					map[i][j] = new Tile("block","","");
				} else if (i == 4 && j == 8) {
					map[i][j] = new Tile("plain", "start","");
					map[i][j].discover();
					p1 = new Player(map.length, map[i].length, i, j, values);
				} else if (i == 6 && j == 8) {
					map[i][j] = new Tile("plain", "goal","");
				} else if (i == 4 && (j >= 2 && j <= 4)) {
					map[i][j] = new Tile("lava", "","");
				} else if (i >= 4 && j == 4) {
					map[i][j] = new Tile("lava", "","");
				} else if (i == 5 && j >= 5) {
					map[i][j] = new Tile("lava", "","");
				} else if (i == 8 && j == 1) {
					map[i][j] = new Tile("plain", "fl", "5,5,plain");
				} else if ((i == 6 && j == 3) || (i == 8 && j == 7)) {
					map[i][j] = new Tile("plain","loot","");
				} else {
					map[i][j] = new Tile("plain","","");
				}
			}
		}
		
		return p1;
	}
	
	public static Player level3(Tile[][] map, int[] values) {
		Player p1 = new Player();
		
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				if(i == 0 || i == map.length - 1 || j == 0 || j == map[i].length - 1) {
					map[i][j] = new Tile("block","","");
				} else if (j % 2 == 0 && i >= 2 && i <= 13) {
					map[i][j] = new Tile("lava","","");
				} else if (j == 2) {
					map[i][j] = new Tile("lava","","");
				} else if (i == 13 && j == 1) {
					map[i][j] = new Tile("plain", "goal","");
				} else if (i == 7 && j == 9) {
					map[i][j] = new Tile("plain", "start","");
					map[i][j].discover();
					p1 = new Player(map.length, map[i].length, i, j, values);
				} else if (i == 7 && j == 3) {
					map[i][j] = new Tile("plain", "fl", "1,2,plain");
				} else {
					map[i][j] = new Tile("plain","","");
				}
			}
		}
		
		
		return p1;
	}
	
	
 	public static void printMap(Tile[][] map) {
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[i].length; j++) {
				String[] data = map[i][j].getTileData();
				System.out.println("Tile [" + i + "," + j + "] - " + data[0] + ", " + data[1]);
			}
		}
	}

	public static void printUpdate(Player p1, Tile[][] map, int[] location) {
		String playerData = "Location via Player Map - " + p1.getLocation()[0] + ", " + p1.getLocation()[1] + " on Tile Type " + p1.getLocalData()[0] + " and " + p1.getLocalData()[1];
		String actualData = "Location via Game Map - " + location[0] + ", " + location[1] + " on Tile Type " + map[location[0]][location[1]].getTileData()[0] + " and " + map[location[0]][location[1]].getTileData()[1];
		System.out.println("-------------------------------");
		System.out.println(playerData);
		System.out.println(actualData);
		System.out.println("-------------------------------");
	}
	
	
	public static String[] getLocalData(Player p1, Tile[][] map) {
		//gets data for only the tile she is on
		int[] local = p1.getLocation();
		String[] data = map[local[0]][local[1]].getTileData();
		p1.setLocalData(data);
		return data;
	}
	
	public static int[] getAreaData(Player p1, Tile[][] map, int[] location1) {
		
		//can I move this method into the player class? Maybe not....
		
		
		//gets data for all 8 surrounding tiles. Or maybe just the 4 cardinal ones?
		//just cardinal ones
		int[] value = {-100,-100,-100,-100};
		//streak is to adjust value of continuing in the same direction
		int streak = p1.getStreak();
		
		int[] tempLocation = location1;
		
		//need to get 4 seperate values.
		//but need to get all of them
		//4 different try catch, update value in the catch if failed
		
		//DEADZONE TESTING CODE
			//this is the implementation of the hill method
		// - Integer.parseInt(p1.privMap[location[0] - 1][location[1]].getChangeType())
		
		//ok, so I want to be able to do a basic search method, finding tiles that he hasnt discovered yet.
			//i cna give the tiles additional score for each undiscovered tile, and then additional so for moving in the same direction as the previous move
		
		//the -Integer portion of the following code is to accomodate the hill method
		//System.out.println("Location1[0] - " + location1[0] + " - location1[1] - " + location1[1]);
		
		try {
			//up
			//System.out.println("map - " + map[location1[0] - 1][location1[1]].registerTileValue());
			//System.out.println("privMap - " + Integer.parseInt(p1.privMap[location1[0] - 1][location1[1]].getChangeType()));
			value[0] =  map[location1[0] - 1][location1[1]].registerTileValue(p1) - Integer.parseInt(p1.privMap[location1[0] - 1][location1[1]].getChangeType()) + p1.searchAlg1(location1[0] - 1,location1[1]);
			//value[0] += p1.searchAlg1(location1[0] - 1,location1[1]);
//			tempLocation[0] = location1[0] - 1;
//			tempLocation[1] = location1[1];
//			value[0] += p1.searchAlg1(tempLocation);
//			//System.out.println(p1.searchAlg1(tempLocation));
			if(p1.getLastMove() == 1) {
				value[0] += streak;
			}
		} catch (Exception e) {
			//these exception should only occur if they are right next to the edge, which shouldnt happen rn
			//they are occuring for another reason
			System.out.println("Error in getAreaData - " + e);
		}
		try {
			//down
			value[1] =  map[location1[0] + 1][location1[1]].registerTileValue(p1) - Integer.parseInt(p1.privMap[location1[0] + 1][location1[1]].getChangeType()) + p1.searchAlg1(location1[0] + 1,location1[1]);
			//value[1] += p1.searchAlg1(location1[0] + 1,location1[1]);
//			tempLocation[0] = location1[0] + 1;
//			tempLocation[1] = location1[1];
//			value[1] += p1.searchAlg1(tempLocation);
//			//System.out.println(p1.searchAlg1(tempLocation));
			if(p1.getLastMove() == 2) {
				value[1] += streak;
			}
		} catch (Exception e) {
			System.out.println("Error in getAreaData - " + e);
		}
		try {
			//left
			value[2] =  map[location1[0]][location1[1] - 1].registerTileValue(p1) - Integer.parseInt(p1.privMap[location1[0]][location1[1] - 1].getChangeType()) + p1.searchAlg1(location1[0],location1[1] - 1);
			//value[2] += p1.searchAlg1(location1[0],location1[1] - 1);
//			tempLocation[0] = location1[0];
//			tempLocation[1] = location1[1] - 1;
//			value[2] += p1.searchAlg1(tempLocation);
//			//System.out.println(p1.searchAlg1(tempLocation));
			if(p1.getLastMove() == 3) {
				value[2] += streak;
			}
		} catch (Exception e) {
			System.out.println("Error in getAreaData - " + e);
		}
		try {
			//right
			value[3] =  map[location1[0]][location1[1] + 1].registerTileValue(p1) - Integer.parseInt(p1.privMap[location1[0]][location1[1] + 1].getChangeType()) + p1.searchAlg1(location1[0],location1[1] + 1);
			//value[3] += p1.searchAlg1(location1[0],location1[1] + 1);
//			tempLocation[0] = location1[0];
//			tempLocation[1] = location1[1] + 1;
//			value[3] += p1.searchAlg1(tempLocation);
//			//System.out.println(p1.searchAlg1(tempLocation));
			if(p1.getLastMove() == 4) {
				value[3] += streak;
			}
		} catch (Exception e) {
			System.out.println("Error in getAreaData - " + e);
		}
		
		//System.out.println("--------------------");
		return value;
		
	}
	
	
	
	
	public static void setLocation(Player p1, Tile[][] map, int row, int col, int[] location, int lastMove) {
		//set target row and col
		location[0] = row;
		location[1] = col;
		//System.out.println("Added Value - " + map[row][col].registerTileValue());
		//System.out.println(map[row][col].discovered);
		p1.changeScore(map[row][col].registerTileValue(p1));
		map[row][col].discover();
		p1.setLocation(row, col, lastMove);
		getLocalData(p1, map);
		if(map[row][col].getIntType() != "" && map[row][col].getIntType() != "used" && map[row][col].getIntType() != "goal" && map[row][col].getIntType() != "start") {
			interact(p1, map, location);
			//System.out.println("BOIZ - Interaction at " + row + "," + col);
		}
		if(map[row][col].getChangeType() == "changed") {
			map[row][col].removeChangeType();
		}
		//System.out.println("BOIZ");
	}
	
	
	//public static void getInfo()
	
	/*public int[] getLocation() {
		return location;
	}*/
	
	//player cannot move into blocked or lethal tiles - lethal is lava rn
	public static void moveDown(Player p1, Tile[][] map, int[] location) {
		//row +1
		try {
			if (map[location[0] + 1][location[1]].getTileData()[0] == "lava" || map[location[0] + 1][location[1]].getTileData()[0] == "block") {
				throw new Exception();
			} else {
				p1.moveDown();
				setLocation(p1, map, location[0] + 1, location[1], location, 2);
			}
		} catch (Exception e) {
			System.out.println("----------Error Trying to Move Down");
		}
	}
	public static void moveUp(Player p1, Tile[][] map, int[] location) {
		try {
			if (map[location[0] - 1][location[1]].getTileData()[0] == "lava" || map[location[0] - 1][location[1]].getTileData()[0] == "block") {
				throw new Exception();
			} else {
				p1.moveUp();
				setLocation(p1, map, location[0] - 1, location[1], location, 1);
			}
		} catch (Exception e) {
			System.out.println("----------Error Trying to Move Up");
		}
	}
	public static void moveLeft(Player p1, Tile[][] map, int[] location) {
		try {
			if (map[location[0]][location[1] - 1].getTileData()[0] == "lava" || map[location[0]][location[1] - 1].getTileData()[0] == "block") {
				throw new Exception();
			} else {
				p1.moveLeft();
				setLocation(p1, map, location[0], location[1] - 1, location, 3);
			}
		} catch (Exception e) {
			//System.out.println(e);
			System.out.println("----------Error Trying to Move Left");
		}
	}
	public static void moveRight(Player p1, Tile[][] map, int[] location) {
		try {
			if (map[location[0]][location[1] + 1].getTileData()[0] == "lava" || map[location[0]][location[1] + 1].getTileData()[0] == "block") {
				throw new Exception();
			} else {
				//map[location[0]][location[1] + 1].discover();
				p1.moveRight();
				setLocation(p1, map, location[0], location[1] + 1, location, 4);
			}
		} catch (Exception e) {
			System.out.println("----------Error Trying to Move Right");
		}
	}
	public static void interact(Player p1, Tile[][] map, int[] location) {
		//need to split it into types of interactions
		//first, lootable
		String type = "NO INTERACTION DONE AT " + location[0] + "," + location[1];
		if(map[location[0]][location[1]].getIntType() == "loot") {
			map[location[0]][location[1]].useInt();
			type = "Lootable Container";
		} else if (map[location[0]][location[1]].getIntType() == "fl") {
			String[] data = map[location[0]][location[1]].getChangeType().split(",");
			map[Integer.parseInt(data[0])][Integer.parseInt(data[1])].changeFloor(data[2]);
			map[location[0]][location[1]].useInt();
			type = "Floor Lever";
		} else if (map[location[0]][location[1]].getIntType() == "goal") {
			map[location[0]][location[1]].useInt();
			type = "Goal";
		} else if (map[location[0]][location[1]].getIntType() == "used") {
			type = "Used Interactable";
		}
		p1.interact(type);
	}
	


}
