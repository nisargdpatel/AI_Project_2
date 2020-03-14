import java.sql.Date;
import java.text.SimpleDateFormat;

public class HMM {
    static Cell currentCell;
    public static void main(String[] args) {
        Cell[][] grid = new Cell[6][7];

        loadGrid(grid);
        
        sense(grid, '-', '-', '-', 'O');    //1. Sensing: [-, -, -, O]
        move(grid, 'N');                    //2. Moving northward
        sense(grid, '-', 'O', '-', '-');    //3. Sensing: [-, O, -, -]
        move(grid, 'E');                    //4. Moving eastward
        sense(grid, '-', 'O', '-', '-');    //5. Sensing: [-. O. -. -]
        move(grid, 'E');                    //6. Moving eastward
        sense(grid, '-', '-', 'O', '-');    //7. Sensing: [-, -, O, -]

    }

    //Sensing with a given evidence
    public static void sense(Cell[][] grid, char W, char N, char E, char S)
    {
        double total= 0.0;

        //Step 1: Change to decimal values and apply probabilities
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                double value = ((grid[row][col].getValue()/100) * filter(grid, row, col, W, N, E, S));
                grid[row][col].setValue(value);
            }
        }

        //Step 2: Add all updated probabilities to normalize
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                total += grid[row][col].getValue();
            }
        }

        //Step 3: Normalize and change back to percentage values
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                grid[row][col].setValue((grid[row][col].getValue() / total) * 100);
            }
        }

        //Step 4 - Print resulting grid
        System.out.println("Filtering after Evidence [" + W + ", " + N + ", " + E + ", " + S + "]");
        printGrid(grid);
    }

    //Filtering the given evidence (Sensor)
    public static double filter(Cell[][] grid, int row, int col, char W, char N, char E, char S)
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


    //Moving in a given direction
    public static void move(Cell[][] grid, char direction)
    {
        //Calls specific prediction method for the given direction
        if (direction == 'N')
            predictNorth(grid);
        else if (direction == 'E')
            predictEast(grid);
        else
            System.out.println("YOU HAVE GOOFED! Your robot is trying to move in an invalid direction");

        //Print grid
        System.out.println("Prediction after Action " + direction);
        printGrid(grid);
    }


    
    //Prediction after Action 'N' (Moving North)
    public static void predictNorth(Cell[][] grid)
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
                //Step 1:
                if ((col-1 >= 0) && !grid[row][col-1].getBoundary())
                {
                    movementProbability += (grid[row][col-1].getValue() * 0.1);
                }
                //Step 2:
                else
                {
                    movementProbability += (grid[row][col].getValue() * 0.1);
                }
                //Step 3:
                if ((col+1 <= 6) && !grid[row][col+1].getBoundary())
                {
                    movementProbability += (grid[row][col+1].getValue() * 0.1);
                }
                //Step 4:
                else
                {
                    movementProbability += (grid[row][col].getValue() * 0.1);
                }
                //Step 5:
                if ((row+1 <= 5) && !grid[row+1][col].getBoundary())
                {
                    movementProbability += (grid[row + 1][col].getValue() * 0.8);
                }
                //Step 6:
                if ((row-1 < 0) || grid[row-1][col].getBoundary())
                {
                    movementProbability += (grid[row][col].getValue() * 0.8);
                } 
                
                //Update probability to calculated value inside tempGrid
                tempGrid[row][col] = movementProbability;
                movementProbability = 0.0;      //Reset
            }
        }

        copyToGrid(tempGrid, grid);     //Copy data to actual grid
    }

    
    //Prediction after Action 'E' (Moving East)
    public static void predictEast(Cell[][] grid)
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
                //Step 1:
                if ((row-1 >= 0) && !grid[row-1][col].getBoundary())
                {
                    movementProbability += (grid[row-1][col].getValue() * 0.1);
                } 
                //Step 2:
                else 
                {
                    movementProbability += (grid[row][col].getValue() * 0.1);
                }
                //Step 3:
                if ((row+1 <= 5) && !grid[row+1][col].getBoundary())
                {
                    movementProbability += (grid[row+1][col].getValue() * 0.1);
                } 
                //Step 4:
                else 
                {
                    movementProbability += (grid[row][col].getValue() * 0.1);
                }
                //Step 5:
                if ((col-1 >= 0) && !grid[row][col-1].getBoundary())
                {
                    movementProbability += (grid[row][col-1].getValue() * 0.8);
                }
                //Step 6:
                if ((col+1 > 6) || grid[row][col+1].getBoundary())
                {
                    movementProbability += (grid[row][col].getValue() * 0.8);
                }
                
                //Update probability to calculated value inside tempGrid
                tempGrid[row][col] = movementProbability;   
                movementProbability = 0;    //Reset
            }
        }

        copyToGrid(tempGrid, grid);     //Copy data to actual grid
    }
    

    //Copies data from temperory grid array to actual grid array
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


    //Loads initial grid array and assignes appropriate variabels
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

        System.out.println("Initial Location Probabilities");
        printGrid(grid);    //Initial Printed Grid with intial probabilities
    }

    public static void printGrid(Cell[][] grid)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
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
        System.out.println("Authors: Maitra Patel (00984222), Nisarg Patel (88392672)");
        System.out.println("Runtime: " + formatter.format(date) + "\n\n");
    }
}