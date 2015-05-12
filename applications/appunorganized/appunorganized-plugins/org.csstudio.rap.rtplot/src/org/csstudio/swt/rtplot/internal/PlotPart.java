/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import java.util.Objects;
import java.util.logging.Level;

import org.csstudio.swt.rtplot.Activator;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;

/** Base for all parts of the {@link Plot}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PlotPart
{
    final private static boolean debug = false;
    private long debug_runs = 0;

    // @guardedBy(this)
    private String name;

    // @guardedBy(this)
    private RGB color = new RGB(0, 0, 0);

    final private PlotPartListener listener;

    /** Screen region occupied by this part */
    private volatile Rectangle bounds = new Rectangle(0, 0, 10, 10);

    /** @param name Part name
     *  @param listener {@link PlotPartListener}, or <code>null</code> if set via <code>setListener</code>
     */
    public PlotPart(final String name, final PlotPartListener listener)
    {
        this.name = name;
        this.listener = Objects.requireNonNull(listener);
        if (debug)
        {
            final Thread debug_thread = new Thread(() ->
            {
                try
                {
                    Thread.sleep(2000);
                    while (true)
                    {
                        for (long busy=0; busy<100000L; ++busy)
                            Thread.sleep(0);
                        ++debug_runs;
                        requestRefresh();
                    }
                }
                catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }, "PlotPartDebug");
            debug_thread.setDaemon(true);
            debug_thread.start();
        }
    }

    /** @return Part name */
    public synchronized String getName()
    {
        return name;
    }

    /** @return Part name */
    public void setName(final String name)
    {
        Objects.requireNonNull(name);
        synchronized (this)
        {
            if (name.equals(this.name))
                return;
            this.name = name;
        }
        requestLayout();
        requestRefresh();
    }

    /** @return Color to use for this part */
    public synchronized RGB getColor()
    {
        return color;
    }

    /** @param color Color to use for this part */
    public void setColor(final RGB color)
    {
        Objects.requireNonNull(color);
        synchronized (this)
        {
            if (color.equals(this.color))
                return;
            this.color = color;
        }
        requestRefresh();
    }

    /** @param bounds New screen coordinates */
    public void setBounds(final Rectangle bounds)
    {   // Pass on to clone bounds
        setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /** @param x New screen coordinate
     *  @param y New screen coordinate
     *  @param width New screen coordinate
     *  @param height New screen coordinate
     */
    public void setBounds(final int x, final int y, final int width, final int height)
    {
        bounds = new Rectangle(x, y, width, height);
        Activator.getLogger().log(Level.FINER, "setBound({0}) to {1}", new Object[] { getName(), bounds });
    }

    /** @return Screen coordinates */
    public Rectangle getBounds()
    {
        return bounds;
    }

    /** Derived part can call to request re-computation of layout */
    protected void requestLayout()
    {
        listener.layoutPlotPart(this);
    }

    /** Derived part can call to request refresh */
    protected void requestRefresh()
    {
        listener.refreshPlotPart(this);
    }

    /** Invoked to paint the part.
     *
     *  <p>Is invoked on background thread.
     *  <p>Derived part can override, should invoke super.
     *
     *  @param gc {@link GC} for painting in background thread
     *  @param media {@link SWTMediaPool}
     */
    public void paint(final GC gc, final SWTMediaPool media)
    {
        if (debug)
        {
            final Color old_fg = gc.getForeground();
            gc.setForeground(media.get(new RGB(255, 0, 255)));
            final int old_lw = gc.getLineWidth();
            final int lw = 2;
            gc.setLineWidth(lw);
            gc.drawRectangle(bounds.x+lw/2, bounds.y+lw/2, bounds.width-lw, bounds.height-lw);

            final String text = Long.toString(debug_runs);
            final int tx = bounds.x + (bounds.width - gc.getFontMetrics().getAverageCharWidth()*name.length()) / 2;
            final int ty = bounds.y + (bounds.height - 2*gc.getFontMetrics().getHeight()) / 2;
            gc.drawText(name, tx, ty, true);
            gc.drawText(text, tx, ty + gc.getFontMetrics().getHeight(), true);

            gc.setLineWidth(old_lw);
            gc.setForeground(old_fg);
        }
    }

    /** Derived part can implement to allow disposal of resources */
    public void dispose()
    {
        // NOP
    }
}
