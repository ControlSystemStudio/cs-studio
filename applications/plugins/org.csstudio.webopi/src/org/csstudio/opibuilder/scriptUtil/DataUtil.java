/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import java.util.LinkedHashMap;

import org.csstudio.opibuilder.util.MacrosInput;
import org.mozilla.javascript.NativeArray;


/**Utility class to facilitate Javascript programming
 * for data operation. The basic data type such as int, double, boolean and string are 
 * exchangeable between JavaScript and Java, which means these types of JavaScript variables
 * can be directly used as parameters of Java methods, but <b>array</b> is not exchangeable between 
 * JavaScript and Java. This utility class provides methods to create Java array or convert
 * JavaScript array to Java array.  
 * @author Xihui Chen
 *
 */
public class DataUtil {

	/**Create a new int array with given size.
	 * @param size the size of the array
	 * @return an int array with given size.
	 */
	public final static int[] createIntArray(int size){
		int[] result = new int[size];	
		return result;
	}

	/**Create a new double array with given size.
	 * @param size the size of the array
	 * @return a double array with given size.
	 */
	public final static double[] createDoubleArray(int size){
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
	
	/**Create a MacrosInput, which can be used as the macros input for a container widget or display.
	 * New macro can be added or replaced by 
	 * <code>MacrosInput.put(String macroName, String macroValue);</code> 
	 * @param include_parent_macros If parent macros should be included.
	 * @return a new created MacrosInput.
	 */
	public final static MacrosInput createMacrosInput(boolean include_parent_macros){
		return new MacrosInput(new LinkedHashMap<String, String>(), include_parent_macros);
	}	
}
