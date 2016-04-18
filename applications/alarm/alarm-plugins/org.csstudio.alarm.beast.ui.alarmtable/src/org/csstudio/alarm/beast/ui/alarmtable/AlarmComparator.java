/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.time.Instant;
import java.util.Comparator;

import org.csstudio.alarm.beast.AnnunciationFormatter;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.client.GDCDataStructure;

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
        case PV:
            return new AlarmComparator(up)
            {
                @Override
                protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
                {
                    final String prop1 = pv1.getName();
                    final String prop2 = pv2.getName();
                    final int cmp = prop1.compareTo(prop2);
                    return cmp != 0  ?  cmp  :  super.doCompare(pv1, pv2);
                }
            };
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
        case ICON:
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
        case CURRENT_STATUS:
            return new AlarmComparator(up)
            {
                @Override
                protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
                {
                    final int cmp = pv1.getCurrentMessage().compareTo(pv2.getCurrentMessage());
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
                    final String desc1 = AnnunciationFormatter.format(pv1.getDescription(),
                            pv1.getSeverity().getDisplayName(), pv1.getValue(), true);
                    final String desc2 = AnnunciationFormatter.format(pv2.getDescription(),
                            pv2.getSeverity().getDisplayName(), pv2.getValue(), true);
                    final int cmp = desc1.compareTo(desc2);
                    return cmp != 0  ?  cmp  :  super.doCompare(pv1, pv2);
                }
            };
        case ACK:
            return new AlarmComparator(up)
            {
                @Override
                protected int doCompare(AlarmTreePV pv1, AlarmTreePV pv2)
                {
                    boolean active1 = pv1.getSeverity().isActive();
                    boolean active2 = pv2.getSeverity().isActive();
                    if (active1 == active2)
                        return super.doCompare(pv1, pv2);
                    return active1 ? -1 : 1;
                }
            };
        case VALUE:
            return new AlarmComparator(up)
            {
                @Override
                protected int doCompare(AlarmTreePV pv1, AlarmTreePV pv2)
                {
                    if (pv1.getValue() != null && pv2.getValue() != null)
                    {
                        final int cmp = pv1.getValue().compareTo(pv2.getValue());
                        if (cmp != 0)
                            return cmp;
                    }
                    else if ((pv1.getValue() != null && pv2.getValue() == null)
                            || (pv1.getValue() == null && pv2.getValue() != null))
                    {
                        return pv1.getValue() == null ? 1 : -1;
                    }
                    return super.doCompare(pv1, pv2);
                }
            };
        case ACTION:
            return new AlarmComparator(up)
            {
                @Override
                protected int doCompare(AlarmTreePV pv1, AlarmTreePV pv2)
                {
                    final int cmp = compareGuidance(pv1, pv2, false);
                    if (cmp != 0)
                        return cmp;
                    return super.doCompare(pv1, pv2);
                }
            };
        case ID:
            return new AlarmComparator(up)
            {
                @Override
                protected int doCompare(AlarmTreePV pv1, AlarmTreePV pv2)
                {
                    final int cmp = compareGuidance(pv1, pv2, true);
                    if (cmp != 0)
                        return cmp;
                    return super.doCompare(pv1, pv2);
                }
            };
        case TIME:
        default:
            return new AlarmComparator(up);
        }
    }

    private static int compareGuidance(AlarmTreePV pv1, AlarmTreePV pv2, boolean title)
    {
        GDCDataStructure[] g1 = pv1.getGuidance();
        GDCDataStructure[] g2 = pv2.getGuidance();
        if (g1.length != 0 && g2.length != 0)
        {
            int cmp = 0;
            if (title)
                cmp = g1[0].getTitle().toLowerCase().compareTo(g2[0].getTitle().toLowerCase());
            else
                cmp = g1[0].getDetails().toLowerCase().compareTo(g2[0].getDetails().toLowerCase());

            if (cmp != 0)
                return cmp;
        }
        else if (g1.length == 0 && g2.length != 0)
            return 1;
        else if (g1.length != 0 && g2.length == 0)
            return -1;
        return 0;
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
    protected int doCompare(final AlarmTreePV pv1, final AlarmTreePV pv2)
    {
        Instant time1 = pv1.getTimestamp();
        Instant time2 = pv2.getTimestamp();
        if (time1 == null)
            time1 = Instant.ofEpochSecond(0);
        if (time2 == null)
            time2 = Instant.ofEpochSecond(0);
        final int cmp = time1.compareTo(time2);
        if (cmp != 0)
            return cmp;
        final String prop1 = pv1.getName();
        final String prop2 = pv2.getName();
        return prop1.compareTo(prop2);
    }
}
