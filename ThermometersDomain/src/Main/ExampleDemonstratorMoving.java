package Main;

import Utilities.Triple;
import BoardActions.ApplyMovesToBoard;
import BoardRepresentation.BoardFileIO;
import BoardRepresentation.ComplexThermsBoard;
import BoardRepresentation.SimpleThermsBoard;
import Demonstrators.ComplexThermometersAI;
import Demonstrators.SimpleThermometersAI;



/** This class provides examples of having a demonstrator make a move on a board
 *    and then applying that move to the board, for both domains.
 * 
 * 
 * @author Brandon Packard
 *
 */
public class ExampleDemonstratorMoving
{
	/** Test Driver
	 * 
	 * @param args Unused
	 */
	public static void main(String[] args)
	{
		SimpleDemonstratorExample();

		System.out.println();
		System.out.println();
		
		ComplexDemonstratorExample();
	}
	
	/**  This method provides an example of  creating a Simple Thermometers board,
	 *     Initializing the Simple Thermometers AI, having it make a move, and 
	 *     applying that move to the board.
	 */
	public static void SimpleDemonstratorExample()
	{
		//Create board instance from file
		SimpleThermsBoard board = new SimpleThermsBoard(BoardFileIO.loadInBoardbyNumber(3));
		
		//Print out the current grid
		System.out.println("Before demonstrator's move");
		board.printCurrentGrid();
		System.out.println();
		
		//Initialize demonstrator AI
		SimpleThermometersAI demonstrator = new SimpleThermometersAI();
		
		//Get a move from the demonstrator
		Triple demonstratorMove = demonstrator.makeAMove(board);
		
		//Apply that move to the board
		ApplyMovesToBoard.applyMove(board, demonstratorMove);
		
		//Print out the updated grid
		System.out.println("After demonstrator's move");
		board.printCurrentGrid();
	}
	
	/**  This method provides an example of  creating a Complex Thermometers board,
	 *     Initializing the Complex Thermometers AI, having it make a move, and 
	 *     applying that move to the board.
	 */
	public static void ComplexDemonstratorExample()
	{
		//Create board instance from file
		ComplexThermsBoard board = new ComplexThermsBoard(BoardFileIO.loadInBoardbyNumber(3));
		
		//Print out the current grid
		System.out.println("Before demonstrator's move");
		board.printCurrentGrid();
		System.out.println();
		
		//Initialize demonstrator AI
		ComplexThermometersAI demonstrator = new ComplexThermometersAI();
		
		//Get a move from the demonstrator
		Triple demonstratorMove = demonstrator.makeAMove(board);
		
		//Apply that move to the board
		ApplyMovesToBoard.applyMove(board, demonstratorMove);
		
		//Print out the updated grid
		System.out.println("After demonstrator's move");
		board.printCurrentGrid();
	}
	
}