package org.csstudio.display.pvtable.ui;


import org.csstudio.display.pvtable.model.PVListEntry;
import org.csstudio.display.pvtable.model.PVListModel;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;

/** Editor helper for PVTable cells
 *  @author Kay Kasemir
 */
public class PVTableCellModifier implements ICellModifier
{
    private final PVListModel pv_list;
    
    /** Create a CellModifier for the PV Table and attach it to the TableViewer
     *  @param table_viewer The TableViewer to which to attach. 
     *  @param pv_list The model on which this cell editor operates.
     */
    public PVTableCellModifier(TableViewer table_viewer, PVListModel pv_list)
    {
        this.pv_list = pv_list;
        CellEditor editors[] = new CellEditor[PVTableHelper.properties.length];
        Table table = table_viewer.getTable();
        // Clarification:
        // I was expecting to see a checkbox-type display item
        // and editor when using the CheckboxCellEditor.
        // Doesn't happen. One needs a label provoder that returns
        // for example a checked resp. un-checked image for the 
        // two states of the item.
        // The CheckboxCellEditor simply expects data of type Boolean,
        // and turns mouse clicks into modify() calls with a new Boolean.
        // It doesn't help at all with the display (label provider).
        for (int i=0; i<PVTableHelper.properties.length; ++i)
        {
            if (i == PVTableHelper.SELECT)
                editors[i] = new CheckboxCellEditor(table);
            else
                editors[i] = new TextCellEditor(table);
        }
        
        // They use 'properties', not column numbers, to identify a column.
        table_viewer.setColumnProperties(PVTableHelper.properties);
        table_viewer.setCellEditors(editors);       
        table_viewer.setCellModifier(this);
    }

    /** @return Returns true for cells that may be edited */
    public boolean canModify(Object element, String property)
    {
        try
        {
            int id = PVTableHelper.getPropertyID(property);
            if (element == PVTableViewerHelper.empty_row)
                return id == PVTableHelper.NAME;
            return id == PVTableHelper.SELECT  ||  id == PVTableHelper.READBACK;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /** @returns Returns the string for the given property. */
    public Object getValue(Object element, String property)
    {
        try
        {
            if (element == PVTableViewerHelper.empty_row)
                return ""; //$NON-NLS-1$
            return PVTableHelper.getProperty((PVListEntry)element, property);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }        
        return null;
    }

    /** Set property of element to new value. */
    public void modify(Object element, String property, Object value)
    {
        // Did some validator reject the value?
        if (value == null)
            return;
        
        // Javadoc from ICellModifier:
        // Note that it is possible for an SWT Item to be passed instead of 
        // the model element. To handle this case in a safe way, ...
        if (element instanceof Item)
            element = ((Item) element).getData();
        if (element == PVTableViewerHelper.empty_row)
        {
            pv_list.addPV(value.toString());
            return;
        }
        PVListEntry entry = (PVListEntry) element;
        try
        {
            int id = PVTableHelper.getPropertyID(property);
            if (id == PVTableHelper.SELECT)
                pv_list.setSelected(entry, ((Boolean) value).booleanValue());
            if (id == PVTableHelper.READBACK)
                pv_list.modifyReadbackPV(entry, (String)value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
