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
public class TimeAxis extends XAxis
{
    private static final int FIFTY_YEARS = 60*60*24*365*50;
    private static final double A_FEW_MILLISECS = 0.003;

    /** Create axis with label and listener. */
    public TimeAxis(String label, XAxisListener listener)
    {
        super(label, listener, new TimeTicks());
    }
    
    /* @see org.csstudio.swt.chart.axes.XAxis#setValueRange(double, double)
     */
    @Override
    public boolean setValueRange(double low, double high)
    {
        double range = Math.abs(high - low);
        // The Java time stamp has a resolution of milliseconds,
        // so anything finer than that doesn't show up on the screen.
        // Furthermore, we have to prevent 'distance == 0' calculations
        // in the TimeTick computation.
        // So we don't allow any zoom finer than a few milliseconds.
        // There's also a problem with displaying huge ranges,
        // so we limit it to 50 years
        if (range <= A_FEW_MILLISECS  ||  range >= FIFTY_YEARS)
            return false;
        return super.setValueRange(low, high);
    }

    @Override
    public void paint(final PaintEvent e)
    {
        if (!region.intersects(e.x, e.y, e.width, e.height))
            return;
        if (Chart.debug)
            System.out.println("paint axis '" + getLabel() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        final GC gc = e.gc;
        // Axis and Tick marks
        computeTicks(gc);
        gc.drawLine(region.x, region.y, region.x + region.width-1, region.y);
        for (double tick = ticks.getStart();
            tick <= high_value;
            tick = ticks.getNext(tick))
        {
            int x = getScreenCoord(tick);
            gc.drawLine(x, region.y, x, region.y + TICK_LENGTH);
            String mark = ((TimeTicks)ticks).getMultiline(tick);
            Point mark_size = gc.textExtent(mark);
            gc.drawText(mark, x - mark_size.x/2,
                          region.y + TICK_LENGTH, false);
        }
        
        // Label: centered at bottom of region
        /*
        gc.drawString(label,
                region.x + (region.width - label.length()*fm.getAverageCharWidth())/2,
                region.y + region.height - fm.getHeight() - 1, false);
         */
        if (Chart.debug)
            gc.drawRectangle(region.x, region.y, region.width-1, region.height-1);
    }
}
