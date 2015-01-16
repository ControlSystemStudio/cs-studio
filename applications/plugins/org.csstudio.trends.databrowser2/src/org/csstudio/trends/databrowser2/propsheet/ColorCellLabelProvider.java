/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.propsheet;

import org.csstudio.trends.databrowser2.Messages;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

/** CellLabelProvider that shows a color.
 *
 *  <p>Will _always_ show the color, even if the table cell/row is "selected".
 *
 *  @param <E> Type of table element
 *  @author Kay Kasemir
 */
abstract public class ColorCellLabelProvider<E> extends StyledCellLabelProvider
{
    public ColorCellLabelProvider()
    {
        super(COLORS_ON_SELECTION);
    }

    /** @param element Table element
     *  @return Color for this element
     */
    abstract protected Color getColor(E element);

    @Override
    public String getToolTipText(Object element)
    {
        return Messages.ColorTT;
    }

    @SuppressWarnings({ "unchecked", "nls" })
    @Override
    protected void paint(final Event event, final Object element)
    {
        // Most complicated part of this implementation is
        // obtaining the 'bounds' for painting the color.
        // ViewerCell is not available within paint().
        // API used in super.paint() to get cell is not public.
        // The cell provided to update() gets re-used.
        // -> Found that event.item has the TableItem & index
        if (! (event.item instanceof TableItem))
            throw new IllegalArgumentException("Expect TableItem");
        final TableItem item = (TableItem) event.item;
        final GC gc = event.gc;
        final Color old_bg = gc.getBackground();
        gc.setBackground(getColor( (E) element));
        gc.fillRectangle(item.getBounds(event.index));
        gc.setBackground(old_bg);
    }
}

