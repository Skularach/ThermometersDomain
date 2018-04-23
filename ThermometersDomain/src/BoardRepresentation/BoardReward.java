package BoardRepresentation;


/**
 * This library contains all of the (currently 1) functions 
 *  to get rewards from boards.
 * @author Brandon Packard
 *
 */
public class BoardReward{
	
	/** This method gets reward according to the constraints on the board.
	 *    The reward function is as follows:
	 *       (# of constraints satisfied)
	 *       ---------------------------
	 *         (# of total constraints)
	 *         
	 *  For example, if a 5x5 board has 10 thermometers in it, there are 20 possible constraints:
	 *  5 for if the # next to a row matches the number filled in that row,
	 *  5 for if the # next to a column matches the number filled in that column,
	 *  and 10 for making sure each thermometer has a "valid" fill (any mercury in it
	 *     is filled from the bulb outwards)       
	 * 
	 * @param board The board for which to get the reward from.
	 * @return
	 */
	public static float getReward(Board board)
	{
		int constraintsMet = 0;
		int totalConstraints = 0;
		
		//Count the number of row constraints satisfied
		for(int i = 0; i < board.grid.size(); i++)
		{
			int filledCount = 0;
			int emptyCount = 0;
			for(int j = 0; j < board.grid.get(i).size();j++)
			{
				if(board.grid.get(i).get(j) == 1)
					filledCount++;
				else if(board.grid.get(i).get(j) == -1)
					emptyCount++;
				
				
			}
			
			//If the number of filled tiles matches the row constraint
			if(filledCount == board.rowCounts.get(i) && emptyCount == board.grid.size() - filledCount)
				constraintsMet++;
			totalConstraints++;
		}
		
		//Count the number of column constraints satisfied
		for(int i = 0; i < board.grid.get(0).size(); i++)
		{
			int filledCount = 0;
			int emptyCount = 0;
			for(int j = 0; j < board.grid.size();j++)
			{
				if(board.grid.get(j).get(i) == 1)
					filledCount++;
				else if(board.grid.get(j).get(i) == -1)
					emptyCount++;
				
				
			}
			
			//If the number of filled tiles matches the column constraint
			if(filledCount == board.colCounts.get(i) && emptyCount == board.grid.size() - filledCount)
				constraintsMet++;
			totalConstraints++;
		}
		
		//For each row
		for(int i = 0; i < board.rowTherms.size();i++)
		{
			//For each thermometer in that row
			for(int j = 0; j < board.rowTherms.get(i).size(); j++)
			{
				boolean validTherm = true;
				boolean fillSolid = false;
				
				//If the bulb is filled, set that we current have a solidly-filled thermometer (filled from the bulb up with no gaps)
				if(board.rowTherms.get(i).get(j).get(0).getFillStatus() == 1)
					fillSolid = true;
				
				//For each piece past the bulb
				for(int k = 1; k < board.rowTherms.get(i).get(j).size(); k++)
				{
					//If part of the thermometer is unknown, automatically flag the constraint as not met.
					//  If the thermometer hasn't been completely filled in, we can't determine if it's been filled correctly.
					if(board.rowTherms.get(i).get(j).get(k).getFillStatus() == 0)
					{
						validTherm = false;
						break;
					}
					
					//If this piece of the thermometer is filled, and the fill was previously broken,
					//  its an invalid setup
					//   Example:
					//  Filled Empty Filled can never be a valid thermometer
					if(board.rowTherms.get(i).get(j).get(k).getFillStatus() == 1)
					{
						if(!fillSolid)
						{
							validTherm = false;
							break;
						}
					}
					
					//If the current thermometer piece is empty, mark that we no longer have a solid fill
					if(board.rowTherms.get(i).get(j).get(k).getFillStatus() == -1)
					{
						fillSolid = false;
					}
				}
				
				if(validTherm)
					constraintsMet++;
				totalConstraints++;
			}
		}
		
		//For each column
		for(int i = 0; i < board.colTherms.size();i++)
		{
			//For each thermometer in that row
			for(int j = 0; j < board.colTherms.get(i).size(); j++)
			{
				boolean validTherm = true;
				boolean fillSolid = false;
				
				//If the bulb is filled, set that we current have a solidly-filled thermometer (filled from the bulb up with no gaps)
				if(board.colTherms.get(i).get(j).get(0).getFillStatus() == 1)
					fillSolid = true;
				
				//For each piece past the bulb
				for(int k = 1; k < board.colTherms.get(i).get(j).size(); k++)
				{
					//If part of the thermometer is unknown, automatically flag the constraint as not met.
					//  If the thermometer hasn't been completely filled in, we can't determine if it's been filled correctly.
					if(board.colTherms.get(i).get(j).get(k).getFillStatus() == 0)
					{
						validTherm = false;
						break;
					}
					
					//If this piece of the thermometer is filled, and the fill was previously broken,
					//  its an invalid setup
					//   Example:
					//  Filled Empty Filled can never be a valid thermometer
					if(board.colTherms.get(i).get(j).get(k).getFillStatus() == 1)
					{
						if(!fillSolid)
						{
							validTherm = false;
							break;
						}
					}
					
					//If the current thermometer piece is empty, mark that we no longer have a solid fill
					if(board.colTherms.get(i).get(j).get(k).getFillStatus() == -1)
					{
						fillSolid = false;
					}
				}
				
				if(validTherm)
					constraintsMet++;
				totalConstraints++;
			}
		}
		
		//Return the proportion of constraints satisfied, converted to a percentage
		return 100 * (constraintsMet / (totalConstraints * 1.0f));			
	}
}