/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.gui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Abstract base for a Listener to table column selections, sorting the table on
 * a column.
 *
 * @author Kay Kasemir
 */
abstract public class SortingColumnSelector extends SelectionAdapter
{
    final protected TableViewer table_viewer;
    final protected TableColumn column;

    /**
     * Initialize
     *
     * @param table_viewer
     *            Table viewer
     * @param column
     *            Column on which to sort
     */
    public SortingColumnSelector(final TableViewer table_viewer,
            final TableColumn column)
    {
        this.table_viewer = table_viewer;
        this.column = column;
    }

    /**
     * To be implemented by derived class: Sort the table.
     *
     * @param up
     *            Sort 'up' or 'down'?
     */
    abstract protected void sort(boolean up);

    @Override
    public void widgetSelected(final SelectionEvent e)
    {
        final Table table = this.table_viewer.getTable();

        // Initial sort for newly selected columns: up
        boolean up = true;
        // Was this column already used for sorting?
        if (table.getSortColumn() == this.column)
        { // toggle
            if (table.getSortDirection() == SWT.UP)
            {
                up = false;
            }
        }
        table.setSortDirection(up ? SWT.UP : SWT.DOWN);
        table.setSortColumn(this.column);
        sort(up);
    }
}
