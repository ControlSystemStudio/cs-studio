/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import java.util.ArrayList;

import org.csstudio.swt.chart.Trace;
import org.csstudio.swt.util.GraphicsUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

/** A 'Y' or 'vertical' axis that uses the trace names as labels.
 *  <p>
 *  The chart maintains one or more Y axes.
 *  Each trace to plot needs to be assigned to a Y axis.
 *  
 *  @see Trace
 *  @author Kay Kasemir
 */
public class TraceNameYAxis extends YAxis
{
    private boolean traces_changed = true;
    private int space_width;
    /** Number of traces in each label row. */
    private int row_items[];
    /** Pixel width (or height, since they're vertical) of each row. */
    private int row_widths[];
    
    /** @see YAxis#YAxis */
    public TraceNameYAxis(String label, YAxisListener listener)
    {
        super(label, listener);
    }
    
    // In addition to inherited bahavior, trigger a new layout
    @Override
    public final void addTrace(Trace trace)
    {
        traces_changed = true;
        super.addTrace(trace);
        fireEvent(YAxisListener.Aspect.LABEL);
    }

    // In addition to inherited bahavior, trigger a new layout
    @Override
    public final void removeTrace(Trace trace)
    {
        traces_changed = true;
        super.removeTrace(trace);
        fireEvent(YAxisListener.Aspect.LABEL);
    }
    
    // In addition to inherited bahavior, trigger a new layout
    @Override
    public final void setRegion(int x, int y, int width, int height)
    {
        traces_changed = true;
        super.setRegion(x, y, width, height);
    }

    private void determineLayout(GC gc)
    {
        if (!traces_changed)
            return;
        ArrayList<Integer> items = new ArrayList<Integer>();
        ArrayList<Integer> widths = new ArrayList<Integer>();
        space_width = gc.stringExtent(", ").x; //$NON-NLS-1$
        int wid = 0;
        int itms = 0;
        // See how much fits in each row.
        for (int i=0; i<getNumTraces(); ++i)
        {
            int name_width = gc.textExtent(getTrace(i).getName()).x;
            // Start new row when exceeding available space.
            // Except: If there's only one item
            // in this row.. tough. It'll be truncated.
            if (itms > 0  &&
                wid + name_width + space_width > region.height)
            {   // Start new row
                items.add(new Integer(itms));
                widths.add(new Integer(wid));
                itms = 0;
                wid = 0;
            }
            ++itms;
            // Won't need the space_width for the last entry... Oh, well.
            wid += name_width + space_width;
        }
        if (itms > 0)
        {
            items.add(new Integer(itms));
            widths.add(new Integer(wid));
        }
        assert items.size() == widths.size();
        // Turn ArrayList<Integer> into int[]
        row_items = new int[items.size()];
        for (int i=0; i<row_items.length; ++i)
            row_items[i] = items.get(i).intValue();
        row_widths = new int[widths.size()];
        for (int i=0; i<row_widths.length; ++i)
            row_widths[i] = widths.get(i).intValue();
        traces_changed = false;
    }
    
    @Override
    public final int getPixelWidth(final GC gc)
    {
        if (!isVisible())
            return 0;
        determineLayout(gc);
        final Point char_size = gc.textExtent("X"); //$NON-NLS-1$
        // Room for (vertical) label rows + value text + tick markers.
        return (row_widths.length + 1)*char_size.y + TICK_LENGTH;
    }

    @Override
    protected void paintLabel(GC gc)
    {
        if (getNumTraces() < 1)
        {
            // Do not use setLabel, because that would cause a redraw,
            // and we are already in a redraw -> endless loop.
            label = "y"; //$NON-NLS-1$
            super.paintLabel(gc);
            return;
        }
        determineLayout(gc);
        // Label: At left edge of region, vertically apx. centered
        int row = 0;
        int items = 0;
        int x = region.x + 1;
        // y+height = botton, then go up by half the 'extra'
        int y = region.y + (region.height + row_widths[row])/2;
        Color fg = gc.getForeground();
    	
        for (int i=0; i<getNumTraces(); ++i)
        {
            String name = getTrace(i).getName();
            gc.setForeground(getTrace(i).getColor());
            Point text_size = gc.textExtent(name);
            int name_width = text_size.x;
            if (items >= row_items[row])
            {   // Start another row
                ++row;
                y = region.y + (region.height + row_widths[row])/2;
                x += text_size.y;
                items = 0;
            }
            y -= name_width;
            
            GraphicsUtils.drawVerticalText(name, x, y, gc, SWT.UP);
            gc.setForeground(fg); // restore original fg
            if (i < getNumTraces()-1)
            {
                y -= space_width;
                GraphicsUtils.drawVerticalText(", ", x, y, gc, SWT.UP); //$NON-NLS-1$
            }

            ++items;
        }
    }
}
