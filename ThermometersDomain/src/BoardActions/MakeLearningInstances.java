package BoardActions;

import java.util.ArrayList;

import weka.core.DenseInstance;
import weka.core.Instance;
import Utilities.Quadruple;
import Utilities.Triple;
import WEKA.Classification;
import WEKA.InstanceCreation;
import BoardRepresentation.Board;
import BoardRepresentation.ComplexThermsBoard;
import BoardRepresentation.SimpleThermsBoard;
import FactsExtraction.RuleBasedBoardFacts;
import javafx.util.Pair;


/**
 * This class is a library for creating WEKA-ready learning instances from thermometers boards.
 * Handles both Simple and Complex Thermometers boards
 * 
 * @author Brandon Packard
 *
 */
public class MakeLearningInstances
{
	//Indices for Complex Thermometer features- precalculated for ease of understanding
	public final static int wholeColumnRulesStartIndex = Board.BOARDSIZE * Board.BOARDSIZE;
	public final static int complicatedColRulesStartIndex = wholeColumnRulesStartIndex + 2 * Board.BOARDSIZE;
	public final static int remainingColRulesIndex = complicatedColRulesStartIndex + Board.BOARDSIZE * Board.BOARDSIZE*2;
	public final static int startingRowIndex = remainingColRulesIndex + Board.BOARDSIZE * Board.BOARDSIZE * 2;
	public final static int complicatedRowRulesStartIndex = startingRowIndex + 2 * Board.BOARDSIZE;
	public final static int remainingRowRulesIndex = complicatedRowRulesStartIndex + Board.BOARDSIZE * Board.BOARDSIZE*2;
		
	
	
	/**General case.  Determines whether the passed in board is a Simple or Complex board,
	*   and calls the appropriate instance-creation method.
	*  @param board The board from which to create the instance
	*   */
	public static Instance getInstance(Board board) {
		Instance instance;
		
		//Use instanceof to see whether we are dealing with a simple or complex board,
		//    and call the appropriate instance creation method.
		if(board instanceof SimpleThermsBoard)
			instance = createSimpleThermsInstance((SimpleThermsBoard)board);
		else
			instance = createInstanceComplexTherms((ComplexThermsBoard)board);
		return instance;
	}
	
	/** Creates a learning instance for simple thermometers boards.  The features are:
	*  0-4: Whether each tile is full, empty, or unknown
	*  5-9: Whether each tile has a relevant thermometer piece (if we are looking at rows, a vertical thermometer is irrelevant.  Likewise with columns and horizontal.
	*  10: The number of tiles (total) that should be filled in the row/column
	*  11-13: The current number of filled, empty, and blank tiles, in that order
	*  14: The action taken (this will be left unknown by this method, since it is not yet known)
	*  
	*  @param board The board from which to create the instance
	*   */
	private static Instance createSimpleThermsInstance(SimpleThermsBoard board)
	{
		//Initialize instance
		Instance anInstance = new DenseInstance(InstanceCreation.numWEKAattributes);
		anInstance.setDataset(Classification.globalInstanceDatabase);
		
		//Set all Therm Piece features to the default of "NO", for "no relevant thermometer piece on this tile"
		try{
			for(int i = 0; i < board.grid.size(); i++)
				anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE + i),"NO");
		}catch(Exception e)
			{e.printStackTrace();}
			
		//Rows and columns handled separately, which is the usual case for Simple Thermometers
		if(!board.lookingAtRows)
		{
			//Update the thermometer data with the newest tile values, just in case it hasn't already been done
			board.updateThermData();
			
			//Set the fill for each tile to whatever the fill currently is for that file.
			for(int i = 0; i < board.grid.size(); i++)
					anInstance.setValue(InstanceCreation.WEKAattributes.get(0+i), board.grid.get(i).get(board.colIndex) + "");
			
			//For each thermometer in the column
			for(int j = 0; j < board.colTherms.get(board.colIndex).size(); j++)
			{
				//for each piece in that thermometer
				for(int k = 0; k < board.colTherms.get(board.colIndex).get(j).size(); k++)
				{
					//Get the string code for that piece. Details on the string codes can be found in BoardFileIO.java
					int location = board.colTherms.get(board.colIndex).get(j).get(k).getXPos();
					String code = board.stringGrid[board.colTherms.get(board.colIndex).get(j).get(k).getXPos()][board.colTherms.get(board.colIndex).get(j).get(k).getYPos()];
					
					//This framework treats rows and columns the same, so replace column-only code values with their row equivalents for consistency.
					code = code.replace('D', 'R');
					code = code.replace('U', 'L');
					code = code.replace('V', 'H');
					
					//The last letter in each 3 letter code is the "true fill" of the tile (the fill it will have when the board is solved)
					//   This is information the learner should not have, so we remove it.
					code = code.substring(0,code.length() - 1);
					anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE + location), code);
				}	
				
			}
			
			//Set the next feature as the number of filled tiles the column should have when the board is solved
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2), board.colCounts.get(board.colIndex));
			
			//Set the next 3 features as the current number of filled, blank, and empty tiles in the row
			int numBlank = 0;
			int numFilled = 0;
			int numEmpty = 0;
			for(int i = 0; i < board.grid.size(); i++)
			{
				if(board.grid.get(i).get(board.colIndex) == 1)
					numFilled++;
				else if(board.grid.get(i).get(board.colIndex)== 0)
					numBlank++;
				else
					numEmpty++;
			}	
			
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2 + 1), numFilled);
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2 + 2), numEmpty);
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2 + 3), numBlank);
		}
		
		else//(RowOrColumn.lookingAtRows)
		{
			//Update the thermometer data with the newest tile values, just in case it hasn't already been done
			board.updateThermData();
			
			//Set the fill for each tile to whatever the fill currently is for that file.
			for(int i = 0; i < board.grid.size(); i++)
					anInstance.setValue(InstanceCreation.WEKAattributes.get(0+i), board.grid.get(board.rowIndex).get(i)+"");
			
			//For each thermometer in the row
			for(int j = 0; j < board.rowTherms.get(board.rowIndex).size(); j++)
			{
				//For each piece of the thermometer
				for(int k = 0; k < board.rowTherms.get(board.rowIndex).get(j).size(); k++)
				{
					//Get the string code for that piece. Details on the string codes can be found in BoardFileIO.java
					int location = board.rowTherms.get(board.rowIndex).get(j).get(k).getYPos();
					String code = board.stringGrid[board.rowTherms.get(board.rowIndex).get(j).get(k).getXPos()][board.rowTherms.get(board.rowIndex).get(j).get(k).getYPos()];
					

					//The last letter in each 3 letter code is the "true fill" of the tile (the fill it will have when the board is solved)
					//   This is information the learner should not have, so we remove it.
					code = code.substring(0,code.length() - 1);
					anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE + location), code);
				}	
			}
			
			//Set the next feature as the number of filled tiles the column should have when the board is solved
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2), board.rowCounts.get(board.rowIndex));
			
			//Set the next 3 features as the current number of filled, blank, and empty tiles in the row
			int numBlank = 0;
			int numFilled = 0;
			int numEmpty = 0;
			for(int i = 0; i < board.grid.size(); i++)
			{
				if(board.grid.get(board.rowIndex).get(i) == 1)
					numFilled++;
				else if(board.grid.get(board.rowIndex).get(i)== 0)
					numBlank++;
				else
					numEmpty++;
			}	
			
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2 + 1), numFilled);
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2 + 2), numEmpty);
			anInstance.setValue(InstanceCreation.WEKAattributes.get(Board.BOARDSIZE * 2 + 3), numBlank);
		}
		return anInstance;	
	}
	
	
	/** Creates a learning instance for simple thermometers boards.  
 	*  There are 6 rules used to play the game, and the features correspond to which of those rules apply where.  
	*  For more information, please refer to RuleBasedBoardFacts.java
	*  
	*  @param board The board from which to create the instance
	*   */
	private static Instance createInstanceComplexTherms(ComplexThermsBoard board)
	{
		//Initialize instance
		Instance anInstance = new DenseInstance(InstanceCreation.numWEKAattributes);
		
		//Set default values for features
		for(int i = 0; i < Board.BOARDSIZE * Board.BOARDSIZE; i++)
			anInstance.setValue(InstanceCreation.WEKAattributes.get(i), "NotBlank");
		for(int i = wholeColumnRulesStartIndex; i < wholeColumnRulesStartIndex + Board.BOARDSIZE * 2; i++)
			anInstance.setValue(InstanceCreation.WEKAattributes.get(i), "no");
		for(int i = complicatedColRulesStartIndex; i < complicatedColRulesStartIndex + Board.BOARDSIZE * Board.BOARDSIZE * 2; i++)
			anInstance.setValue(InstanceCreation.WEKAattributes.get(i), "-1");
		for(int i = remainingColRulesIndex; i < remainingColRulesIndex + Board.BOARDSIZE * Board.BOARDSIZE * 2; i++)
			anInstance.setValue(InstanceCreation.WEKAattributes.get(i), "no");
		for(int i = startingRowIndex; i < startingRowIndex + Board.BOARDSIZE * 2; i++)
			anInstance.setValue(InstanceCreation.WEKAattributes.get(i), "no");
		for(int i = complicatedRowRulesStartIndex; i < complicatedRowRulesStartIndex + Board.BOARDSIZE * Board.BOARDSIZE * 2; i++)
			anInstance.setValue(InstanceCreation.WEKAattributes.get(i), "-1");
		for(int i = remainingRowRulesIndex; i < remainingRowRulesIndex + Board.BOARDSIZE * Board.BOARDSIZE * 2; i++)
			anInstance.setValue(InstanceCreation.WEKAattributes.get(i), "no");
			

		//grid binary features (blanks only!)
		for(int i = 0; i < board.grid.size(); i++)
			for(int j = 0; j < board.grid.get(0).size(); j++)
				if(board.grid.get(i).get(j) == 0)
					anInstance.setValue(InstanceCreation.WEKAattributes.get(j* Board.BOARDSIZE+i), "Blank");

		//Rule 1
		ArrayList<Pair<Character, Integer>> ruleFacts = RuleBasedBoardFacts.restFilledRuleFacts(board.grid, board.rowCounts, board.colCounts);

		for(int i = 0; i < ruleFacts.size(); i++)
		{		
			if(ruleFacts.get(i).getKey() == 'c')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(wholeColumnRulesStartIndex +  ruleFacts.get(i).getValue()), "yes");
			}
			
			else if(ruleFacts.get(i).getKey() == 'r')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(startingRowIndex + ruleFacts.get(i).getValue()), "yes");
			}
		}
		
		
		//Rule 2
		ruleFacts = RuleBasedBoardFacts.restEmptyRuleFacts(board.grid, board.rowCounts, board.colCounts);
		
		for(int i = 0; i < ruleFacts.size(); i++)
		{
			if(ruleFacts.get(i).getKey() == 'c')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(wholeColumnRulesStartIndex + Board.BOARDSIZE + ruleFacts.get(i).getValue()), "yes");
			}
			 
			else if(ruleFacts.get(i).getKey() == 'r')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(startingRowIndex + Board.BOARDSIZE + ruleFacts.get(i).getValue()), "yes");
			}
		
		}
		
		//Rule 3
		ArrayList<Triple> ruleTriples = RuleBasedBoardFacts.restThermEmptyRuleFacts(board.grid, board.rowCounts, board.colCounts, board.rowTherms, board.colTherms);
		
		for(int i = 0; i < ruleTriples.size(); i++)
		{
			
			if(ruleTriples.get(i).getFirst() == 'c')
			{
				
				anInstance.setValue(InstanceCreation.WEKAattributes.get(remainingColRulesIndex + Board.BOARDSIZE * Board.BOARDSIZE + ruleTriples.get(i).getSecond() * Board.BOARDSIZE + ruleTriples.get(i).getThird()), "yes");
			}
			else if(ruleTriples.get(i).getFirst() == 'r')
			{	
				anInstance.setValue(InstanceCreation.WEKAattributes.get(remainingRowRulesIndex + Board.BOARDSIZE * Board.BOARDSIZE + ruleTriples.get(i).getSecond() * Board.BOARDSIZE + ruleTriples.get(i).getThird()), "yes");
			}
		
		}
		
		//Rule 4
		ruleTriples = RuleBasedBoardFacts.restThermFilledRuleFacts(board.grid, board.rowCounts, board.colCounts, board.rowTherms, board.colTherms);
		
		for(int i = 0; i < ruleTriples.size(); i++)
		{
			if(ruleTriples.get(i).getFirst() == 'c')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(remainingColRulesIndex + ruleTriples.get(i).getSecond() * Board.BOARDSIZE + ruleTriples.get(i).getThird()), "yes");
			}
			else if(ruleTriples.get(i).getFirst() == 'r')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(remainingRowRulesIndex + ruleTriples.get(i).getSecond() * Board.BOARDSIZE + ruleTriples.get(i).getThird()), "yes");
			}
		}
		
		//Rule 5
		ArrayList<Quadruple> ruleQuads = RuleBasedBoardFacts.thermTooBigRuleFacts(board.grid, board.rowCounts, board.colCounts, board.rowTherms, board.colTherms);
		
		for(int i = 0; i < ruleQuads.size(); i++)
		{
			if(ruleQuads.get(i).getFirst() == 'c')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(complicatedColRulesStartIndex + ruleQuads.get(i).getSecond() * Board.BOARDSIZE + ruleQuads.get(i).getThird()),ruleQuads.get(i).getFourth() );
			}
			else if(ruleQuads.get(i).getFirst() == 'r')
			{			
				anInstance.setValue(InstanceCreation.WEKAattributes.get(complicatedRowRulesStartIndex + ruleQuads.get(i).getSecond() * Board.BOARDSIZE + ruleQuads.get(i).getThird()),ruleQuads.get(i).getFourth() );
			}
		}
		
		//Rule 6
		ruleQuads = RuleBasedBoardFacts.partiallyFillThermRuleFacts(board.grid, board.rowCounts, board.colCounts, board.rowTherms, board.colTherms);
		
		for(int i = 0; i < ruleQuads.size(); i++)
		{
			if(ruleQuads.get(i).getFirst() == 'c')
			{
				anInstance.setValue(InstanceCreation.WEKAattributes.get(complicatedColRulesStartIndex + Board.BOARDSIZE * Board.BOARDSIZE + ruleQuads.get(i).getSecond() * Board.BOARDSIZE + ruleQuads.get(i).getThird()),ruleQuads.get(i).getFourth());
			}
			else if(ruleQuads.get(i).getFirst() == 'r')
			{
				
				anInstance.setValue(InstanceCreation.WEKAattributes.get(complicatedRowRulesStartIndex + Board.BOARDSIZE * Board.BOARDSIZE + ruleQuads.get(i).getSecond() * Board.BOARDSIZE + ruleQuads.get(i).getThird()),ruleQuads.get(i).getFourth());
			}
		}
		
		return anInstance;
	}
}