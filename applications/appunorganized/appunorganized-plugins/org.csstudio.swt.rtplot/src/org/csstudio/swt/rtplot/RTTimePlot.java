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
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
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

    /** Steps to use when scrolling */
    private volatile Duration scroll_step = Duration.ofSeconds(10);

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
                getUndoableActionManager().execute(new UpdateScrolling(RTTimePlot.this, scroll.getSelection()));
                plot.fireXAxisChange();
            }
        });

        // Stop scrolling when x axis modified by user
        plot.addListener(new RTPlotListener<Instant>()
        {
            @Override
            public void changedXAxis(final Axis<Instant> x_axis)
            {
                if (! isScrolling())
                    return;
                final long now = Instant.now().getEpochSecond();
                final AxisRange<Instant> value_range = x_axis.getValueRange();
                final long range = value_range.getHigh().getEpochSecond() - value_range.getLow().getEpochSecond();
                // Iffy range?
                if (range <= 0)
                    return;
                final long dist = Math.abs(value_range.getHigh().getEpochSecond() - now);
                // In scroll mode, if the end time selected by the user via
                // the GUI is 25 % away from 'now', stop scrolling
                if (dist * 100 / (range + scroll_step.getSeconds()) > 25)
                    getUndoableActionManager().execute(new UpdateScrolling(RTTimePlot.this, false));
            }
        });

        parent.addDisposeListener((final DisposeEvent e) -> handleDisposal());
   }

    private void initToolItemImages(final Display display)
    {
        synchronized (RTTimePlot.class)
        {
            if (images != null)
                return;
            final ImageRegistry image_reg = new ImageRegistry(display);
            for (Icons icon : Icons.values())
                image_reg.put(icon.name(), Activator.getIcon(icon.name().toLowerCase()));
            images = image_reg;
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
            // Scroll once so that end of axis == 'now',
            // because otherwise one of the listeners might right away
            // disable scrolling
            scroll();
            final long scroll_period = scroll_step.toMillis();
            was_scrolling = scrolling.getAndSet(scroll_timer.scheduleAtFixedRate(RTTimePlot.this::scroll, scroll_period, scroll_period, TimeUnit.MILLISECONDS));
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

    /** @param scroll_step Step size to use when scrolling */
    public void setScrollStep(final Duration scroll_step)
    {
        this.scroll_step = scroll_step;
        setScrolling(isScrolling());
    }

    /** Update time axis to have 'now' at right end, keeping current duration */
    private void scroll()
    {
        final Axis<Instant> x_axis = plot.getXAxis();
        final AxisRange<Instant> range = x_axis.getValueRange();
        final Duration duration = Duration.between(range.getLow(), range.getHigh());
        final Instant end = Instant.now().plus(scroll_step);
        x_axis.setValueRange(end.minus(duration), end);
    }

    private void handleDisposal()
    {
        final ScheduledFuture<?> was_scrolling = scrolling.getAndSet(null);
        if (was_scrolling != null)
            was_scrolling.cancel(false);
    }

    public void setXAxisLabel(Instant time) {
        plot.setXAxisLabel(time);
    }
}
