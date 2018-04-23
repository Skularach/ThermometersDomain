package BoardRepresentation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



/** This library deals with loading in boards, both specified and random, from files.
 * 
 * Guide to interpreting the 3 character String codes
 *    The first character determines the type of the piece
 *        B: Bulb
 *        T: Tube
 *        C: Cap
 * 
 * 	  The second character determines the orientation of the piece
 *        U: (Bulbs and Caps only) The part that connects to other thermometers is facing up
 *        R: (Bulbs and Caps only) The part that connects to other thermometers is facing right
 *        D: (Bulbs and Caps only) The part that connects to other thermometers is facing down
 *        L: (Bulbs and Caps only) The part that connects to other thermometers is facing left
 *        H: (Tubes only) The parts that connect to other thermometers on the left and right
 *        V: (Tubes only) The parts that connect to other thermometers on the top and bottom 
 * 
 * 	  The third character determines the "correct" fill of the tile.  
 *        F: The piece is filled
 *        E: The piece is empty
 *        
 *        
 *    Examples:
 *       BUE:  Empty bulb with the connecting part pointing upwards
 *       THF: Horizontal filled tube
 *       CLE: Empty cap with the connecting part pointing to the left
 * 
 * @author Brandon Packard
 *
 */
public class BoardFileIO{
	//Will hold a list of all board numbers (0-167999 but then randomly shuffled)
	public static ArrayList<Integer> boardNums;
	
	//Random used to select boards
	public static Random levelRand = new Random(12535);
	
	//The total number of boards stored on disk, 
	//the maximum # allowed to use for training to set aside a validation set.
	//With these values, 160,000 boards are available for training and the last
	//8k are saved for validation purposes.
	private static int totalNumBoards = 168000;
	public static int maxTrainingLevel = 159999;
	public static int startingValidationLevel = 160000;
	
	
	/** This method loads in the board specified by boardNum from a file.
	 * 
	 * @param boardNum Which board to load.
	 * @return The string representation of the board structure.
	 */
	public static String[][] loadInBoardbyNumber(int boardNum) 
	{		
		//5x5 Boards are stored at the program's working directory /Puzzles/5x5/Puzzle#########.txt
		//Initial String Setup
		String pwd = System.getProperty("user.dir");
		pwd += "/Puzzles/" + Board.BOARDSIZE+ "x" + Board.BOARDSIZE + "/Puzzle000";
		
		//Add the appropriate number of 0s to make every file name have exactly 9 numbers
		String loadString;
		if(boardNum < 10)
			loadString = "00000" + boardNum;
		else if(boardNum < 100)
			loadString = "0000" + boardNum;
		else if(boardNum < 1000)
			loadString = "000" + boardNum;
		else if(boardNum < 10000)
			loadString = "00" + boardNum;
		else if(boardNum < 100000)
			loadString = "0" + boardNum;
		else
			loadString = boardNum + "";
		
		//Add the two parts of the strong and create a file based on it.
		pwd += loadString + ".txt";
		File file = new File(pwd);
		
		//Read in the file from the file object and return what it returns
		return readGridFromFile(file);
		
	}
	
	/** This method reads in a blank Thermometers board from the file object passed into it.
	 * 
	 * @param file The file object containing the path to the file on disk that we want to read.
	 * @return A 2D String array which contains the structure of a Thermometers board
	 */
	public static String[][] readGridFromFile(File file)
	{
		//Get board size based on the first line
		// ASSUMES ALL BOARDS ARE SQUARE
		// For example, if the first line is:
		//   BRF THF THF THE CLE
		//   then the board is 5x5
		//  
		try(BufferedReader testReader = new BufferedReader(new FileReader(file)))
		{
			String line =  testReader.readLine();
			Board.BOARDSIZE = line.split(" ").length;
		}
		catch(Exception e){System.out.println("Error getting board size");}
		
		//Initialize the stringGrid representation to the appropriate boardsize
		String[][] returnGrid = new String[Board.BOARDSIZE][Board.BOARDSIZE];
		
		//Now read in and store all of the codes
		try(BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			int rowIndex = 0;
			String currentLine;
			while ((currentLine = reader.readLine()) != null)
			{
				String[] splitLine = currentLine.split(" ");
				
				for(int j = 0; j < splitLine.length; j++)
				{
				//	if(splitLine[j].charAt(0) != '-')
						returnGrid[rowIndex][j] = splitLine[j];
				}
				
				rowIndex++;
			}
		}
		catch(Exception e){System.err.println("Error reading in file");};
		
		return returnGrid;
	}
	
	/**  This method selects and reads in a random grid from the list,
	 *     but excludes some boards from the random selection.
	 * 
	 * @param max  The maximum board # from which to pick from.
	 * @return A 2D String array representing the structure of the loaded in Thermometers board.
	 */
	public static String[][] selectRandomGrid(int max) {
		//Uses the random number to get a board # from boardNums, then loads that board.
		// This is because boards that are sequential files tend to be very similiar,
		// So this way we can shuffle them up which feels more random (and also gives
		// a larger variety in the validation boards)
		int randIndex = levelRand.nextInt(max);
		return loadInBoardbyNumber(boardNums.get(randIndex));
	}
	
	
	/** This method creates a list of board numbers to select from later.
	 * This exists because boards that are sequential on disk, with the
	 * boards included with this domain, tend to be very similar, so this 
	 * shuffles them up.  It adds more variety, especially to the validation 
	 * levels since those are taken sequentially.
	 * 
	 */
	public static void getboardNumList()
	{
		//Create a list from 0 to totalNumBoards-1
		boardNums = new ArrayList<Integer>();
		for(int i = 0; i < totalNumBoards; i++)
			boardNums.add(i);
		
		//Randomly shuffle the list.  Note this currently uses the same
		// random generator as the level selection itself.
		Collections.shuffle(boardNums, levelRand);
	}

	/**  This method is the same as SelectRandomGrid, but with the 
	 *     idea of the maximum #ed board being the highest numbered
	 *     training board built in instead of taking any int as a parameter.
	 * 
	 * @return A 2D String array representing the structure of the loaded in Thermometers board.
	 */
	public static String[][] selectRandomTrainingGrid() {
		int randIndex = levelRand.nextInt(maxTrainingLevel);
		return loadInBoardbyNumber(boardNums.get(randIndex));
	}
}