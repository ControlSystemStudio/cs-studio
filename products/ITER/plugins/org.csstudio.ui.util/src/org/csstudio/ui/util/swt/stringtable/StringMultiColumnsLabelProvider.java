/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.util.swt.stringtable;

import java.util.List;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;

/** Label provider that transforms Integer index into corresponding
 * string in table data to display.
 *  @author Xihui Chen
 */
class StringMultiColumnsLabelProvider extends CellLabelProvider {
	final private TableViewer tableViewer;
	final private boolean editable;

	/**
	 * @param tableViewer
	 * @param editable whether this column is editable
	 */
	public StringMultiColumnsLabelProvider(final TableViewer tableViewer,
			final boolean editable) {
		super();
		this.tableViewer = tableViewer;
		this.editable = editable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(ViewerCell cell) {
		final List<String[]> items = (List<String[]>)tableViewer.getInput();
		final int index = ((Integer) cell.getElement()).intValue();
		//if this is the extra row
		if (index < 0)
			if(editable)
				cell.setText(Messages.StringTableEditor_AddRowText);
			else
				cell.setText(""); //$NON-NLS-1$
		//if not
		else
		{
		    // For multi-line text, only show the first line
			final int column = cell.getColumnIndex();
            String text = items.get(index)[column];
			// Not sure whether to look for '\r' or '\n'. Try both
			int nl = text.indexOf('\r');
			if (nl < 0)
			    nl = text.indexOf('\n');
			if (nl > 0)
			    text = text.substring(0, nl) + "..."; //$NON-NLS-1$
            cell.setText(text);
		}
	}
}
