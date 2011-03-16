/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.model.PVListModel;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Provide PVListEntry data for table with stype SWT.VIRTUAL.
 *  @author Kay Kasemir
 */
public class PVTableLazyContentProvider implements ILazyContentProvider
{
	private TableViewer table_viewer;
	private PVListModel pv_list;

	/** Construct content provider for given table viewer. */
	public PVTableLazyContentProvider(TableViewer table_viewer, PVListModel pvlist)
	{
		this.table_viewer = table_viewer;
		this.pv_list = pvlist;
	}

	/** Called by 'lazy' table, needs to 'replace' entry of given row. */
	@Override
    public void updateElement(int row)
	{	// System.out.println("LazyLogContentProvider update row " + row);
        if (row < pv_list.getEntryCount())
            table_viewer.replace(pv_list.getEntry(row), row);
        else
            table_viewer.replace(PVTableViewerHelper.empty_row, row);
	}

	@Override
    public void dispose()
    { /* NOP */ }

	@Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    { /* NOP */ }
}
