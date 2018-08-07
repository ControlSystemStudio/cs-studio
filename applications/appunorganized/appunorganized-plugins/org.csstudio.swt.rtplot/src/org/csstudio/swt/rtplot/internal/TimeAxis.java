/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.AxisRange;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.internal.util.TimeScreenTransform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/** 'X' or 'horizontal' axis for time stamps.
 *  @see HorizontalNumericAxis
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class TimeAxis extends AxisPart<Instant>
{
	
    protected volatile boolean show_now = false;

    /** Create axis with label and listener. */
    public static TimeAxis forDuration(final String name, final PlotPartListener listener,
            final Duration duration)
    {
        final Instant end = Instant.now();
        final Instant start = end.minus(duration);
        return new TimeAxis(name, listener, start, end);
    }

    /** Create axis with label and listener. */
    public TimeAxis(final String name, final PlotPartListener listener,
            final Instant start, final Instant end)
    {
        super(name, listener,
              true, // Horizontal
              start, end,
              new TimeScreenTransform(),
              new TimeTicks());
    }

    /** {@inheritDoc} */
    @Override
    public final int getDesiredPixelSize(final Rectangle region, final GC gc)
    {
        Activator.getLogger().log(Level.FINE, "TimeAxis({0}) layout for {1}", new Object[] { getName(),  region });

        final int label_size;
        if (isAxisNameVisible() && !getName().isEmpty()) {
            gc.setFont(getLabelFont());
            label_size = gc.getFontMetrics().getHeight();
        } else {
            label_size = 0;
        }
        gc.setFont(getScaleFont());
        final int scale_size = gc.getFontMetrics().getHeight();
        // Need room for ticks, two tick labels, and axis label
        // Plus a few pixels space at the bottom.
        return TICK_LENGTH + 2*scale_size + label_size;
    }

    /** {@inheritDoc} */
    @Override
    public void zoom(final int center, final double factor)
    {
        final Instant fixed = getValue(center);
        final Instant new_low = fixed.minus(scaledDuration(range.getLow(), fixed, factor));
        final Instant new_high = fixed.plus(scaledDuration(fixed, range.getHigh(), factor));
        setValueRange(new_low, new_high);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isIndicatorLineVisible()
    {
        return show_now;
    }

    /** {@inheritDoc} */
    @Override
    public void setIndicatorLineVisible(final boolean now)
    {
        if (show_now == now)
            return;
        show_now = now;
        requestLayout();
        requestRefresh();
    }

    /** Scale Duration by floating point number
     *  @param start Start of a duration
     *  @param end End of a duration
     *  @param factor Scaling factor
     *  @return Scaled Duration
     */
    final private static Duration scaledDuration(final Instant start, final Instant end, final double factor)
    {
        final Duration duration = Duration.between(start, end);
        final double scaled = (duration.getSeconds() + 1e-9*duration.getNano()) * factor;
        final int seconds = (int)scaled;
        final int nano = (int) ((scaled - seconds) * 1e9);
        return Duration.ofSeconds(seconds, nano);
    }

    /** {@inheritDoc} */
    @Override
    public void pan(final AxisRange<Instant> original_range, final Instant t1, final Instant t2)
    {
        final Instant low = original_range.getLow();
        final Instant high = original_range.getHigh();
        final Duration shift = Duration.between(t2, t1);
        setValueRange(low.plus(shift), high.plus(shift));
    }

    /** {@inheritDoc} */
    @Override
    public void paint(final GC gc, final SWTMediaPool media, final Rectangle plot_bounds)
    {
        if (! isVisible())
            return;

        super.paint(gc, media);
        final Rectangle region = getBounds();
        final int region_end = region.x + region.width;

        final int old_width = gc.getLineWidth();
        final Color old_fg = gc.getForeground();
        gc.setForeground(media.get(getColor()));
        gc.setFont(getScaleFont());

        // Simple line for the axis
        gc.drawLine(region.x, region.y, region.x + region.width-1, region.y);

        // Axis and Tick marks
        computeTicks(gc);
        final int minor_ticks = ticks.getMinorTicks();
        Instant tick = ticks.getStart();
        int x = getScreenCoord(tick);
        int prev_x = getScreenCoord(ticks.getPrevious(tick));
        while (x < region_end)
        {   // Minor ticks?
            for (int i=1; i<minor_ticks; ++i)
            {
                final int minor_x = prev_x + ((x - prev_x)*i)/minor_ticks;
                if (minor_x < region.x)
                    continue;
                gc.drawLine(minor_x, region.y, minor_x, region.y + MINOR_TICK_LENGTH);
            }

            // Major tick marks
            gc.setLineWidth(TICK_WIDTH);
            gc.drawLine(x, region.y, x, region.y + TICK_LENGTH);
            gc.setLineWidth(old_width);

            // Grid line
            if (show_grid)
            {
                gc.setLineStyle(SWT.LINE_DOT);
                gc.drawLine(x, plot_bounds.y, x, region.y-1);
                gc.setLineStyle(SWT.LINE_SOLID);
            }

            // Tick Label
            drawTickLabel(gc, media, tick, false);

            prev_x = x;
            tick = ticks.getNext(tick);
            x = getScreenCoord(tick);
        }
        // Minor ticks after last major tick?
        for (int i=1; i<minor_ticks; ++i)
        {
            final int minor_x = prev_x + ((x - prev_x)*i)/minor_ticks;
            if (minor_x >= region_end)
                break;
            gc.drawLine(minor_x, region.y, minor_x, region.y + MINOR_TICK_LENGTH);
        }

        if (! getName().isEmpty() && isAxisNameVisible())
        {   // Label: centered at bottom of region
            gc.setFont(getLabelFont());
            final Point label_size = gc.textExtent(getName());
            gc.drawString(getName(),
                    region.x + (region.width - label_size.x)/2,
                    region.y + region.height - label_size.y - 1, false);
        }

        // Current time marker
        if (show_now) {
            final Instant now = Instant.now();
            int now_x = getScreenCoord(now);
            gc.setLineStyle(SWT.LINE_DASH);
            gc.drawLine(now_x, plot_bounds.y, now_x, region.y-1);
            gc.setLineStyle(SWT.LINE_SOLID);
        }

        gc.setForeground(old_fg);
    }

    /** {@inheritDoc} */
    @Override
    public void drawTickLabel(final GC gc, final SWTMediaPool media, final Instant tick, final boolean floating)
    {
        final int x = getScreenCoord(tick);
        int y = getBounds().y + TICK_LENGTH;
        for (String mark : ticks.format(tick).split("\n"))
        {
            final Point mark_size = gc.textExtent(mark);
            final int tx = x - mark_size.x/2;
            if (floating)
            {
                gc.fillRectangle(tx, y, mark_size.x, mark_size.y);
                gc.drawRectangle(tx, y, mark_size.x, mark_size.y);
            }
            gc.drawText(mark, tx, y, true);
            y += mark_size.y;
        }
    }
}
