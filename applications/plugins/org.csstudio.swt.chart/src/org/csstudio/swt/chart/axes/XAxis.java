/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.Chart;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/** 'X' or 'horizontal' axis.
 *  @see org.csstudio.swt.chart.Chart
 *  @author Kay Kasemir
 */
public class XAxis extends Axis
{
    private XAxisListener listener;
    
    /** Create axis with label and listener. */
    public XAxis(String label, XAxisListener listener)
    {
        this(label, listener, new Ticks());
    }

    /** Create axis with label and listener. */
    protected XAxis(String label, XAxisListener listener, Ticks ticks)
    {
        super(true, label, ticks);
        this.listener = listener;
    }
    
    /** Set a new label. */
    public final void setLabel(String new_label)
    {
        label = new_label;
        listener.changedXAxis(this);
    }
    
    @Override
    public boolean setValueRange(double low, double high)
    {
        if (super.setValueRange(low, high))
        {
            listener.changedXAxis(this);
            return true;
        }
        return false;
    }
    
    /** Helper for layout of axis, used by Chart.
     *  @return Aprx. height of axis in pixel.
     */
    public final int getPixelHeight(GC gc)
    {
        Point char_size = gc.textExtent("X"); //$NON-NLS-1$
        // Need room for ticks, tick labels, and axis label
        // Plus a few pixels space at the bottom.
        return TICK_LENGTH + 2*char_size.y+2;
    }
    
    public void paint(final PaintEvent event)
    {
        if (!region.intersects(event.x, event.y, event.width, event.height))
            return;
        if (Chart.debug)
            System.out.println("paint axis '" + getLabel() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        final GC gc = event.gc;
        // Axis and Tick marks
        computeTicks(gc);
        gc.drawLine(region.x, region.y, region.x + region.width-1, region.y);
        for (double tick = ticks.getStart();
            tick <= high_value;
            tick = ticks.getNext(tick))
        {
            int x = getScreenCoord(tick);
            gc.drawLine(x, region.y, x, region.y + TICK_LENGTH);
            String mark = ticks.format(tick);
            Point mark_size = gc.textExtent(mark);
            gc.drawString(mark, x - mark_size.x/2,
                          region.y + TICK_LENGTH, false);
        }
        
        // Label: centered at bottom of region
        Point label_size = gc.textExtent(getLabel());
        gc.drawString(getLabel(),
                region.x + (region.width - label_size.x)/2,
                region.y + region.height - label_size.y - 1, false);
        if (Chart.debug)
            gc.drawRectangle(region.x, region.y, region.width-1, region.height-1);
    }
}
