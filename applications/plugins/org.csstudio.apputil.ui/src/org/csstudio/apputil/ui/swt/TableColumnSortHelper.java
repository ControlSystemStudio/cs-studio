/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.swt;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/** Helper for sorting tables via clicks on the table column header.
 * 
 *  <p>When clicking on the table header, it is updated
 *  to include a sort indicator, toggling between
 *  an 'up' or 'down' sort order.
 *  
 *  <p>Abstract class requires a <code>compare</code> function
 *  for basic comparison of table model items.
 *  The up/down is handled in this class.
 *  
 *  <p>This helper can be used for {@link TableViewer} based
 *  tables that support sorting in the viewer.
 *  When using {@link SWT#VIRTUAL} tables,
 *  the sorting needs to be performed inside the model.
 *  
 *  @author Kay Kasemir
 *
 *  @param <E> Data type used for rows in the table
 */
public abstract class TableColumnSortHelper<E>
	extends SelectionAdapter
{
	final private TableViewer viewer;
    final private TableColumn column;
    private boolean up = true; // Initial sort: up
    
    /** Initialize
     *  @param viewer {@link TableViewer}
     *  @param column {@link TableViewerColumn} of column to sort
     */
	public TableColumnSortHelper(final TableViewer viewer,
            final TableViewerColumn column)
    {
		this.viewer = viewer;
		this.column = column.getColumn();
		this.column.addSelectionListener(this);
    }

    /** React to column selection
     *  @param event Not used
     */
	@Override
    public void widgetSelected(final SelectionEvent e)
    {
        final Table table = viewer.getTable();
        
        // Was this column already used for sorting?
        if (table.getSortColumn() == column)
        {   // toggle
        	up = table.getSortDirection() != SWT.UP;
        }
        table.setSortDirection(up ? SWT.UP : SWT.DOWN);
        table.setSortColumn(column);
        
        viewer.setComparator(new ViewerComparator()
        {
			@SuppressWarnings("unchecked")
            @Override
            public int compare(final Viewer viewer,
            		final Object object1, final Object object2)
            {
				final E item1 = (E)object1;
				final E item2 = (E)object2;
				final int cmp = TableColumnSortHelper.this.compare(item1, item2);
                return up ? cmp : -cmp;
            }
        });
    }

	/** To be provided by implementing class
	 *  @param item1 One of the items to compare
	 *  @param item2 item to compare against
	 *  @return -1, 0 or 1 depending on comparison
	 */
	abstract protected int compare(E item1, E item2);
}
