/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tm1ipchanger;

/**
 *
 * @author tom.saxton-howes
 */
public class isEven {
    
    /**
     * returns false if num odd
     * @param num number to test
     * @return false if or odd, true if even or 0
     */
    public static boolean isEvenOr0(int num){
        if (num == 0) {
            return true;
        } else {
            double number = (double) num;
            int whole = 0;
            double answer = 0;
            //double whole = 0;
            double dec = 0;
            dec = number / 2;
            whole = (int) dec;
            answer = dec - whole;
            if (answer > 0) {
                return false;
            } else {
                return true;
            }
        }
    }
}
