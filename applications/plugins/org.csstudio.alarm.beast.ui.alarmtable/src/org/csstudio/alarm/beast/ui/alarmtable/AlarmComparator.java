/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.Comparator;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableLabelProvider.ColumnInfo;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;

/** Comparator (= table sorter) that compares one column of an alarm.
 *  @author Kay Kasemir
 */
public class AlarmComparator implements Comparator<AlarmTreePV>
{
	/** Create comparator for AlarmTreePV entries
	 * 
	 *  @param col_info What to use for the comparison
	 *  @param up Up or downward sort?
	 *  @return Comparator<AlarmTreePV>
	 */
    public static Comparator<AlarmTreePV> getComparator(final ColumnInfo col_info, final boolean up)
    {
    	switch (col_info)
        {
        case CURRENT_SEVERITY:
        	return new AlarmComparator(up)
			{
				@Override
				protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
				{
					final int level1 = pv1.getCurrentSeverity().ordinal();
					final int level2 = pv2.getCurrentSeverity().ordinal();
		            if (level1 == level2)
		                return super.doCompare(pv1, pv2);
		            return level1 - level2;
				}
			};
        case SEVERITY:
        	return new AlarmComparator(up)
			{
				@Override
				protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
				{
					final int level1 = pv1.getSeverity().ordinal();
					final int level2 = pv2.getSeverity().ordinal();
		            if (level1 == level2)
		                return super.doCompare(pv1, pv2);
		            return level1 - level2;
				}
			};
        case STATUS:
        	return new AlarmComparator(up)
			{
				@Override
				protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
				{
		            final int cmp = pv1.getMessage().compareTo(pv2.getMessage());
		            if (cmp != 0)
		            	return cmp;
	                return super.doCompare(pv1, pv2);
				}
			};
        case DESCRIPTION:
        	return new AlarmComparator(up)
			{
				@Override
				protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
				{
		            final int cmp = pv1.getDescription().compareTo(pv2.getDescription());
		            if (cmp != 0)
		            	return cmp;
	                return super.doCompare(pv1, pv2);
				}
			};
        case TIME:
        	return new AlarmComparator(up)
			{
				@Override
				protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
				{
					ITimestamp time1 = pv1.getTimestamp();
					ITimestamp time2 = pv2.getTimestamp();
					if (time1 == null)
						time1 = TimestampFactory.createTimestamp(0, 0);
					if (time2 == null)
						time2 = TimestampFactory.createTimestamp(0, 0);
					final int cmp = time1.compareTo(time2);
		            if (cmp != 0)
		            	return cmp;
	                return super.doCompare(pv1, pv2);
				}
			};
        default:
        	return new AlarmComparator(up);
        }
    }

    final private boolean up;
    
    /** Initialize
     *  @param up Sort 'up' or 'down'?
     */
    private AlarmComparator(final boolean up)
    {
        this.up = up;
    }

    /** {@inhericDoc} */
    @Override
    public int compare(final AlarmTreePV pv1, final AlarmTreePV pv2)
    {
    	if (up)
    		return doCompare(pv1, pv2);
    	else
    		return -doCompare(pv1, pv2);
    }

    /** Compare PVs in 'up' order
     * 
     *  Default compares by name, derived class can override.
     *  @param pv1
     *  @param pv2
     *  @return comparison -1, 0, 1
     */
    protected int doCompare(AlarmTreePV pv1, AlarmTreePV pv2)
    {
    	final String prop1 = pv1.getName();
        final String prop2 = pv2.getName();
        return prop1.compareTo(prop2);
    }
}
