import java.lang.Object;

public class HMM {
    static Cell currentCell;
    public static void main(String[] args) {
        Cell[][] grid = new Cell[6][7];

        loadGrid(grid);
        
        //Algorithm
        //1. Sensing: [-, -, -, O]
        sense(grid, '-', '-', '-', 'O');
        //2. Moving northward
        //3. Sensing: [-, O, -, -]
        //4. Moving eastward
        //5. Sensing: [-. O. -. -]
        //6. Moving eastward
        //7. Sensing: [-, -, O, -]

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



    //Sensing
    public static void old_sense(Cell[][] grid, char W, char N, char E, char S)
    {
        boolean westGivenObstacle;
        boolean northGivenObstacle;
        boolean eastGivenObstacle; 
        boolean southGivenObstacle;
       
        double[][] probability = new double[2][2];
        //0 - not obstacle
        //1 - obstacle
        probability[1][1] = 0.85; //P(O2|O1) = 0.85
        probability[0][1] = 0.15; //P(!O2|O1) = 0.15
        probability[1][0] = 0.10; //P(O2|!O1) = 0.10
        probability[0][0] = 0.90; //P(!O2|!O1) = 0.90

        double result = 1;
        
        for(int row = 0; row < grid.length; row++)
        {
            for(int col = 0; col < grid[row].length; col++)
            {
                    //Checks if westGivenObstacle(true) or westGivenNotObstacle(false)
                    westGivenObstacle = (col-1 < 0) ? true : (grid[row][col-1].getBoundary() ? true: false);
                    //Checks if northGivenObstacle(true) or northGivenNotObstacle(false)
                    northGivenObstacle = (row-1 < 0) ? true : (grid[row][row-1].getBoundary() ? true: false);
                    //Checks if eastGivenObstacle(true) or eastGivenNotObstacle(false)
                    eastGivenObstacle = (col+1 > 6) ? true : (grid[row][col+1].getBoundary() ? true: false);
                    //Checks if southGivenObstacle(true) or southGivenNotObstacle(false)
                    southGivenObstacle = (row+1 > 5) ? true : (grid[row][row+1].getBoundary() ? true: false);
                    
                    //Check West
                    if (westGivenObstacle)  //given an obstacle
                    {
                        if (W == 'O')   //If obstacle given obstacle
                        {
                            result *= probability[1][1];
                        }
                        else if (W == '-')  //If not obstacle given obstacle
                        {
                            result *= probability[0][1];
                        }
                    }
                    else {
                        if (W == 'O')   //If obstacle given not obstacle
                        {
                            result *= probability[1][0];
                        }
                        else if (W == '-')  //If not obstacle given not obstacle
                        {
                            result *= probability[0][0];
                        }
                    }
                    //Check North
                    if (northGivenObstacle)
                    {
                        if (N == 'O')   //If obstacle given obstacle
                        {
                            result *= probability[1][1];
                        }
                        else if (N == '-')  //If not obstacle given obstacle
                        {
                            result *= probability[0][1];
                        }
                    }
                    else {
                        if (N == 'O')   //If obstacle given not obstacle
                        {
                            result *= probability[1][0];
                        }
                        else if (N == '-')  //If not obstacle given not obstacle
                        {
                            result *= probability[0][0];
                        }
                    }
                    //Check East
                    if (eastGivenObstacle)
                    {
                        if (E == 'O')   //If obstacle given obstacle
                        {
                            result *= probability[1][1];
                        }
                        else if (E == '-')  //If not obstacle given obstacle
                        {
                            result *= probability[0][1];
                        }
                    }
                    else {
                        if (E == 'O')   //If obstacle given not obstacle
                        {
                            result *= probability[1][0];
                        }
                        else if (E == '-')  //If not obstacle given not obstacle
                        {
                            result *= probability[0][0];
                        }
                    }
                    //Check South
                    if (southGivenObstacle)
                    {
                        if (S == 'O')   //If obstacle given obstacle
                        {
                            result *= probability[1][1];
                        }
                        else if (S == '-')  //If not obstacle given obstacle
                        {
                            result *= probability[0][1];
                        }
                    }
                    else {
                        if (S == 'O')   //If obstacle given not obstacle
                        {
                            result *= probability[1][0];
                        }
                        else if (S == '-')  //If not obstacle given not obstacle
                        {
                            result *= probability[0][0];
                        }
                    }
                grid[row][col].setValue(result * grid[row][col].getValue());    
            }

        }
        
        

        
    }
    
    
    //Moving
    public static void move(char direction)
    {
        
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
    }
}