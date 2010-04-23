package org.csstudio.opibuilder.scriptUtil;

import org.mozilla.javascript.NativeArray;


/**Utility class to facilitate Javascript programming
 * for data operation. The basic data type such as int, double, boolean and string are 
 * exchangeable between JavaScript and Java, which means these types of JavaScript variables
 * can be directly used as parameters of Java methods. But <b>array</b> is not exchangeable between 
 * JavaScript and Java. This utility class provides methods to create Java array or convert
 * JavaScript array to Java array.  
 * @author Xihui Chen
 *
 */
public class DataUtil {

	/**Returns a new int array with given size.
	 * @param size the size of the array
	 * @return an int array with given size.
	 */
	public final static int[] getIntArray(int size){
		int[] result = new int[size];	
		return result;
	}

	/**Returns a new double array with given size.
	 * @param size the size of the array
	 * @return a double array with given size.
	 */
	public final static double[] getDoubleArray(int size){
		double[] result = new double[size];	
		return result;
	}
	
	/**Convert JavaScript array to Java int array.
	 * @param jsArray JavaScript array
	 * @return java int array.
	 */
	public final static int[] toJavaIntArray(NativeArray jsArray){
		int[] result = new int[(int) jsArray.getLength()];
		int i=0;
		for(Object id : jsArray.getIds()){
			Object o = jsArray.get((Integer)id, null);
			if(o instanceof Number)
				result[i++]=((Number)o).intValue();
			else
				result[i++] = 0;				
		}
		return result;
	}
	
	/**Convert JavaScript array to Java double array.
	 * @param jsArray JavaScript array
	 * @return java array.
	 */
	public final static double[] toJavaDoubleArray(NativeArray jsArray){
		double[] result = new double[(int) jsArray.getLength()];
		int i=0;
		for(Object id : jsArray.getIds()){
			Object o = jsArray.get((Integer)id, null);
			if(o instanceof Number)
				result[i++]=((Number)o).doubleValue();
			else
				result[i++] = 0;				
		}
		return result;
	}
	
}
