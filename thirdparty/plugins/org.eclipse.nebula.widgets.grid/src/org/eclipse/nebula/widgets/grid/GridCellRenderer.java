/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    chris.gross@us.ibm.com - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.nebula.widgets.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

/**
 * <p>
 * NOTE:  THIS WIDGET AND ITS API ARE STILL UNDER DEVELOPMENT.  THIS IS A PRE-RELEASE ALPHA 
 * VERSION.  USERS SHOULD EXPECT API CHANGES IN FUTURE VERSIONS.
 * </p> 
 * The super class for all grid cell renderers.  Contains the properties specific
 * to a grid cell.
 *
 * @author chris.gross@us.ibm.com
 */
public abstract class GridCellRenderer extends AbstractInternalWidget
{

    private int row = 0;

    private int column = 0;

    private int alignment = SWT.LEFT;

    private boolean tree = false;

    private boolean check = false;

    private boolean rowHover = false;

    private boolean columnHover = false;

    private boolean rowFocus = false;

    private boolean cellFocus = false;
    
    private boolean cellSelected = false;
    
    private boolean wordWrap = false;
    
    private boolean dragging = false;

    /**
     * @return Returns the row.
     */
    public int getRow()
    {
        return row;
    }

    /**
     * @param row The row to set.
     */
    public void setRow(int row)
    {
        this.row = row;
    }

    /**
     * @return Returns the alignment.
     */
    public int getAlignment()
    {
        return alignment;
    }

    /**
     * @param alignment The alignment to set.
     */
    public void setAlignment(int alignment)
    {
        this.alignment = alignment;
    }

    /**
     * @return Returns the check.
     */
    public boolean isCheck()
    {
        return check;
    }

    /**
     * @param check The check to set.
     */
    public void setCheck(boolean check)
    {
        this.check = check;
    }

    /**
     * @return Returns the tree.
     */
    public boolean isTree()
    {
        return tree;
    }

    /**
     * @param tree The tree to set.
     */
    public void setTree(boolean tree)
    {
        this.tree = tree;
    }

    /**
     * @return Returns the column.
     */
    public int getColumn()
    {
        return column;
    }

    /**
     * @param column The column to set.
     */
    public void setColumn(int column)
    {
        this.column = column;
    }

    /**
     * @return Returns the columnHover.
     */
    public boolean isColumnHover()
    {
        return columnHover;
    }

    /**
     * @param columnHover The columnHover to set.
     */
    public void setColumnHover(boolean columnHover)
    {
        this.columnHover = columnHover;
    }

    /**
     * @return Returns the rowHover.
     */
    public boolean isRowHover()
    {
        return rowHover;
    }

    /**
     * @param rowHover The rowHover to set.
     */
    public void setRowHover(boolean rowHover)
    {
        this.rowHover = rowHover;
    }

    /**
     * @return Returns the columnFocus.
     */
    public boolean isCellFocus()
    {
        return cellFocus;
    }

    /**
     * @param columnFocus The columnFocus to set.
     */
    public void setCellFocus(boolean columnFocus)
    {
        this.cellFocus = columnFocus;
    }

    /**
     * @return Returns the rowFocus.
     */
    public boolean isRowFocus()
    {
        return rowFocus;
    }

    /**
     * @param rowFocus The rowFocus to set.
     */
    public void setRowFocus(boolean rowFocus)
    {
        this.rowFocus = rowFocus;
    }

    /**
     * @return the cellSelected
     */
    public boolean isCellSelected()
    {
        return cellSelected;
    }

    /**
     * @param cellSelected the cellSelected to set
     */
    public void setCellSelected(boolean cellSelected)
    {
        this.cellSelected = cellSelected;
    }
    
    /**
     * Returns the bounds of the text in the cell.  This is used when displaying in-place tooltips.
     * If <code>null</code> is returned here, in-place tooltips will not be displayed.  If the 
     * <code>preferred</code> argument is <code>true</code> then the returned bounds should be large
     * enough to show the entire text.  If <code>preferred</code> is <code>false</code> then the 
     * returned bounds should be be relative to the current bounds.
     * 
     * @param item item to calculate text bounds.
     * @param preferred true if the preferred width of the text should be returned.
     * @return bounds of the text.
     */
    public Rectangle getTextBounds(GridItem item, boolean preferred)
    {
        return null;
    }

    /**
     * @return the wordWrap
     */
    public boolean isWordWrap()
    {
        return wordWrap;
    }

    /**
     * @param wordWrap the wordWrap to set
     */
    public void setWordWrap(boolean wordWrap)
    {
        this.wordWrap = wordWrap;
    }
    
    /**
     * Gets the dragging state. 
     *
     * @return Returns the dragging state.
     */
    public boolean isDragging()
    {
    	return dragging;
    }

    /**
     * Sets the dragging state.
     *
     * @param dragging The state to set.
     */
    public void setDragging(boolean dragging)
    {
    	this.dragging = dragging;
    }
}
