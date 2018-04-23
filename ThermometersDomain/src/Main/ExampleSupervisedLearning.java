package Main;

import java.util.ArrayList;
import java.util.Collections;

import Utilities.Printing;
import Utilities.Triple;
import WEKA.Classification;
import WEKA.Conversions;
import WEKA.InstanceCreation;
import weka.core.Instance;
import weka.core.Instances;
import BoardActions.ApplyMovesToBoard;
import BoardActions.MakeLearningInstances;
import BoardRepresentation.Board;
import BoardRepresentation.BoardFileIO;
import BoardRepresentation.BoardReward;
import BoardRepresentation.SimpleThermsBoard;
import Demonstrators.SimpleThermometersAI;
import BoardRepresentation.ComplexThermsBoard;
import Demonstrators.ComplexThermometersAI;


/** This class provides an example of supervised learning in the Simple Thermometers domain.
 * 
 * The way that we do supervised learning here is by running the demonstrator on a bunch
 *	of boards to get a large chunk of training data, shuffling that data, and then
 *	splitting it up into pieces.  The "ssSizes" array holds the data for how we are going to make that split:
 *	For example, if SSsizes is {40, 80} then the first "iteration" will hold the first 40 training instances from 
 *	the huge chunk of data, and the second "iteration" will hold the first 40 training instances plus the next
 *	40 training instances.  This may seem odd, but this format allows it to be easily be compared to iterative algorithm
 *	 
 * @author Brandon Packard
 */
@SuppressWarnings("unused")
public class ExampleSupervisedLearning{
	//Number of levels to evaluate each learner on
	static int numValidationLevels = 10;
	
	//Number of iterations to emulate
	static int numIterations = 25;
	
	static ArrayList<ArrayList<Integer>> allDatabaseSizes = new ArrayList<ArrayList<Integer>>();
	
	public static int[] ssSizes = {38,88,138,183,225,266,310,360,404,443,482,528,574,614,663,701,751,797,844,883,925,965,1000,1027,1038};
	
	
	/** Test Driver. Walks through all aspects of doing simple supervised learning in the Simple Thermometers domain.
	 * 
	 * TO CHANGE THIS TO COMPLEX THERMOMETERS: 
	 *  1. Change 'S' to 'C' in the first line in this method
	 *  2. Change all instances of "Simple" (other than the imports) to "Complex"
	 *  
	 * 
	 * @param args Not Used
	 */
	public static void main(String[] args)
	{
		//S for simple therms (vs C for complex)
		//j for j48, (vs k for knn)
		InstanceCreation.initializeWeka('S', 'j');
		
		//Initialize the list of board numbers to be pulled from later
		BoardFileIO.getboardNumList();
		
		//Have the demonstrator solve boards
		for(int i = 0; i < 100; i++)
		{
			//Get a random training board
			String[][] stringGrid = BoardFileIO.selectRandomTrainingGrid();
			SimpleThermsBoard board = new SimpleThermsBoard(stringGrid);
			
			//initialize demonstrator
			SimpleThermometersAI demonstrator = new SimpleThermometersAI();
			
			int numMoves = 0; 
			while(numMoves < Board.MaxTrainingMoves && !board.isSolved())
			{
				board.updateThermData();
				//Get demonstrator move
				Triple demonstratorMove = demonstrator.makeAMove(board);
				
				//Add world and demonstrator's move to database
				Instance worldInstance = MakeLearningInstances.getInstance(board);
				
				String actionLine = Conversions.parseDemonstratorMove(demonstratorMove);
				InstanceCreation.addInstance(worldInstance, actionLine);
				
				
				//Apply the demonstrator's move to the board
				ApplyMovesToBoard.applyMove(board, demonstratorMove);
				numMoves++;
			}
			
			//Single Trace stores all boards
			//tempInstances stores 1 board
			//allInstanceDatabases stores all versions of singleTrace over time
			//   (spot 1 is board 1s data, spot 2 is board 1 and 2s data, etc)
			Classification.singleTraceInstances.addAll(Classification.tempInstances);
			//for(int k = 0; k < Classification.tempInstances.size(); k++)
			//	System.out.println(Classification.tempInstances.get(k));
			
			Classification.tempInstances.clear();
		}
		
		Collections.shuffle(Classification.singleTraceInstances, BoardFileIO.levelRand);
		
		//now that we have data from X expert boards, take the appropriate amount
		for(int i = 0; i < ssSizes.length; i++)
		{
			Classification.allInstanceDatabases.add(new Instances("allInstances",InstanceCreation.WEKAattributes,Classification.maxInstances));
			
			for(int j = 0; j < ssSizes[i]; j++)
			{
				Classification.allInstanceDatabases.get(Classification.allInstanceDatabases.size()-1).add(Classification.singleTraceInstances.get(j));
			}
		}
		
		
		
		
		//Validation
		float[][] valRewards = getValidationRewards();
		ArrayList<float[][]> allValRewards = new ArrayList<float[][]>();
		allValRewards.add(valRewards);
		Printing.print2DFloatArray(valRewards);
		Printing.printRewardsWithDatabaseSizes(allDatabaseSizes,allValRewards);
	}

	/** This method creates a board from text contained in a file
	 * 
	 * 
	 * @param fileNo The board number to read in. By default, must be between 0 and 169,999
	 */
	static void createBoardFromFile(int fileNo)
	{
		String[][] stringGrid = BoardFileIO.loadInBoardbyNumber(fileNo);
		
		//Create board instance
		Board board = new SimpleThermsBoard(stringGrid);
		
		//Print string representation of grid with constraints
		System.out.println("===== Grid With Row and Column Constraints (from file)");
		board.printStringGridWithConstraints();
	
		
	}
	
	/** This method solves the validation boards with the learners and records
	 *    how well they do.
	 * 
	 * 
	 * @return a 2D float array representing the reward gained on each board for each learner
	 */
	private static float[][] getValidationRewards()
	{			
		//Pre-emptively load the boards into a list so we don't have to keep reloading
		ArrayList<String[][]> boardList = new ArrayList<String[][]>();
		for(int i = BoardFileIO.startingValidationLevel; i < BoardFileIO.startingValidationLevel + numValidationLevels; i++)
    	{
			boardList.add(BoardFileIO.loadInBoardbyNumber(BoardFileIO.boardNums.get(i)));
    	}
		
		//Initialize learners x validation levels reward array
    	float[][] rewardScores = new float[Classification.allInstanceDatabases.size()][numValidationLevels];
    	
    	//Store a record of how much training data each learner has
    	allDatabaseSizes.add(new ArrayList<Integer>());
    	for(int i = 0; i < Classification.allInstanceDatabases.size(); i++)
    	{
    		allDatabaseSizes.get(allDatabaseSizes.size()-1).add(Classification.allInstanceDatabases.get(i).size());
    	}
    	
    	//For each learner
    	for(int j = 0; j <  Classification.allInstanceDatabases.size(); j++)
		{     
   			//Set globalInstanceDatabase to be the appropriate learner iteration, since that is the 
    		//  database used for training and classification.
			Classification.globalInstanceDatabase = Classification.allInstanceDatabases.get(j);
			Classification.globalInstanceDatabase.setClassIndex(InstanceCreation.WEKAattributes.size()-1);
			
			//Train a learner on all of the training data accrueed
			Classification.trainClassifier();

			//For each validation board, solve it with this learner and store the reward in
			//   the appropriate place in the reward 2D array.
	    	for(int i = BoardFileIO.startingValidationLevel; i < BoardFileIO.startingValidationLevel + numValidationLevels; i++)
	    	{
		   			rewardScores[j][i-BoardFileIO.startingValidationLevel] = solveBoardLearner(boardList.get(i-BoardFileIO.startingValidationLevel),Board.MaxValidationMoves);
	    	}
		}
    	
    	//We would include the line below if doing multiple trials of the overall process (for sake of averaging results), so that 
    	//   not all trials are run on the same validation levels.
    	//BoardFileIO.startingValidationLevel += numValidationLevels;
    	
    	//return the stored rewards
    	return rewardScores;
	}
	
	/** This method solves a board using the currently trained learner and returns the reward accumulated by doing so.
	 * 	
	 * @param fullGrid The grid to solve
	 * @param maxMoves The maximum number of moves allowed before stopping the attempt to solve the board.
	 * @return
	 */
	public static float solveBoardLearner(String[][] fullGrid, int maxMoves)
	{
		int movesTaken = 0;
		
		//Create the board
		Board board = new SimpleThermsBoard(fullGrid);
		
		//Loop until we either solve the board or run out of moves
		while(!board.isSolved() && movesTaken < maxMoves)
		{
			//Classification.globalInstanceDatabase = Classification.featureSelectionInstanceDatabase;
			Instance worldInstance = MakeLearningInstances.getInstance(board);
			
			Triple learnerMove = Classification.predictActionWEKASimpleThermometers(worldInstance);
	
			//Apply the move to the board
			ApplyMovesToBoard.applyMove(board, learnerMove);
			
			movesTaken++;
		}
		
		
		return BoardReward.getReward(board);
	}
}