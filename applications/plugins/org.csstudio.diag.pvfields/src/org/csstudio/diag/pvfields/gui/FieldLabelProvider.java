/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.gui;

import org.csstudio.diag.pvfields.model.PVInfo;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


/**
 * Turns one PVField from the model into the string or image for a specific
 * table column
 */
@SuppressWarnings("nls")
public class FieldLabelProvider extends BaseLabelProvider implements
        ITableLabelProvider, ITableColorProvider
{
    @Override
    public Image getColumnImage(Object element, int columnIndex)
    {
        // No image
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex)
    {
        final PVInfo field = (PVInfo) element;

        // TODO I don't know why pv_field sometimes comes back as null after
        // hitting enter key.
        // sometimes it does this protects against null pointer errors.
        if (field == null)
            return "<null>";

        switch (columnIndex)
        {
        case 0:
            return field.getFirstColumn();
        case 1:
            return field.getSecondColumn();
        case 2:
            return field.getOrigValue();
        case 3:
            return field.getCurrentValue();
        default:
            return "Column " + columnIndex + "?";
        }
    }

    @Override
    public Color getBackground(Object element, int columnIndex)
    {
        Color grey = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);

        final PVInfo field = (PVInfo) element;

        // I don't know why pv_field sometimes comes back as null after hitting
        // enter key.
        // sometimes it does this protects against null pointer errors.
        if (field == null)
            return null;
        if (field.getCurrentValue() != "?")
        {
            switch (columnIndex)
            {
            case 0:
                return null;
            case 1:
                return null;
            case 2:
                return null;
            case 3:
                /**
                 * this returns grey if file value isn't equal to the live
                 * value and we only look if file field value is there.
                 */
                if (field.getOrigValue() != "" && field.getOrigValue()!= null)
                {
                    String fld = field.getOrigValue().trim();
                    String cur = field.getCurrentValue().trim();
                    // if fields file value isn't equal to the live value
                    if (fld.compareTo(cur) != 0)
                    {
                        // they might not be equal strings but equal
                        // numbers.
                        if (checkIfNumber(fld, cur))
                            return null;
                        else
                            return grey;
                    }
                    else
                        return null;
                }
                else
                    return null;
            default:
                return null;
            }
        }
        // if pv_field.getCurrentFldVal() == "?"
        else
            return null;
    }

    @Override
    public Color getForeground(Object element, int columnIndex)
    {
        return null;
    }

    /**
     * Validates if input String is a number
     */
    public boolean checkIfNumber(String fld, String cur)
    {
        Double newFld, newCur;

        try
        {
            newFld = Double.parseDouble(fld);
            newCur = Double.parseDouble(cur);
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
        if (newFld.equals(newCur))
            return true;
        else
            return false;
    }

}
