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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
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
    public final int getDesiredPixelSize(final Rectangle region, final GC gc)

    {
        Activator.getLogger().log(Level.FINE,  "XAxis layout");
        gc.setFont(getLabelFont());
        final int label_size = gc.getFontMetrics().getHeight();
        gc.setFont(getScaleFont());
        final int scale_size = gc.getFontMetrics().getHeight();
        // Need room for ticks, tick labels, and axis label
        return TICK_LENGTH + label_size + scale_size;
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final GC gc, final SWTMediaPool media, final Rectangle plot_bounds)
    {
        super.paint(gc, media);
        final Rectangle region = getBounds();

        final Color old_fg = gc.getForeground();
        gc.setForeground(media.get(getColor()));
        gc.setFont(getScaleFont());

        // Axis and Tick marks
        gc.drawLine(region.x, region.y, region.x + region.width-1, region.y);
        computeTicks(gc);

        final double high_value = range.getHigh();
        final int minor_ticks = ticks.getMinorTicks();
        double tick = ticks.getStart();
        double prev = ticks.getPrevious(tick);
        for (/**/;
            tick <= high_value  &&  Double.isFinite(tick);
            tick = ticks.getNext(tick))
        {
            // Minor ticks?
            for (int i=1; i<minor_ticks; ++i)
            {
                final double minor = prev + ((tick - prev)*i)/minor_ticks;
                final int x = getScreenCoord(minor);
                if (x < region.x)
                    continue;
                gc.drawLine(x, region.y, x, region.y + MINOR_TICK_LENGTH);
            }

            drawTickLabel(gc, media, tick, false);
            if (show_grid)
            {
                final int x = getScreenCoord(tick);
                gc.setLineStyle(SWT.LINE_DOT);
                gc.drawLine(x, plot_bounds.y, x, plot_bounds.y + plot_bounds.height-1);
                gc.setLineStyle(SWT.LINE_SOLID);
            }

            prev = tick;
        }
        // Minor ticks after last major tick?
        if (Double.isFinite(tick))
            for (int i=1; i<minor_ticks; ++i)
            {
                final double minor = prev + ((tick - prev)*i)/minor_ticks;
                if (minor > high_value)
                    break;
                final int x = getScreenCoord(minor);
                gc.drawLine(x, region.y, x, region.y + MINOR_TICK_LENGTH);
            }

        // Label: centered at bottom of region
        gc.setFont(getLabelFont());
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
