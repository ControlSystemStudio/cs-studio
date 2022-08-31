/*******************************************************************************
 * Copyright (c) 2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.List;

import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** Plot part for legend.
 *
 *  <p>Lists trace names.
 *
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kunal Shroff - Original Composite/Label based LegendHandler
 *  @author Kay Kasemir
 */
public class LegendPart<XTYPE extends Comparable<XTYPE>> extends PlotPart
{
    private volatile boolean visible = true;

    private volatile int grid_x = 100, grid_y = 15;

    public LegendPart(String name, PlotPartListener listener)
    {
        super(name, listener);
    }

    /** @return <code>true</code> if legend is visible */
    public boolean isVisible()
    {
        return visible;
    }

    /** @param show <code>true</code> if legend should be displayed */
    public void setVisible(final boolean show)
    {
        visible = show;
    }

    /** Compute height
     *  @param gc
     *  @param bounds_width
     *  @param font
     *  @param traces
     *  @return Desired height in pixels
     */
    public int getDesiredHeight(final GC gc, final int bounds_width,
                                final Font font, final List<Trace<XTYPE>> traces )
    {
        if (! visible)
            return 0;

        // Determine largest legend entry
        final Font orig_font = gc.getFont();
        gc.setFont(font);

        int max_width = 1, max_height = 1; // Start with 1 pixel to avoid later div-by-0
        for (Trace<XTYPE> trace : traces)
        {
            final Point size = gc.textExtent(trace.getLabel());
            if (size.x > max_width)
                max_width = size.x;
            if (size.y > max_height)
                max_height = size.y;
        }
        // Arrange in grid with some extra space
        grid_x = max_width + max_height / 2;
        grid_y = max_height;

        gc.setFont(orig_font);

        final int items = traces.size();
        final int items_per_row = Math.max(1, bounds_width / grid_x); // Round down, counting full items
        final int rows = (items + items_per_row-1) / items_per_row;   // Round up
        return rows * grid_y;
    }

    /** Paint the legend
     *  @param gc
     *  @param media
     *  @param font
     *  @param traces
     */
    public void paint(final GC gc, final SWTMediaPool media, final Font font,
                      final List<Trace<XTYPE>> traces)
    {
        if (! visible)
            return;

        super.paint(gc, media);

        final Color orig_color = gc.getForeground();
        final Font orig_font = gc.getFont();
        gc.setFont(font);

        final Rectangle bounds = getBounds();
        int x = bounds.x, y = bounds.y;
        for (Trace<XTYPE> trace : traces)
        {
            if (!trace.isVisible())
                continue;

            gc.setForeground(media.get(trace.getColor()));
            gc.drawText(trace.getLabel(), x, y, true);
            x += grid_x;
            if (x > bounds.width - grid_x)
            {
                x = bounds.x;
                y += grid_y;
            }
        }
        gc.setFont(orig_font);
        gc.setForeground(orig_color);
    }
}
