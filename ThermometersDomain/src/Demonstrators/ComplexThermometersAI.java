package Demonstrators;

import BoardActions.ApplyMovesToBoard;
import BoardRepresentation.ComplexThermsBoard;
import BoardRepresentation.ThermometerPiece;
import Utilities.Triple;

/** AI Demonstrator for the Complex Thermometers board. Can solve every one of the 168,000 included 5x5 boards
 * 
 * Possible Actions:
 *    Fill one of the 25 tiles (assuming a 5x5 board)
 *    Empty one of the 25 tiles
 *    Set one of the 25 tiles to unknown.
 * 
 * 
 * @author Brandon Packard
 *
 */
public class ComplexThermometersAI
{
	public ComplexThermometersAI()
	{
		
	}

	/** Given a board, make a logical move on it
	 * 
	 * @param board The board on which to make a move.
	 * @return The move to make, as a Triple
	 */
	public Triple makeAMove(ComplexThermsBoard board)
	{
		Triple expertMove = null;
		
		//If there are any detectable mistakes, make a move to help fix them.
		expertMove = undoMistakesRule(board);
		if(expertMove != null)
			return expertMove;
		
		//If there is a row/column where we know the rest should be filled in
		expertMove = restFilledRule(board);
		if(expertMove != null)
			return expertMove;

		//If there is a row/column where we know the rest should be empty
		expertMove = restEmptyRule(board);
		if(expertMove != null)
			return expertMove;
		
		//If there is a thermometer where we know the rest should be filled in
		expertMove = restThermEmptyRule(board);
		if(expertMove != null)
			return expertMove;
		
		//If there is a thermometer where we know the rest should be empty
		expertMove = restThermFilledRule(board);
		if(expertMove != null)
			return expertMove;
		
		//If there is a thermometer where we know some tiles are empty because the thermometer is too big to fully fill in
		expertMove = thermTooBigRule(board);
		if(expertMove != null)
			return expertMove;
		
		//If there is a thermometer where some MUST be filled in because of how many tiles still need filled in
		expertMove = partiallyFillThermRule(board);
		return expertMove;
	}
	
	/** This rule triggers when we know the rest of a row or column must be filled in.
	 *    For example, if a row says 3 tiles should be filled, and we have 2 filled 2 empty and 1 unknown,
	 *    we know the last unknown must be filled in.
	 * 
	 * 
	 * @param board  The board on which to make a move
	 * @return The move as a triple, or null if the move does not apply.
	 */
	public Triple restFilledRule(ComplexThermsBoard board) 
	{
		
		//rows
		for(int i = 0; i < board.grid.size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get the number of filled and empty squares
			for(int j = 0; j < board.grid.get(i).size();j++)
			{
				if(board.grid.get(i).get(j) == 1)
					filledSquares++;
				if(board.grid.get(i).get(j) == 0)
					blankSquares++;
			}
			
			//The rule applies because the number of unknown tiles + the number of filled tiles matches the required number of filled tiles
			if(blankSquares == board.rowCounts.get(i) - filledSquares)
			{
				//Get the next tile to fill
				for(int j = 0; j < board.grid.get(i).size();j++)
				{
					if(board.grid.get(i).get(j) == 0)
					{
						
						return new Triple(i,j,1);
						
					}
				}
			}
		}
		
		
		
		//Columns
		for(int i = 0; i < board.grid.get(0).size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get the number of filled and empty tiles.
			for(int j = 0; j < board.grid.size();j++)
			{
				if(board.grid.get(j).get(i) == 1)
					filledSquares++;
				if(board.grid.get(j).get(i) == 0)
					blankSquares++;
			}
			
			//The rule applies because the number of unknown tiles + the number of filled tiles matches the required number of filled tiles
			if(blankSquares == board.colCounts.get(i) - filledSquares)
			{
				for(int j = 0; j < board.grid.size();j++)
				{
					if(board.grid.get(j).get(i) == 0)
					{						
					return new Triple(j,i,1);
					
					}
				}
			}
		}
		
		return null;
	}
	
	
	/** This rule triggers when we know the rest of a row or column must be empty.
	 *    For example, if a row says 2 tiles should be filled (so 3 should be empty), and we have 2 filled 2 empty and 1 unknown,
	 *    we know the last unknown must be empty.
	 * 
	 * 
	 * @param board  The board on which to make a move
	 * @return The move as a triple, or null if the move does not apply.
	 */
	public Triple restEmptyRule(ComplexThermsBoard board) 
	{
		
		//rows
		for(int i = 0; i < board.grid.size(); i++)
		{
			int blankSquares = 0;
			int emptySquares = 0;
			
			//Get the number of blanks and the number of empty squares
			for(int j = 0; j < board.grid.get(i).size();j++)
			{
				if(board.grid.get(i).get(j) == 0)
					blankSquares++;
				if(board.grid.get(i).get(j) == -1)
					emptySquares++;
			}
			
			//Rule applies because the number of blanks + the number of empties == the number of tiles that should be blank in that row.
			if(blankSquares == (board.grid.size() - board.rowCounts.get(i)) - emptySquares)
			{
				//Get the next tile to empty
				for(int j = 0; j < board.grid.get(i).size();j++)
				{
					if(board.grid.get(i).get(j) == 0)
					{
						
						return new Triple(i,j,-1);
						
					}
				}
			}
		}
		
		
		
		//columns
		for(int i = 0; i < board.grid.get(0).size(); i++)
		{
			int blankSquares = 0;
			int emptySquares = 0;
			
			//Get the number of blanks and the number of empty squares
			for(int j = 0; j < board.grid.size();j++)
			{
				if(board.grid.get(j).get(i) == 0)
					blankSquares++;
				if(board.grid.get(j).get(i) == -1)
					emptySquares++;
			}
			
			//Rule applies because the number of blanks + the number of empties == the number of tiles that should be blank in that row.
			if(blankSquares == (board.grid.get(0).size() - board.colCounts.get(i)) - emptySquares)
			{
				//Get the next tile to set empty
				for(int j = 0; j < board.grid.size();j++)
				{
					if(board.grid.get(j).get(i) == 0)
					{						
						return new Triple(j,i,-1);
						
					}
				}
			}
		}
		
		return null;
	}

	
	/** If there is an empty tile in a thermometer, we know the rest of it has to be empty as well.
	 * 
	 * 
	 * 
	 * @param board The board on which to make a move
	 * @return The move to make as a triple, null if there is none.
	 */
	public Triple restThermEmptyRule(ComplexThermsBoard board)
	{
		
		//Each row
		for(int i = 0; i < board.rowTherms.size(); i++)
		{
			//Each thermometer in the row
			for(int j = 0; j < board.rowTherms.get(i).size(); j++)
			{
				//Move to the first empty piece of the thermometer (or the end, if there is none)
				int thermIndex = 0;
				while(thermIndex < board.rowTherms.get(i).get(j).size() && (board.rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0 || board.rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1) )
				{
					thermIndex++;
				}
				
				//If there is a empty piece in the thermometer, then it has to be empty from that place onwards
				if(thermIndex < board.rowTherms.get(i).get(j).size()
							&& board.rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == -1)
				{
					//Return the next tile in the thermometer that needs emptied
					int fillTherm = thermIndex+1;
					while(fillTherm < board.rowTherms.get(i).get(j).size())
					{
						ThermometerPiece examinedPiece = board.rowTherms.get(i).get(j).get(fillTherm);
						if(board.rowTherms.get(i).get(j).get(fillTherm).getFillStatus() == 0)
						{
							return new Triple(examinedPiece.getXPos(), examinedPiece.getYPos(),-1);
							
						}
						fillTherm++;
					}
				}
			}
		}
		
		//Each column
		for(int i = 0; i < board.colTherms.size(); i++)
		{
			for(int j = 0; j < board.colTherms.get(i).size(); j++)
			{
				//Each thermometer in column
				int thermIndex = 0;
				
				//Move to the first empty piece of the thermometer (or the end, if there is none)
				while(thermIndex < board.colTherms.get(i).get(j).size() && (board.colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0 || board.colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 1))
				{
					thermIndex++;
				}
				
				//If there is a empty piece in the thermometer, then it has to be empty from that place onwards
				if(thermIndex < board.colTherms.get(i).get(j).size()
							&& board.colTherms.get(i).get(j).get(thermIndex).getFillStatus() == -1)
				{
					//Return the next tile in the thermometer that needs emptied
					int fillTherm = thermIndex+1;
					while(fillTherm < board.colTherms.get(i).get(j).size())
					{
						
						ThermometerPiece examinedPiece = board.colTherms.get(i).get(j).get(fillTherm);
						if(board.colTherms.get(i).get(j).get(fillTherm).getFillStatus() == 0)
						{
							return new Triple(examinedPiece.getXPos(), examinedPiece.getYPos(),-1);
						
						}
						fillTherm++;
					}
				}
			}
		}
	
		//Rule didnt apply, so return null
		return null;
	}
	
	/** If there is a filled tile in a thermometer, we know the tiles before it must be filled as well.
	 * 
	 * 
	 * 
	 * @param board The board on which to make a move
	 * @return The move to make as a triple, null if there is none.
	 */
	public Triple restThermFilledRule(ComplexThermsBoard board) 
	{
		//Each row
		for(int i = 0; i < board.rowTherms.size(); i++)
		{
			//Each Thermometer
			for(int j = 0; j < board.rowTherms.get(i).size(); j++)
			{
				//get the farthest filled square from the base
				int farthestFilled = -1;
				for(int k = 0; k < board.rowTherms.get(i).get(j).size(); k++)
				{
					if(board.rowTherms.get(i).get(j).get(k).getFillStatus() == 1)
						farthestFilled = k;
				}
				
				//Make sure every square before that farthest one is filled
				for(int k = 0; k < farthestFilled; k++)
					if(board.rowTherms.get(i).get(j).get(k).getFillStatus() == 0)
						return new Triple(board.rowTherms.get(i).get(j).get(k).getXPos(), board.rowTherms.get(i).get(j).get(k).getYPos(), 1);
			}
		}
		
		//Each column
		for(int i = 0; i < board.colTherms.size(); i++)
		{
			//Each thermometer
			for(int j = 0; j < board.colTherms.get(i).size(); j++)
			{
				//get the farthest filled square from the base
				int farthestFilled = -1;
				for(int k = 0; k < board.colTherms.get(i).get(j).size(); k++)
				{
					if(board.colTherms.get(i).get(j).get(k).getFillStatus() == 1)
						farthestFilled = k;
				}
				
				//Check that each tile before that one is filled in
				for(int k = 0; k < farthestFilled; k++)
					if(board.colTherms.get(i).get(j).get(k).getFillStatus() == 0)
						return new Triple(board.colTherms.get(i).get(j).get(k).getXPos(), board.colTherms.get(i).get(j).get(k).getYPos(), 1);

			}
		}
		
		//None of the rules applied, so return null
		return null;
	}
	
	
	
	/** Thermometer is so big it can't possibly be filled.
	 * 
	 * For example, if there is one 5-tile long thermometer (on a 5x5 board), and only 3 squares should be filled in the row/column, 
	 *   then we know the last two tiles in the thermometer must be empty, since it has to be filled from the bulb.
	 * 
	 * 
	 * @param board The board on which to make a move
	 * @return A triple containing the move to make, null if none
	 */
	public Triple thermTooBigRule(ComplexThermsBoard board) 
	{		
		//Each row
		for(int i = 0; i < board.rowTherms.size(); i++)
		{
			int filledSquares = 0;
			
			//Get number of filled squares and squares left to be filled
			for(int j = 0; j < board.grid.get(i).size();j++)
			{
				if(board.grid.get(i).get(j) == 1)
					filledSquares++;
			}
			
			int squaresLeftToFill = board.rowCounts.get(i) - filledSquares;
			
			
			for(int j = 0; j < board.rowTherms.get(i).size(); j++)
			{
				//Get the blanks in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < board.rowTherms.get(i).get(j).size(); k++)
					if(board.rowTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				//If there are less squares left to fill than blanks
				if(squaresLeftToFill < blanksInTherm)
				{
					//starting at the cap, set blanks to -1
					int thermIndex = board.rowTherms.get(i).get(j).size() - 1;
					int squaresGlassed = 0;
					while(thermIndex >= 0 && board.rowTherms.get(i).get(j).get(thermIndex).getFillStatus() != 1
								&& squaresGlassed < (blanksInTherm - squaresLeftToFill))
					{
						if(board.rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
							return new Triple(board.rowTherms.get(i).get(j).get(thermIndex).getXPos(),board.rowTherms.get(i).get(j).get(thermIndex).getYPos(), -1);
						}
						thermIndex--;
					}
				}
					
			}
		}
	
		
		
		
		//Each column
		for(int i = 0; i < board.colTherms.size(); i++)
		{
			int filledSquares = 0;
			
			//Get number of filled squares and squares left to be filled
			for(int j = 0; j < board.grid.get(i).size();j++)
			{
				if(board.grid.get(j).get(i) == 1)
					filledSquares++;
			}
			
			int squaresLeftToFill = board.colCounts.get(i) - filledSquares;
			
			for(int j = 0; j < board.colTherms.get(i).size(); j++)
			{
				//Get the blanks in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < board.colTherms.get(i).get(j).size(); k++)
					if(board.colTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				//If there are less squares left to fill than blanks
				if(squaresLeftToFill < blanksInTherm)
				{
					//If there are less squares left to fill than blanks
					int thermIndex = board.colTherms.get(i).get(j).size() - 1;
					int squaresGlassed = 0;
					
					//starting at the cap, set blanks to -1
					while(thermIndex >= 0 && board.colTherms.get(i).get(j).get(thermIndex).getFillStatus() != 1
								&& squaresGlassed < (blanksInTherm - squaresLeftToFill))
					{
						if(board.colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
							return new Triple(board.colTherms.get(i).get(j).get(thermIndex).getXPos(),board.colTherms.get(i).get(j).get(thermIndex).getYPos(), -1);
						}
						thermIndex--;
					}
				}
					
			}
		}
	
		//Rule did not apply, so return null
		return null;
	}
	
	
	/** Thermometer has to be partially filled.
	 * 
	 * For example, if there is one 5-tile long thermometer (on a 5x5 board), and 2 squares should be filled in the row/column, 
	 *   then we know the bulb and the next piece after it must be the ones that are filled, since thermometers must be filled from the base.
	 * 
	 * 
	 * @param board The board on which to make a move
	 * @return A triple containing the move to make, null if none
	 */
	public Triple partiallyFillThermRule(ComplexThermsBoard board) 
	{
		//Each row
		for(int i = 0; i < board.rowTherms.size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get number of filled squares and blanks on the row.
			for(int j = 0; j < board.grid.get(i).size();j++)
			{
				if(board.grid.get(i).get(j) == 1)
					filledSquares++;
				if(board.grid.get(i).get(j) == 0)
					blankSquares++;
			}
			
			//Number of squares still needing filled
			int squaresLeftToFill = board.rowCounts.get(i) - filledSquares;
			
			for(int j = 0; j < board.rowTherms.get(i).size(); j++)
			{
				//Get the number of blank tiles in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < board.rowTherms.get(i).get(j).size(); k++)
					if(board.rowTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				int blanksNotInTherm = blankSquares - blanksInTherm;
				
				//If there aren't enough blanks outside of the thermometer to fill enough tiles to meet the required # of filled tiles, 
				//  then we know that some tiles in the thermometer must be filled, and we know that they have to be filled from the base up.
				if(blanksNotInTherm < squaresLeftToFill)
				{
					//Fill in the unknown tile in the thermometer that is closest to the bulb
					int thermIndex = 0;
					int squaresFilled= 0;
					while(thermIndex < board.rowTherms.get(i).get(j).size() && board.rowTherms.get(i).get(j).get(thermIndex).getFillStatus() != -1
								&& squaresFilled < (squaresLeftToFill - blanksNotInTherm))
					{
						if(board.rowTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
							return new Triple(board.rowTherms.get(i).get(j).get(thermIndex).getXPos(),board.rowTherms.get(i).get(j).get(thermIndex).getYPos(), 1);
						}
						thermIndex++;
					}
				}
					
			}
		}
	
		//Each Column
		for(int i = 0; i < board.colTherms.size(); i++)
		{
			int filledSquares = 0;
			int blankSquares = 0;
			
			//Get number of filled squares and blanks on the column.
			for(int j = 0; j < board.grid.get(i).size();j++)
			{
				if(board.grid.get(j).get(i) == 1)
					filledSquares++;
				if(board.grid.get(j).get(i) == 0)
					blankSquares++;
			}
			
			//Number of squares still needing filled
			int squaresLeftToFill = board.colCounts.get(i) - filledSquares;
			
			for(int j = 0; j < board.colTherms.get(i).size(); j++)
			{
				//Get the number of blank tiles in the thermometer
				int blanksInTherm = 0;
				for(int k = 0; k < board.colTherms.get(i).get(j).size(); k++)
					if(board.colTherms.get(i).get(j).get(k).getFillStatus() == 0)
						blanksInTherm++;
				
				int blanksNotInTherm = blankSquares - blanksInTherm;
				
				//If there aren't enough blanks outside of the thermometer to fill enough tiles to meet the required # of filled tiles, 
				//  then we know that some tiles in the thermometer must be filled, and we know that they have to be filled from the base up.
				if(blanksNotInTherm < squaresLeftToFill)
				{
					//there arent enough blanks outside the therm, so fill in the therm starting at the bottom
					
					int thermIndex = 0;
					int squaresFilled= 0;
					
					//Fill in the unknown tile in the thermometer that is closest to the bulb
					while(thermIndex < board.colTherms.get(i).get(j).size() && board.colTherms.get(i).get(j).get(thermIndex).getFillStatus() != -1
								&& squaresFilled < (squaresLeftToFill - blanksNotInTherm))
					{
						if(board.colTherms.get(i).get(j).get(thermIndex).getFillStatus() == 0)
						{
							return new Triple(board.colTherms.get(i).get(j).get(thermIndex).getXPos(),board.colTherms.get(i).get(j).get(thermIndex).getYPos(), 1);
							//allMoves.add(aMove);
							//squaresFilled++;
						}
						thermIndex++;
					}
				}
					
			}
		}
		
		//The rule did not apply, so return null
		return null;
	}
	
	/** If something on the board does not match the correct value for that tile, 
	 *    then set it back to blank.
	 * 
	 * @param board The board on which to make a move 
	 * @return The move to make, or null if the rule does not apply
	 */
	public Triple undoMistakesRule(ComplexThermsBoard board)
	{
		for(int i = 0; i < board.grid.size(); i++)
			for(int j = 0; j < board.grid.size(); j++)
				//If the tile doesn't match the correct value for it, set it to unknown
				if(board.grid.get(i).get(j) != 0
					&& board.grid.get(i).get(j) != board.solutionGrid.get(i).get(j))
				{
					return (new Triple(i,j,0));
				}
		
		//No rule applied, so return null
		return null;
	}
	
	
	
	/** This method checks to see if a board is solvable with this demonstrator.  The only reason a board should not be solvable is if it has multiple valid solutions.
	 *     I don't actually use this in the domain, but its included so that if you want to add more boards, you can make sure the AI demonstrator is able to solve them.
	 *  
	 * 
	 * @param board The board to check
	 * @return Whether or not the board is solvable by the ComplexThermometers AI
	 */
	public boolean checkIfSolvable(ComplexThermsBoard board)
	{
		int moveCount = 0;
		//Set the maximum number of moves.  If the board is solvable, it will take N * N moves to solve, where N is the size of the board (and boards are always square), 
		//  so set the maximum to that value +2, for an easy way to avoid off by 1 errors (If the board is not solvable, no amount of moves will help!)
		int maxMoves = board.grid.size() * board.grid.size() + 2;
		
		
		//Attempt to solve the board by making moves
		while(!board.isSolved())
		{
			board.updateThermData();
			
			Triple expertMove = makeAMove(board);

			ApplyMovesToBoard.applyMove(board, expertMove);
			
			//If we have made more than the maximum number of moves, return the board is not solvable.
			if(moveCount > maxMoves)
				return false;
			
			moveCount++;
		}
		
		//The board is solvable! Rejoice!
		return true;
	}
}

