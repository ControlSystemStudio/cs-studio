/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Houston <schmeeky@gmail.com> - initial API and implementation
 *    Tom Schindl <tom.schindl@bestsolution.at> - bug fix in: 191216
 *******************************************************************************/ 
package org.eclipse.nebula.jface.gridviewer;

import java.util.List;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Widget;

/**
 * A concrete viewer based on an Grid control.
 * <p>
 * This class is not intended to be subclassed outside the viewer framework. It
 * is designed to be instantiated with a pre-existing Grid control and
 * configured with a domain-specific content provider, label provider, element
 * filter (optional), and element sorter (optional).
 * <p>
 * Content providers for grid tree viewers must implement the
 * {@link ITreeContentProvider} interface.
 * <p><b>The current implementation does not support lazy content providers.</b></p>
 */
public class GridTreeViewer extends AbstractTreeViewer {
	
	/** This viewer's grid control. */
	private Grid grid;
	
	private GridViewerRow cachedRow;

	/**
	 * If true, this grid viewer will ensure that the grid's
	 * rows / GridItems are always sized to their preferred height.
	 */
	private boolean autoPreferredHeight = false;


	/**
     * Creates a grid tree viewer on a newly-created grid control under the given
     * parent. The grid control is created using the SWT style bits
     * <code>MULTI, H_SCROLL, V_SCROLL,</code> and <code>BORDER</code>. The
     * viewer has no input, no content provider, a default label provider, no
     * sorter, and no filters.
     * 
     * @param parent 
     * 				the parent control
     */
	public GridTreeViewer(Composite parent) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}

	/**
     * Creates a grid tree viewer on a newly-created grid control under the given
     * parent. The grid control is created using the given SWT style bits. The
     * viewer has no input, no content provider, a default label provider, no
     * sorter, and no filters.
     * 
     * @param parent 
     * 				the parent control
     * @param style 
     * 				the SWT style bits used to create the grid.
     */
	public GridTreeViewer(Composite parent, int style) {
		this(new Grid(parent, style));
	}

	/**
     * Creates a grid tree viewer on the given grid control. The viewer has no
     * input, no content provider, a default label provider, no sorter, and no
     * filters.
     * 
     * @param grid 
     * 				the grid control
     */
	public GridTreeViewer(Grid grid) {
		this.grid = grid;
		hookControl(grid);
	}

	/**
     * Returns the underlying {@link Grid} Control. 
     * 
     * @return grid control.
     */
	public Grid getGrid() {
		return grid;
	}
	
	/** {@inheritDoc} */
	protected Item getItemAt(Point point) {
		return grid.getItem(point);
	}

	/** {@inheritDoc} */
	protected ColumnViewerEditor createViewerEditor() {
		return new GridViewerEditor(this,
				new ColumnViewerEditorActivationStrategy(this),
				ColumnViewerEditor.DEFAULT);
	}

	/** {@inheritDoc} */
	protected void addTreeListener(Control control, TreeListener listener) {
		((Grid) control).addTreeListener(listener);
	}

	/** {@inheritDoc} */
	protected Item[] getChildren(Widget o) {
		if (o instanceof GridItem) {
			return ((GridItem) o).getItems();
		}
		if (o instanceof Grid) {
			return ((Grid) o).getRootItems();
		}
		return null;
	}
	
	/** {@inheritDoc} */
	protected boolean getExpanded(Item item) {
		return ((GridItem) item).isExpanded();
	}

	/** {@inheritDoc} */
	protected int getItemCount(Control control) {
		return ((Grid) control).getItemCount();
	}

	/** {@inheritDoc} */
	protected int getItemCount(Item item) {
		return ((GridItem) item).getItemCount();
	}

	/** {@inheritDoc} */
	protected Item[] getItems(Item item) {
		return ((GridItem) item).getItems();
	}

	/** {@inheritDoc} */
	protected Item getParentItem(Item item) {
		return ((GridItem) item).getParentItem();
	}

	/** {@inheritDoc} */
	protected Item[] getSelection(Control control) {
		return ((Grid) control).getSelection();
	}

	/** {@inheritDoc} */
	protected Item newItem(Widget parent, int style, int index) {
		GridItem item;

		if (parent instanceof GridItem) {
			item = (GridItem) createNewRowPart(getViewerRowFromItem(parent),
					style, index).getItem();
		} else {
			item = (GridItem) createNewRowPart(null, style, index).getItem();
		}

		return item;
	}

	/**
	 * Create a new ViewerRow at rowIndex
	 * 
	 * @param parent 
	 * 				the parent row
	 * @param style 
	 * 				the style bits to use for the new row
	 * @param rowIndex 
	 * 				the index at which the new row should be created under the parent
	 * @return ViewerRow 
	 * 				the new row
	 */
	private ViewerRow createNewRowPart(ViewerRow parent, int style, int rowIndex) {
		if (parent == null) {
			if (rowIndex >= 0) {
				return getViewerRowFromItem(new GridItem(grid, style, rowIndex));
			}
			return getViewerRowFromItem(new GridItem(grid, style));
		}

		if (rowIndex >= 0) {
			return getViewerRowFromItem(new GridItem((GridItem) parent
					.getItem(), SWT.NONE, rowIndex));
		}

		return getViewerRowFromItem(new GridItem((GridItem) parent.getItem(),
				SWT.NONE));
	}
	
	/** {@inheritDoc} */
	protected void removeAll(Control control) {
		((Grid) control).removeAll();
	}

	/** {@inheritDoc} */
	protected void setExpanded(Item item, boolean expand) {
		((GridItem) item).setExpanded(expand);
	}
	
	/** {@inheritDoc} */
	protected void setSelection(List items) {
		Item[] current = getSelection(getGrid());

		// Don't bother resetting the same selection
		if (isSameSelection(items, current)) {
			return;
		}

		GridItem[] newItems = new GridItem[items.size()];
		items.toArray(newItems);
		getGrid().setSelection(newItems);
	}

	/** {@inheritDoc} */
	protected void showItem(Item item) {
		getGrid().showItem((GridItem) item);

	}

	/** {@inheritDoc} */
	public Control getControl() {
		return getGrid();
	}

	/** {@inheritDoc} */
	protected ViewerRow getViewerRowFromItem(Widget item) {
		if (cachedRow == null) {
			cachedRow = new GridViewerRow((GridItem) item);
		} else {
			cachedRow.setItem((GridItem) item);
		}

		return cachedRow;
	}

	/** {@inheritDoc} */
	protected Widget getColumnViewerOwner(int columnIndex) {
		if (columnIndex < 0
				|| (columnIndex > 0 && columnIndex >= getGrid()
						.getColumnCount())) {
			return null;
		}

		if (getGrid().getColumnCount() == 0)// Hang it off the table if it
			return getGrid();

		return getGrid().getColumn(columnIndex);
	}

	/**
	 * Returns the number of columns of this viewer.
	 *
	 * @return the number of columns
	 */
	protected int doGetColumnCount() {
		return grid.getColumnCount();
	}

	/**
	 * When set to true, this grid viewer will ensure that each of
	 * the grid's items is always automatically sized to its preferred
	 * height. The default is false.
	 * <p>
	 * Since this mechanism usually leads to a grid with rows of
	 * different heights and thus to a grid with decreased performance,
	 * it should only be applied if that is intended.  To set the
	 * height of all items to a specific value, use {@link Grid#setItemHeight(int)}
	 * instead.
	 * <p>
	 * When a column with activated word wrapping is resized
	 * by dragging the column resizer, the items are only auto-resized
	 * properly if you use {@link GridViewerColumn} to create the
	 * columns.
	 * <p>
	 * When this method is called, existing rows are not resized to their 
	 * preferred height.  Therefore it is suggested that this method be called
	 * before rows are populated (i.e. before setInput).
	 */
	public void setAutoPreferredHeight(boolean autoPreferredHeight) {
		this.autoPreferredHeight = autoPreferredHeight;
	}

	/**
	 * @return  true if this grid viewer sizes its rows to their
	 *          preferred height
	 * @see #setAutoPreferredHeight(boolean)
	 */
	public boolean getAutoPreferredHeight() {
		return autoPreferredHeight;
	}

	/** {@inheritDoc} */
	protected void doUpdateItem(final Item item, Object element) {
		super.doUpdateItem(item, element);
		if(autoPreferredHeight && !item.isDisposed())
			((GridItem)item).pack();
	}
}
