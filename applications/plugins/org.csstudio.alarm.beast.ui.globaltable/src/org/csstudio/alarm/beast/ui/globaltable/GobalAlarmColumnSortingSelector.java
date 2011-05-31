/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.globaltable;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/** Listener to table column selections, will sort alarms by sel. column.
 *  @author Kay Kasemir
 */
public class GobalAlarmColumnSortingSelector extends SelectionAdapter
{
    final private TableViewer table_viewer;
    final private TableColumn column;
    final private GlobalAlarmColumnInfo col_info;

    public GobalAlarmColumnSortingSelector(final TableViewer table_viewer,
            final TableColumn column, final GlobalAlarmColumnInfo col_info)
    {
        this.table_viewer = table_viewer;
        this.column = column;
        this.col_info = col_info;
    }

    @Override
    public void widgetSelected(SelectionEvent e)
    {
        final Table table = table_viewer.getTable();

        // Initial sort for newly selected columns: up
        boolean up = true;
        // Was this column already used for sorting?
        if (table.getSortColumn() == column)
        {   // toggle
            if (table.getSortDirection() == SWT.UP)
                up = false;
        }
        table.setSortDirection(up ? SWT.UP : SWT.DOWN);
        table.setSortColumn(column);

        final GlobalAlarmComparator comparator = col_info.getComparator();
        comparator.setDirection(up);
        final ViewerComparator old_comparator = table_viewer.getComparator();
        table_viewer.setComparator(comparator);
        // We're re-using the same comparator. setComparator
        // only updates the table when the comparator changes, but
        // since we re-use the same comparator when switching up/down,
        // force a refresh
        if (old_comparator == comparator)
            table_viewer.refresh();
    }
}
