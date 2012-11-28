/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.device;

import java.util.Date;

import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.ScanSampleFactory;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.VNumberArray;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VType;
import org.epics.pvmanager.data.ValueUtil;
import org.epics.util.array.ListNumber;

/** Helper for handling {@link VType} data
 *  @author Kay Kasemir
 */
public class VTypeHelper
{
	/** Format value as string
	 *  @param value {@link VType}
	 *  @return String representation
	 */
	final public static String toString(final VType value)
	{
		if (value instanceof VNumber)
			return ((VNumber)value).getValue().toString();
		if (value instanceof VEnum)
		{
			try
			{
				return ((VEnum)value).getValue();
			}
			catch (ArrayIndexOutOfBoundsException ex)
			{	// PVManager doesn't handle enums that have no label
				return "<enum " + ((VEnum)value).getIndex() + ">";
			}
		}
		if (value instanceof VString)
			return ((VString)value).getValue();
		if (value == null)
			return "null";
		return value.toString();
	}

	/** Get VType as double or NaN if not possible
	 *  @param value {@link VType}
	 *  @return double or NaN
	 */
	final public static double toDouble(final VType value)
	{
		if (value instanceof VNumber)
			return ((VNumber)value).getValue().doubleValue();
		if (value instanceof VEnum)
			return ((VEnum)value).getIndex();
		return Double.NaN;
	}

	/** Get VType as double[]; empty array if not possible
	 *  @param value {@link VType}
	 *  @return double[]
	 */
	public static double[] toDoubles(final VType value)
	{
		final double[] array;
		if (value instanceof VNumberArray)
		{
			final ListNumber list = ((VNumberArray) value).getData();
			array = new double[list.size()];
			for (int i=0; i<array.length; ++i)
				array[i] = list.getDouble(i);
		}
		else
			array = new double[0];
		return array;
	}

	/** Create ScanSample for control system value
     *  @param serial Serial to identify when the sample was taken
     *  @param value {@link VType}
     *  @return {@link ScanSample}
     *  @throws IllegalArgumentException if the value type is not handled
     */
    public static ScanSample createSample(final long serial, final VType value) throws IllegalArgumentException
    {
    	final Date date = getDate(value);
        // Log anything numeric as NumberSample
    	if (value instanceof VNumber)
        	return ScanSampleFactory.createSample(date, serial, ((VNumber) value).getValue());
        else if (value instanceof VNumberArray) 
        {
        	final ListNumber list = ((VNumberArray) value).getData();
        	final Number[] numbers = new Number[list.size()];
        	for (int i=0; i<numbers.length; ++i)
        		numbers[i] = list.getDouble(i);
        	return ScanSampleFactory.createSample(date, serial, numbers);
        }
        else
            // String arrays are not really handled when this is written, but ...
            return ScanSampleFactory.createSample(date, serial, new String[] { toString(value) });
    }

    /** @param value {@link VType}
     *  @return {@link Date}
     */
    private static Date getDate(final VType value)
    {
    	return ValueUtil.timeOf(value).getTimestamp().toDate();
    }
}
