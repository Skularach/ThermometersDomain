package Utilities;

import java.util.ArrayList;


/**  This class is used in order to print arrays and arraylists of data.
 * 
 * 
 * @author Brandon Packard
 *
 */
public class Printing
{
	/** Print out a 2 dimensional array of floats
	 * 
	 * @param rewardScores  The 2D array of floats that needs printed
	 */
	public static void print2DFloatArray(float[][] rewardScores)
	{
    	System.out.println("Rewards (Average for a Policy is average of a ROW)");
    	for(int i = 0; i < rewardScores.length; i++)
    	{
    		for(int j = 0; j < rewardScores[i].length; j++)
    			System.out.print(rewardScores[i][j] + ",");
    		System.out.println();
    	}
	}
	
	/**  This function will take in the set of database sizes accrued by all iterations of the algorithm over all trials,
	 *     and then print out the averages as follows:
	 *     1,100,500
	 *     2,200,1000
	 *     
	 *     This is a comma-separated-value file, which can easily be split into columns in excel, for example
	 * 
	 * 
	 * @param allDatabaseSizes  The set of amounts of training data accrued by all iterations over all trials
	 * @param allRewardScores  The set of reward data accrued by all iterations over all trials
	 */
	public static void printRewardsWithDatabaseSizes(
			ArrayList<ArrayList<Integer>> allDatabaseSizes, ArrayList<float[][]> allRewardScores) {
		
		//First get the average over all trials, so we have a list of averages by iteration
		ArrayList<Float> databaseAverages = getAverageByIteration(allDatabaseSizes);
		ArrayList<Float> rewardAverages = getAverageByIterationFloat(allRewardScores);
		
		//Blank cell for spacing, when split on ","
		System.out.print(","); 
		//Other headers
		if(databaseAverages.size() > 0)
			System.out.print("Database Sizes,");
		if(rewardAverages.size() > 0)
			System.out.print("Rewards,");
		
		System.out.println("");
		
		//Print out the iteration number, then a comma, then the average amount of training data for that iteration,
		//  then a comma, then the average reward over all trials for that iteration.
		for(int i = 0; i < databaseAverages.size(); i++)
		{
			System.out.print((i+1)+",");
			if(databaseAverages.size() > 0)
				System.out.print(databaseAverages.get(i) + ",");
			if(rewardAverages.size() > 0)
				System.out.print(rewardAverages.get(i) + ",");
			System.out.println();
		}
		
	}
	
	/** Given a 2D arraylist of integers, assume that the second dimension is trials.  Average the 
	 *   Get the average over the second dimension (average of the rows, if you will) 
	 *   
	 *   For example:
	 *     1,2,3
	 *     4,5,6
	 *     7,8,9
	 *     
	 *     Would become 
	 *     2,5,8
	 *     
	 *   
	 * 
	 * @param listOfLists  The 2D array full of integers that needs turned into an average array
	 * @return An array of Floats which contains the averages of the 2D array over rows
	 */
	static ArrayList<Float> getAverageByIteration(
			ArrayList<ArrayList<Integer>> listOfLists) {
		
		//We keep track of the average as a sum and count
		ArrayList<Float> averages = new ArrayList<Float>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		
		
		//get max size of any iteration for indexing
		int max = 0; 
		for(int i  = 0; i <listOfLists.size(); i++)
			if(listOfLists.get(i).size() > max)
				max = listOfLists.get(i).size();
		
		//Initialize all averages and counts to 0
		for(int i = 0; i < max; i++)
		{
			averages.add(0f);
			counts.add(0);
		}
		
		//Update the count and averages by row
		for(int j = 0; j < listOfLists.size(); j++)
			for(int i = 0; i < listOfLists.get(j).size(); i++)
			{
				averages.set(i, averages.get(i) + listOfLists.get(j).get(i));
				counts.set(i, counts.get(i) + 1);
			}
		
		//Calculate the averages for each row
		for(int i = 0; i < averages.size(); i++)
			averages.set(i, averages.get(i) / counts.get(i));
	
		//Return the 1D array of averages
		return averages;
	}	
	
	
	/** Given a 2D arraylist of floats, assume that the second dimension is trials.  Average the 
	 *   Get the average over the second dimension (average of the rows, if you will).
	 *   
	 *   There is probably some way to combine this and getAverageByIteration using generics, but this works fine.
	 *   
	 *   For example:
	 *     1.2,1.5,1.8
	 *     1.1,1.2,1.3
	 *     2.4,2.5,2.6
	 *     
	 *     Would become 
	 *     1.5,1.2,2.5
	 *   
	 * 
	 * @param listOfLists  The 2D array full of floats that needs turned into an average array
	 * @return An array of Floats which contains the averages of the 2D array over rows
	 */
	private static ArrayList<Float> getAverageByIterationFloat(
			ArrayList<float[][]> allRewardScores) {
		//We keep track of the average as a sum and count
		ArrayList<Float> averages = new ArrayList<Float>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		
		//get max size of any iteration for indexing
		int max = 0; 
		for(int i  = 0; i <allRewardScores.size(); i++)
			if(allRewardScores.get(i).length > max)
				max = allRewardScores.get(i).length;
		
		//Initialize all averages and counts to 0
		for(int i = 0; i < max; i++)
		{
			averages.add(0f);
			counts.add(0);
		}
		
		//Update the count and averages by row
		for(int j = 0; j < allRewardScores.size(); j++)
			for(int i = 0; i < allRewardScores.get(j).length; i++)
				for(int r = 0; r < allRewardScores.get(j)[i].length; r++)
				{
					averages.set(i, averages.get(i) + allRewardScores.get(j)[i][r]);
					counts.set(i, counts.get(i) + 1);
				}
		
		//Calculate the averages for each row
		for(int i = 0; i < averages.size(); i++)
			averages.set(i, averages.get(i) / counts.get(i));
	
		//Return the 1D array of averages
		return averages;
	}
}