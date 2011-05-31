/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.io.Serializable;
import java.util.Comparator;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableLabelProvider.ColumnInfo;

/** ViewerComparator (= table sorter) that compares one column of an alarm.
 *  @author Kay Kasemir
 */
public class AlarmComparator implements Comparator<AlarmTreePV>, Serializable
{
    /** Serializable... */
    private static final long serialVersionUID = 1L;

	final private ColumnInfo col_info;
    final private boolean up;

    /** Initialize
     *  @param col_info ColumnInfo that selects what to compare
     *  @param up Sort 'up' or 'down'?
     */
    public AlarmComparator(final ColumnInfo col_info, final boolean up)
    {
        this.col_info = col_info;
        this.up = up;
    }

    /** {@inhericDoc} */
    @Override
    public int compare(final AlarmTreePV pv1, final AlarmTreePV pv2)
    {
        switch (col_info)
        {
        case CURRENT_SEVERITY:
        {
            int level1 = pv1.getCurrentSeverity().ordinal();
            int level2 = pv2.getCurrentSeverity().ordinal();
            if (level1 == level2)
                return compareByName(pv1, pv2);
            if (up)
                return level2 - level1;
            return level1 - level2;
        }
        case SEVERITY:
        {
            int level1 = pv1.getSeverity().ordinal();
            int level2 = pv2.getSeverity().ordinal();
            if (level1 == level2)
                return compareByName(pv1, pv2);
            if (up)
                return level2 - level1;
            return level1 - level2;
        }
        case STATUS:
            return compareProperties(pv1, pv2, pv1.getMessage(), pv2.getMessage());
        case TIME:
            return compareProperties(pv1, pv2, pv1.getTimestampText(), pv2.getTimestampText());
        case DESCRIPTION:
            return compareProperties(pv1, pv2, pv1.getDescription(), pv2.getDescription());
        default:
        }
        return compareByName(pv1, pv2);
    }

    /** Compare properties, falling back to name when they're equal
     *  @param pv1
     *  @param pv2
     *  @param prop1
     *  @param prop2
     *  @return comparison -1, 0, 1
     */
    private int compareProperties(final AlarmTreePV pv1, final AlarmTreePV pv2,
                final String prop1, final String prop2)
    {
        final int cmp = up ? prop2.compareTo(prop1) : prop1.compareTo(prop2);
        if (cmp == 0)
            return compareByName(pv1, pv2);
        return cmp;
    }

    /** Compare PV names
     *  @param pv1
     *  @param pv2
     *  @return comparison -1, 0, 1
     */
    private int compareByName(final AlarmTreePV pv1, final AlarmTreePV pv2)
    {
        final String prop1 = pv1.getName();
        final String prop2 = pv2.getName();
        if (up)
            return prop2.compareTo(prop1);
        return prop1.compareTo(prop2);
    }
}
