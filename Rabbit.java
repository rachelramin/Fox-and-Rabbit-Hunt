public class Rabbit extends Animal {
	
    private boolean canSeeFoxNow = false;
    private int distanceToFox;
    private int directionToFox;
    private boolean atBush = false;
    private int directionToBush;
    private int shortestBushDistance = 100;
    private int directionToEdge;


    public Rabbit(Model model, int row, int column) {
        super(model, row, column);
    }
    
    //Checks if the rabbit is directly N, E, S, or W of bush
    //AND That that same bush is 1 away
    boolean checkBushPlacement() {
    	for (int i = Model.MIN_DIRECTION; i <= Model.MAX_DIRECTION; i++) {
            if (look(i) == Model.BUSH && distance(i) <= shortestBushDistance) {
                shortestBushDistance = distance(i);
                directionToBush = i;
            }
        }
    	if (shortestBushDistance <= 1 && (directionToBush ==Model.N || directionToBush ==Model.E || directionToBush ==Model.S || directionToBush ==Model.W)) {
              return true;
            }
    	return false;
    }
    
    //Moves clockwise around bush
    int moveClockwise() {
    	int moveDirection = Model.turn(directionToBush, -1);
    	directionToBush = Model.turn(directionToBush, 2);
    	if(canMove(moveDirection)) {
    		return moveDirection;
    	}else {
    		//Accounts for multiple bushes in a row
    		moveDirection = Model.turn(moveDirection, -1);
    		directionToBush = Model.turn(directionToBush, -2);
    		return moveDirection;
    	}
    	
    	
    }
    
    //Moves counter-clockwise around bush
    int moveCounterClockwise() {
    	int moveDirection = Model.turn(directionToBush, 1);
    	directionToBush = Model.turn(directionToBush, -2);
    	if(canMove(moveDirection)) {
    		return moveDirection;
    	}else {
    		//Accounts for multiple bushes in a row
    		moveDirection = Model.turn(moveDirection, 1);
    		directionToBush = Model.turn(directionToBush, 2);
    		return moveDirection;
    	}
    	
    }
    
    //Checks what row a given bush is (0-19)
    int checkBushRow(int bushDirection, int bushDistance) {
    	if(bushDirection == Model.E || bushDirection == Model.W) {
    		return this.row;
    	}else if(bushDirection == Model.N || bushDirection == Model.NE || bushDirection == Model.NW) {
    		return this.row - bushDistance;
    	}else if(bushDirection == Model.S || bushDirection == Model.SE || bushDirection == Model.SW) {
    		return this.row + bushDistance;
    	}else {
    		return 0;
    	}
    }
    
    //Checks what column a bush is (0-19)
    int checkBushCol(int bushDirection, int bushDistance) {
    	if(bushDirection == Model.N || bushDirection == Model.S) {
    		return this.column;
    	}else if(bushDirection == Model.E || bushDirection == Model.NE || bushDirection == Model.SE) {
    		return this.column + bushDistance;
    	}else if(bushDirection == Model.W || bushDirection == Model.NW || bushDirection == Model.SW) {
    		return this.column - bushDistance;
    	}else {
    		return 0;
    	}
    }
    
    //Returns true if bush is on an edge
    boolean isEdgeBush(int bushDirection, int bushDistance) {
    	int bushCol = checkBushCol(bushDirection, bushDistance);
    	int bushRow = checkBushRow(bushDirection, bushDistance);
    	
    	if(bushCol == 0 || bushCol == 19 || bushRow == 0 || bushRow == 19) {
    		return true;
    	}
    	return false;   	   	
    }
    

    
    int decideMove() {    	
    	//If not next to a bush, move to a bush
    	if(!atBush){
    		//If too close to the edge, move in the opposite direction
    		for (int i = Model.MIN_DIRECTION; i <= Model.MAX_DIRECTION; i++) {
                if (look(i) == Model.EDGE && distance(i) < 3) {
                    directionToEdge = i;
                    int awayFromEdge = Model.turn(directionToEdge, 4);
                    if(canMove(awayFromEdge)) {
                    	return awayFromEdge;
                    }else {
                    	//Handles if close to the edge and can't
                    	//move directly 180 degrees away
                    	for(int j = Model.MIN_DIRECTION; j <= Model.MAX_DIRECTION; j++) {
                    		int newDirection = Model.turn(awayFromEdge, j);
                    		if(canMove(newDirection)) {
                    			return newDirection;
                    		}
                    	}
                    }
                    
                }
            }
    //Once away from edge, finds the closest bush
    		
    		//Finds closest bush, not on an edge
    		for (int i = Model.MIN_DIRECTION; i <= Model.MAX_DIRECTION; i++) {
                if (look(i) == Model.BUSH && distance(i) < shortestBushDistance && !isEdgeBush(i, distance(i))) {
                    shortestBushDistance = distance(i);
                    directionToBush = i;
                }
            }
    		
    		//If rabbit cannot see a bush, move in a random direction
    		if(shortestBushDistance == 100) {
    			int currentDirection = Model.random(Model.MIN_DIRECTION, Model.MAX_DIRECTION);
    				for (int i = 0; i < 8; i++) {
    					if (canMove(currentDirection)) {
    					return currentDirection;
    					}else {
    					currentDirection = Model.turn(currentDirection, 1);
    					}
    				}
    		}
    		//Moves to next phase if rabbit gets to bush
    		//Makes sure rabbit is in proper placement around the bush
    		if(shortestBushDistance <= 1 && !checkBushPlacement()) {
    			atBush = true;
    			int moveDirection = Model.turn(directionToBush, 1);
    			directionToBush = Model.turn(directionToBush, -1);			
    			return moveDirection;
    		}else if(shortestBushDistance <= 1) {
    			atBush = true;
    		}
    		//Moves towards bush if not next to it
    		return directionToBush;
    	}
    	
    	
    	//Once at the bush, look for the fox
    	canSeeFoxNow = false;
    	//Look around for the fox
    	//If fox is found, direction and distance to fox are set
        for (int i = Model.MIN_DIRECTION; i <= Model.MAX_DIRECTION; i++) {
            if (look(i) == Model.FOX) {
                canSeeFoxNow = true;
                directionToFox = i;
                distanceToFox = distance(i);
            }
        }
   
    	//If the fox is seen, move opposite around the bush
        if(canSeeFoxNow) {
        	//Fox is NORTH of rabbit
        	if(directionToFox == Model.N && directionToBush == Model.E) {
        		return moveCounterClockwise();
        	}else if(directionToFox == Model.N && directionToBush == Model.W) {
        		return moveClockwise();
        	}else if(directionToFox == Model.N && directionToBush == Model.S) {
        		return moveClockwise();
        	}       	
        	//Fox is EAST of rabbit
        	else if(directionToFox == Model.E && directionToBush != Model.N) {
        		return moveCounterClockwise();
        	}else if(directionToFox == Model.E && directionToBush == Model.N) {
        		return moveClockwise();
        	}
        	//Fox is WEST of rabbit
        	else if(directionToFox == Model.W && directionToBush != Model.N) {
        		return moveClockwise();
        	}else if(directionToFox == Model.W && directionToBush == Model.N) {
        		return moveCounterClockwise();
        	}
        	//Fox is SOUTH of rabbit
        	else if(directionToFox == Model.S && directionToBush == Model.E) {
        		return moveClockwise();
        	}else if(directionToFox == Model.S && directionToBush == Model.W) {
        		return moveCounterClockwise();
        	}else if(directionToFox == Model.S && directionToBush == Model.N) {
        		return moveClockwise();
        	}
        	
        	//NORTHEAST
        	else if(directionToFox == Model.NE && (directionToBush == Model.S || directionToBush == Model.E)) {
        		return moveCounterClockwise();
        	}else if(directionToFox == Model.NE){
        		return moveClockwise();
        	}
        	//NORTHWEST
        	else if(directionToFox == Model.NW && (directionToBush == Model.S || directionToBush == Model.W)) {
        		return moveClockwise();
        	}else if(directionToFox == Model.NW){
        		return moveCounterClockwise();
        	}
        	//SOUTHEAST
        	else if(directionToFox == Model.SE && (directionToBush == Model.N || directionToBush == Model.E)) {
        		return moveClockwise();
        	}else if(directionToFox == Model.SE){
        		return moveCounterClockwise();
        	}
        	//SOUTHWEST
        	else if(directionToFox == Model.SW && (directionToBush == Model.S || directionToBush == Model.E)) {
        		return moveClockwise();
        	}else if(directionToFox == Model.SW){
        		return moveCounterClockwise();
        	}

        }            
 		return Model.STAY;
    }
    
}
