/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rmcamara@us.ibm.com                       - initial API and implementation
 *    Tom Schindl <tom.schindl@bestsolution.at> - various significant contributions
 *    											  bug fix in: 191216
 *******************************************************************************/

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

public class GridViewerEditor extends ColumnViewerEditor {
	/** Editor support for tables. */
    private GridEditor gridEditor;

	GridViewerEditor(ColumnViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		super(viewer, editorActivationStrategy, feature);
		this.gridEditor = new GridEditor((Grid) viewer.getControl());
	}

	protected StructuredSelection createSelection(Object element)
    {
        return new StructuredSelection(element);
    }

    protected void setEditor(Control w, Item item, int fColumnNumber)
    {
        gridEditor.setEditor(w, (GridItem) item, fColumnNumber);
    }

    protected void setLayoutData(LayoutData layoutData)
    {
        gridEditor.grabHorizontal = layoutData.grabHorizontal;
        gridEditor.horizontalAlignment = layoutData.horizontalAlignment;
        gridEditor.minimumWidth = layoutData.minimumWidth;
    }

	public ViewerCell getFocusCell() {
		Grid grid = (Grid)getViewer().getControl();

		if( grid.getCellSelectionEnabled() ) {
			Point p = grid.getFocusCell();

			if( p.x >= 0 && p.y >= 0 ) {
				GridItem item = grid.getItem(p.y);
				if( item != null ) {
					ViewerRow row = getViewerRowFromItem(item);
					return row.getCell(p.x);
				}
			}
		}

		return null;
	}

	private ViewerRow getViewerRowFromItem(GridItem item) {
		if( getViewer() instanceof GridTableViewer ) {
			return ((GridTableViewer)getViewer()).getViewerRowFromItem(item);
		} else {
			return ((GridTreeViewer)getViewer()).getViewerRowFromItem(item);
		}
	}

	protected void updateFocusCell(ViewerCell focusCell, ColumnViewerEditorActivationEvent event) {
		if (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
				|| event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {
			Grid grid = ((Grid)getViewer().getControl());
			grid.deselectAllCells();
			grid.setFocusColumn(grid.getColumn(focusCell.getColumnIndex()));
			grid.setFocusItem((GridItem) focusCell.getItem());
			grid.setCellSelection(new Point(focusCell.getColumnIndex(),grid.indexOf((GridItem) focusCell.getItem())));
		}
	}

	public static void create(GridTableViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		viewer.setColumnViewerEditor(new GridViewerEditor(viewer,editorActivationStrategy,feature));
	}

	public static void create(GridTreeViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		viewer.setColumnViewerEditor(new GridViewerEditor(viewer,editorActivationStrategy,feature));
	}
}
