package Demonstrators;



import BoardRepresentation.Board;
import BoardRepresentation.DisplayManager;
import Utilities.Triple;

/** Class for a Human Demonstrator to be able to solve boards.
 * 
 * 
 * @author Skularach
 *
 */
public class HumanExpert
{
	HumanExpert()
	{
		
	}

	/** When we need a demonstrator move, call this to get one from the user.
	 * 
	 * 
	 * @param board The board on which to make a move
	 * @return The move the human demonstrator makes
	 */
	public static Triple makeAMove(Board board)
	{
		/** Wait for the user to click something.
		 * 
		 */
		while(DisplayManager.userClicked == false)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		//Return the move according to what the user clicks
		DisplayManager.userClicked = false;
		if(DisplayManager.userRightClicked)
			return new Triple(DisplayManager.userI, DisplayManager.userJ,-1);
		else if (DisplayManager.userLeftClicked)
			return new Triple(DisplayManager.userI, DisplayManager.userJ,1);
		else 
			return new Triple(DisplayManager.userI, DisplayManager.userJ,0);
	}
}