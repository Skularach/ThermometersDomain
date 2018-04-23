package Main;

import Utilities.Triple;
import BoardActions.ApplyMovesToBoard;
import BoardRepresentation.BoardFileIO;
import BoardRepresentation.ComplexThermsBoard;
import BoardRepresentation.SimpleThermsBoard;
import Demonstrators.ComplexThermometersAI;
import Demonstrators.SimpleThermometersAI;

/** This class provides an example of having a demonstrator provide moves until
 *    a board is completely solved, for both Thermometers domains
 * 
 * 
 * @author Brandon Packard
 *
 */
public class ExampleHaveDemonstratorSolveBoard
{
	/** Test Driver
	 * 
	 * @param args Not Used
	 */
	public static void main(String[] args)
	{
		haveAISolveSimpleBoard();
		
		System.out.println();
		
		haveAISolveComplexBoard();
		
	}
	
	/** Example of having the AI demonstrator solve a Simple Thermometers board
	 */
	private static void haveAISolveSimpleBoard()
	{
		//Create board instance from file
		SimpleThermsBoard board = new SimpleThermsBoard(BoardFileIO.loadInBoardbyNumber(3));
	
		//Initialize demonstrator AI
		SimpleThermometersAI demonstrator = new SimpleThermometersAI();
		
		//while the board is not solved
		while(!board.isSolved())
		{
			//Get a move from the demonstrator
			Triple demonstratorMove = demonstrator.makeAMove(board);
		
			//Apply that move to the board
			ApplyMovesToBoard.applyMove(board, demonstratorMove);
		}
		
		//Print the current board and the solution board
		//Note that they now match.
		board.printCurrentGrid();
		System.out.println();
		board.printSolutionGrid();
	}
	
	/** Example of having the AI demonstrator solve a Complex Thermometers board
	 */
	private static void haveAISolveComplexBoard()
	{
		//Create board instance from file
		ComplexThermsBoard board = new ComplexThermsBoard(BoardFileIO.loadInBoardbyNumber(3));
	
		//Initialize demonstrator AI
		ComplexThermometersAI demonstrator = new ComplexThermometersAI();
		
		//while the board is not solved
		while(!board.isSolved())
		{
			//Get a move from the demonstrator
			Triple demonstratorMove = demonstrator.makeAMove(board);
		
			//Apply that move to the board
			ApplyMovesToBoard.applyMove(board, demonstratorMove);
		}
		
		//Print the current board and the solution board
		//Note that they now match.
		board.printCurrentGrid();
		System.out.println();
		board.printSolutionGrid();
	}
	
	
	
	
}