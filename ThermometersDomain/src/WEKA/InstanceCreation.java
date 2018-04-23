package WEKA;

import java.util.ArrayList;

import BoardRepresentation.ComplexThermsBoard;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;



/**  This class deals with other instance operations: adding actions to instances, initializing WEKA values, etc 
 * 
 * 
 * @author Brandon Packard
 *
 */
public class InstanceCreation
{
	
	public static int numWEKAattributes = -1;
	public static ArrayList<Attribute> WEKAattributes;
	
	
		public static char domain = 'T';
	
		
	
		
	/** This method initializes the WEKA attribute lists and other minor values.
	 * 
	 * 
	 * @param callingDomain The domain that is calling the function ('S' for Simple Thermometers, 'C' for Complex Thermometers
	 * @param classifierToUse Which underlying standard supervised learning method to use.  'j' for J48, 'k' for knn. Basically just stores this to a data member
	 */
	public static void initializeWeka(char callingDomain, char classifierToUse)
	{
		//Save parameters
		Classification.classifierToUse = classifierToUse;
		domain = callingDomain;
		
		//Simple Thermometers
		if(callingDomain == 'S')
		{
			//List of all attributes for the domain
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			
			//Possible values for thermometer pieces
			ArrayList<String> tPieceVals = new ArrayList<String>(6);
			tPieceVals.add("BR");
			tPieceVals.add("BL");
			tPieceVals.add("TH");
			tPieceVals.add("CR");
			tPieceVals.add("CL");
			tPieceVals.add("NO");
			
			//Possible tile fills
			ArrayList<String> tileFills = new ArrayList<String>(3);
			tileFills.add("-1");
			tileFills.add("0");
			tileFills.add("1");
			
			
		 
			//Create 5 tile fills (assuming 5x5 board), with the possible tile fills defined above
			Attribute tile0Fill = new Attribute("tile0Fill", tileFills);
			Attribute tile1Fill = new Attribute("tile1Fill", tileFills);
			Attribute tile2Fill = new Attribute("tile2Fill", tileFills);
			Attribute tile3Fill = new Attribute("tile3Fill", tileFills);
			Attribute tile4Fill = new Attribute("tile4Fill", tileFills);
			
			
			//Create 5 thermometer piece attributes (assuming 5x5 board), with the possible thermometers values defined above
			Attribute tPiece0Shape = new Attribute("tPiece0",tPieceVals);
			Attribute tPiece1Shape = new Attribute("tPiece1",tPieceVals);
			Attribute tPiece2Shape = new Attribute("tPiece2",tPieceVals);
			Attribute tPiece3Shape = new Attribute("tPiece3",tPieceVals);
			Attribute tPiece4Shape = new Attribute("tPiece4",tPieceVals);
			
			//Attributes for the required number of filled tiles and the current number of filled, empty, and blank
			Attribute fillCount = new Attribute("fillCount");
			Attribute numFilled = new Attribute("numFilled");
			Attribute numEmpty = new Attribute("numEmpty");
			Attribute numBlank =  new Attribute("numBlank");
			
			//Add all of the attributes to the attribute list
			attributes.add(tile0Fill);
			attributes.add(tile1Fill);
			attributes.add(tile2Fill);
			attributes.add(tile3Fill);
			attributes.add(tile4Fill);
			
			attributes.add(tPiece0Shape);
			attributes.add(tPiece1Shape);
			attributes.add(tPiece2Shape);
			attributes.add(tPiece3Shape);
			attributes.add(tPiece4Shape);
			
			attributes.add(fillCount);
			attributes.add(numFilled);
			attributes.add(numEmpty);
			attributes.add(numBlank);
		
			
			//Possible action values
			ArrayList<String> outputClassValues = new ArrayList<String>();
			outputClassValues.add("fill(0)");
			outputClassValues.add("fill(1)");
			outputClassValues.add("fill(2)");
			outputClassValues.add("fill(3)");
			outputClassValues.add("fill(4)");
			outputClassValues.add("empty(0)");
			outputClassValues.add("empty(1)");
			outputClassValues.add("empty(2)");
			outputClassValues.add("empty(3)");
			outputClassValues.add("empty(4)");
			outputClassValues.add("move()");
			outputClassValues.add("clear()");
			
			//Create the action attribute and add it as well
			Attribute outputClass = new Attribute("action",outputClassValues);
			attributes.add(outputClass);
			
			//Store the number of attributes and attributes themselves 
			numWEKAattributes = attributes.size();
			WEKAattributes = attributes;
		
			//Set up globalInstanceDatabase to be the training database
			 Classification.globalInstanceDatabase = new Instances("Training",attributes,0);
			 Classification.globalInstanceDatabase.setClassIndex(Classification.globalInstanceDatabase.numAttributes()-1);
		}	 
		
		//Complex Thermometers
		else //if CallingDomain == 'C'
		{
			//Possible tile fills
			ArrayList<String> tileFills = new ArrayList<String>(3);
			tileFills.add("NotBlank");
			tileFills.add("Blank");
			
			//Possible rule values
			ArrayList<String> wholeRuleValues = new ArrayList<String>();
			wholeRuleValues.add("yes");
			wholeRuleValues.add("no");
			
			//Values from 0 to the size of the board, since a 5x5 board could have a column/row constraint
			// of 0,1,2,3,4, or 5
			ArrayList<String> zeroToBoardsize = new ArrayList<String>();
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE + 1; i++)
				zeroToBoardsize.add(""+i);
			
			//Values from -1 to the size of the board.
			ArrayList<String> negOneToBoardsize = new ArrayList<String>();
			for(int i = -1; i < ComplexThermsBoard.BOARDSIZE + 1; i++)
				negOneToBoardsize.add(""+i);
			
			
			ArrayList<Attribute> attributes = new ArrayList<Attribute>();
			
			//get tile fill attributes
			ArrayList<Attribute> tiles = new ArrayList<Attribute>();
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE * ComplexThermsBoard.BOARDSIZE; i++)
				tiles.add(new Attribute("tile" + i,tileFills));
			
			
			
			//get rule based attributes for columns
			ArrayList<Attribute> wholeColRules = new ArrayList<Attribute>();
	
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				wholeColRules.add(new Attribute("FillAllInCol"+i , wholeRuleValues));
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				wholeColRules.add(new Attribute("EmptyRestInCol"+i, wholeRuleValues));	
			
			//get rule based attributes for column thermometers
			ArrayList<Attribute> ruleAttributesCol = new ArrayList<Attribute>();
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesCol.add(new Attribute("ThermTooBigCol"+i + "," +j,negOneToBoardsize));
				}
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesCol.add(new Attribute("ThermFilledRequiredCol"+i + "," +j,negOneToBoardsize));
				}
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesCol.add(new Attribute("RestThermFilledCol"+i + "," +j,wholeRuleValues));
				}
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesCol.add(new Attribute("RestThermEmptyCol"+i + "," +j,wholeRuleValues));
				}
			
			
			//get rule based attributes for rows
			ArrayList<Attribute> wholeRowRules = new ArrayList<Attribute>();
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				wholeRowRules.add(new Attribute("FillAllInRow"+i, wholeRuleValues));
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				wholeRowRules.add(new Attribute("EmptyRestInRow"+i, wholeRuleValues));	
			
			//get rule based attributes for row thermometers
			ArrayList<Attribute> ruleAttributesRow = new ArrayList<Attribute>();
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesRow.add(new Attribute("ThermTooBigRow"+i + "," +j,negOneToBoardsize));
				}
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesRow.add(new Attribute("ThermFilledRequiredRow"+i + "," +j,negOneToBoardsize));
				}
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesRow.add(new Attribute("RestThermFilledRow"+i + "," +j,wholeRuleValues));
				}
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
				{
					ruleAttributesRow.add(new Attribute("RestThermEmptyRow"+i + "," +j,wholeRuleValues));
				}
			
			//add all of the attribute types to the attribute list
			attributes.addAll(tiles);
			attributes.addAll(wholeColRules);
			attributes.addAll(ruleAttributesCol);
			attributes.addAll(wholeRowRules);
			attributes.addAll(ruleAttributesRow);
			
			//Get the 75 possible action values
			ArrayList<String> outputClassValues = new ArrayList<String>();
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
					outputClassValues.add("undo(" + i + "," + j + ")");
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
					outputClassValues.add("markFull(" + i + "," + j + ")");
			
			for(int i = 0; i < ComplexThermsBoard.BOARDSIZE; i++)
				for(int j = 0; j < ComplexThermsBoard.BOARDSIZE; j++)
					outputClassValues.add("markEmpty(" + i + "," + j + ")");
			
		
			//add the action attribute to the attribute list
			Attribute outputClass = new Attribute("action",outputClassValues);
			attributes.add(outputClass);
			
			//Store the number of attributes and the attributes themselves 
			numWEKAattributes = attributes.size();
			WEKAattributes = attributes;
			
			//Initialize globalInstancesDatabase to be the training database
			Classification.globalInstanceDatabase = new Instances("Training",attributes,0);
			Classification.globalInstanceDatabase.setClassIndex(Classification.globalInstanceDatabase.numAttributes()-1);
		}
	
		
		 //Set name, attributes, class index, etc of the various instance-holding databases
    	Classification.globalInstanceDatabase = new Instances("global",InstanceCreation.WEKAattributes,Classification.maxInstances);
    	Classification.tempInstances =  new Instances("temp",InstanceCreation.WEKAattributes,Classification.maxInstances);
    	Classification.singleTraceInstances = new Instances("trace",InstanceCreation.WEKAattributes,Classification.maxInstances);
    	Classification.allInstanceDatabases = new ArrayList<Instances>();
    	Classification.globalInstanceDatabase.setClassIndex(Classification.globalInstanceDatabase.numAttributes() - 1);
    	Classification.tempInstances.setClassIndex(Classification.tempInstances.numAttributes() - 1);
        Classification.singleTraceInstances.setClassIndex(Classification.singleTraceInstances.numAttributes() - 1);
    
	}
	

	/** This method takes a world state without an action, and an action as a string, and adds the action to the world state instance.
	 *   This seems silly as a basically 1 line function, but it makes the code that uses it read much easier.  This can almost be thought
	 *   of as an alias for the operation more than a true "method"
	 * @param inputInstance  The instance without an action
	 * @param action  The action, as a string, to be added to the instance
	 * @return The input instance, only with the inputted action added to it
	 */
	public static Instance addOutputClassToInstance(Instance inputInstance, String action)
	{	
		//Set the appropriate value to the action
		inputInstance.setValue(WEKAattributes.get(WEKAattributes.size()-1), action);
		
		return inputInstance;
	}
	
	/**  Adds an action to an instance, and then adds that to the temporary instance database
	 * 
	 * @param instance The instance to add to the database
	 * @param actionFacts The action to add to the instance
	 * @return
	 */
	public static Instance addInstance(Instance instance, String actionFacts)
	{
		//add action to instance
		instance = addOutputClassToInstance(instance, actionFacts);
		
		//add instance to database
		Classification.tempInstances.add(instance);
		
		return instance;
	}



	



	



	
	

}