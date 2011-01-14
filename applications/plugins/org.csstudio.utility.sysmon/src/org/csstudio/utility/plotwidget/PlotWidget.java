/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.plotwidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

/** Simple X/Y plot, no axes detail, data always painted above zero etc.
 *  @author Kay Kasemir
 */
public class PlotWidget extends Canvas implements PaintListener
{
    PlotSamples samples;
    
    public PlotWidget(Composite parent, int style)
    {
        super(parent, style);
        addPaintListener(this);
    }
    
    public void setSamples(PlotSamples samples)
    {
        this.samples = samples;
    }

    /** @see PaintListener */
    public void paintControl(PaintEvent e)
    {
        final GC gc = e.gc;
        
        Rectangle rect = getClientArea();
        // When used with drawRectangle, this adjustment makes it actually fit
        --rect.width;
        --rect.height;
        // Background and border
        gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(rect);
        gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
        gc.drawRectangle(rect);
        
        final int N = samples.getSampleCount();
        // Any samples to show?
        if (N < 2)
            return;
        // Determine maximum value (auto-scale)
        double max = 0.0;
        for (int i=0; i<N; ++i)
        {
            final double values[] = samples.getValues(i);
            for (double v : values)
                if (v > max)
                    max = v;
        }

        // Paint all the traces over x
        int last_x = 0;
        int last_y[] = new int[samples.getTraceCount()];
        for (int i=0; i<N; ++i)
        {
            final int x = rect.x + i * rect.width / (N-1);
            final double values[] = samples.getValues(i);
            for (int t=0; t<values.length; ++t)
            {
                final int y = (int) Math.round(
                                rect.y + rect.height * (1.0 - values[t] / max));
                if (i > 0)
                {
                    gc.setForeground(samples.getColor(t));
                    gc.drawLine(last_x, last_y[t], x, y);
                }
                last_y[t] = y;
            }
            last_x = x;
        }
    }
}
