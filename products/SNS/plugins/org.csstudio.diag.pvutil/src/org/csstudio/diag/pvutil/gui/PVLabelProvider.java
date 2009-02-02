package org.csstudio.diag.pvutil.gui;

import org.csstudio.diag.pvutil.model.PV;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** Turns a "PV" into the string to image for a specific table column */
@SuppressWarnings("nls")
public class PVLabelProvider extends BaseLabelProvider implements ITableLabelProvider, ITableColorProvider
{
    public Image getColumnImage(Object element, int columnIndex)
    {
        // No image
        return null;
    }

    public String getColumnText(Object element, int columnIndex)
    {
        PV device = (PV) element;
        switch (columnIndex)
        {
        case 0:
            return device.getName();
        case 1:
            return device.getInfoString();
        default:
            return "Column " + columnIndex + "?";
        }
    }

    public Color getBackground(Object element, int columnIndex)
    {
        PV device = (PV) element;
        if (device.getInfoString() == "bi" && columnIndex == 1)
            return Display.getDefault().getSystemColor(13);
        return null;
    }

    public Color getForeground(Object element, int columnIndex)
    {
         return null;
    }
}
