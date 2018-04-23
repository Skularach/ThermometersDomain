package WEKA;

import Utilities.Triple;
import weka.core.Instance;
import BoardRepresentation.SimpleThermsBoard;

/** This class is a library that handles simple conversions, such as a string to a boolean array or a triple to a string.
 * 
 * 
 * @author Brandon Packard
 *
 */
public class Conversions
{
	// Indices for the complex-thermometers domain
	// These determine where each subset of features start and end, and are precalculated here for understandability later on
	int wholeColumnRulesStartIndex = SimpleThermsBoard.BOARDSIZE * SimpleThermsBoard.BOARDSIZE;
	int complicatedColRulesStartIndex = wholeColumnRulesStartIndex + 2 * SimpleThermsBoard.BOARDSIZE;
	int remainingColRulesIndex = complicatedColRulesStartIndex + SimpleThermsBoard.BOARDSIZE*SimpleThermsBoard.BOARDSIZE*2;
	int startingRowIndex = remainingColRulesIndex + SimpleThermsBoard.BOARDSIZE * SimpleThermsBoard.BOARDSIZE * 2;
	int complicatedRowRulesStartIndex = startingRowIndex + 2 * SimpleThermsBoard.BOARDSIZE;
	int remainingRowRulesIndex = complicatedRowRulesStartIndex + SimpleThermsBoard.BOARDSIZE*SimpleThermsBoard.BOARDSIZE*2;

	
	/** This method takes in a WEKA Instance object, and returns that instance
	 *     as an array of doubles (basically the double values of the instance
	 *     without all of the other info from the Instance class
	 * 
	 * @param instance The instance object from which to grab the values
	 * @return A double array containing all of the values from the WEKA instance
	 */
	public static double[] getInstanceAsDoubleArray(Instance instance)
	{
		double[] doubleArray = new double[instance.numAttributes()];
		
		//Copy the values into the double array.
		for(int i = 0; i < instance.numAttributes(); i++)
			doubleArray[i] = instance.value(i);
		
		return doubleArray;
	}
	
	
	/** Method for parsing a triple representing an action into a string (that matches the possible action values in WEKA). This method is for the 
	 *    the Complex Thermometers  domain.
	 * 
	 * @param move  The move to parse, as a Triple
	 * @return A string representing the action Triple, that is one of the acceptable values for the action attribute in WEKA
	 */
	public static String parseDemonstratorMoveComplexThermometers(Triple expertMove)
	{
		String action;
		
		//If the third value is 0, the move is setting a tile to unknown
		if(expertMove.getThird() == 0)
			action = "undo(" + expertMove.getFirst() + "," + expertMove.getSecond()+")";
		//If the third value is 1, the move is filling a tile
		else if(expertMove.getThird() == 1)
			action = "markFull(" + expertMove.getFirst() + "," + expertMove.getSecond()+")";	
		//If the third value is -1 (the only other possibility) the move is setting a tile to empty
		else 
			action = "markEmpty(" + expertMove.getFirst() + "," + expertMove.getSecond()+")";	
		
		return action;
	}
	
	/** Method for parsing a triple representing an action into a string (that matches the possible action values in WEKA). This method is for the 
	 *    the Simple Thermometers  domain.  Note that the Simple Thermometers domain actually only uses the first 2 values, but is also 
	 *    stored as a Triple  to prevent a lot of if-thens everyhere
	 * 
	 * @param move  The move to parse, as a Triple
	 * @return A string representing the action Triple, that is one of the acceptable values for the action attribute in WEKA
	 */
	public static String parseDemonstratorMoveSimpleThermometers(Triple expertMove)
	{
		String action;
		
		//If the first item is an e, the move is to empty the tile at location {second item}
		if(expertMove.getFirst() == 'e')
			action = "empty(" + expertMove.getSecond()+")";
		//If the first item is an f, the move is to fill the tile at location (second item)
		else if(expertMove.getFirst() == 'f')
			action = "fill(" + expertMove.getSecond()+")";
		//If the first item is an m, the move is to move to the next row/column
		else if(expertMove.getFirst() == 'm')
			action = "move()";
		//If the first item is a c, the move is to clear the row/column and move to the first column/row, respectively
		else
			action = "clear()";
			
		return action;
		
	}
	
	/** This method is called to parse a demonstrator's move to a string so it can be 
	 *    entered in WEKA as an attribute value.  This method checks to see whether the action
	 *    is from a Simple or Complex Thermometers action, and calls the appropriate parsing method
	 * 
	 * 
	 * @param expertMove The move to convert to a string
	 * @return The move as a string so it can be put into WEKA as an attribute value
	 */
	public static String parseDemonstratorMove(Triple expertMove)
	{
		//If the third value is a -1000, it was a Simple Thermometers action
		if(expertMove.getThird() == -1000)
			return parseDemonstratorMoveSimpleThermometers(expertMove);
		else
			return parseDemonstratorMoveComplexThermometers(expertMove);
	}
}

