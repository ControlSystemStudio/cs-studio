/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.csstudio.swt.rtplot.undo.UpdateScrolling;
import org.csstudio.swt.rtplot.util.NamedThreadFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;

/** Real-time plot using time stamps on the 'X' axis.
 *
 *  <p>Support scrolling.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class RTTimePlot extends RTPlot<Instant>
{
    final private static ScheduledExecutorService scroll_timer =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("RTPlotScroll"));

    private enum Icons
    {
        SCROLL_ON,
        SCROLL_OFF
    }
    private static ImageRegistry images = null;

    /** Scroll period in millisecs */
    private volatile long scroll_period = 500;

    /** When scrolling, holds Future for canceling the scheduled scroll calls. Otherwise <code>null</code> */
    private AtomicReference<ScheduledFuture<?>> scrolling = new AtomicReference<>();

    private ToolItem scroll;

    /** @param parent Parent widget */
    public RTTimePlot(final Composite parent)
    {
        super(parent, Instant.class);
        initToolItemImages(parent.getDisplay());

        scroll = addToolItem(SWT.CHECK, null, "");
        setScrolling(true);
        scroll.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                getUndoableActionManager().perform(new UpdateScrolling(RTTimePlot.this, scroll.getSelection()));
                plot.fireXAxisChange();
            }
        });

        // Stop scrolling when x axis modified by user
        plot.addListener(new PlotListenerAdapter<Instant>()
        {
            @Override
            public void changedXAxis(final Axis<Instant> x_axis)
            {
                if (! isScrolling())
                    return;
                final long time = Instant.now().getEpochSecond();
                final AxisRange<Instant> value_range = x_axis.getValueRange();
                final long range = value_range.getHigh().getEpochSecond() - value_range.getLow().getEpochSecond();
                // Iffy range?
                if (range <= 0)
                    return;
                final long dist = Math.abs(value_range.getHigh().getEpochSecond() - time);
                // In scroll mode, if the end time selected by the user via
                // the GUI is 10 % away from 'now', stop scrolling
                if (dist * 100 / range > 10)
                    getUndoableActionManager().perform(new UpdateScrolling(RTTimePlot.this, false));
            }
        });
    }

    private void initToolItemImages(final Display display)
    {
        if (images != null)
            return;
        images = new ImageRegistry(display);
        for (Icons icon : Icons.values())
        {
            final ImageDescriptor image = Activator.getIcon(icon.name().toLowerCase());
            images.put(icon.name(), image);
        }
    }

    /** @return <code>true</code> if scrolling is enabled */
    public boolean isScrolling()
    {
        return scrolling.get() != null;
    }

    /** @param enabled <code>true</code> to enable scrolling */
    public void setScrolling(final boolean enabled)
    {
        scroll.setSelection(enabled);
        final ScheduledFuture<?> was_scrolling;
        if (enabled)
        {   // Show that scrolling is 'on', and tool tip explains that it can be turned off
            scroll.setImage(images.get(Icons.SCROLL_ON.name()));
            scroll.setToolTipText(Messages.Scroll_Off_TT);
            was_scrolling = scrolling.getAndSet(scroll_timer.scheduleAtFixedRate(RTTimePlot.this::scroll, scroll_period, scroll_period, TimeUnit.MILLISECONDS));
            // Scroll once so that end of axis == 'now',
            // because otherwise one of the listeners might right away
            // disable scrolling
            scroll();
        }
        else
        {   // Other way around
            scroll.setImage(images.get(Icons.SCROLL_OFF.name()));
            scroll.setToolTipText(Messages.Scroll_On_TT);
            was_scrolling = scrolling.getAndSet(null);
        }
        if (was_scrolling != null)
            was_scrolling.cancel(false);
    }

    /** @param millisecs Desired scroll period */
    public void setScrollPeriod(final long millisecs)
    {
        scroll_period = millisecs;
        setScrolling(isScrolling());
    }

    /** Update time axis to have 'now' at right end, keeping current duration */
    private void scroll()
    {
        final Axis<Instant> x_axis = plot.getXAxis();
        final AxisRange<Instant> range = x_axis.getValueRange();
        final Duration duration = Duration.between(range.getLow(), range.getHigh());
        final Instant now = Instant.now();
        x_axis.setValueRange(now.minus(duration), now);
    }
}
