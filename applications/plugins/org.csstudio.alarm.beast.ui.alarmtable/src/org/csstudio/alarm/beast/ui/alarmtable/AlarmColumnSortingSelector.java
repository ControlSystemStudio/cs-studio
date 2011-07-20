/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui.alarmtable;

import org.csstudio.alarm.beast.ui.alarmtable.AlarmTableLabelProvider.ColumnInfo;
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
    final protected TableViewer table_viewer;
    final protected TableColumn column;
    final protected ColumnInfo col_info;

    /** Initialize
     *  @param table_viewer Table viewer
     *  @param column SWT Table column on which to sort
     *  @param col_info Info about the column (which piece of the alarm is in there?)
     */
    public AlarmColumnSortingSelector(final TableViewer table_viewer,
            final TableColumn column, final ColumnInfo col_info)
    {
        this.table_viewer = table_viewer;
        this.column = column;
        this.col_info = col_info;
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
        table.setSortDirection(up ? SWT.UP : SWT.DOWN);
        table.setSortColumn(column);
        
        ((AlarmTableContentProvider)table_viewer.getContentProvider())
            .setComparator(AlarmComparator.getComparator(col_info, up));
    }
}
