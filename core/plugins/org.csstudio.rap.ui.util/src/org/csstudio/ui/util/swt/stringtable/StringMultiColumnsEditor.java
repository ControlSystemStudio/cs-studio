/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.swt.stringtable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

/** Editor for table with multiple columns (List<String[]>)
 *  @author Xihui Chen
 */
class StringMultiColumnsEditor extends EditingSupport {
    final private TableViewer table_viewer;
	final private int columnNo;
	final private int numOfColumns;

	public StringMultiColumnsEditor(final TableViewer viewer,
			final int numOfColumns, final int columnNo) {
		super(viewer);
		this.table_viewer = viewer;
		this.columnNo = columnNo;
		this.numOfColumns = numOfColumns;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		final Table parent = (Table) getViewer().getControl();
		return new TextCellEditor(parent);
	}

	@SuppressWarnings("unchecked")
    @Override
	protected Object getValue(Object element) {

		if (element == StringTableContentProvider.ADD_ELEMENT)
			return ""; //$NON-NLS-1$
		final int index = ((Integer)element).intValue();
		final List<String[]> items = (List<String[]>) table_viewer.getInput();
		return items.get(index)[columnNo];
	}

    @SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object element, Object value) {
        final List<String[]> items = (List<String[]>) table_viewer.getInput();
		String[] rowData;
		if (element == StringTableContentProvider.ADD_ELEMENT)
		{
			rowData = new String[numOfColumns];
			Arrays.fill(rowData, ""); //$NON-NLS-1$
			rowData[columnNo] = value.toString();
			items.add(rowData);
			getViewer().refresh();
			return;
		}
		// else
		final int index = ((Integer)element).intValue();
		rowData = items.get(index);
		rowData[columnNo] = value.toString();
		items.set(index, rowData);
		getViewer().refresh(element);
	}
}
