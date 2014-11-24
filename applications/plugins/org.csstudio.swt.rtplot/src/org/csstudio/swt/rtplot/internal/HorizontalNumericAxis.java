/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** 'X' or 'horizontal' axis for numbers.
 *  @see TimeAxis
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class HorizontalNumericAxis extends NumericAxis
{
    /** Create axis with label and listener. */
    public HorizontalNumericAxis(final String name, final PlotPartListener listener)
    {
        super(name, listener,
              true,       // Horizontal
              0.0, 10.0); // Initial range
    }

    /** {@inheritDoc} */
    @Override
    public final int getDesiredPixelSize(final Rectangle region, final GC gc, final Font label_font, final Font scale_font)

    {
        Activator.getLogger().log(Level.FINE,  "XAxis layout");
        gc.setFont(label_font);
        final int label_size = gc.getFontMetrics().getHeight();
        gc.setFont(scale_font);
        final int scale_size = gc.getFontMetrics().getHeight();
        // Need room for ticks, tick labels, and axis label
        return TICK_LENGTH + label_size + scale_size;
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final GC gc, final SWTMediaPool media, final Font label_font, final Font scale_font)
    {
        super.paint(gc, media);
        final Rectangle region = getBounds();

        final Color old_fg = gc.getForeground();
        gc.setForeground(media.get(getColor()));

        // Axis and Tick marks
        computeTicks(gc);
        gc.setFont(scale_font);
        gc.drawLine(region.x, region.y, region.x + region.width-1, region.y);
        final double high_value = range.getHigh();
        for (double tick = ticks.getStart();
            tick <= high_value;
            tick = ticks.getNext(tick))
            drawTickLabel(gc, media, tick, false);

        // Label: centered at bottom of region
        gc.setFont(label_font);
        final Point label_size = gc.textExtent(getName());
        gc.drawString(getName(),
                region.x + (region.width - label_size.x)/2,
                region.y + region.height - label_size.y - 1, false);
        gc.setForeground(old_fg);
    }

    /** {@inheritDoc} */
    @Override
    public void drawTickLabel(final GC gc, final SWTMediaPool media, final Double tick, final boolean floating)
    {
        final Rectangle region = getBounds();
        final int x = getScreenCoord(tick);
        gc.drawLine(x, region.y, x, region.y + TICK_LENGTH);
        final String mark = ticks.format(tick);
        final Point mark_size = gc.textExtent(mark);
        gc.drawString(mark, x - mark_size.x/2,
                      region.y + TICK_LENGTH, !floating);
    }
}
