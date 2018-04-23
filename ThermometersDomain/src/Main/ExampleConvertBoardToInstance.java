package Main;

import java.util.Arrays;

import Utilities.Triple;
import WEKA.Conversions;
import WEKA.InstanceCreation;
import weka.core.Instance;
import BoardActions.MakeLearningInstances;
import BoardRepresentation.BoardFileIO;
import BoardRepresentation.SimpleThermsBoard;
import Demonstrators.SimpleThermometersAI;

/** This class provides an example of creating a simple thermometers board and then creating
 *    a learning instance from that board.  It gets one action from the demonstrator to have 
 *    an action for the output class of the instance, but demonstrator moving is given a better
 *    example in the next file, ExampleDemonstratorMoving.java
 * 
 * 
 * @author Brandon Packard
 *
 */
public class ExampleConvertBoardToInstance
{
	/** Test Driver
	 * 
	 * @param args Unused
	 */
	public static void main(String[] args)
	{
		//Create board instance from file
		SimpleThermsBoard board = new SimpleThermsBoard(BoardFileIO.loadInBoardbyNumber(3));
			
		//Initialize WEKA for the domain
		//Parameters used here:
		// 'S' for simple thermometers
		// 'j' to use j48 for learning
		InstanceCreation.initializeWeka('S', 'j');
		
		//Now make a WEKA instance from that board.
		Instance testInstance = MakeLearningInstances.getInstance(board);
		
		//Print instance as string
		System.out.println("===== Instance Without Demonstrator Action =====");
		System.out.println(testInstance.toString());
		
		//Print instance as double array
		System.out.println(Arrays.toString(Conversions.getInstanceAsDoubleArray(testInstance)));
				
		
		//Initialize Demonstrator, get its action
		SimpleThermometersAI simpleAI = new SimpleThermometersAI();
		Triple demonMove = simpleAI.makeAMove(board);
		
		//Add the output class.  Note that we call parseExpertMovePair because this is the Simple Therms domain.
		//You can also call Conversions.parseExpertMoveToString and pass in true for Simple Thermometers or false for Complex Thermometers
		testInstance = InstanceCreation.addOutputClassToInstance(testInstance, Conversions.parseDemonstratorMoveSimpleThermometers(demonMove));
		
		//Print instance as string
		System.out.println("===== Instance With Demonstrator Action =====");
		System.out.println(testInstance.toString());
		
		//Print instance as double array
		System.out.println(Arrays.toString(Conversions.getInstanceAsDoubleArray(testInstance)));
			
	
	}
	
	
}