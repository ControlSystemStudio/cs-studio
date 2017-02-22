/*******************************************************************************
 * Copyright (c) 2010-2017 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.epics.pvtree.swt;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.diag.epics.pvtree.model.TreeModelItem;
import org.diirt.vtype.AlarmSeverity;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/** Label provider for {@link TreeModelItem} entries.
 *  @author Kay Kasemir
 */
class PVTreeLabelProvider extends LabelProvider implements IColorProvider, DisposeListener
{
    final private Map<AlarmSeverity, Image> images = new HashMap<AlarmSeverity, Image>();

    public PVTreeLabelProvider(final Control widget)
    {
        final Display display = widget.getDisplay();
        images.put(AlarmSeverity.NONE,
            createImage(display, display.getSystemColor(SWT.COLOR_GREEN)));
        images.put(AlarmSeverity.MINOR,
                createImage(display, display.getSystemColor(SWT.COLOR_YELLOW)));
        images.put(AlarmSeverity.MAJOR,
                createImage(display, display.getSystemColor(SWT.COLOR_RED)));
        images.put(AlarmSeverity.INVALID,
                createImage(display, display.getSystemColor(SWT.COLOR_MAGENTA)));
        images.put(AlarmSeverity.UNDEFINED,
                createImage(display, display.getSystemColor(SWT.COLOR_MAGENTA)));

        // Arrange for image disposal
        widget.addDisposeListener(this);
    }

    /** Dispose images */
    @Override
    public void widgetDisposed(final DisposeEvent e)
    {
        for (Image image : images.values())
            image.dispose();
        images.clear();
    }

    private Image createImage(final Display display, final Color color)
    {
        final Image image = new Image(display, 16, 16);
        final GC gc = new GC(image);
        gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(0, 0, 16, 16);
        gc.setBackground(color);
        gc.fillOval(0, 0, 16, 16);
        gc.dispose();
        return image;
    }

    @Override
    public String getText(final Object element)
    {
        return element.toString();
    }

    @Override
    public Image getImage(final Object element)
    {
        final AlarmSeverity severity = getSeverity(element);
        return images.get(severity);
    }

    @Override
    public Color getBackground(final Object element)
    {
        return null;
    }

    @Override
    public Color getForeground(final Object element)
    {
        final AlarmSeverity severity = getSeverity(element);
        if (severity == null)
            return null;
        switch (severity)
        {
        case UNDEFINED:
            return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
        case INVALID:
            return Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
        case MAJOR:
            return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
        case MINOR:
            return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
        default:
            return null;
        }
    }

    /** @param element Tree element, presumably {@link PVTreeItem}
     *  @return {@link AlarmSeverity} of that tree element
     */
    private AlarmSeverity getSeverity(final Object element)
    {
        if (element instanceof TreeModelItem)
            return ((TreeModelItem)element).getSeverity();
        return null;
    }
}
