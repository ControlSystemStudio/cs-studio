/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.apputil.ui.swt.CheckBoxImages;
import org.csstudio.display.pvtable.model.PVListEntry;
import org.csstudio.display.pvtable.model.PVListModel;
import org.csstudio.utility.pv.PV;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/** The JFace label provider for the  <class>PVListModel</class> data.
 *  @author Kay Kasemir
 */
public class PVTableLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider
{
    final private PVListModel pv_list;
    final private Color red;
    final private CheckBoxImages images;

    public PVTableLabelProvider(final Control control,
                                final PVListModel pv_list)
    {
        this.pv_list = pv_list;
        // no need to dispose system color
        red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        images = CheckBoxImages.getInstance(control);
    }

	@Override
    public String getText(Object obj)
	{
		return getColumnText(obj, 0);
	}

	@Override
    public Image getImage(Object obj)
	{
		return null;
	}

    /** Get text for all but the 'select' column. */
	@Override
    public String getColumnText(Object obj, int index)
	{
        if (obj == PVTableViewerHelper.empty_row)
            return index == PVTableHelper.NAME ?
                            PVTableViewerHelper.empty_row : ""; //$NON-NLS-1$
        return PVTableHelper.getText((PVListEntry) obj, index);
	}

    /** Get column image (only for the 'select' column) */
	@Override
    public Image getColumnImage(final Object obj, final int index)
	{
        if (index == PVTableHelper.SELECT  &&
            obj != PVTableViewerHelper.empty_row)
        {
            final PVListEntry entry = (PVListEntry) obj;
            return images.getImage(entry.isSelected());
        }
        return null;
	}

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    @Override
    public Color getBackground(Object obj, int index)
    {
        return null;
    }

    /** @see org.eclipse.jface.viewers.ITableColorProvider */
    @Override
    public Color getForeground(Object obj, int index)
    {
        if (shouldStandout(obj, index))
            return red;
        return null;
    }

    /** Determine if the table cell should shown in a special way. */
    private boolean shouldStandout(Object obj, int index)
    {
        if (obj == PVTableViewerHelper.empty_row)
            return false;

        PVListEntry entry = (PVListEntry) obj;
        double tolerance = pv_list.getTolerance();
        PV pv;

        switch (index)
        {
        case PVTableHelper.NAME:
            // Set PV name to red while disconnected
            if (! entry.getPV().isConnected())
                return true;
            return false;
        case PVTableHelper.READBACK:
            // Set readback PV name to red while disconnected
            pv = entry.getReadbackPV();
            if (pv != null  && ! pv.isConnected())
                return true;
            return false;
        case PVTableHelper.VALUE:
            return entry.getSavedValue().differ(entry.getPV(), tolerance);
        case PVTableHelper.READBACK_VALUE:
            return entry.getSavedReadbackValue().differ(entry.getReadbackPV(), tolerance);
        }
        return false;
    }
}
