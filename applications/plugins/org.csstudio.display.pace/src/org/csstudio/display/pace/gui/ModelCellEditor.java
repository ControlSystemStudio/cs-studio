package org.csstudio.display.pace.gui;

import org.csstudio.display.pace.model.Instance;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

/** Editor for a cell in the Model, allowing users to enter values
 *  which are meant to replace the PV's value
 *  //TODO add cause of replace ... after save to elog ...
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

    /** Update model with entered value
     *  @param element Should be Instance because of ModelInstanceProvider
     *  @param value Should be String because of getCellEditor()
     */
    // TODO Clarify "update model", that is doesn't yet store/save the "entered value"
    @Override
    protected void setValue(final Object element, final Object value)
    {
        final Instance instance = (Instance) element;
        instance.getCell(cell_index).setUserValue((String) value);
    }
}
