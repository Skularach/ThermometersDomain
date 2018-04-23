package Utilities;

import java.util.*;

public class DistributionTools
{
	//Random used for probability distributions
	static Random distRand = new Random(6);

	
	/**
	 * Given a probability distribution, get a random index with those probabilities.
	 *   For example, a probability distribution of [.75, .25] would return
	 *   0 with a 75% chance and 1 with a 25% chance.
	 * 
	 * 
	 * @param dist The distribution from which to select
	 * @return  The returned index
	 */
	public static int getIndexGivenProbDist(double[] dist)
	{
		//Get a random double and use that to determine the index.
		double r = distRand.nextDouble();
	    double a = 0;
		int idx = -1;
    	while(r > a)
    	{	
        	idx = idx + 1;
        	a = a + dist[idx];
		}
    	
    	return idx;
	}
	
}