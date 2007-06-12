package org.csstudio.trends.databrowser.model.formula_gui;

import org.csstudio.trends.databrowser.Plugin;
import org.csstudio.trends.databrowser.model.FormulaInput;
import org.csstudio.trends.databrowser.model.IModelItem;
import org.csstudio.util.formula.VariableNode;
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
            Plugin.logException("Error", ex); //$NON-NLS-1$
        }
        return null;
    }

	/** Editor finished and tries to update element's property. */
	public void modify(Object element, String property, Object value)
    {
        if (value == null)
            return;
        try
        {   // Note that it is possible for an SWT Item to be passed
            // instead of the model element.
            if (element instanceof Item)
                element = ((Item) element).getData();
            
            // Edit existing item
            FormulaInput entry = (FormulaInput) element;
            if (property.equals(InputTableHelper.Column.VARIABLE.getTitle()))
            {
                final String new_var_name = (String) value;
                // VariableNode has immutable name, so create new input
                // array where that one VariableNode is replaced.
                FormulaInput old_input[] = (FormulaInput []) viewer.getInput();
                FormulaInput new_input[] = new FormulaInput[old_input.length];
                for (int i=0; i<new_input.length; ++i)
                {   
                    final IModelItem item = entry.getModelItem();
                    // Can compare model item pointers, no need to parse name
                    if (old_input[i].getModelItem() == item)
                        new_input[i] = new FormulaInput(item,
                                                new VariableNode(new_var_name));
                    else
                        new_input[i] = old_input[i];
                }
                viewer.setInput(new_input);
                // viewer.refresh(entry);
            }
        }
        catch (Exception ex)
        {
            Plugin.logException("Error", ex); //$NON-NLS-1$
        }
    }
}
