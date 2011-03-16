/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.formula;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.apputil.ui.Activator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Item;

/** Table cell modifier for tables with InputTableItem data.
 *  @author Kay Kasemir
 */
public class InputTableCellModifier implements ICellModifier
{
    final private FormulaDialog dialog;
    final private TableViewer viewer;

    InputTableCellModifier(final FormulaDialog dialog, final TableViewer viewer)
    {
        this.dialog = dialog;
        this.viewer = viewer;
    }

	/** Variable name can change. */
	@Override
    public boolean canModify(final Object element, final String col_title)
	{
        return col_title.equals(InputTableHelper.Column.VARIABLE.getTitle());
    }

	/** @return Returns the original cell value. */
    @Override
    public Object getValue(final Object element, final String col_title)
    {
        InputItem entry = (InputItem) element;
        try
        {
            InputTableHelper.Column col = InputTableHelper.findColumn(col_title);
            return InputTableHelper.getText(entry, col);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Activator.ID).log(Level.WARNING, "Formula Input Error", ex); //$NON-NLS-1$
        }
        return null;
    }

	/** Editor finished and tries to update element's property. */
	@Override
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
            final InputItem entry = (InputItem) element;
            entry.setVariableName(new_var_name);
            viewer.refresh(element);
            // Trigger check of the formula
            dialog.parseFormula();
        }
        catch (Exception ex)
        {
            Logger.getLogger(Activator.ID).log(Level.WARNING, "Formula Input Error", ex); //$NON-NLS-1$
        }
    }
}
