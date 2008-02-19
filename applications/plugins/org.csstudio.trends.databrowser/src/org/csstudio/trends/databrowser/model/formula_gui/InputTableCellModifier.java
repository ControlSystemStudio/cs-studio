package org.csstudio.trends.databrowser.model.formula_gui;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.FormulaInput;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;

/** Table cell modifier for tables with InputTableItem data.
 *  @author Kay Kasemir
 */
public class InputTableCellModifier implements ICellModifier
{
    private final TableViewer viewer;
    
    InputTableCellModifier(TableViewer viewer)
    {
        this.viewer = viewer;
    }
    
	/** Variable name can change. */
	public boolean canModify(Object element, String col_title)
	{
        return col_title.equals(InputTableHelper.Column.VARIABLE.getTitle());
    }

	/** @return Returns the original cell value. */
    public Object getValue(Object element, String col_title)
    {
        FormulaInput entry = (FormulaInput) element;
        try
        {
            InputTableHelper.Column col = InputTableHelper.findColumn(col_title);
            return InputTableHelper.getText(entry, col);
        }
        catch (Exception ex)
        {
            Plugin.getLogger().error("Error", ex); //$NON-NLS-1$
        }
        return null;
    }

	/** Editor finished and tries to update element's property. */
	public void modify(Object element, final String property,
	        final Object value)
    {
        if (value == null)
            return;
        if (!property.equals(InputTableHelper.Column.VARIABLE.getTitle()))
            return;
        try
        {   // Note that it is possible for an SWT Item to be passed
            // instead of the model element.
            if (element instanceof Item)
                element = ((Item) element).getData();
            
            // Edit existing item
            final String new_var_name = (String) value;
            final FormulaInput entry = (FormulaInput) element;
            entry.setVariable(new_var_name);
            viewer.refresh();
        }
        catch (Exception ex)
        {
            Plugin.getLogger().error("Error", ex); //$NON-NLS-1$
        }
    }
}
