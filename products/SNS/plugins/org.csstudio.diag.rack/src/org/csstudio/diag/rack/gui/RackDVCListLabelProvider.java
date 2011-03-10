/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.rack.gui;

import org.csstudio.diag.rack.model.RackList;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** Turns a "PV" into the string to image for a specific table column */
@SuppressWarnings("nls")
public class RackDVCListLabelProvider extends BaseLabelProvider implements ITableLabelProvider, ITableColorProvider
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
        RackList device = (RackList) element;
        switch (columnIndex)
        {
        case 0:
            return device.getDvcId();
        case 1:
            return ""+device.getBGN();
        case 2:
            return ""+device.getEND();
        case 3:
            return device.getDvcTypeId();
        case 4:
            return device.getBlDvcInd();
        default:
            return "Column " + columnIndex + "?";
        }
    }

    @Override
    public Color getBackground(Object element, int columnIndex)
    {
        RackList device = (RackList) element;
        if (device.getBlDvcInd() == "Y" && columnIndex == 1)
            return Display.getDefault().getSystemColor(13);
        return null;
    }

    @Override
    public Color getForeground(Object element, int columnIndex)
    {
         return null;
    }
}
