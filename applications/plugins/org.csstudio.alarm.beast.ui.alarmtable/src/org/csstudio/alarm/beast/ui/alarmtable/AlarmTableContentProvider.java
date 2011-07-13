/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import java.util.Arrays;
import java.util.Comparator;

import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableLabelProvider.ColumnInfo;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;

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
	final private int alarm_table_row_limit = Preferences.getAlarmTableRowLimit();
    private TableViewer table_viewer;
    private AlarmTreePV[] alarms;
    private Comparator<AlarmTreePV> comparator = AlarmComparator.getComparator(ColumnInfo.SEVERITY, false);
	
    /** Update the list of alarms to display.
     *  @param alarms
     */
    public void setAlarms(final AlarmTreePV alarms[])
    {
        if (alarms == null)
        {
        	this.alarms = null;
            table_viewer.setItemCount(0);
        }
        else if (alarms.length > alarm_table_row_limit)
    	{	// Use only a subset of actual alarms
    		this.alarms = new AlarmTreePV[alarm_table_row_limit + 1];
    		System.arraycopy(alarms, 0, this.alarms, 0, alarm_table_row_limit);
    		// Add explanatory entry to end
    		final AlarmTreePV info = new AlarmTreePV(null, Messages.AlarmTableRowLimitMessage, -1);
    		info.setDescription(NLS.bind(Messages.AlarmTableRowLimitInfoFmt, alarm_table_row_limit));
			this.alarms[alarm_table_row_limit] = info;
			// Sort all but that explanatory entry
            Arrays.sort(this.alarms, 0, alarm_table_row_limit, comparator);
            table_viewer.setItemCount(alarm_table_row_limit + 1);
    	}
    	else
    	{	// Use alarms as received
            this.alarms = alarms;
            Arrays.sort(this.alarms, comparator);
            table_viewer.setItemCount(alarms.length);
    	}
        table_viewer.refresh();
    }

    /** @return Alarms to be shown in table */
    public AlarmTreePV[] getAlarms()
    {
        return alarms;
    }

    /** @param comparator Comparator that's used to sort alarms */
    public void setComparator(final Comparator<AlarmTreePV> comparator)
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
