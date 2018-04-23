package BoardRepresentation;


/** This class represents a Complex Thermometers board.
 *    It's Existence is so that given a board, we can use inheritance
 *    to check whether it is a Simple or Complex board based on which it
 *    was initialized as.
 * 
 * @author Brandon Packard
 *
 */
public class ComplexThermsBoard extends Board
{
	/** Constructor, simply calls the general board constructor to make a 
	 *    board object.
	 * 
	 * @param stringGrid The board (in string format) to create a board object from
	 */
	public ComplexThermsBoard(String[][] stringGrid)
	{
		super(stringGrid);
	}
}