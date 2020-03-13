
public class HMM {
    static Cell currentCell;
    public static void main(String[] args) {
        Cell[][] grid = new Cell[6][7];

        loadGrid(grid);
        
        //Algorithm
        //1. Sensing: [-, -, -, O]
        sense(grid, '-', '-', '-', 'O');
        //2. Moving northward
        move(grid, 'N');
        //3. Sensing: [-, O, -, -]
        sense(grid, '-', 'O', '-', '-');
        //4. Moving eastward
        move(grid, 'E');
        //5. Sensing: [-. O. -. -]
        sense(grid, '-', 'O', '-', '-');
        //6. Moving eastward
        move(grid, 'E');
        //7. Sensing: [-, -, O, -]
        sense(grid, '-', '-', 'O', '-');

    }

    public static void sense(Cell[][] grid, char W, char N, char E, char S)
    {
        double total= 0.0;

        //Step 1
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                double value = ((grid[row][col].getValue()/100) * sensor(grid, row, col, W, N, E, S));
                grid[row][col].setValue(value);
            }
        }

        //Step 2
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                total += grid[row][col].getValue();
            }
        }

        //Step 3
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                grid[row][col].setValue((grid[row][col].getValue() / total) * 100);
            }
        }

        //Step 4 - Print
        printGrid(grid);
    }

    //Sensor
    public static double sensor(Cell[][] grid, int row, int col, char W, char N, char E, char S)
    {
        double result = 1.0;

        if (grid[row][col].getBoundary())
        {
            return 0.0;
        }
        else if ((col-1<0) || grid[row][col-1].getBoundary())  //Given West Obstacle
        {
            if (W == '-')   
            {
                result *= 0.15;     //If P(!O|O)
            } else {        
                result *= 0.85;     //If P(O|O)
            }
        } else {    //Given West Not Obstacle
            if (W == '-')   
            {
                result *= 0.90;     //If P(!O|!O)
            } else {        
                result *= 0.10;     //If P(O|!O)
            }
        }
        
        
        if ((row-1<0) || grid[row-1][col].getBoundary())  //Given North Obstacle
        {
            if (N == '-')
            {
                result *= 0.15;     //If P(!O|O)
            } else {
                result *= 0.85;      //If P(O|O)
            }
        } else {    //Given North Not Obstacle
            if (N == '-')
            {
                result *= 0.90;     //If P(!O|!O)
            } else {
                    result *= 0.10; //If P(O|!O)
            }
        }


        if ((col+1>6) || grid[row][col+1].getBoundary())  //Given East Obstacle
        {
            if (E == '-')
            {
                result *= 0.15; //If P(!O|O)
            } else {
                result *= 0.85; //If P(O|O)
            }
        } else {    //Given East Not Obstacle
            if (E == '-')
            {
                result *= 0.90; //If P(!O|!O)
            } else {
                result *= 0.10; //If P(O|!O)
            }
        }
        
        
        if ((row+1>5) || grid[row+1][col].getBoundary())  //Given South Obstacle
        {
            if (S == '-')
            {
                result *= 0.15; //If P(!O|O)
            } else {
                result *= 0.85; //If P(O|O)
            }
        } else {    //Given South Not Obstacle
            if (S == '-')
            {
                result *= 0.90; //If P(!O|!O)
            } else {
                result *= 0.10; //If P(O|!O)
            }
        }

        return result;
    }


    public static void move(Cell[][] grid, char direction)
    {
        if (direction == 'N')
            moveNorth(grid);
        else if (direction == 'E')
            moveEast(grid);
        else
            System.out.println("YOU HAVE GOOFED! Your robot is trying to move in an invalid direction");

    }


    
    //Moving North
    public static void moveNorth(Cell[][] grid)
    {
        //Moving North (Robot Facing North)
        //You can get to the current spot by
            //1. Coming from left side (ONLY when left side is an open square)
                //Drift (10%)
            //2. Coming from left side (ONLY when there is a blockade in left side)
                //Bounce back (10%)
            //3. Coming from right side (ONLY when right side is an open square)
                //Drift (10%)
            //4. Coming from right side (ONLY when there is a blockade in right side)
                //Bounce back (10%)
            //5. Coming from bottom side (ONLY when bottom side is an open square)
                //Successful move (80%)
            //6. Coming from current spot (ONLY when there is a blockade in top side)
                //Bounce back (10%)
        //Assign the calculated value to current cell
            double movementProbability = 0.0;
            double tempGrid[][] = new double[6][7];
            for(int row = 0; row < grid.length; row++)
            {
                for(int col = 0; col < grid[row].length; col++)
                {
                    
                    if ((col-1 >= 0) && !grid[row][col-1].getBoundary()) //there is a cell on the left & it is not a obstacle, coming right to current
                    {
                        movementProbability += (grid[row][col-1].getValue() * 0.1);
                    }
                    else //else it is a blocade or boundary so drift-bounceback
                    {
                        movementProbability += (grid[row][col].getValue() * 0.1);
                    }
                    if ((col+1 <= 6) && !grid[row][col+1].getBoundary()) //there is a cell on the right & it is not a obstacle, coming left to current
                    {
                        movementProbability += (grid[row][col+1].getValue() * 0.1);
                    }
                    else //else it is a blocade or boundary so drift-bounceback
                    {
                        movementProbability += (grid[row][col].getValue() * 0.1);
                    }
                    if ((row+1 <= 5) && !grid[row+1][col].getBoundary()) //there is a cell on the bottom & it is not a obstacle, coming bottom to current
                    {
                        movementProbability += (grid[row + 1][col].getValue() * 0.8);
                    }
                    if ((row-1 < 0) || grid[row-1][col].getBoundary()) //current spot to top spot, but top spot is obstacle so you bounce back
                    {
                        movementProbability += (grid[row][col].getValue() * 0.8);
                    } 
                    
                    tempGrid[row][col] = movementProbability;
                    movementProbability = 0.0;
                }
            }

            copyToGrid(tempGrid, grid);
            
            //print
            printGrid(grid);
    }

    
    //Moving East
    public static void moveEast(Cell[][] grid)
    {
        //Moving North (Robot Facing North)
        //You can get to the current spot by
            //1. Coming from left side (ONLY when left side is an open square)
                //Drift (10%)
            //2. Coming from left side (ONLY when there is a blockade in left side)
                //Bounce back (10%)
            //3. Coming from right side (ONLY when right side is an open square)
                //Drift (10%)
            //4. Coming from right side (ONLY when there is a blockade in right side)
                //Bounce back (10%)
            //5. Coming from bottom side (ONLY when bottom side is an open square)
                //Successful move (80%)
            //6. Coming from current spot (ONLY when there is a blockade in top side)
                //Bounce back (10%)
        //Assign the calculated value to current cell
        double movementProbability = 0.0;
        double tempGrid[][] = new double[6][7];
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                
                if ((row-1 >= 0) && !grid[row-1][col].getBoundary())
                {
                    movementProbability += (grid[row-1][col].getValue() * 0.1);
                } 
                else 
                {
                    movementProbability += (grid[row][col].getValue() * 0.1);
                }
                if ((row+1 <= 5) && !grid[row+1][col].getBoundary())
                {
                    movementProbability += (grid[row+1][col].getValue() * 0.1);
                } 
                else 
                {
                    movementProbability += (grid[row][col].getValue() * 0.1);
                }
                if ((col-1 >= 0) && !grid[row][col-1].getBoundary())
                {
                    movementProbability += (grid[row][col-1].getValue() * 0.8);
                }
                if ((col+1 > 6) || grid[row][col+1].getBoundary())
                {
                    movementProbability += (grid[row][col].getValue() * 0.8);
                }
                
                tempGrid[row][col] = movementProbability;    
                movementProbability = 0;   
            }
        }

        copyToGrid(tempGrid, grid);

        //print
        printGrid(grid);   
    }
    
    public static void copyToGrid(double tempGrid[][], Cell[][]grid)
    {
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                grid[row][col].setValue(tempGrid[row][col]);
            }
        }
    }


    public static void loadGrid(Cell[][] grid)
    {
        //Initialize Grid
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                grid[row][col] = new Cell();
                grid[row][col].setRow(row);
                grid[row][col].setCol(col);
                //Setting this to 3.23 because all open squares have equal chances of robot being there (100/31)
                grid[row][col].setValue(3.23);
            }
        }
        
        //Load cells
        grid[3][1].setBoundary(true);
        grid[3][2].setBoundary(true);
        grid[2][2].setBoundary(true);
        grid[1][2].setBoundary(true);
        grid[1][3].setBoundary(true);
        grid[1][4].setBoundary(true);
        grid[1][5].setBoundary(true);
        grid[2][5].setBoundary(true);
        grid[3][5].setBoundary(true);
        grid[4][5].setBoundary(true);
        grid[4][4].setBoundary(true);
        
        //Boundary values to 0
        grid[3][1].setValue(0.0);
        grid[3][2].setValue(0.0);
        grid[2][2].setValue(0.0);
        grid[1][2].setValue(0.0);
        grid[1][3].setValue(0.0);
        grid[1][4].setValue(0.0);
        grid[1][5].setValue(0.0);
        grid[2][5].setValue(0.0);
        grid[3][5].setValue(0.0);
        grid[4][5].setValue(0.0);
        grid[4][4].setValue(0.0);
    }

    public static void printGrid(Cell[][] grid)
    {
        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid[row].length; col++)
            {
                if (grid[row][col].getBoundary())
                {
                    System.out.printf(" %5s ", "XXXX");
                } else {
                    System.out.printf(" %5.2f ", grid[row][col].getValue()); 
                }   
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }
}