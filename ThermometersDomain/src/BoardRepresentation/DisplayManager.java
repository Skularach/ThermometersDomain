package BoardRepresentation;
// Display Manager
// Brandon Packard
// 11/14/2015
//
// Is responsible for the game board that the user sees, and updating it in such a way that
// it is easy for the user to follow what is going on

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import BoardRepresentation.Board;
import BoardRepresentation.RotatedIcon;

/**  This class handles displaying the thermometers visual
 *   This is turned off by default in the domain, but was included for
 *   easier integration of visuals for those wanting to use the domain.
 *   
 *   
 * @author Brandon Packard
 *
 */
public class DisplayManager extends JPanel implements MouseListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	
	//Data for Simple Therms domain
	public static boolean lookingAtRows = true;
	public static int rowIndex = 0;
	public static int colIndex = 0;
	
	//Variables controlled by user interaction
	public static boolean stopTraining = false;
	public static boolean userClicked = false;
	public static boolean userRightClicked = false;
	public static boolean userLeftClicked = false;
	public static boolean userMiddleClicked = false;
	public static boolean userTypedSpace = false;
	public static boolean userTypedS = false;
	public static boolean userTypedN = false;
	
	//Location and fill of tile the user clicked
	public static int userI = -1;
	public static int userJ = -1;
	public static int userFill = -50;
	
	//What fill a tile should get when the user _____ clicks
	public static int fillIfRightClick = -1;
	public static int fillIfLeftClick = 1;
	public static int fillIfMiddleClick = 0;
	
	//Board data
	private Board board;
	String[][] globalFullGrid;
	final static int BOARDSIZE = Board.BOARDSIZE;

	
	//Labels and visual data
	private static ArrayList<MyPanel> colLabels = new ArrayList<MyPanel>();
	private static ArrayList<MyPanel> rowLabels = new ArrayList<MyPanel>();	
	private static ArrayList<MyPanel> instLabels = new ArrayList<MyPanel>();
	private static JPanel colorPanel = new JPanel();
	private static MyPanel[][] gridSquares = new MyPanel[BOARDSIZE][BOARDSIZE];
	private static JPanel blankPanel = new JPanel();
	private JFrame mainWindow = new JFrame("Thermometers");
	
		
	//Constructor: Add a listener to capture the user input
	public DisplayManager()
	{
		mainWindow.addKeyListener(this);

	}
	

	/** Initializes the visualization based on an input board.
	 *     Constructs the panels, labels, etc and displays the blank board
	 *     
	 *     
	 * @param boardIn The board to visualize
	 * @param iterationNo  The iteration we are currently on in the algorithm.  Used to display how many boards a user has solved.
	 */
	protected void initializeGui(Board boardIn, int iterationNo)
	{
		//Set data members
		this.board = boardIn;
		globalFullGrid = board.stringGrid;
		
		//Remove all components related to the board
		this.remove(colorPanel);

		for(int i = 0; i < gridSquares.length; i++)
			for(int j = 0; j < gridSquares[0].length; j++)
				if(gridSquares[i][j] != null)
					this.remove(gridSquares[i][j]);
		
		for(int i = 0; i < colLabels.size(); i++)
				this.remove(colLabels.get(i));
		
		for(int i = 0; i < rowLabels.size(); i++)
			this.remove(rowLabels.get(i));
		
		for(int i = 0; i < instLabels.size(); i++)
			this.remove(instLabels.get(i));
		
		this.remove(blankPanel);
		
		
		//Clear data structures holding board information
		colLabels.clear();
		rowLabels.clear();
		instLabels.clear();
		
		//Set size and properties of board
		mainWindow.setSize(400,400);
		mainWindow.setVisible(true);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.add(this);
		
		//Set up the overall layout of the window.
		GridBagLayout gameGrid = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 5, 5);
		this.setLayout(gameGrid); 
		
		//Start the panel indicator at red, for "the user should make a move here"
		colorPanel = getRedPanel();
		
		//Show the user how many boards they have done
		((JLabel)colorPanel.getComponent(0)).setText(""+(iterationNo+1));
		
		//Add the color indicator panel onto the window
		c.gridx = 0;
		c.gridy = 0;
		this.add(colorPanel,c);
		
		//Add the column labels
		for(int i = 0; i < Board.BOARDSIZE; i++)
		{
			MyPanel colLabel = makeStarterPanel(board.colCounts.get(i).toString());
			
			colLabels.add(colLabel);
			c.gridx = i+1;
			c.gridy = 0;
			this.add(colLabel,c);
		}
		
		//Add the instruction label
		MyPanel instLabel = makeInstructionPanel(0);
		instLabels.add(instLabel);
		
		c.gridx = BOARDSIZE+1;
		c.gridy = 0;
		this.add(instLabel,c);

		//Add the row labels, tiles, and instructions
		for(int i = 0; i < BOARDSIZE; i++)
		{

			MyPanel rowLabel = makeStarterPanel(board.rowCounts.get(i).toString());
			rowLabels.add(rowLabel);
			c.gridx = 0;
			c.gridy = i + 1;
			this.add(rowLabel,c);
			for(int j = 0; j < BOARDSIZE; j++)
			{
				
				JLabel label = new JLabel();
				
				label.setFont(new Font("Veranda",1,20));
				label.setVerticalTextPosition(JLabel.CENTER);

				RotatedIcon icon = getBlankIcon(board.stringGrid,i,j);
				
				label.setIcon(icon);
				
				gridSquares[i][j] = new MyPanel(label);
				
			//	gridSquares[i][j].add(label);
				
				gridSquares[i][j].setBorder(new LineBorder(Color.BLACK));

				//gridSquares[i][j].setSize(10,10);
				gridSquares[i][j].setBackground(Color.WHITE);
				
				gridSquares[i][j].addMouseListener(this);
				
				c.gridx = 1 + j;
				c.gridy = i + 1;
				this.add(gridSquares[i][j],c);
				
				
			}
			
			 instLabel = makeInstructionPanel(i+1);
			 //nstLabel.setHorizontalAlignment(SwingConstants.LEFT);
			instLabels.add(instLabel);
			c.gridx  =BOARDSIZE+1;
			c.gridy = i + 1;
			this.add(instLabel,c);
		}
		
		//If we are dealing with a SimpleThermsBoard, only show one row or column.
		if(board instanceof SimpleThermsBoard)
			coverBoardForSingleThermsDomain();
		
		//Required for the main window to show up correctly.
		mainWindow.pack();
	}

	/**There are only 3 files for blank thermometer icons, so to get all 12 
	 *   (3 types * 4 rotations), we need to rotate those files.  This function
	 *   creates an "unknown" thermometers icon rotated the correct way.
	 * 
	 * 
	 * @param fullGrid  Grid to extract icon from
	 * @param i  X position on grid of icon
	 * @param j  Y position on grid of icon
	 * @return A RotatedIcon object which has the correct thermometer piece in the correct rotation.
	 */
	private RotatedIcon getBlankIcon(String[][] fullGrid, int i, int j) {
		RotatedIcon newIcon;
		
		//If the tile in question is a base pointing up, whether its full or empty,
		//  we need a blank thermometers base rotated 0 degrees (since the base image points up)
		if(fullGrid[i][j].equals("BUF") || fullGrid[i][j].equals("BUE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBaseBlank.jpg"),0);
		//If the tile in question is a base pointing right, whether its full or empty,
		//  we need a blank thermometers base rotated 90 degrees
		else if(fullGrid[i][j].equals("BRF") || fullGrid[i][j].equals("BRE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBaseBlank.jpg"),90);
		else if(fullGrid[i][j].equals("BDF") || fullGrid[i][j].equals("BDE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBaseBlank.jpg"),180);
		else if(fullGrid[i][j].equals("BLF") || fullGrid[i][j].equals("BLE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBaseBlank.jpg"),270);
		
		//Thermometers Caps
		else if(fullGrid[i][j].equals("CUF") || fullGrid[i][j].equals("CUE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTopBlank.jpg"),180);
		else if(fullGrid[i][j].equals("CRF") || fullGrid[i][j].equals("CRE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTopBlank.jpg"),270);
		else if(fullGrid[i][j].equals("CDF") || fullGrid[i][j].equals("CDE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTopBlank.jpg"),0);
		else if(fullGrid[i][j].equals("CLF") || fullGrid[i][j].equals("CLE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTopBlank.jpg"),90);
		
		//Thermomters Tubes
		else if(fullGrid[i][j].equals("THF") || fullGrid[i][j].equals("THE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermMiddleBlank.jpg"));
		else if(fullGrid[i][j].equals("TVF") || fullGrid[i][j].equals("TVE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermMiddleBlank.jpg"),180);
		else
			newIcon = null;
		
		//Return the rotated ivon
		return newIcon;
	}
	
	/** Gets the rotated icon corresponding to where the user clicked 
	 * and what fill they wanted.  Used to change the board when the user clicks.
	 * 
	 * @param fullGrid  The string grid holding the board
	 * @param i The x position of the tile on the board
	 * @param j The y position of the tile on the board
	 * @param fill  The fill the user wanted in the tile
	 * @return  A RotatedIcon object which corresponds to the tile the user clicked on and the fill they gave it.
	 */
	private static RotatedIcon getUserSelectedIcon(String[][] fullGrid, int i, int j, int fill) {
RotatedIcon newIcon;
		String imageFill;
		if(fill == -1)
			imageFill = "Empty";
		else if(fill == 0)
			imageFill = "Blank";
		else 
			imageFill = "Full";
		
		//Choose the correct icon and rotate it.
		// For this first if, if the icon is really a base pointing up, make a base 
		// pointing up with the fill the user specified (not necessarily the "correct" fill!)
		if(fullGrid[i][j].equals("BUF") || fullGrid[i][j].equals("BUE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBase"+imageFill+".jpg"),0);
		else if(fullGrid[i][j].equals("BRF") || fullGrid[i][j].equals("BRE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBase"+imageFill+".jpg"),90);
		else if(fullGrid[i][j].equals("BDF") || fullGrid[i][j].equals("BDE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBase"+imageFill+".jpg"),180);
		else if(fullGrid[i][j].equals("BLF") || fullGrid[i][j].equals("BLE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermBase"+imageFill+".jpg"),270);
		
		else if(fullGrid[i][j].equals("CUF") || fullGrid[i][j].equals("CUE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTop"+imageFill+".jpg"),180);
		else if(fullGrid[i][j].equals("CRF") || fullGrid[i][j].equals("CRE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTop"+imageFill+".jpg"),270);
		else if(fullGrid[i][j].equals("CDF") || fullGrid[i][j].equals("CDE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTop"+imageFill+".jpg"),0);
		else if(fullGrid[i][j].equals("CLF") || fullGrid[i][j].equals("CLE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermTop"+imageFill+".jpg"),90);
		
		else if(fullGrid[i][j].equals("THF") || fullGrid[i][j].equals("THE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermMiddle"+imageFill+".jpg"));
		else if(fullGrid[i][j].equals("TVF") || fullGrid[i][j].equals("TVE"))
			newIcon = new RotatedIcon(new ImageIcon("Sprites/ThermMiddle"+imageFill+".jpg"),180);
		else
			newIcon = null;
		return newIcon;
	}
	
	/** Mouse listener.  Gets applied to all tiles to listen for clicks.
	 * 
	 * @param arg0 data for the MouseEvent
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		JPanel clickedPanel = (JPanel)arg0.getSource();
		
		//Find the panel the user clicked
		for(int i = 0; i < BOARDSIZE; i++)
			for(int j = 0; j < BOARDSIZE; j++)
				if(clickedPanel == gridSquares[i][j])
				{
					userJ = j;
					userI = i;
				}
		
		userClicked = true;
		
		//Flag which button the user clicked
		if (SwingUtilities.isLeftMouseButton(arg0))
		{
			userLeftClicked = true;
			userRightClicked = false;
			userMiddleClicked = false;
			userFill = fillIfLeftClick;
		}
		else if (SwingUtilities.isRightMouseButton(arg0))
		{
			userRightClicked = true;
			userLeftClicked = false;
			userMiddleClicked = false;
			userFill = fillIfRightClick;
		}
		
		else
		{
			userRightClicked = false;
			userLeftClicked = false;
			userMiddleClicked = true;
			userFill = fillIfMiddleClick;
		}
	}

	/** This method is required due to inheritence, but is not used for anything.
	 * 
	 * @param arg0
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	
	/** This method is required due to inheritence, but is not used for anything.
	 * 
	 * @param arg0
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	
	/** This method is required due to inheritence, but is not used for anything.
	 * 
	 * @param arg0
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}

	
	/** This function takes in a tile location and fill,
	 *   and sets the appropriate label to the correct icon.
	 * 
	 * @param x  x position of tile on grid
	 * @param y  y position of tile on grid
	 * @param fill  the fill the file should have
	 */
	public void updateTile(int x, int y, int fill) {
		RotatedIcon icon = getUserSelectedIcon(board.stringGrid, x, y, fill);
		JLabel label = (JLabel)gridSquares[x][y].getComponent(0);
		label.setIcon(icon);
	}
	
	
	/** Makes a panel object with the provided text.
	 * 
	 * @param text The text to place on the panel
	 * @return  The panel containing the passed-in text
	 */
	private MyPanel makeStarterPanel(String text)
	{
		
		JLabel testLabel = new JLabel(text, SwingConstants.LEFT);
		CardLayout layout = new CardLayout();
		testLabel.setLayout(layout);
	
		testLabel.setFont(new Font("Veranda",1,20));
		testLabel.setVerticalTextPosition(JLabel.CENTER);
		
		testLabel.setHorizontalTextPosition(JLabel.LEFT);
		
		MyPanel testPanel = new MyPanel(testLabel);
		return testPanel;
	}
	
	
	/** Makes a panel with a piece of the game instructions.  Which
	 *    piece of the instructions the panel has is determined by
	 *    the passed in index value.  It was programmed this way to loop well with
	 *    placing the grid panels.
	 * 
	 * @param index Which piece of the instructions should be put on the panel.
	 * @return A panel with part of the game instructions printed on it.
	 */
	private MyPanel makeInstructionPanel(int index)
	{
		JLabel testLabel = new JLabel();//text, SwingConstants.LEFT);
		if(index == 0)
			testLabel.setText("Instructions");
		else if(index == 1)
			testLabel.setText("Fill Tile: LMB");
		else if(index == 2)
			testLabel.setText("Empty Tile: RMB");
		else if(index == 3)
			testLabel.setText("Move Row/Column: MMB");
		else if(index == 4)
			testLabel.setText("Clear Row/Column: Space");
		else if(index == 5)
			testLabel.setText("End Training: E");
		
		//Label attributes
		CardLayout layout = new CardLayout();
		testLabel.setLayout(layout);
		testLabel.setFont(new Font("Veranda",1,20));
		testLabel.setVerticalTextPosition(JLabel.CENTER);
		testLabel.setHorizontalTextPosition(JLabel.LEFT);

		//Make and return the panel containing the label
		MyPanel testPanel = new MyPanel(testLabel);
		return testPanel;		
	}
	
	
	/** Literally just gets a blank panel colored red.
	 * 
	 * @return A panel with no text and a red background
	 */
	private JPanel getRedPanel()
	{
		//Label data
		JLabel testLabel = new JLabel();
		testLabel.setFont(new Font("Veranda",1,20));
		testLabel.setVerticalTextPosition(JLabel.CENTER);
		testLabel.setText(" ");//"+Board.colCounts.get(i));

		//Panel creation and return
		JPanel testPanel = new JPanel();
		testPanel.setBackground(Color.RED);
		testPanel.add(testLabel);
		return testPanel;
	}
	
	

	/** By default, the entire board is displayed at once.
	 *   This method hides all but one row or column of the board,
	 *   corresponding with what row/column is being looked at in the 
	 *   Simple Thermometers domain.
	 */
	public void coverBoardForSingleThermsDomain()
	{
		//set everything to invisible (labels)
		for(int i = 0; i < rowLabels.size(); i++)
		{
			rowLabels.get(i).setEnabled(false);
			colLabels.get(i).setEnabled(false);
			rowLabels.get(i).hidePanel();
			colLabels.get(i).hidePanel();
		}
		
		//set everything to invisible (grid panels)
		for(int i = 0; i < gridSquares.length; i++)
			for(int j = 0; j < gridSquares[0].length; j++)
			{
				gridSquares[i][j].setEnabled(false);
				gridSquares[i][j].hidePanel();
				
			}
		
		
		//make visible the appropriate row or column
		if(lookingAtRows)
		{
			int index = rowIndex;
			rowLabels.get(index).setEnabled(true);
			rowLabels.get(index).showPanel();
			
			//for the whole row..
			for(int i = 0; i < gridSquares.length; i++)
			{
				gridSquares[index][i].setEnabled(true);
				gridSquares[index][i].showPanel();
			}
		}
		
		else
		{
			int index = colIndex;
			colLabels.get(index).setEnabled(true);
			colLabels.get(index).showPanel();
			
			//for the whole column...
			for(int i = 0; i < gridSquares.length; i++)
			{
				gridSquares[i][index].setEnabled(true);
				gridSquares[i][index].showPanel();
			}
		}
	}

	/** This method is required due to inheritence, but is not used for anything.
	 * 
	 * @param arg0
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	
	/** This method is required due to inheritence, but is not used for anything.
	 * 
	 * @param arg0
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	
	/** Key event handler.  Captures if the user presses keys and
	 *    sets the appropriate flags.
	 * 
	 * @param arg0 The data for the KeyEvent that was trigger
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
		//User typed space
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE)
		{
			userTypedSpace = true;
		}
		
		//User pressed E
		if (arg0.getKeyCode() == KeyEvent.VK_E)
		{
			userClicked = true;
			stopTraining = true;
		}
		
		//User pressed S
		if(arg0.getKeyCode() == KeyEvent.VK_S)
		{
			userTypedS = true;
		}
		
		//User pressed N
		if(arg0.getKeyCode() == KeyEvent.VK_N)
		{
			userTypedN = true;
		}
	}
	
	
	/** MiniClass that acts just like a normal panel, 
	 *    but has easy show and hide functionality.
	 *    
	 * @author Brandon Packard
	 *
	 */
	 @SuppressWarnings("serial")
	private static class MyPanel extends JPanel {
			CardLayout layout;

			//Constructor, adds a blank label and panel, and sets the MyPanel to visible
	        public MyPanel(JLabel aLabel) {
	            layout = new CardLayout();
	            setLayout(layout);
	            add(aLabel, "visible");
	            add(new JPanel(), "invisible");
	            layout.show(this, "visible");
	        }

	        //Makes the MyPanel invisible
	        public void hidePanel() {
	            layout.show(this, "invisible");
	        }
	        
	        //Makes the MyPanel visible.
	        public void showPanel(){
	        	layout.show(this, "visible");
	        }
	    }
	 
	 /** Determines whether the indicator in the top left of the window
	  *   should be green or red (red if we need the demonstrator to make a move, 
	  *   green otherwise)
	  * 
	  * @param needExpertMove Whether or not we need a move from the demonstrator
	  */
	 public static void setIndicatorLight(boolean needExpertMove)
	 {
			if(needExpertMove)
				colorPanel.setBackground(Color.red);
			else
				colorPanel.setBackground(Color.green);
	 }
}