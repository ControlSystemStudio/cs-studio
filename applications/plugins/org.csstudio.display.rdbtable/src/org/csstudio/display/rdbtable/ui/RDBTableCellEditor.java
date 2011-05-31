/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.rdbtable.ui;

import org.csstudio.display.rdbtable.model.RDBTableRow;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

/** Editor for a cell in a table that displays RDBTableModel data,
 *  i.e. each row is an RDBTableRow
 *  
 *  @author Kay Kasemir
 */
public class RDBTableCellEditor extends EditingSupport
{
    /** Index of column to edit */
    final private int column;
    
    /** Initialize
     *  @param table_viewer TableViewer that displays the model
     *  @param cell_index Cell index within a Model's Instance
     */
    public RDBTableCellEditor(final TableViewer viewer, final int column)
    {
        super(viewer);
        this.column = column;
    }

    /** The first 'key' column can not be edited
     *  except for rows that were just added and thus
     *  require entry of a key.
     *  @param element Should be RDBTableRow because of RDBTableModelContentProvider
     */
    @Override
    protected boolean canEdit(final Object element)
    {
        if (column == 0)
        {   // Only edit newly added 'key' column, not those read from RDB
            final RDBTableRow row = (RDBTableRow) element;
            return ! row.wasReadFromRDB();
        }
        return true;
    }

    /** Edit all cells as Text
     *  @param element Should be RDBTableRow because of RDBTableModelContentProvider
     */
    @Override
    protected CellEditor getCellEditor(final Object element)
    {
        final Table parent = (Table) getViewer().getControl();
        return new TextCellEditor(parent);
    }

    /** @param element Should be RDBTableRow because of RDBTableModelContentProvider
     *  @return Value of cell, sent to editor
     */
    @Override
    protected Object getValue(final Object element)
    {
        final RDBTableRow row = (RDBTableRow) element;
        return row.getColumn(column);
    }

    /** User entered a new value into a table call
     *  @param element Should be RDBTableRow because of RDBTableModelContentProvider
     *  @param value Should be String because of TextCellEditor
     */
    @Override
    protected void setValue(final Object element, final Object value)
    {
        final RDBTableRow row = (RDBTableRow) element;
        row.setColumn(column, value.toString());
    }
}
