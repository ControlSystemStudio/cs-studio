/*******************************************************************************
 * Copyright (c) 2007 Tom Schindl and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *******************************************************************************/

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.grid.GridItem;

/**
 * A label provider that provides hooks for extra functionality in the {@link Grid}.  This is currently
 * limited to supplying the row header text.
 * <p>
 * <b> Only one from all {@link GridColumnLabelProvider} in a viewer should
 * return a none <code>null</code></b>
 * </p>
 */
public class GridColumnLabelProvider extends ColumnLabelProvider {

	/**
	 * Returns the row header text for this element.
	 * 
	 * @param element
	 *            the model element
	 * @return the text displayed in the row-header or <code>null</code> if
	 *         this label provider would not like to modify the default text
	 */
	public String getRowHeaderText(Object element) {
		return null;
	}

	public void update(ViewerCell cell) {
		super.update(cell);
		String rowText = getRowHeaderText(cell.getElement());

		if (rowText != null) {
			((GridItem) cell.getViewerRow().getItem()).setHeaderText(rowText);
		}
	}

}
