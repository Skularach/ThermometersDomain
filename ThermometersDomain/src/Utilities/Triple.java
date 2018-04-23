package Utilities;

/** Simple 3-tuple Triple class.  Used to store 3 related integers together.
 *    Used in many cases, often to represent actions in the domain.
 * 
 * @author Brandon Packard
 *
 */
public class Triple  {
	private final int first;
	private final int second;
	private final int third;
	
	/** Constructor: Takes in 3 values and store them into a triple
	 * 
	 * @param first The value to put in the first place in the triple
	 * @param second The value to put in the second place in the triple
	 * @param third The value to put in the third place in the triple
	 */
   public Triple (int first, int second, int third) {
	   this.first = first;
	   this.second = second;
      this.third = third;
   }

   /** Function to get the first item from a triple
    * 
    * @return The first value in the Triple
    */
   public int getFirst () { return first; }
   
   /** Function to get the second item from a triple
    * 
    * @return The second value in the Triple
    */
   public int getSecond () { return second; }
   
   /** Function to get the third item from a triple
    * 
    * @return The third value in the Triple
    */
   public int getThird () { return third; }
}