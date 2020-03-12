
public class HMM {
    static Cell currentCell;
    public static void main(String[] args) {
        Cell[][] grid = new Cell[6][7];

        loadGrid(grid);
        
        //Algorithm
        //1. Sensing: [-, -, -, O]
        sense(grid, '-', '-', 'O', '-');
        //2. Moving northward
        //3. Sensing: [-, O, -, -]
        //4. Moving eastward
        //5. Sensing: [-. O. -. -]
        //6. Moving eastward
        //7. Sensing: [-, -, O, -]


        printGrid(grid);

    }

    //Sensing
    public static void sense(Cell[][] grid, char W, char N, char E, char S)
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
    }

    public static void printGrid(Cell[][] grid)
    {
        for (int row = 0; row < grid.length; row++)
        {
            for (int col = 0; col < grid[row].length; col++)
            {
                if (grid[row][col].getBoundary())
                {
                    System.out.print("XXXX ");
                } else {
                    System.out.print(grid[row][col].getValue() + " "); 
                }
               
            }
            System.out.println();
        }
    }
}