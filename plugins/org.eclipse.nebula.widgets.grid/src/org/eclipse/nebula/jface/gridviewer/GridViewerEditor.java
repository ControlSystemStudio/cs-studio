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
 *    Jake fisher<fisherja@gmail.com>           - fixed minimum height (bug 263489)
 *******************************************************************************/

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

/**
 * FIXME
 */
public class GridViewerEditor extends ColumnViewerEditor {
	/** Editor support for tables. */
    private GridEditor gridEditor;

    /**
     * The selection follows the editor
     */
    public static final int SELECTION_FOLLOWS_EDITOR = 1 << 30;
    
    private boolean selectionFollowsEditor = false;
    
	GridViewerEditor(ColumnViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		super(viewer, editorActivationStrategy, feature);
		this.selectionFollowsEditor = (feature & SELECTION_FOLLOWS_EDITOR) == SELECTION_FOLLOWS_EDITOR;
		this.gridEditor = new GridEditor((Grid) viewer.getControl());
	}

	/**
	 * FIXME
	 * {@inheritDoc}
	 */
    protected void setEditor(Control w, Item item, int fColumnNumber)
    {
        gridEditor.setEditor(w, (GridItem) item, fColumnNumber);
    }

    /**
     * FIXME
     * {@inheritDoc}
     */
    protected void setLayoutData(LayoutData layoutData)
    {
        gridEditor.grabHorizontal = layoutData.grabHorizontal;
        gridEditor.horizontalAlignment = layoutData.horizontalAlignment;
        gridEditor.minimumWidth = layoutData.minimumWidth;
        
		gridEditor.verticalAlignment = layoutData.verticalAlignment;

		if (layoutData.minimumHeight != SWT.DEFAULT) {
			gridEditor.minimumHeight = layoutData.minimumHeight;
		} else {
			gridEditor.minimumHeight = SWT.DEFAULT;
		}
    }

    /**
     * FIXME
     * {@inheritDoc}
     */
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

	/**
	 * {@inheritDoc}
	 */
	protected void updateFocusCell(ViewerCell focusCell, ColumnViewerEditorActivationEvent event) {
		Grid grid = ((Grid)getViewer().getControl());

		if (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC
				|| event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) {
			grid.setFocusColumn(grid.getColumn(focusCell.getColumnIndex()));
			grid.setFocusItem((GridItem) focusCell.getItem());
			
			if( selectionFollowsEditor ) {
				grid.setCellSelection(new Point(focusCell.getColumnIndex(),grid.indexOf((GridItem)focusCell.getItem())));
			}
		}
				
		grid.showColumn(grid.getColumn(focusCell.getColumnIndex()));
		grid.showItem((GridItem) focusCell.getItem()); 
	}

	/**
	 * FIXME
	 * @param viewer
	 * @param editorActivationStrategy
	 * @param feature
	 */
	public static void create(GridTableViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		viewer.setColumnViewerEditor(new GridViewerEditor(viewer,editorActivationStrategy,feature));
	}

	/**
	 * FIXME
	 * @param viewer
	 * @param editorActivationStrategy
	 * @param feature
	 */
	public static void create(GridTreeViewer viewer,
			ColumnViewerEditorActivationStrategy editorActivationStrategy,
			int feature) {
		viewer.setColumnViewerEditor(new GridViewerEditor(viewer,editorActivationStrategy,feature));
	}
}
