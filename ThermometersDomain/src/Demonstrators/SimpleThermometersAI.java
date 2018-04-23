package Demonstrators;

import java.util.ArrayList;

import BoardRepresentation.Board;
import BoardRepresentation.SimpleThermsBoard;
import BoardRepresentation.ThermometerPiece;
import Utilities.Triple;

/** AI Demonstrator for the Simple Thermometers board.  Acts a lot like the Complex Thermometers demonstrator,
 *   but is only given a single row or column of the board at a time, and has different actions (although mostly the same logic).
 *   
 *  Possible actions:
 *     Fill a tile in the row/column
 *     Empty a tile in the row/column
 *     Move to the next row/column
 *     Clear the row/column and move to the first column/row (respectively). 
 *   
 * 
 * 
 * @author Skularach
 *
 */
public class SimpleThermometersAI
{
	/** General make a move rule.  Checks each rule to see if any moves are applicable.
	 * 
	 * 
	 * @param board The board on which to make a move.
	 * @return The move to make.
	 */
	public Triple makeAMove(SimpleThermsBoard board )
	{
		//Set up linear "view"
		ArrayList<Integer> currentLine =  new ArrayList<Integer>();
		ArrayList<ArrayList<ThermometerPiece>> therms;
		int fillCount = -1;
		boolean isRow = board.lookingAtRows;
		int rowOrColIndex = -1;
		if(isRow)
			rowOrColIndex = board.rowIndex;
		else
			rowOrColIndex = board.colIndex;
		
		//Get the data for the row or column we are supposed to make a move for.
		if(isRow)
		{
			therms = board.rowTherms.get(rowOrColIndex);
			fillCount = board.rowCounts.get(rowOrColIndex);
			for(int i = 0; i < Board.BOARDSIZE; i++)
				currentLine.add(board.grid.get(rowOrColIndex).get(i));
		}
		else
		{
			therms = board.colTherms.get(rowOrColIndex);
			fillCount = board.colCounts.get(rowOrColIndex);
			for(int i = 0; i < Board.BOARDSIZE; i++)
				currentLine.add(board.grid.get(i).get(rowOrColIndex));
		}
		
		Triple expertMove = null;
		
		//If there are constraint violations in the row/column, 
		//  always return the clear move.
		if(board.detectConstraintViolations(currentLine, fillCount, therms))
			return new Triple ('c',-1,-1000);
		
		//Call each rule in order, return the first one that doesn't give us a null move.
		expertMove = restFilledRule(currentLine,fillCount,isRow,rowOrColIndex);
		if(expertMove != null)
			return expertMove;
		
		expertMove = restEmptyRule(currentLine,fillCount,isRow,rowOrColIndex);
		if(expertMove != null)
			return expertMove;
		
		expertMove = restThermEmptyRule(currentLine,fillCount,therms, isRow);
		if(expertMove != null)
			return expertMove;
		
		expertMove = restThermFilledRule(currentLine,fillCount,therms, isRow);
		if(expertMove != null)
			return expertMove;
		
		expertMove = thermTooBigRule(currentLine,fillCount,therms, isRow);
		if(expertMove != null)
			return expertMove;
		
		expertMove = partiallyFillThermRule(currentLine, fillCount, therms, isRow);
		if(expertMove != null)
			return expertMove;
		
		//if STILL null, cycle to next row/column with "move" action
		return new Triple('m',-1,-1000);
	}
	
	/** If the rest of the tiles in the row/column should be filled
	 * 
	 * 
	 * @param linearArray The row or column
	 * @param fillCount The number of tiles that should be filled in
	 * @param isRow If the linearArray is a row
	 * @param rowOrColIndex index of which row or column we are acting upon.
	 * @return
	 */
	public Triple restFilledRule(ArrayList<Integer> linearArray,
			int fillCount, boolean isRow, int rowOrColIndex) 
	{
		
		
		int filledSquares = 0;
		int blankSquares = 0;
		
		//Get number of filled and blank squares
		for(int j = 0; j < linearArray.size();j++)
		{
			if(linearArray.get(j) == 1)
				filledSquares++;
			if(linearArray.get(j) == 0)
				blankSquares++;
		}
		
		//The rest of the tiles need filled in
		if(blankSquares == fillCount - filledSquares)
		{
			//Return to fill the first blank value in row/column
			for(int j = 0; j < linearArray.size();j++)
			{
				if(linearArray.get(j) == 0)
				{
						return new Triple('f',j,-1000);
				}
			}
		}
		
		//Rule did not apply, return null
		return null;
	}
	
	
	/** If the rest of the tiles in the row/column should be empty
	 * 
	 * 
	 * @param linearArray The row or column
	 * @param fillCount The number of tiles that should be filled in
	 * @param isRow If the linearArray is a row
	 * @param rowOrColIndex index of which row or column we are acting upon.
	 * @return
	 */
	public Triple restEmptyRule(ArrayList<Integer> grid,
			int fillCount, boolean isRow, int rowOrColIndex) 
	{
			int blankSquares = 0;
			int emptySquares = 0;
			
			//Get the current number of blank and empty squares
			for(int j = 0; j < grid.size();j++)
			{
				if(grid.get(j) == 0)
					blankSquares++;
				if(grid.get(j) == -1)
					emptySquares++;
			}
			
			//The rest of the tiles must be empty, because the number of unknown squares is the same as the remaining
			//  number of tiles that need to be empty.
			if(blankSquares == (grid.size() - fillCount) - emptySquares)
			{
				for(int j = 0; j < grid.size();j++)
				{
					if(grid.get(j) == 0)
					{
						return new Triple('e',j,-1000);	
					}
				}
			}
		
		//Rule did not apply
		return null;
	}

	
	/**  If a thermometer has an empty tile in it at some point, we know all tiles after that one in 
	 *     the thermometer must also be emptied.
	 * 
	 * @param grid  The current fills for the row/column
	 * @param fillCount  The required fills for the row/column
	 * @param therms  All of the thermometers in the row/column
	 * @param isRow  Whether we are looking at a row or column
	 * @return  The move to make as a Triple
	 */
	public Triple restThermEmptyRule(ArrayList<Integer> grid,
			int fillCount, ArrayList<ArrayList<ThermometerPiece>> therms, boolean isRow) 
	{
		
		//For each thermometer
		for(int j = 0; j < therms.size(); j++)
		{
			int thermIndex = 0;
			while(thermIndex < therms.get(j).size() && (therms.get(j).get(thermIndex).getFillStatus() == 0 ||therms.get(j).get(thermIndex).getFillStatus() == 1) )
			{
				thermIndex++;
			}
			
			//If there is an empty tile in the thermometer
			if(thermIndex < therms.get(j).size()
						&& therms.get(j).get(thermIndex).getFillStatus() == -1)
			{

				int fillTherm = thermIndex+1;

				//Empty the first unknown tile in the thermometer AFTER the empty 
				while(fillTherm < therms.get(j).size())
				{
					ThermometerPiece examinedPiece = therms.get(j).get(fillTherm);
					if(therms.get(j).get(fillTherm).getFillStatus() == 0)
					{
						//Construct triple based on whether we are looking at row or column
						if(isRow)
							return new Triple('e',examinedPiece.getYPos(),-1000);
						else
							return new Triple('e',examinedPiece.getXPos(),-1000);
					}
					fillTherm++;
				}
			}
		}

		//The rule does not apply.
		return null;
	}
	
	/**  If a thermometer has a filled tile in it at some point, all of the tiles before
	 *     that one (relative to the bulb) should be filled in.
	 * 
	 * @param grid  The current fills for the row/column
	 * @param fillCount  The required fills for the row/column
	 * @param therms  All of the thermometers in the row/column
	 * @param isRow  Whether we are looking at a row or column
	 * @return  The move to make as a Triple
	 */
	public Triple restThermFilledRule(ArrayList<Integer> grid,
			int fillCount, ArrayList<ArrayList<ThermometerPiece>> therms, boolean isRow) 
	{
		//Each thermometer
		for(int j = 0; j < therms.size(); j++)
		{
			
			//Get the farthest tile in the thermometer that has been filled
			int farthestFilled = -1;
			for(int k = 0; k < therms.get(j).size(); k++)
			{
				if(therms.get(j).get(k).getFillStatus() == 1)
					farthestFilled = k;
			}
			
			//The first tile from the bulb up that is blank and before the farthest filled tile must be filled.
			for(int k = 0; k < farthestFilled; k++)
				if(therms.get(j).get(k).getFillStatus() == 0)
					if(isRow)
						return new Triple('f',therms.get(j).get(k).getYPos(),-1000);
					else
						return new Triple('f',therms.get(j).get(k).getXPos(),-1000);
		}
		
		//The rule does not apply.
		return null;
	}
	
	
	/** If we have less tiles left to fill than there are blanks in a thermometer, we know some of those tiles must be marked empty.
	 * 
	 *  For example, if there is one 5-tile long thermometer (on a 5x5 board), and only 3 squares should be filled in the row/column, 
	 *   then we know the last two tiles in the thermometer must be empty, since it has to be filled from the bulb.
	 *
	 * @param grid  The current fills for the row/column
	 * @param fillCount  The required fills for the row/column
	 * @param therms  All of the thermometers in the row/column
	 * @param isRow  Whether we are looking at a row or column
	 * @return  The move to make as a Triple
	 */
	public Triple thermTooBigRule(ArrayList<Integer> grid,
			int fillCount, ArrayList<ArrayList<ThermometerPiece>> therms, boolean isRow) 
	{		
	
		int filledSquares = 0;
		
		//Get number of currently filled in squares
		for(int j = 0; j < grid.size();j++)
		{
			if(grid.get(j) == 1)
				filledSquares++;
		}
		
		int squaresLeftToFill = fillCount - filledSquares;
		
		//For each thermometer
		for(int j = 0; j < therms.size(); j++)
		{
			//Get the number of unknown tiles
			int blanksInTherm = 0;
			for(int k = 0; k < therms.get(j).size(); k++)
				if(therms.get(j).get(k).getFillStatus() == 0)
					blanksInTherm++;
			
			//If there are less squares that still need filled to meet the row/column constraint than there are blanks in the thermometer,
			if(squaresLeftToFill < blanksInTherm)
			{
				//starting at the cap, set blanks to -1
				int thermIndex = therms.get(j).size() - 1;
				int squaresGlassed = 0;
				while(thermIndex >= 0 && therms.get(j).get(thermIndex).getFillStatus() != 1
							&& squaresGlassed < (blanksInTherm - squaresLeftToFill))
				{
					if(therms.get(j).get(thermIndex).getFillStatus() == 0)
					{
						//Return the move, dependent on whether we are looking at rows or columns.
						if(isRow)
							return new Triple('e',therms.get(j).get(thermIndex).getYPos(),-1000);
						else
							return new Triple('e',therms.get(j).get(thermIndex).getXPos(),-1000);
					}
					thermIndex--;
				}
			}
				
		}

		//The rule does not apply.
		return null;
	}
	
	
	/** If we have more tiles left to fill than there are blanks outside the thermometer, we know some of the tiles must be filled.
	 * 
	 *  For example, if there is one 5-tile long thermometer (on a 5x5 board), and only 3 squares should be filled in the row/column, 
	 *   then we know the first three tiles (relative to the bulb) in the thermometer must be empty, since it has to be filled from the bulb.
	 *
	 * @param grid  The current fills for the row/column
	 * @param fillCount  The required fills for the row/column
	 * @param therms  All of the thermometers in the row/column
	 * @param isRow  Whether we are looking at a row or column
	 * @return  The move to make as a Triple
	 */
	public Triple partiallyFillThermRule(ArrayList<Integer> grid,
			int fillCount, ArrayList<ArrayList<ThermometerPiece>> therms, boolean isRow) 
	{
		int filledSquares = 0;
		int blankSquares = 0;
		
		//Get number of filled and blank squares in the row/column
		for(int j = 0; j < grid.size();j++)
		{
			if(grid.get(j) == 1)
				filledSquares++;
			if(grid.get(j) == 0)
				blankSquares++;
		}
		
		//Squares left to need filled
		int squaresLeftToFill = fillCount - filledSquares;
		
		for(int j = 0; j < therms.size(); j++)
		{
			//Get the number of blanks in the thermometer
			int blanksInTherm = 0;
			for(int k = 0; k < therms.get(j).size(); k++)
				if(therms.get(j).get(k).getFillStatus() == 0)
					blanksInTherm++;
			
			int blanksNotInTherm = blankSquares - blanksInTherm;
			
			//If there are less blanks outside the thermometer than there are tiles left to fill
			if(blanksNotInTherm < squaresLeftToFill)
			{
				//Set the first tile, from the bulb up, in the thermometer that is unknown to filled
				int thermIndex = 0;
				int squaresFilled= 0;
				while(thermIndex < therms.get(j).size() && therms.get(j).get(thermIndex).getFillStatus() != -1
							&& squaresFilled < (squaresLeftToFill - blanksNotInTherm))
				{
					if(therms.get(j).get(thermIndex).getFillStatus() == 0)
					{
						//Return to fill a tile, based on whether we are looking at a row or column
						if(isRow)
							return new Triple('f',therms.get(j).get(thermIndex).getYPos(),-1000);						//allMoves.add(aMove);
						else
							return new Triple('f',therms.get(j).get(thermIndex).getXPos(),-1000);
					}
					thermIndex++;
				}
			}
				
		}
		
		//The rule does not apply
		return null;
	}
}