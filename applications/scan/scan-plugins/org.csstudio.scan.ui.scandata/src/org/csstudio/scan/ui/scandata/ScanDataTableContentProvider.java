/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import java.util.List;

import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/** Content provider for table with {@link List} of {@link ScanDataRow}s
 *  @author Kay Kasemir
 */
public class ScanDataTableContentProvider implements ILazyContentProvider
{
	private TableViewer table_viewer = null;

	private List<ScanDataRow> rows = null;

	@SuppressWarnings("unchecked")
    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, Object input)
    {
		table_viewer = (TableViewer) viewer;

		if (input instanceof List<?>)
		{
			rows = (List<ScanDataRow>) input;
			table_viewer.setItemCount(rows.size());
		}
		else
			table_viewer.setItemCount(0);
    }

	@Override
    public void updateElement(final int index)
    {
		table_viewer.replace(rows.get(index), index);
    }

	@Override
    public void dispose()
    {
	    // NOP
    }
}
