/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.platform.data;

import java.util.List;

import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.pvmanager.PMObjectValue;
import org.csstudio.opibuilder.pvmanager.PVManagerHelper;


/** Helper for decoding the data in a {@link IValue},
 *  mostly for display purposes. This is extended for compatibility with
 *  PVManager in BOY.
 *  @author Kay Kasemir, Xihui Chen
 */
public class ValueUtil
{
    /** @return Array length of the value. <code>1</code> for scalars. */
    public static int getSize(final IValue value)
    {
    	if(value instanceof PMObjectValue){
    		return PVManagerHelper.getSize(((PMObjectValue)value).getLatestValue());
    	}
    	
        return org.csstudio.data.values.ValueUtil.getSize(value);
    }

    /** Try to get a double number from the Value.
     *  <p>
     *  Some applications only deal with numeric data,
     *  so they want to interprete integer, enum and double values
     *  all the same.
     *  @param value The value to decode.
     *  @return A double, or <code>Double.NaN</code> in case the value type
     *          does not decode into a number, or
     *          <code>Double.NEGATIVE_INFINITY</code> if the value's severity
     *          indicates that there happens to be no useful value.
     */
    public static double getDouble(final IValue value)
    {
        return getDouble(value, 0);
    }

    /** Try to get a double-typed array element from the Value.
     *  @param value The value to decode.
     *  @param index The array index, 0 ... getSize()-1.
     *  @see #getSize(Value)
     *  @see #getDouble(Value)
     *  @return A double, or <code>Double.NaN</code> in case the value type
     *          does not decode into a number, or
     *          <code>Double.NEGATIVE_INFINITY</code> if the value's severity
     *          indicates that there happens to be no useful value.
     */
    public static double getDouble(final IValue value, final int index)
    {
    	if (value instanceof PMObjectValue){
        	Double v = org.epics.vtype.ValueUtil.numericValueOf(
        			((PMObjectValue)value).getLatestValue());
        	if(v==null)
        		return Double.NaN;
        	return v;
    	}
    	return org.csstudio.data.values.ValueUtil.getDouble(value, index);
       
    }

    /** Try to get a double-typed array from the Value.
     *  @param value The value to decode.
     *  @see #getSize(Value)
     *  @see #getDouble(Value)
     *  @return A double array, or an empty double array in case the value type
     *          does not decode into a number, or if the value's severity
     *          indicates that there happens to be no useful value.
     */
    public static double[] getDoubleArray(final IValue value)
    {
    	if(value instanceof PMObjectValue){
        	Object obj = ((PMObjectValue)value).getLatestValue();
        	return PVManagerHelper.getDoubleArray(obj);
    	}
     
        return org.csstudio.data.values.ValueUtil.getDoubleArray(value);
    }
    
    /**Get all buffered double values for PVManager value.
     * @param value
     * @return
     */
    public static double[] getAllBufferedDoubles(PMObjectValue value){
    	List<Object> allValues = value.getAllValues();
    	if(allValues!=null){
    		double[] result = new double[allValues.size()];
    		int i=0;
    		for(Object obj : allValues){
    			result[i++] = org.epics.vtype.ValueUtil.numericValueOf(obj);
    		}
    		return result;
    	}else
    		return new double[]{getDouble(value)};
    	
    }
    
    /**
     * Converts the given value into a string representation. For string values,
     * returns the value. For numeric (double and long) values, returns a
     * non-localized string representation. Double values use a point as the
     * decimal separator. For other types of values, the value's
     * {@link IValue#format()} method is called and its result returned.
     *
     * @param value
     *            the value.
     * @return a string representation of the value.
     */
    @SuppressWarnings("nls")
    public static String getString(final IValue value)
    {    	
    	if (value instanceof PMObjectValue)
        	return ((PMObjectValue)value).format();
    	return org.csstudio.data.values.ValueUtil.getString(value);
    }
}
