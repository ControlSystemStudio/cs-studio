/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.VNumber;
import org.epics.pvmanager.data.VString;
import org.epics.pvmanager.data.VType;

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

	
	/** Compare the values of to {@link VType}s
	 *  @param value {@link VType}
	 *  @param other {@link VType}
	 *  @param tolerance Numeric tolarance
	 *  @return <code>true</code> if their value (not timestamp, not alarm state) are equal
	 */
	final public static boolean equalValue(final VType value, final VType other, final double tolerance)
	{
		if (value instanceof VString)
			return toString(value).equals(toString(other));
		final double v1 = toDouble(value);
		final double v2 = toDouble(other);
		return Math.abs(v2 - v1) <= tolerance;
	}
}
