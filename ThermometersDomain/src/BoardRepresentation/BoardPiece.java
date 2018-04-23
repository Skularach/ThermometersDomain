package BoardRepresentation;

/** This class simply represents a tile on the board,
 *    with helper methods for location on the grid (x and y) and fill status.
 *
 * @author Brandon Packard
 */
public class BoardPiece
{
	private int xPos;
	private int yPos;
	private int fillStatus;
	
	/** Constructor for the BoardPiece class.
	 *   Takes in the position of the tile on the board and its fill, and 
	 *   makes a BoardPiece object to store it all.
	 *   
	 * @param xIn  The x location of the tile on the board
	 * @param yIn  The y location of the tile on the board
	 * @param statusIn  The fill status of the tile.
	 */
	BoardPiece(int xIn, int yIn, int statusIn)
	{
		xPos = xIn;
		yPos = yIn;
		fillStatus = statusIn;
	}
	
	/** Getter for the x position of the tile on the board
	 * 
	 * @return The x position of the tile on the board
	 */
	public int getXPos() {
		return xPos;
	}

	/** Getter for the y position of the tile on the board
	 * 
	 * @return The y position of the tile on the board
	 */
	public int getYPos() {
		return yPos;
	}

	/** Getter for the fill status position of the tile on the board
	 * 
	 * @return The fill status position of the tile on the board
	 */
	public int getFillStatus() {
		return fillStatus;
	}	
}