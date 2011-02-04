/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.Arrays;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableLabelProvider.ColumnInfo;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Lazy content provider that links model (current alarms) to table viewer.
 *  <p>
 *  The 'input' to the TableViewer used to be the list of alarms to display,
 *  updated via TableViewer.setInput(), as one might find natural.
 *  But that resulted in flicker on Linux.
 *  Now the 'input' to the table is actually null, and <code>setAlarms()</code>
 *  is used to keep track of the alarms to display.
 *
 *  @author Kay Kasemir
 */
public class AlarmTableContentProvider implements ILazyContentProvider
{
    private TableViewer table_viewer;
    private AlarmTreePV[] alarms;
    private AlarmComparator comparator = new AlarmComparator(ColumnInfo.SEVERITY, false);

    /** Update the list of alarms to display.
     *  @param alarms
     */
    public void setAlarms(final AlarmTreePV alarms[])
    {
        this.alarms = alarms;
        if (alarms == null)
            table_viewer.setItemCount(0);
        else
        {
            Arrays.sort(this.alarms, comparator);
            table_viewer.setItemCount(alarms.length);
            table_viewer.refresh();
        }
    }

    /** @return Alarms to be shown in table */
    public AlarmTreePV[] getAlarms()
    {
        return alarms;
    }

    /** @param comparator Comparator that's used to sort alarms */
    public void setComparator(final AlarmComparator comparator)
    {
        this.comparator = comparator;
        // trigger refresh
        if (table_viewer != null)
            setAlarms(alarms);
    }

    /** {@inheritDoc} */
    @Override
    public void inputChanged(final Viewer viewer, final Object old_input, final Object new_input)
    {
        table_viewer = (TableViewer) viewer;
    }

    /** {@inheritDoc} */
    @Override
    public void updateElement(final int row)
    {
        if (row < alarms.length)
            table_viewer.replace(alarms[row], row);
    }

    /** {@inheritDoc} */
    @Override
    public void dispose()
    {
        // Nothing to dispose
    }
 }
