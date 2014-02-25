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
 *    Mark-Oliver Reiser <mopr1@web.de>         - support for differing row heights ; fix in bug 191216
 *******************************************************************************/ 

package org.eclipse.nebula.jface.gridviewer;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * The concrete implementation of the ColumnViewer for the grid.
 */
public final class GridViewerColumn extends ViewerColumn 
{
    /** This is either a GridTableViewer or a GridTreeViewer. */
    private ColumnViewer viewer;

    /** The concrete grid column that is being represented by the {@code ViewerColumn}.*/
    private GridColumn column;

    /** Editor support for handling check events. */
    private CheckEditingSupport checkEditingSupport;

    /** Listener used to get informed when the colum resizes */
    protected Listener columnResizeListener = null;


    /**
     * Create a new column in the {@link GridTableViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link GridColumn}
     * @see GridColumn#GridColumn(Grid, int)
     */
    public GridViewerColumn(GridTableViewer viewer, int style) 
    {
        this(viewer, style, -1);
    }

    /**
     * Create a new column in the {@link GridTreeViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link GridColumn}
     * @see GridColumn#GridColumn(Grid, int)
     */
    public GridViewerColumn(GridTreeViewer viewer, int style) {
    	 this(viewer, style, -1);
    }
    
    /**
     * Create a new column in the {@link GridTableViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link GridColumn}
     * @param index
     *            the index of the newly created column
     * @see GridColumn#GridColumn(Grid, int, int)
     */
    public GridViewerColumn(GridTableViewer viewer, int style, int index) 
    {
        this(viewer, createColumn((Grid) viewer.getControl(), style, index));
    }
    
    /**
     * Create a new column in the {@link GridTreeViewer}
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param style
     *            the style used to create the column for style bits see
     *            {@link GridColumn}
     * @param index
     *            the index of the newly created column
     * @see GridColumn#GridColumn(Grid, int, int)
     */
    public GridViewerColumn(GridTreeViewer viewer, int style, int index) 
    {
        this(viewer, createColumn((Grid) viewer.getControl(), style, index));
    }

    /**
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param column
     *            the column the viewer is attached to
     */
    public GridViewerColumn(GridTreeViewer viewer, GridColumn column) 
    {
    	this((ColumnViewer)viewer,column);
    }
    
    /**
     * 
     * @param viewer
     *            the viewer the column belongs to
     * @param column
     *            the column the viewer is attached to
     */
    public GridViewerColumn(GridTableViewer viewer, GridColumn column) 
    {
        this((ColumnViewer)viewer,column);
    }
    
    GridViewerColumn(ColumnViewer viewer, GridColumn column) {
    	super(viewer, column);
    	this.viewer = viewer;
        this.column = column;
        hookColumnResizeListener();
    }
    
    private static GridColumn createColumn(Grid table, int style, int index) 
    {
        if (index >= 0) 
        {
            return new GridColumn(table, style, index);
        }

        return new GridColumn(table, style);
    }

    /**
     * Returns the underlying column.
     * 
     * @return the underlying Nebula column
     */
    public GridColumn getColumn() 
    {
        return column;
    }
    
    /** {@inheritDoc} */
    public void setEditingSupport(EditingSupport editingSupport)
    {
        if (editingSupport instanceof CheckEditingSupport)
        {
            if (checkEditingSupport == null)
            {
                final int colIndex = getColumn().getParent().indexOf(getColumn());
                
                getColumn().getParent().addListener(SWT.Selection, new Listener()
                {                
                    public void handleEvent(Event event)
                    {                         
                        if (event.detail == SWT.CHECK && event.index == colIndex)
                        {
                            GridItem item = (GridItem)event.item;
                            Object element = item.getData();
                            checkEditingSupport.setValue(element, new Boolean(item.getChecked(colIndex)));
                        }
                    }                
                });
            }
            checkEditingSupport = (CheckEditingSupport)editingSupport;
        }
        else
        {
            super.setEditingSupport(editingSupport);
        }        
    }


    private void hookColumnResizeListener() {
        if (columnResizeListener == null)
        {
            columnResizeListener = new Listener() {
                public void handleEvent(Event event)
                {
                    boolean autoPreferredSize=false;
                    if(viewer instanceof GridTableViewer)
                    	autoPreferredSize = ((GridTableViewer)viewer).getAutoPreferredHeight();
                    if(viewer instanceof GridTreeViewer)
                        autoPreferredSize = ((GridTreeViewer)viewer).getAutoPreferredHeight();

                    if(autoPreferredSize && column.getWordWrap())
                    {
                        Grid grid = column.getParent();
                        for(int cnt=0;cnt<grid.getItemCount();cnt++)
                            grid.getItem(cnt).pack();
                        grid.redraw();
                    }
                }
            };
            column.addListener(SWT.Resize, columnResizeListener);
            column.addListener(SWT.Hide, columnResizeListener);
            column.addListener(SWT.Show, columnResizeListener);
        }
    }
    private void unhookColumnResizeListener() {
        if (columnResizeListener != null)
        {
            column.removeListener(SWT.Resize, columnResizeListener);
            columnResizeListener = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void handleDispose() {
        unhookColumnResizeListener();
        super.handleDispose();
    }
}
