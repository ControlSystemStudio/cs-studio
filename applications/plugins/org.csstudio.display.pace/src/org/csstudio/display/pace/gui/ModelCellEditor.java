/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.model.Instance;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

/** Editor for a cell in the Model, allowing users to enter values
 *  which are meant to replace the PV's value with a "user" value
 *  which can later be saved to the PV after ELog entry
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 01/29/09
 */
public class ModelCellEditor extends EditingSupport
{
    /** Cell index within Model's Instance that this editor edits */
    final int cell_index;
    
    /** Initialize
     *  @param table_viewer TableViewer that displays the model
     *  @param cell_index Cell index within a Model's Instance
     */
    public ModelCellEditor(final TableViewer table_viewer, final int cell_index)
    {
        super(table_viewer);
        this.cell_index = cell_index;
    }

    /** Only created for columns that are editable, so always 'yes' */
    @Override
    protected boolean canEdit(final Object element)
    {
        return true;
    }

    /** Edit all cells as Text */
    @Override
    protected CellEditor getCellEditor(final Object element)
    {
        final Table parent = (Table) getViewer().getControl();
        return new TextCellEditor(parent);
    }

    /** @param element Should be Instance because of ModelInstanceProvider
     *  @return Value of cell */
    @Override
    protected Object getValue(final Object element)
    {
        final Instance instance = (Instance) element;
        return instance.getCell(cell_index).getValue();
    }

    /** Set cell's "user" value to entered value
     *  @param element Should be Instance because of ModelInstanceProvider
     *  @param value Should be String because of getCellEditor()
     */
    @Override
    protected void setValue(final Object element, final Object value)
    {
        final Instance instance = (Instance) element;
        instance.getCell(cell_index).setUserValue((String) value);
    }
}
