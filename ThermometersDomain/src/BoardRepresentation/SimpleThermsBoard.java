package BoardRepresentation;

import java.util.ArrayList;

/** This class represents a Simple Thermometers board.
 *    It allows us to, given a board, can use inheritance
 *    to check whether it is a Simple or Complex board based on which it
 *    was initialized as.  It also adds some methods necessary for 
 *    working with Simple Thermometer boards.
 * 
 * @author Brandon Packard
 *
 */
public class SimpleThermsBoard extends Board
{
	/** Constructor, calls the general Board constructor to initialize values
	 *    and then reinitializes the Row and Column data
	 * 
	 * @param stringGrid The 2D String array that holds the data from a file to be made into a board object.
	 */
	public SimpleThermsBoard(String[][] stringGrid)
	{
		super(stringGrid);
		reinitIndexes();
	}
	
	//We start looking at row 0. When the action taken is to move to the
	// next row/column, we increment rowIndex by 1. When we are done with
	// rows, we start looking at the first column, and so on.
	public boolean lookingAtRows = true;
	public int rowIndex = 0;
	public int colIndex = 0;

	/** This method basically carries out the "move" action on the domain:
	 *    It adjusts the board data to signify that we have moved to the next row or column.
	 *    For example, on a 3x3 board, calling cycleCursor 6 times would lead to the following sequence of rows/columns being looked at:
	 *    Looking at row 0
	 *    Looking at row 1
	 *    Looking at row 2
	 *    Looking at column 0
	 *    Looking at column 1
	 *    Looking at column 2
	 *    Looking at row 0
	 */
	public void cycleCursor()
	{
		if(lookingAtRows)
		{
			//Increment row index to look at next row. If out of 
			// rows, go to first column.
			if(rowIndex < BOARDSIZE - 1)
				rowIndex++;
			else
			{
				lookingAtRows = false;
				colIndex = 0;
			}
		}
		
		else 
		{
			//Increment column index to look at next column. If out of 
			// columns, go to first row.
			if(colIndex < BOARDSIZE - 1)
				colIndex++;
			else
			{
				lookingAtRows = true;
				rowIndex = 0;
			}
		}
		
	}
	
	
	/** This method checks to see whether there are any constraints violated
	 *    in the current row/column.  Mostly used by the AI to see if a mistake
	 *    has been made
	 * 
	 * @param grid  The tile fill data for the board
	 * @param fillCount  The number of tiles that should be filled
	 * @param therms   The thermometer data for the row/column
	 * @return
	 */
	public boolean detectConstraintViolations(ArrayList<Integer> grid,
			int fillCount, ArrayList<ArrayList<ThermometerPiece>> therms)
	{
		//Get the current number of filled and empty squares
		int filledSquares = 0;
		int blankSquares = 0;
		for(int j = 0; j < grid.size();j++)
		{
			if(grid.get(j) == 1)
				filledSquares++;
			if(grid.get(j) == 0)
				blankSquares++;
		}
		
		
		//Too many or not enough tiles, so there is a constraint violation
		if(filledSquares + blankSquares < fillCount)
			return true;
		else if(filledSquares > fillCount)
			return true;
		
		
		//Check to see if each thermometer is valid by the following logic:
		// Working from the bulb up, if we encounter an empty tile and then
		// afterwards at any point a filled tile, a mistake has been made because
		// the thermometer configuration is impossible.
		for(int j = 0; j < therms.size(); j++)
		{
			boolean hasAnEmpty = false;
			for(int k = 0; k < therms.get(j).size(); k++)
				if(therms.get(j).get(k).getFillStatus() == -1)
					hasAnEmpty = true;
				else if(therms.get(j).get(k).getFillStatus() == 1)
					if(hasAnEmpty)
						return true;
			}
		return false;
	}
	
	
	/** Reset all indices so we are looking at the first row again.
	 *    Mostly used when first creating a board.
	 * 
	 */
	public void reinitIndexes()
	{
		rowIndex = 0;
		colIndex = 0;
		lookingAtRows = true;
	}
}