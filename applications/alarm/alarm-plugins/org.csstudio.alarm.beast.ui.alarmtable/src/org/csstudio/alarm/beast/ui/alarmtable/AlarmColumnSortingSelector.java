/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/** Listener to table column selections, will sort alarms by sel. column.
 *  @author Kay Kasemir
 */
public class AlarmColumnSortingSelector extends SelectionAdapter
{
    final private TableViewer table_viewer;
    final private TableColumn column;
    final private ColumnInfo col_info;
    final private TableViewer second_table;
    final private TableColumn secondary_column;

    /** Initialize
     *  @param table_viewer Table viewer
     *  @param second_table the secondary table to sync with the table_viewer
     *  @param column SWT Table column on which to sort
     *  @param secondary_column the column in the secondary table on which to sort
     *  @param col_info Info about the column (which piece of the alarm is in there?)
     */
    public AlarmColumnSortingSelector(final TableViewer table_viewer, final TableViewer second_table,
            final TableColumn column, final TableColumn secondary_column, final ColumnInfo col_info)
    {
        this.table_viewer = table_viewer;
        this.second_table = second_table;
        this.column = column;
        this.col_info = col_info;
        this.secondary_column = secondary_column;
    }

    /** React to column selection
     *  @param event Not used, will actually be <code>null</code> in fake
     *               call during GUI initialization
     */
    @Override
    public void widgetSelected(final SelectionEvent event)
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
        setSortDirection(up);
    }

    public void setSortDirection(final boolean up)
    {
        final Table table = table_viewer.getTable();
        table.setSortDirection(up ? SWT.UP : SWT.DOWN);
        table.setSortColumn(column);
        ((AlarmTableContentProvider)table_viewer.getContentProvider())
            .setComparator(AlarmComparator.getComparator(col_info, up));

        if (second_table != null)
        {
            Table stable = second_table.getTable();
            stable.setSortDirection(up ? SWT.UP : SWT.DOWN);
            stable.setSortColumn(secondary_column);
            ((AlarmTableContentProvider)second_table.getContentProvider())
                .setComparator(AlarmComparator.getComparator(col_info, up));
        }
    }
}
