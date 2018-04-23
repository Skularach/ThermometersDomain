package FactsExtraction;

import java.util.ArrayList;

import javafx.util.Pair;
import BoardRepresentation.ThermometerPiece;
import Utilities.Quadruple;
import Utilities.Triple;

/** This class is for extracting what rules apply to a Thermometers board so that learning instances
 *    can be made from that board.  Used for the Complex Thermometers domain only.
 * 
 * 
 * @author Brandon Packard
 *
 */
public class RuleBasedBoardFacts
{
	/** This function checks whether the restFilled rule applies.  This rule checks to see if
	 *    the rest of the tiles in any row/column must be filled.  This happens if the number 
	 *    of unknown tiles in the row matches the number of tiles that must still be filled in.
	 * 
	 * @param grid The grid on which to check the rule
	 * @param rowCounts The number of tiles that should be filled in each row
	 * @param colCounts The number of tiles that should be filled in each column
	 * @return A list of all places where the rule applies, where each place is <{r,c},{index of row/column} >
	 */
	public static ArrayList<Pair<Character, Integer>> restFilledRuleFacts(ArrayList<ArrayList<Integer>> grid,
			ArrayList<Integer> rowCounts, ArrayList<Integer> colCounts) 
	{
		ArrayList<Pair<Character, Integer>> allPairs = new ArrayList<Pair<Character, Integer>>();
		
		//For each row
		for(int i = 0; i < grid.size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get number of filled and empty squares
			for(int j = 0; j < grid.get(i).size();j++)
			{
				if(grid.get(i).get(j) == 1)
					filledSquares++;
				if(grid.get(i).get(j) == 0)
					blankSquares++;
			}
			
			//If the number of blank squares is the same as the number of tiles we still
			//  need to fill in that row, add a location where the rule applies
			if(blankSquares == rowCounts.get(i) - filledSquares && blankSquares > 0)
			{
				allPairs.add(new Pair<Character, Integer>('r', i)); 
			}
		}
		
		
		
		//For each column
		for(int i = 0; i < grid.get(0).size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get number of filled and empty squares
			for(int j = 0; j < grid.size();j++)
			{
				if(grid.get(j).get(i) == 1)
					filledSquares++;
				if(grid.get(j).get(i) == 0)
					blankSquares++;
			}
			
			//If the number of blank squares is the same as the number of tiles we still
			//  need to fill in that row, add a location where the rule applies
			if(blankSquares == colCounts.get(i) - filledSquares && blankSquares > 0)
			{
				allPairs.add(new Pair<Character, Integer>('c', i)); 
			}
		}
		
		//Return all locations where the rule applies
		return allPairs;
	}
	
	/** This function checks whether the restEmpty rule applies.  This rule checks to see if
	 *    the rest of the tiles in any row/column must be empty.  This happens if the number 
	 *    of unknown tiles in the row matches the number of tiles that must still be emtpied.
	 * 
	 * @param grid The grid on which to check the rule
	 * @param rowCounts The number of tiles that should be filled in each row
	 * @param colCounts The number of tiles that should be filled in each column
	 * @return A list of all places where the rule applies, where each place is <{r,c},{index of row/column} >
	 */
	public static ArrayList<Pair<Character, Integer>>  restEmptyRuleFacts(ArrayList<ArrayList<Integer>> grid,
			ArrayList<Integer> rowCounts, ArrayList<Integer> colCounts) 
	{
		//List of locations where the rule applies
		ArrayList<Pair<Character, Integer>> allPairs = new ArrayList<Pair<Character, Integer>>();
		
		//For each row
		for(int i = 0; i < grid.size(); i++)
		{
			int blankSquares = 0;
			int emptySquares = 0;
			
			//Get the number of blank and empty tiles
			for(int j = 0; j < grid.get(i).size();j++)
			{
				if(grid.get(i).get(j) == 0)
					blankSquares++;
				if(grid.get(i).get(j) == -1)
					emptySquares++;
			}
			
			//If the number of blank squares is equal to the number of squares that still need 
			//  emptied in the row due to the row constraint.
			if(blankSquares == (grid.size() - rowCounts.get(i)) - emptySquares  && blankSquares > 0)
			{
				allPairs.add(new Pair<Character, Integer> ('r',i) );
			}
		}
		
		
		
		//For each column
		for(int i = 0; i < grid.get(0).size(); i++)
		{
			int blankSquares = 0;
			int emptySquares = 0;
			
			//Get the number of blank and empty tiles
			for(int j = 0; j < grid.size();j++)
			{
				if(grid.get(j).get(i) == 0)
					blankSquares++;
				if(grid.get(j).get(i) == -1)
					emptySquares++;
			}
			
			//If the number of blank squares is equal to the number of squares that still need 
			//  emptied in the column due to the column constraint.
			if(blankSquares == (grid.get(0).size() - colCounts.get(i)) - emptySquares  && blankSquares > 0)
			{
				allPairs.add(new Pair<Character, Integer> ('c',i) );
			}
		}
		
		//Return all locations where the rule applies
		return allPairs;
	}
	
	
	/** This method checks to see if the rest of a thermometer must be empty due to part of it being empty
	 *    Specifically, if there is an empty tile in the thermometer, all tiles after that one (away from the bulb)
	 *    must also be empty, since thermometers must be filled from the bulb up.
	 * 
	 * @param grid The grid on which to check if the rule applies
	 * @param rowCounts The number of tiles that should be filled in each row
	 * @param colCounts The number of tiles that should be filled in each column
	 * @param rowTherms The thermometer data for each row
	 * @param colTherms The thermometer data for each column
	 * @return List of locations on the board where the rule applies
	 */
	public static ArrayList<Triple> restThermEmptyRuleFacts(ArrayList<ArrayList<Integer>> grid,
			ArrayList<Integer> rowCounts, ArrayList<Integer> colCounts, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> rowTherms, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> colTherms) 
	{
		ArrayList<Triple> allFacts = new ArrayList<Triple>();
		
		//For each row
		for(int i = 0; i < rowTherms.size(); i++)
		{
			//For each thermometer
			for(int j = 0; j < rowTherms.get(i).size(); j++)
			{
				//While the tile is filled or unknown, move through the thermometer
				int thermIndex = 0;
				while(thermIndex < rowTherms.get(i).get(j).size() && (rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0 ||rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1) )
				{
					thermIndex++;
				}
				
				//If there are still tiles left, we found a -1 tile, so mark the next as  empty as well
				if(thermIndex < rowTherms.get(i).get(j).size()
							&& rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == -1)
				{
					
					allFacts.add(new Triple('r',i,j));
				}
			}
		}
		
		//For each column
		for(int i = 0; i < colTherms.size(); i++)
		{
			for(int j = 0; j < colTherms.get(i).size(); j++)
			{
				//While the tile is filled or unknown, move through the thermometer
				int thermIndex = 0;
				while(thermIndex < colTherms.get(i).get(j).size() && (colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0 || colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1))
				{
					thermIndex++;
				}
				
				//If there are still tiles left, we found a -1 tile, so mark the next as  empty as well
				if(thermIndex < colTherms.get(i).get(j).size()
							&& colTherms.get(i).get(j).get(thermIndex).getFillStatus() == -1)
				{
					//If there are still tiles left, we found a -1 tile, so mark the next as  empty as well
					allFacts.add(new Triple('c',i,j));
				}
			}
		}
	
		//Return the list of places where the rule applies
		return allFacts;
	}

	
	/** This method checks to see if the start of a thermometer must be filled due to part of it being filled
	 *    Specifically, if there is an filled tile in the thermometer, all tiles before that one (from the bulb)
	 *    must also be filled, since thermometers must be filled from the bulb up.
	 * 
	 * @param grid The grid on which to check if the rule applies
	 * @param rowCounts The number of tiles that should be filled in each row
	 * @param colCounts The number of tiles that should be filled in each column
	 * @param rowTherms The thermometer data for each row
	 * @param colTherms The thermometer data for each column
	 * @return List of locations on the board where the rule applies
	 */
	public static ArrayList<Triple> restThermFilledRuleFacts(ArrayList<ArrayList<Integer>> grid,
			ArrayList<Integer> rowCounts, ArrayList<Integer> colCounts, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> rowTherms, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> colTherms) 
	{
		ArrayList<Triple> allFacts = new ArrayList<Triple>();
		
		//For each row
		for(int i = 0; i < rowTherms.size(); i++)
		{
			//For each thermometer
			for(int j = 0; j < rowTherms.get(i).size(); j++)
			{
				boolean hasBlank = false;
				int thermIndex = 0;
				
				//Move through the thermometer until we encounter an empty tile or run out of tiles
				while(thermIndex < rowTherms.get(i).get(j).size() && (rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0 || rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1))
				{
					//Store if we encounter an unknown tile at any point
					if (rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						hasBlank = true;
					thermIndex++;
					
				}
				
				//If there was a blank that needs filled, fill it by moving backwards
				if(thermIndex == rowTherms.get(i).get(j).size() && hasBlank)
					thermIndex--;
				else if(thermIndex > 0 && hasBlank && rowTherms.get(i).get(j).get(thermIndex - 1).getFillStatus() == 1)
					thermIndex--;
				
				if(thermIndex < rowTherms.get(i).get(j).size()
							&& rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1)
				{
					//rule applies, fill it with 1s previous to this point
					allFacts.add(new Triple('r',i,j));
				}
			}
		}
		
		//For each row
		for(int i = 0; i < colTherms.size(); i++)
		{
			//For each column
			for(int j = 0; j < colTherms.get(i).size(); j++)
			{
				boolean hasBlank = false;
				int thermIndex = 0;
				
				//Move through the thermometer until we encounter an empty tile or run out of tiles
				while(thermIndex < colTherms.get(i).get(j).size() && (colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0 || colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1))
				{
					if (colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						hasBlank = true;
					thermIndex++;
					
				}
				
				//If there was a blank that needs filled, fill it by moving backwards
				if(thermIndex == colTherms.get(i).get(j).size() && hasBlank)
					thermIndex--;
				else if(thermIndex > 0 && hasBlank && colTherms.get(i).get(j).get(thermIndex - 1).getFillStatus() == 1)
					thermIndex--;
					
				if(thermIndex < colTherms.get(i).get(j).size()
							&& colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1)
				{
					//rule applies, fill it with 1s previous to this point
					allFacts.add(new Triple('c',i,j));
				}
			}
		}
	
		//Return all locations where the rule applies
		return allFacts;
	}

	/** This method checks to see if a thermometer is too big and must be partially empty.  For example, if a thermometer is 3 tiles long,
	 *     all unknown, and only 2 more tiles should be filled, we know the last tile in the thermometer must be empty
	 * 
	 * 
	 * 
	 * @param grid  The grid on which to check if the rule applies
	 * @param rowCounts The number of tiles that should be filled in each row
	 * @param colCounts The number of tiles that should be filled in each column
	 * @param rowTherms Data on the thermometers in each row
	 * @param colTherms Data on the thermometers in each column
	 * @return
	 */
	public static ArrayList<Quadruple> thermTooBigRuleFacts(ArrayList<ArrayList<Integer>> grid,
			ArrayList<Integer> rowCounts, ArrayList<Integer> colCounts, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> rowTherms, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> colTherms) 
	{
		ArrayList<Quadruple> allFacts = new ArrayList<Quadruple>();
		
		//For each row
		for(int i = 0; i < rowTherms.size(); i++)
		{
			int filledSquares = 0;
			
			//Get the number of filled in squares
			for(int j = 0; j < grid.get(i).size();j++)
			{
				if(grid.get(i).get(j) == 1)
					filledSquares++;
			}
			
			//Number of squares that still need filled
			int squaresLeftToFill = rowCounts.get(i) - filledSquares;
			
			//For each thermometer
			for(int j = 0; j < rowTherms.get(i).size(); j++)
			{
				//Get the number of blanks in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < rowTherms.get(i).get(j).size(); k++)
					if(rowTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				//if there are less squares to fill than blanks in the thermometer...
				if(squaresLeftToFill < blanksInTherm)
				{
					//starting at the cap, set tiles to empty (if there are 2 less tiles to fill than blanks in the 
					// thermometer,  set 2 tiles to empty)
					int thermIndex = rowTherms.get(i).get(j).size() - 1;
					int squaresGlassed = 0;
					while(thermIndex >= 0 && rowTherms.get(i).get(j).get(thermIndex).getFillStatus() != 1
								&& squaresGlassed < (blanksInTherm - squaresLeftToFill))
					{
						if(rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
							squaresGlassed++;
						}
						thermIndex--;
					}
					
					//Information for the rule: row, tile location, and number of squares to mark as empty
					allFacts.add(new Quadruple('r', i, j, squaresGlassed));
				}
					
			}
		}
	
		
		
		
		//For each column
		for(int i = 0; i < colTherms.size(); i++)
		{
			int filledSquares = 0;
			
			//Get the number of filled in squares
			for(int j = 0; j < grid.get(i).size();j++)
			{
				if(grid.get(j).get(i) == 1)
					filledSquares++;
			}
			
			//Number of squares that still need filled
			int squaresLeftToFill = colCounts.get(i) - filledSquares;
			
			//For each thermometer
			for(int j = 0; j < colTherms.get(i).size(); j++)
			{
				//Get the number of blanks in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < colTherms.get(i).get(j).size(); k++)
					if(colTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				//if there are less squares to fill than blanks in the thermometer...
				if(squaresLeftToFill < blanksInTherm)
				{
					//starting at the cap, set tiles to empty (if there are 2 less tiles to fill than blanks in the 
					// thermometer,  set 2 tiles to empty)
					int thermIndex = colTherms.get(i).get(j).size() - 1;
					int squaresGlassed = 0;
					while(thermIndex >= 0 && colTherms.get(i).get(j).get(thermIndex).getFillStatus() != 1
								&& squaresGlassed < (blanksInTherm - squaresLeftToFill))
					{
						if(colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
							squaresGlassed++;
						}
						thermIndex--;
					}
					
					//Information for the rule: column, tile location, and number of squares to mark as empty
					allFacts.add(new Quadruple('c', i, j, squaresGlassed));
				}
					
			}
		}
		
		//Return all applications of the rule
		return allFacts;
	}
	
	/** This method checks to see if a thermometer must be partially full due to the number of tiles that still need filled in the row/column.
	 *     For example,if there are 3 tiles that need filled in a row, and the row consists of 2 filled tiles and a 
	 *     thermometer of size 3 which is currently all unknown, the bulb of the thermometer must be filled because
	 *     there must be at least one tile filled in the thermometer to meet the row/column constraint, and
	 *     thermometers must be filled from the bulb up.
	 * 
	 * @param grid  The grid on which to check if the rule applies
	 * @param rowCounts The number of tiles that should be filled in each row
	 * @param colCounts The number of tiles that should be filled in each column
	 * @param rowTherms Data on the thermometers in each row
	 * @param colTherms Data on the thermometers in each column
	 * @return
	 */
	public static ArrayList<Quadruple>  partiallyFillThermRuleFacts(ArrayList<ArrayList<Integer>> grid,
			ArrayList<Integer> rowCounts, ArrayList<Integer> colCounts, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> rowTherms, ArrayList<ArrayList<ArrayList<ThermometerPiece>>> colTherms) 
	{
		ArrayList<Quadruple> allFacts = new ArrayList<Quadruple>();
		
		
		//For each row
		for(int i = 0; i < rowTherms.size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get the number of filled and unknown squares
			for(int j = 0; j < grid.get(i).size();j++)
			{
				if(grid.get(i).get(j) == 1)
					filledSquares++;
				if(grid.get(i).get(j) == 0)
					blankSquares++;
			}
			
			//Number of squares which still need to be filled in the row
			int squaresLeftToFill = rowCounts.get(i) - filledSquares;
			
			//For each thermometer
			for(int j = 0; j < rowTherms.get(i).size(); j++)
			{
				//Get the number of blanks in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < rowTherms.get(i).get(j).size(); k++)
					if(rowTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				//Get the number of blanks outside the thermometer
				int blanksNotInTherm = blankSquares - blanksInTherm;
				
				//If there aren't enough blanks outside the thermometer to meet the constraint...
				if(blanksNotInTherm < squaresLeftToFill)
				{
					int thermIndex = 0;
					int squaresFilled= 0;
					
					//Fill in the needed number of tiles inside the thermometer, starting at the bulb
					while(thermIndex < rowTherms.get(i).get(j).size() && rowTherms.get(i).get(j).get(thermIndex).getFillStatus() != -1
								&& squaresFilled < (squaresLeftToFill - blanksNotInTherm))
					{
						if(rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
							squaresFilled++;
						}
						thermIndex++;
					}
					//Information for the rule: column, tile location, and number of squares to mark as filled
					allFacts.add(new Quadruple('r',i, j,squaresFilled));
					
				}
					
			}
		}
	
		//For each column
		for(int i = 0; i < colTherms.size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get number of filled and blank squares
			for(int j = 0; j < grid.get(i).size();j++)
			{
				if(grid.get(j).get(i) == 1)
					filledSquares++;
				if(grid.get(j).get(i) == 0)
					blankSquares++;
			}
			
			//Get number of squares left to fill in the column
			int squaresLeftToFill = colCounts.get(i) - filledSquares;
			
			//For each thermometer
			for(int j = 0; j < colTherms.get(i).size(); j++)
			{
				//Get the number of blank tiles in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < colTherms.get(i).get(j).size(); k++)
					if(colTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				//Get the number of blank tiles not in the thermometer
				int blanksNotInTherm = blankSquares - blanksInTherm;
				
				//If there aren't enough blanks outside the thermometer to meet the constraint...
				if(blanksNotInTherm < squaresLeftToFill)
				{
					int thermIndex = 0;
					int squaresFilled= 0;
					
					//Fill in the needed number of tiles inside the thermometer, starting at the bulb
					while(thermIndex < colTherms.get(i).get(j).size() && colTherms.get(i).get(j).get(thermIndex).getFillStatus() != -1
								&& squaresFilled < (squaresLeftToFill - blanksNotInTherm))
					{
						if(colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
						
							squaresFilled++;
						}
						thermIndex++;
					}
					//Information for the rule: column, tile location, and number of squares to mark as filled
					allFacts.add(new Quadruple('c',i, j,squaresFilled));
				}
					
			}
		}
		
		//Return all tiles affected by the rule
		return allFacts;
	}
}