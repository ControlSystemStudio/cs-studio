package org.csstudio.trends.databrowser.model.formula_gui;

import org.csstudio.trends.databrowser.model.FormulaInput;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** The JFace label provider for a table with FormulaInput entries. 
 *  @author Kay Kasemir
 */
public class InputTableLabelProvider extends LabelProvider implements
		ITableLabelProvider
{
    /** Get text for all but the 'select' column,
     *  where some placeholder is returned.
     */
	public String getColumnText(Object obj, int index)
	{
        FormulaInput input = (FormulaInput) obj;
        return InputTableHelper.getText(input, index);
	}

    /** {@inheritDoc} */
	public Image getColumnImage(Object obj, int index)
	{
        return null;
	}
}
