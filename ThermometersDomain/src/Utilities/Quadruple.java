package Utilities;

/** Simple 4-tuple Quadruple class.  Used to store 4 related integers together.
 *    Currently only used in the RuleBasedBoardFacts class
 * 
 * @author Brandon Packard
 *
 */
public class Quadruple  {
	private int first = -1;
	private int second = -1;
	private int third = -1;
	private int fourth = -1;

	/** Constructor: Takes in 4 values and store them into a quadruple
	 * 
	 * @param first The value to put in the first place in the quadruple
	 * @param second The value to put in the second place in the quadruple
	 * @param third The value to put in the third place in the quadruple
	 * @param fourth The value to put in the fourth place in the quadruple
	 */
   public Quadruple (int first, int second, int third, int fourth) {
	   this.first = first;
	   this.second = second;
      this.third = third;
      this.fourth = fourth;
   }

   /** Function to get the first item from a quadruple
    * 
    * @return The first value in the Quadruple
    */
   public int getFirst () { return first; }
   
   /** Function to get the second item from a quadruple
    * 
    * @return The second value in the Quadruple
    */
   public int getSecond () { return second; }
   
   /** Function to get the third item from a quadruple
    * 
    * @return The third value in the Quadruple
    */
   public int getThird () { return third; }
   
   /** Function to get the third item from a quadruple
    * 
    * @return The third value in the Quadruple
    */
   public int getFourth () { return fourth;}
}