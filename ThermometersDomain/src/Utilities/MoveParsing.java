package Utilities;


/**  This class provides the ability to convert actions provided as strings into a 
 *     format that the domain can recognize and apply to a board.
 *     
 *     Conversions currently included are:
 *        1. String to a Pair of Character-Integer (this actually converts to a triple where the first two values
 *           are used to represent a pair.  The reason for this is to simplify code by passing everything around as triples
 *           and then parsing it to a pair if needed, rather than having ifs everywhere.
 * 		  2. String to a Triple - Used for the Complex Thermometers domain, takes the action string and converts
 *           it to a Triple of (move,x,y)
 * 
 * 
 * @author Brandon Packard
 *
 */
public class MoveParsing
{
	/** Takes in an action string and then parses it to a triple that represents an action pair.
	 *    a little odd, but lets us pass all actions as triples and then just convert to a pair when needed.
	 *    saves a lot of if statements.
	 * 
	 * @param action The action to parse as a string
	 * @return A Triple representing the parsed string
	 */
	public static Triple parseStringToCharacterPair(String action)
	{
		//Split the string by (  , and )
		//so (x,y) would become [x,y]
		String[] splitArray = action.split("\\(|,|\\)");
		
		//If the first split piece is empty, the move is to empty a tile
		if(splitArray[0].equals("empty"))
		{
			//The second split piece is the position to empty
			int pos = Integer.parseInt(splitArray[1]);
			
			//3rd item in triple is -1000, because this is really a pair
			Triple aMove = new Triple('e', pos, -1000);
			return aMove;
		}
		
		//If the first split piece is fill, the move is to fill a tile
		else if(splitArray[0].equals("fill"))
		{
			//The second split piece is the position to filled
			int pos = Integer.parseInt(splitArray[1]);
			
			//3rd item in triple is -1000, because this is really a pair
			Triple aMove = new Triple('f', pos,-1000);
			return aMove;
		}
		
		//If the first split piece is move, the action is to move to the next
		//  row or column
		else if(splitArray[0].equals("move"))
		{
			//Move does not require a parameter, so make item #2 in the triple be -1
			Triple aMove = new Triple('m', -1, -1000);
			return aMove;//allMoves.add(aMove);
		}
		
		//If the firt split piece is clear, the action is to clear the row/column
		//   and move to the next column/row (respectively)
		else if(splitArray[0].equals("clear"))
		{
			//Clear does not require a parameter, so make item #2 in the triple be -1
			Triple aMove = new Triple('c', -1,-1000);
			return aMove;
			//allMoves.add(aMove);
		}
		
		//Action was unknown to the parser
		return null;
	}
	
	
	/** Takes in an action string and parses it to a triple representing that action.
	 *    Used for the complex thermometers domain.
	 * 
	 * @param action The action to parse
	 * @return The Triple representation of that action
	 */
	public static Triple parseStringToTriple(String action)
	{
		//Split the string by (  , and )
		//so (x,y,z) would become [x,y,z]
		String[] splitArray = action.split("\\(|,|\\)");
		
		//If the first split item is markFull, the action is to fill a tile on the board
		if(splitArray[0].equals("markFull"))
		{
			//The second and third split items are the X and Y to fill, respectively
			Triple aMove = new Triple(Integer.parseInt(splitArray[1]),Integer.parseInt(splitArray[2]),1);
			return aMove;
		}
		
		//If the first split item is undo, the action is to set a tile on the board as unknown
		else if(splitArray[0].equals("undo"))
		{
			//The second and third split items are the X and Y to mark as unknown, respectively
			Triple aMove = new Triple(Integer.parseInt(splitArray[1]),Integer.parseInt(splitArray[2]),0);
			return aMove;
		}
		
		//If the first split item is undo, the action is to set a tile on the board as empty
		else if(splitArray[0].equals("markEmpty"))
		{
			//The second and third split items are the X and Y to mark as empty, respectively
			Triple aMove = new Triple(Integer.parseInt(splitArray[1]),Integer.parseInt(splitArray[2]),-1);
			return aMove;
		}
		
		//Action was unknown for the parser
		else
		{
			System.out.println("Couldn't find a spot for " + splitArray[0]);
		}
		
		return null;
	}
	
}