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

/** Label provider that transforms Integer index into list
 *  into the string to display.
 *  @author Kay Kasemir
 */
class StringColumnLabelProvider extends CellLabelProvider
{
	final private TableViewer viewer;

	/** Initialize
	 *  @param items It
	 */
	public StringColumnLabelProvider(final TableViewer viewer)
	{
		this.viewer = viewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(final ViewerCell cell)
	{
		final List<String> items = (List<String>)viewer.getInput();
		final int index = ((Integer)cell.getElement()).intValue();
		if (index < 0)
			cell.setText(Messages.StringTableEditor_AddRowText);
		else
			cell.setText(items.get(index));
	}
}
