package BoardRepresentation;

import java.util.ArrayList;

/**  This is the main class used to represent a Thermometers board.
 *   It stores the current state of each tile, the "correct" grid that has all of the correct fills (which is never placed into the learning instances),
 *   the location and fill of all the thermometers, and some constants
 * 
 * @author Brandon Packard
 *
 */
public abstract class Board
{
	//Constant values: All boards used in my own work were 5x5.
	// 168,000 5x5 boards and 624 3x3 boards are provided (3x3 boards are much more limited in the possible configurations).
	// MaxTrainingMoves and MaxValidationMoves are used to advance learning/validation along when the learner is stuck,
	//    via explicitly limiting how many moves are allowed to be made on each board before it has to stop being solved.
	public static int BOARDSIZE = 5;
	public final static int MaxTrainingMoves = 100;
	public final static int MaxValidationMoves = 100;
	
	//Row and column constraints for the grid
	public ArrayList<Integer> rowCounts = new ArrayList<Integer>();
	public ArrayList<Integer> colCounts = new ArrayList<Integer>();;
	
	//Current values of the tiles on the board
	public ArrayList<ArrayList<Integer>> grid;
	
	//String representation of the thermometers on the grid (this is what is read in from the file)
	public String[][] stringGrid;
	
	//Thermometers data for each row and column
	public ArrayList<ArrayList<ArrayList<ThermometerPiece>>> rowTherms = new ArrayList<ArrayList<ArrayList<ThermometerPiece>>>();
	public ArrayList<ArrayList<ArrayList<ThermometerPiece>>> colTherms = new ArrayList<ArrayList<ArrayList<ThermometerPiece>>>();
	
	//Correct values for all tiles on the board -- Only used for verification, never for learning.
	public ArrayList<ArrayList<Integer>> solutionGrid;
	
	/** Constructor.  Takes in a new board represented as a 2D array of strings and creates 
	 * a usable board instance corresponding to those strings.
	 * 
	 * @param stringGridIn  The 2D array of strings, most likely read in from a file, to create a usable board from.
	 */
	public Board(String[][] stringGridIn)
	{
		//Store parameter in data member
		stringGrid = stringGridIn;
		
		//Initialize all grid squares to 0 for unknown,
		//   Set all solutionGrid squares to the correct fills for those tiles in a solved board
		grid = new ArrayList<ArrayList<Integer>>();
		solutionGrid = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < stringGrid.length; i++)
		{
			grid.add(new ArrayList<Integer>());
			solutionGrid.add(new ArrayList<Integer>());
			for(int j = 0; j < stringGrid[0].length; j++)
			{
				if(stringGrid[i][j].endsWith("E"))
					solutionGrid.get(i).add(-1);
				else
					solutionGrid.get(i).add(1);
				
				grid.get(i).add(0);
			}
		}
		
		//Get the how-many-tiles-must-be-filled constraints for each row
		rowCounts.clear();
		for(int i = 0; i < stringGrid.length; i++)
		{
			int count = 0;
			for(int j = 0; j < stringGrid[0].length; j++)
			{
				//The last character in the string for a tile determines the "correct" fill -- F stands for filled
				if(stringGrid[i][j].endsWith("F"))
					count++;
			}
			rowCounts.add(count);
		}
		
		//Get the how-many-tiles-must-be-filled constraints for each column
		colCounts.clear();
		for(int i = 0; i < stringGrid[0].length; i++)
		{
			int count = 0;
			for(int j = 0; j < stringGrid.length; j++)
			{
				//The last character in the string for a tile determines the "correct" fill -- F stands for filled
				if(stringGrid[j][i].endsWith("F"))
					count++;
			}
			colCounts.add(count);
		}
		

		//Create the initial version of the thermometer data for the grid. Note that this
		//   must be called every time a tile is updated from here on out.
		updateThermData();
	}
	
	
	/** This function updates the information for the thermometers on the board based on the fill
	 *   of the tiles in the grid.  It is VITAL that this function is called every time a tile's fill
	 *   is updated, to keep the Thermometer information in sync with the newest tile fills.
	 */
	public void updateThermData()
	{
		//Clear all thermometer information
		rowTherms.clear();
		colTherms.clear();
		
		//For each row...
		for(int i = 0; i < stringGrid.length; i++)
		{
			//... Make a list of thermometers in that row from left to right.
			rowTherms.add(new ArrayList<ArrayList<ThermometerPiece>>());
			
			//When we see a code starting in BR, we know we have spotted a left-to-right row thermometer,
			//  so add all of the information for the thermometer to the list.  For more information on 
			//  string codes, please refer to BoardFileIO.java
			boolean makingTherm = false;
			for(int j = 0; j < stringGrid[0].length; j++)
			{
				if(stringGrid[i][j].startsWith("BR"))
				{
					rowTherms.get(i).add(new ArrayList<ThermometerPiece>());
					rowTherms.get(i).get(rowTherms.get(i).size()-1).add(new ThermometerPiece(i,j,grid.get(i).get(j)));
					makingTherm = true;
				}
				
				else if(makingTherm && stringGrid[i][j].startsWith("TH"))
				{
					rowTherms.get(i).get(rowTherms.get(i).size()-1).add(new ThermometerPiece(i,j,grid.get(i).get(j)));
				}
				
				else if(makingTherm &&stringGrid[i][j].startsWith("CL"))
				{
					rowTherms.get(i).get(rowTherms.get(i).size()-1).add(new ThermometerPiece(i,j,grid.get(i).get(j)));
					makingTherm = false;
				}	
			}	
		}
		
		
		
		//For each row, make a list of right-to-left thermometers in that row.
		for(int i = 0; i < stringGrid.length; i++)
		{
			//When we see a code starting in BL, we know we have spotted a right-to-left row thermometer,
			//  so add all of the information for the thermometer to the list.  For more information on 
			//  string codes, please refer to BoardFileIO.java
			boolean makingTherm = false;
			for(int j = stringGrid[0].length-1; j >= 0; j--)
			{
				if(stringGrid[i][j].startsWith("BL"))
				{
					rowTherms.get(i).add(new ArrayList<ThermometerPiece>());
					rowTherms.get(i).get(rowTherms.get(i).size()-1).add(new ThermometerPiece(i,j,grid.get(i).get(j)));
					makingTherm = true;
				}
				
				else if(makingTherm && stringGrid[i][j].startsWith("TH"))
				{
					rowTherms.get(i).get(rowTherms.get(i).size()-1).add(new ThermometerPiece(i,j,grid.get(i).get(j)));
				}
				
				else if(makingTherm &&stringGrid[i][j].startsWith("CR"))
				{
					rowTherms.get(i).get(rowTherms.get(i).size()-1).add(new ThermometerPiece(i,j,grid.get(i).get(j)));
					makingTherm = false;
				}	
			}
		}
		
		
		
		
		
		
		
		//Same for columns: Once for top-to-bottom, once for bottom-to-top
		for(int i = 0; i < stringGrid[0].length; i++)
		{
			colTherms.add(new ArrayList<ArrayList<ThermometerPiece>>());

			//When we see a code starting in BD, we know we have spotted a top-to-bottom column thermometer,
			//  so add all of the information for the thermometer to the list.  For more information on 
			//  string codes, please refer to BoardFileIO.java
			boolean makingTherm = false;
			for(int j = 0; j < stringGrid.length; j++)
			{
				if(stringGrid[j][i].startsWith("BD"))
				{
					colTherms.get(i).add(new ArrayList<ThermometerPiece>());
					colTherms.get(i).get(colTherms.get(i).size()-1).add(new ThermometerPiece(j,i,grid.get(j).get(i)));
					makingTherm = true;
				}
				
				else if(makingTherm && stringGrid[j][i].startsWith("TV"))
				{
					colTherms.get(i).get(colTherms.get(i).size()-1).add(new ThermometerPiece(j,i,grid.get(j).get(i)));
				}
				
				else if(makingTherm &&stringGrid[j][i].startsWith("CU"))
				{
					colTherms.get(i).get(colTherms.get(i).size()-1).add(new ThermometerPiece(j,i,grid.get(j).get(i)));
					makingTherm = false;
				}
			}			
		}
	
		//Columns bottom-to-top
		for(int i = 0; i < stringGrid[0].length; i++)
		{
			//When we see a code starting in BU, we know we have spotted a bottom-to-top column thermometer,
			//  so add all of the information for the thermometer to the list.  For more information on 
			//  string codes, please refer to BoardFileIO.java
			boolean makingTherm = false;
			for(int j = stringGrid.length - 1; j >= 0; j--)
			{
				if(stringGrid[j][i].startsWith("BU"))
				{
					colTherms.get(i).add(new ArrayList<ThermometerPiece>());
					colTherms.get(i).get(colTherms.get(i).size()-1).add(new ThermometerPiece(j,i,grid.get(j).get(i)));
					makingTherm = true;
				}
				
				else if(makingTherm && stringGrid[j][i].startsWith("TV"))
				{
					colTherms.get(i).get(colTherms.get(i).size()-1).add(new ThermometerPiece(j,i,grid.get(j).get(i)));
				}
				
				else if(makingTherm &&stringGrid[j][i].startsWith("CD"))
				{
					colTherms.get(i).get(colTherms.get(i).size()-1).add(new ThermometerPiece(j,i,grid.get(j).get(i)));
					makingTherm = false;
				}
			}	
		}
	}
	
	/** Check to see if the board has been successfully solved
	 * 
	 * @return True for a solved board, and false for one that is not fully solved.
	 */
	public boolean isSolved() {
		//The grid and solution grid are always the same size, so just check that all of the corresponding
		//  tiles match.
		for(int i = 0; i < grid.size(); i++)
			for(int j = 0; j < grid.size(); j++)
				if(grid.get(i).get(j) != solutionGrid.get(i).get(j))
				{
					return false;
				}
		return true;
	}
	
	/**  This function simply prints the string representation of the Thermometers board structure.
	 * Used exclusively for debugging/visualization.
	 */
	public void printStringGrid() {
		for(int i = 0; i < stringGrid.length; i++)
		{
			for(int j = 0; j < stringGrid.length; j++)
				System.out.print(stringGrid[i][j] + ",");
			System.out.println();
		}
	}

	/**  This function simply prints the string representation of the Thermometers board structure
	 * Along with the number of tiles that should be filled in each row or column. 
	 * Used exclusively for debugging/visualization.
	 */
	public void printStringGridWithConstraints() {
		System.out.print("   ");
		
		for(int i = 0; i < stringGrid.length; i++)
			System.out.print("  " + colCounts.get(i) + " ");
		System.out.println();
		
		for(int i = 0; i < stringGrid.length; i++)
		{
			System.out.print(rowCounts.get(i) + "   ");
			for(int j = 0; j < stringGrid.length; j++)
				System.out.print(stringGrid[i][j] + ",");
			System.out.println();
		}
	}	
	
	/**  This function simply prints the current fill of each tile on the Thermometers board.
	 * Used exclusively for debugging/visualization.
	 */
	public void printCurrentGrid() {
		for(int i = 0; i < grid.size(); i++)
		{
			for(int j = 0; j < grid.size(); j++)
				if(grid.get(i).get(j) == -1)
					System.out.print(grid.get(i).get(j) + ",");
				else
					System.out.print(" " + grid.get(i).get(j) + ",");
			System.out.println();
		}
	}
	
	/**  This function simply prints the "correct" fill of each tile on the Thermometers board.
	 * Used exclusively for debugging/visualization.
	 */
	public void printSolutionGrid() {
		for(int i = 0; i < solutionGrid.size(); i++)
		{
			for(int j = 0; j < solutionGrid.size(); j++)
				if(solutionGrid.get(i).get(j) == -1)
					System.out.print(solutionGrid.get(i).get(j) + ",");
				else
					System.out.print(" " + solutionGrid.get(i).get(j) + ",");
			System.out.println();
		}
	}
	
}