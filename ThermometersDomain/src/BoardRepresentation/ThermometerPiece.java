package BoardRepresentation;

/** Small class which extends boardpiece into a thermometer piece.
 *    Used to keep track of the thermometers on the board.
 *    
 * 
 * @author Brandon Packard
 *
 */
public class ThermometerPiece extends BoardPiece
{
	/** Constructor simply calls the superclass's constructor
	 * 
	 * @param x x position on the grid of the tile
	 * @param y y position on the grid of the tile
	 * @param status current fill of the tile
	 */
	ThermometerPiece(int x, int y, int status) {
		super(x, y, status);
	}
	
}