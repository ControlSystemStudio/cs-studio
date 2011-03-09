/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree;

import org.csstudio.data.values.ISeverity;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/** Label provider for PVTreeItem entries.
 *  @author Kay Kasemir
 */
class PVTreeLabelProvider extends LabelProvider implements IColorProvider
{
    @Override
    public String getText(final Object obj)
    {
        return obj.toString();
    }

    @Override
    public Image getImage(final Object obj)
    {
        // Indicate if this is a 'record' of known type...
        //if (obj instanceof PVTreeItem && ((PVTreeItem)obj).getType() != null)
        //    return PlatformUI.getWorkbench().getSharedImages()
        //        .getImage(ISharedImages.IMG_OBJ_FILE);
        // or something else (unknown)
        return null;
    }

    @Override
    public Color getBackground(final Object element)
    {
        return null;
    }

    @Override
    public Color getForeground(final Object element)
    {
        if (! (element instanceof PVTreeItem))
            return null;

        final ISeverity severity = ((PVTreeItem)element).getSeverity();
        if (severity == null)
            return null;
        if (severity.isInvalid())
            return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
        if (severity.isMajor())
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        if (severity.isMinor())
            return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
        return null;
    }
}
