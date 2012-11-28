/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.util.List;

import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.Enum;

/** Helper for dealing with {@link Display}
 *  @author Kay Kasemir
 */
public class MetaDataHelper
{
	private static final double TEST_VALUE = 3.14;

	/** @param display {@link Display}
	 *  @param obj Other {@link Display}
	 *  @return <code>true</code> if both are numerically the same
	 */
	public static boolean equals(final Display display, final Object obj)
	{
		if (display == obj)
			return true;
		if (! (obj instanceof Display))
			return false;
		final Display other = (Display) obj;
		if (! (equals(display.getLowerDisplayLimit(), other.getLowerDisplayLimit())
            && equals(display.getUpperDisplayLimit(), other.getUpperDisplayLimit())
            && equals(display.getLowerCtrlLimit(), other.getLowerCtrlLimit())
            && equals(display.getUpperCtrlLimit(), other.getUpperCtrlLimit())
            && equals(display.getLowerAlarmLimit(), other.getLowerAlarmLimit())
            && equals(display.getUpperAlarmLimit(), other.getUpperAlarmLimit())
            && equals(display.getLowerWarningLimit(), other.getLowerWarningLimit())
            && equals(display.getUpperWarningLimit(), other.getUpperWarningLimit())
            && display.getUnits().equals(other.getUnits())))
            return false;
		
        // Compare formats by result on some test value. Not perfect.
		if (display.getFormat() == null)
			return other.getFormat() == null;
		return display.getFormat().format(TEST_VALUE)
			  .equals(other.getFormat().format(TEST_VALUE));
	}

	/** @param a Double to compare
	 *  @param b Double to compare
	 *  @return true if both are <code>null</code>, or both are numerically equal
	 */
	private static boolean equals(final Double a, final Double b)
	{
		if (a == null)
			return b == null;
		return a.doubleValue() == b.doubleValue();
	}
	
	/** @param labels {@link Enum} labels to compare
	 *  @param other {@link Enum} labels to compare
	 *  @return <code>true</code> if labels are equal
	 */
	public static boolean equals(final List<String> labels, final Object obj)
	{
		if (labels == obj)
			return true;
		if (! (obj instanceof List))
			return false;
		@SuppressWarnings("unchecked")
		final List<String> other = (List<String>) obj;
		return labels.equals(other);
	}
}
