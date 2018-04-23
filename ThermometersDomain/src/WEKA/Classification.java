package WEKA;

import java.util.ArrayList;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import Utilities.MoveParsing;
import Utilities.Triple;


/** This class handles the training and action selection in the domains, using WEKA
 * 
 * 
 * @author Brandon Packard
 *
 */
public class Classification
{
	//Classifiers
	public static IBk instanceBased = null;
	public static J48 j48 = null;

	//Default classifier to use for training/prediction and whether to use pruning for J48
	public static char classifierToUse = 'j';
	public static boolean usePruning = true;
	
	//Databases -- globalInstanceDatabase is the one used for actual training,
	//  The rest are mostly storage
	public static Instances tempInstances;
	public static Instances singleTraceInstances;
	public static Instances globalInstanceDatabase;
	public static ArrayList<Instances> allInstanceDatabases;

	//A maximum of 100,000 training instances in the database, which is 50x more than I personally have ever used.
	public static int maxInstances = 100000;

		
	/**  Given a worldInstance without an action, predict an action and return it as a Triple
	 *     Used for Complex-Thermometers Domain
	 * 
	 * @param worldInstance The instance to get an action for
	 * @return The Triple that corresponds to the predicted action for the passed in worldInstance
	 */
	public static Triple predictActionWEKAComplexThermometers(Instance worldInstance)
	{
		String recordString = getActionStringWeka(worldInstance);
		return MoveParsing.parseStringToTriple(recordString);
	}
	
	
	/**  Given a worldInstance without an action, predict an action and return it as a Triple
	 *     Used for Simple-Thermometers Domain
	 * 
	 * @param worldInstance The instance to get an action for
	 * @return The Triple that corresponds to the predicted action for the passed in worldInstance
	 */
	public static Triple predictActionWEKASimpleThermometers(Instance worldInstance)
	{
		String recordString = getActionStringWeka(worldInstance);
		return MoveParsing.parseStringToCharacterPair(recordString);
	}
	
	/** Method to train a classifier using all of the data in the globalInstanceDatabase variable
	 * 
	 * Currently supports J48 and knn, but others such as random forest and SVM can easily be added
	 */
	public static void trainClassifier()
	{
		try{
			//Using J48
			if(classifierToUse == 'j')
			{
				//Initialize J48 object and set attributes
				Classification.j48 = new J48();
				if(!usePruning)
					Classification.j48.setUnpruned(true);
				Classification.j48.setUseLaplace(true);
				
				//Train the classifier on the data in globalInstanceDatabase, using WEKA
				Classification.j48.buildClassifier(globalInstanceDatabase);
			}
			
			//Using knn
			else
			{
				//Initialize knn with k=9, which is all I ever use in this domain.
				Classification.instanceBased = new IBk(9);
				
				//Train classifier on the data in globalInstanceDatabase, using WEKA
				Classification.instanceBased.buildClassifier(globalInstanceDatabase);
			}
		} catch(Exception e){
			e.printStackTrace();
			}
	}
	
	/** Given a world state, uses WEKA to predict an action for it and returns 
	 *    the action as a string.
	 * 
	 * 
	 * @param toClassify The world instance to predict an action for
	 * @return A string representing the action
	 */
	public static String getActionStringWeka(Instance toClassify)
	{		
		//Mark that the world state belongs to the same dataset as globalInstanceDatabase
		toClassify.setDataset(globalInstanceDatabase);
				
		//Classification using WEKA
		double actionIndex = -1;
		try{
			//Using J48
			if(classifierToUse == 'j')
			{
				try{
					actionIndex = j48.classifyInstance(toClassify);
				}catch(Exception e){
					e.printStackTrace();
					};
				//actionIndex = DistributionTools.getIndexGivenProbDist(j48.distributionForInstance(toClassify));
			}
			//Using knn
			else
				actionIndex = instanceBased.classifyInstance(toClassify);
		} catch(Exception e){e.printStackTrace();}
		

		//Classification actually returns the index of the correct action.  Use that index to extract the string from the WEKA attributes
		String actionString = null;
		try{
				actionString = InstanceCreation.WEKAattributes.get(InstanceCreation.WEKAattributes.size()-1).value((int) actionIndex);
		}catch(Exception e)
		{
			System.out.println("Action index returned by prediction is invalid");
		}
		return actionString;
	}
}