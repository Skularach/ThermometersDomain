package Main;

import BoardRepresentation.Board;
import BoardRepresentation.BoardFileIO;
import BoardRepresentation.SimpleThermsBoard;


/** This class provides an example of creating a board manually by 
 *    constructing a 2D string array, and an example of creating a board by reading in a file.
 *    Information on the string "codes" can be found in BoardRepresentation/BoardFileIO.java
 * 
 * 
 * 
 * @author Brandon Packard
 *
 */
public class ExampleBoardCreation{
	
	/** Test Driver
	 * 
	 * 
	 * @param args Not used
	 */
	public static void main(String args[])
	{
		createBoardManually();
		
		System.out.println();
		System.out.println();
		
		createBoardFromFile(3);
	}
	
	/** This method is an example of how to manually create a board by constructing the
	 *    2D array of strings yourself.  Not sure why you would ever do this instead of reading
	 *    them in, but it's here to show it is possible
	 * 
	 * 
	 */
	private static void createBoardManually()
	{
		//Create String Representation of grid by hand
		String[][] stringGrid = new String[][]
				{{"BRF","THF","THF","THE","CLE"},
				{"BRF","THF","THE","THE","CLE"},
				{"BRF","THF","THE","THE","CLE"},
				{"BRE","THE","THE","THE","CLE"},
				{"BRF","THE","THE","THE","CLE"}};
				
		//Create board instance
		Board board = new SimpleThermsBoard(stringGrid);
		
		//Print string representation of grid
		System.out.println("===== String Representation of Grid  ====");
		board.printStringGrid();

		System.out.println();
		System.out.println();
		
		//Print string representation of grid with constraints
		System.out.println("===== Grid With Row and Column Constraints =====");
		board.printStringGridWithConstraints();

		
		//Print current grid status (which pieces are filled or empty)
		System.out.println();
		System.out.println();
		
		System.out.println("===== Current Status of Grid =====");
		board.printCurrentGrid();
		
		
		//Print solution grid status (which pieces are filled or empty)
		System.out.println();
		System.out.println();
		
		System.out.println("===== Grid Solution =====");
		board.printSolutionGrid();
				
	}
	
	/** This method creates a board from text contained in a file
	 * 
	 * 
	 * @param fileNo The board number to read in. By default, must be between 0 and 169,999
	 */
	static void createBoardFromFile(int fileNo)
	{
		String[][] stringGrid = BoardFileIO.loadInBoardbyNumber(fileNo);
		
		//Create board instance
		Board board = new SimpleThermsBoard(stringGrid);
		
		//Print string representation of grid with constraints
		System.out.println("===== Grid With Row and Column Constraints (from file)");
		board.printStringGridWithConstraints();	
	}
}