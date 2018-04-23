package BoardActions;

import BoardRepresentation.Board;
import BoardRepresentation.ComplexThermsBoard;
import BoardRepresentation.SimpleThermsBoard;
import Utilities.Triple;

/**This class is a library for applying a move from the learner or demonstrator to a thermometers board.
 * It handles both Simple and Complex Thermometers boards.
 * 
 * @author Brandon Packard
 *
 */



public class ApplyMovesToBoard
{
	/**General case.  Determines whether the passed in board is a Simple or Complex board,
	*   and calls the appropriate move-application method.
	 * @param board The board on which to apply the move
	 * @param move The move to apply to the board
	*   */
	public static void applyMove(Board board, Triple move)
	{
		if(board instanceof SimpleThermsBoard)
			applyMoveAsPair((SimpleThermsBoard)board, move);
		else
			applyMoveAsTriple((ComplexThermsBoard)board, move);
	
		//Its very important to put this somewhere it is called
		//   Every time a value on the board grid is changed,
		//   Because it updates data relative to the thermometers
		board.updateThermData();
	}
	
	/** Takes in a Simple Thermometers board and a move,
	 * and applies the move to the board
	 * @param board The board on which to apply the move
	 * @param move The move to apply to the board
	 */
	private static void applyMoveAsPair(SimpleThermsBoard board, Triple move)
	{
		//If the move is to empty a tile
		if(move.getFirst() == 'e')
		{
			//Empty (set to -1) the relevant location in the current row/column
			if(board.lookingAtRows)
				board.grid.get(board.rowIndex).set(move.getSecond(), -1);
			else //if(!RowOrColumn.lookingAtRows)
				board.grid.get(move.getSecond()).set(board.colIndex, -1);
		}
		
		//If the move is to fill a tile
		else if(move.getFirst() == 'f')
		{
			//Fill (set to 1) the relevant location in the current row/column
			if(board.lookingAtRows)
				board.grid.get(board.rowIndex).set(move.getSecond(), 1);
			else //if(!RowOrColumn.lookingAtRows)
				board.grid.get(move.getSecond()).set(board.colIndex, 1);
		}
		
		//If the move is to move to the next row/column
		else if(move.getFirst() == 'm')
		{
			board.cycleCursor();
		}
		
		//If the move is to clear the current row/column and move to
		//  the first column/row (respectively)
		else if(move.getFirst() == 'c')
		{
			if(board.lookingAtRows)
			{
				//Clear the row
				for(int i = 0; i < board.grid.size(); i++)
				{
					board.grid.get(board.rowIndex).set(i,0);
				}
				
				//Update board info to look at first column
				board.lookingAtRows = false;
				board.colIndex = 0;
			}
			
			else 
			{
				//Clear the column
				for(int i = 0; i < board.grid.size(); i++)
				{
					board.grid.get(i).set(board.colIndex,0);
				}
				
				//Update board info to look at first row
				board.lookingAtRows = true;
				board.rowIndex = 0;
			}
					
		}
		
		//Update the thermometer information on the board.  This is probably called
		//   way too often but its vital it gets updated when a tile changes,
		//   so better too often than not often enough.
		board.updateThermData();
	}
	
	/** Takes in a Complex Thermometers board and a move,
	 * and applies the move to the board
	 * @param board The board on which to apply the move
	 * @param move The move to apply to the board
	 */
	private static void applyMoveAsTriple(ComplexThermsBoard board, Triple rule)
	{
		//The first item in triple determines whether to mark a tile as
		//   full, empty, or unknown.  The second and third items determine
		//   the location on the grid of the affected tile.
		board.grid.get(rule.getFirst()).set(rule.getSecond(), rule.getThird());
	}
}