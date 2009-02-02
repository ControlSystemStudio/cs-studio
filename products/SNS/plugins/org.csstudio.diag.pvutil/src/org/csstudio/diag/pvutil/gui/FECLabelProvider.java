package org.csstudio.diag.pvutil.gui;

import org.csstudio.diag.pvutil.model.FEC;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** Turns a Front End Controller "Device or IOC" into the string to image for a specific table column */
@SuppressWarnings("nls")
public class FECLabelProvider extends BaseLabelProvider implements ITableLabelProvider, ITableColorProvider
{
    public Image getColumnImage(Object element, int columnIndex)
    {
        // No image
        return null;
    }

    public String getColumnText(Object element, int columnIndex)
    {
        FEC device = (FEC) element;
        switch (columnIndex)
        {
        case 0:
            return device.getName();
        default:
            return "Column " + columnIndex + "?";
        }
    }

    public Color getBackground(Object element, int columnIndex)
    {
        FEC device = (FEC) element;
        if (device.getName() == null && columnIndex == 1)
            return Display.getDefault().getSystemColor(13);
        return null;
    }

    public Color getForeground(Object element, int columnIndex)
    {
         return null;
    }
}
