public class Cell {

    private double value; //store the actual value contained in this cell
    private int row;
    private int col;
    private boolean boundary;

    Cell(){
        setValue(0);    //"X" -> Obstacle, "  " -> Empty, "#" -> Counter Values
        setBoundary(false);
    }
    //Retrieve col
    public int getCol() {
		return col;
	}
    //Pre: Must pass in a col of type int
    //Set col 
	public void setCol(int col) {
		this.col = col;
	}
    //Retrieve row
	public int getRow() {
		return row;
	}
    //Pre: Must pass in a row of type int
    //Set row 
	public void setRow(int row) {
		this.row = row;
  }
  //Pre: Must pass in a boolean value
    //Set value to 0 if the cell is not a boundary, else if it is, set to 1
  public void setBoundary(boolean x) {
		this.boundary = x;
  }
  public boolean getBoundary() {
      return boundary;
  }

	//Retrieve value inside this cell
    public double getValue() {
		return value;
    }
    //Pre: Must pass in a value of type string
    //Set value inside this cell
	public void setValue(double value) {
		this.value = value;
    }
}
