/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart.axes;

import java.util.ArrayList;

import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Preferences;
import org.csstudio.swt.chart.Trace;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

/** Paints the samples of one trace.
 *  <p>
 *  Specifically, it draws a 'staircase' type line from one sample to the next:
 *  The 'y' value of a previous sample is considered valid until the next sample.
 * 
 *  @author Blaz Lipuscek 
 *  @author Kay Kasemir
 *  @author Xihu Chen found the 'alpha' support for transparency
 */
public class TracePainter
{
    /** Use transparency as supported by 'advanced graphics' ? */
    final private boolean use_advanced_graphics =  Preferences.getUseAdvancedGraphics();

    final private int marker_type = Preferences.getMarkerType();

    final private int marker_size = Preferences.getMarkerSize();
    
    final private float saturation = Preferences.getAreaSaturation();

    /** Paint a trace over given X axis. 
     *  @return <code>true</code> if it enabled advanced graphics
     */
    public boolean paint(final GC gc, final Trace trace, final XAxis xaxis)
    {
        final AxisRangeLimiter limiter = new AxisRangeLimiter(xaxis);
        gc.setForeground(trace.getColor());
        gc.setBackground(trace.getColor());
        gc.setLineWidth(trace.getLineWidth());
        final YAxis yaxis = trace.getYAxis();
        final ChartSampleSequence samples = trace.getSampleSequence();
        // ** Lock the samples, so they don't change on us! **
        if (Chart.debug)
            System.out.println("Tracepainter ..."); //$NON-NLS-1$
        boolean used_advanced_graphics = false;
        synchronized (samples)
        {
            // Instead of painting the whole trace, find the
            // first and last sample that's actually visible on this x axis.
            // Of course this requires the x values to be rising.
            final int i0 = limiter.getLowIndex(samples);
            final int i1 = limiter.getHighIndex(samples);
            switch (trace.getType())
            {
            case Area:
                used_advanced_graphics |= drawArea(gc, xaxis, yaxis, samples, i0, i1, true);
                break;
            case Lines:
                used_advanced_graphics |= drawArea(gc, xaxis, yaxis, samples, i0, i1, false);
                break;
            case SingleLine:
                drawLine(gc, xaxis, yaxis, samples, i0, i1);
                break;
            case Markers:
                int point_size = trace.getLineWidth();
                if (point_size < 2)
                    point_size = 2;
                drawMarkers(gc, xaxis, yaxis, samples, i0, i1, point_size);
                break;
            case Bars:
                drawBars(gc, xaxis, yaxis, samples, i0, i1);
                break;
            }
        }
        if (Chart.debug)
            System.out.println("Tracepainter done."); //$NON-NLS-1$
        return used_advanced_graphics;
    }
    
    /** Show the average data with staircase lines.
     *  For samples with min/max info,
     *  show min/max outline as area or lines.
     *  @param gc GC
     *  @param xaxis XAxis
     *  @param yaxis YAxis
     *  @param samples Samples
     *  @param i0 First sample to use
     *  @param i1 Last sample to use
     *  @param area Use area or lines for min/max envelope?
     *  @return <code>true</code> if we ended up using advanced graphics
     */
    private boolean drawArea(final GC gc,
                             final XAxis xaxis,
                             final YAxis yaxis,
                             final ChartSampleSequence samples,
                             final int i0,
                             final int i1,
                             final boolean area)
    {
        // Did we use advanced graphics?
        boolean used_advanced_graphics = false;
        // Change to lighter version of background:
        // Reduce saturation, use brightness of 1
        final Color old_back = gc.getBackground();
        final float[] hsb = old_back.getRGB().getHSB();
        final Color lighter = new Color(gc.getDevice(),
                new RGB(hsb[0], hsb[1]*saturation, 1.0f));
        // Accumulate info about horizontal position, min/max/average values
        final ArrayList<Integer> pos = new ArrayList<Integer>();
        final ArrayList<Integer> avg = new ArrayList<Integer>();
        final ArrayList<Integer> min = new ArrayList<Integer>();
        final ArrayList<Integer> max = new ArrayList<Integer>();        
        for (int i = i0; i <= i1; ++i)
        {
            final ChartSample sample = samples.get(i);
            final int x0 = xaxis.getScreenCoord(sample.getX());
            final double y = sample.getY();
            int y0;
            if (!Double.isInfinite(y)  &&  !Double.isNaN(y))
            {   // Add x0/y0, maybe min/max to sequence of plotable points
                y0 = yaxis.getScreenCoord(y);
                pos.add(x0);
                avg.add(y0);
                if (sample.haveMinMax())
                {
                    min.add(yaxis.getScreenCoord(sample.getMinY()));
                    max.add(yaxis.getScreenCoord(sample.getMaxY()));
                }
                else
                {
                    min.add(y0);
                    max.add(y0);
                }
            }
            else
            {
                // Not a plottable y value.
                final int n = pos.size();
                if (n > 0)
                {
                    // Extend previous value to here
                    pos.add(x0);
                    avg.add(avg.get(n-1));
                    min.add(min.get(n-1));
                    max.add(max.get(n-1));
                }
                // Draw what might have accumulated, then reset
                y0 = xaxis.getRegion().y;
                if (area)
                {
                    if (use_advanced_graphics)
                    {
                        gc.setAlpha((int) (255*saturation));
                        fillArea(gc, pos, min, max);
                        gc.setAlpha(255);
                        used_advanced_graphics = true;
                    }                        
                    else
                    {
                        gc.setBackground(lighter);
                        fillArea(gc, pos, min, max);
                        gc.setBackground(old_back);
                    }
                }
                else
                {
                    gc.setForeground(lighter);
                    drawStaircaseLine(gc, pos, min);
                    drawStaircaseLine(gc, pos, max);                    
                    gc.setForeground(old_back);
                }
                drawStaircaseLine(gc, pos, avg);
                pos.clear();
                min.clear();
                max.clear();
                avg.clear();
            }
            if (sample.getType() == ChartSample.Type.Point)
                markPoint(gc, x0, y0);
        }
        // Draw what might have accumulated when reaching last sample
        if (area)
        {
            if (use_advanced_graphics)
            {
                gc.setAlpha((int) (255*saturation));
                fillArea(gc, pos, min, max);
                gc.setAlpha(255);
                used_advanced_graphics = true;
            }                        
            else
            {
                gc.setBackground(lighter);
                fillArea(gc, pos, min, max);
                gc.setBackground(old_back);
            }
        }
        else
        {
            gc.setForeground(lighter);
            drawStaircaseLine(gc, pos, min);
            drawStaircaseLine(gc, pos, max);                    
            gc.setForeground(old_back);
        }
        drawStaircaseLine(gc, pos, avg);
        
        lighter.dispose();
        
        return used_advanced_graphics;
    }

    /** Show the average data with staircase lines. No min/max display
     *  @param gc GC
     *  @param xaxis XAxis
     *  @param yaxis YAxis
     *  @param samples Samples
     *  @param i0 First sample to use
     *  @param i1 Last sample to use
     *  @param area Use area of lines for min/max envelope?
     */
    private void drawLine(final GC gc,
                                 final XAxis xaxis,
                                 final YAxis yaxis,
                                 final ChartSampleSequence samples,
                                 final int i0,
                                 final int i1)
    {
        // Accumulate horizontal position and value
        final ArrayList<Integer> pos = new ArrayList<Integer>();
        final ArrayList<Integer> avg = new ArrayList<Integer>();
        for (int i = i0; i <= i1; ++i)
        {
            final ChartSample sample = samples.get(i);
            final int x0 = xaxis.getScreenCoord(sample.getX());
            final double y = sample.getY();
            int y0;
            if (!Double.isInfinite(y)  &&  !Double.isNaN(y))
            {   // Add x0/y0, maybe min/max to sequence of plotable points
                y0 = yaxis.getScreenCoord(y);
                pos.add(x0);
                avg.add(y0);
            }
            else
            {
                // Not a plottable y value.
                final int n = pos.size();
                if (n > 0)
                {
                    // Extend previous value to here
                    pos.add(x0);
                    avg.add(avg.get(n-1));
                }
                // Draw what might have accumulated, then reset
                y0 = xaxis.getRegion().y;
                drawStaircaseLine(gc, pos, avg);
                pos.clear();
                avg.clear();
            }
            if (sample.getType() == ChartSample.Type.Point)
                markPoint(gc, x0, y0);
        }
        // Draw what might have accumulated when reaching last sample
        drawStaircaseLine(gc, pos, avg);
    }

    /** Fill area
     *  @param gc GC
     *  @param pos Horizontal screen positions
     *  @param min Minimum 'y' values in screen coords
     *  @param max .. maximum
     */
    private void fillArea(final GC gc, final ArrayList<Integer> pos,
            final ArrayList<Integer> min, final ArrayList<Integer> max)
    {
        final int N = pos.size();
        if (N <= 0)
            return;
        // Turn pos/min/max into array required by fillPolygon:
        // pos[0], min[0], pos[1], min[1], ..., pos[N-1], max[N-1], pos[N], max[N]
        final int N4 = N * 4;
        final int points[] = new int[N4];
        int head = 0, tail = N4;
        for (int i=0; i<N; ++i)
        {
            points[head++] = pos.get(i);
            points[head++] = min.get(i);
            points[--tail] = max.get(i);
            points[--tail] = pos.get(i);
        }
        gc.fillPolygon(points);
    }

    /** Draw staircase line
     *  @param gc GC
     *  @param pos Horizontal screen positions
     *  @param val Values in screen coordinates
     */
    private void drawStaircaseLine(final GC gc, final ArrayList<Integer> pos,
            final ArrayList<Integer> val)
    {
        if (pos.size() <= 0)
            return;
        // Show at least the initial point
        int x0 = pos.get(0);
        int y0 = val.get(0);
        gc.drawPoint(x0, y0);
        // If there's more, draw as lines
        for (int i=1; i<pos.size(); ++i)
        {
            final int x1 = pos.get(i);
            final int y1 = val.get(i);
            gc.drawLine(x0, y0, x1, y0);
            gc.drawLine(x1, y0, x1, y1);
            x0 = x1;
            y0 = y1;
        }
    }

    /** Draw given sample range with markers. */
    private void drawMarkers(final GC gc,
                                    final XAxis xaxis,
                                    final YAxis yaxis,
                                    final ChartSampleSequence samples,
                                    final int i0,
                                    final int i1,
                                    final int point_size)
    {
        final int half = point_size / 2;
        for (int i = i0; i <= i1; ++i)
        {
            final ChartSample sample = samples.get(i);
            final int x0 = xaxis.getScreenCoord(sample.getX());
            final double y = sample.getY();
            final boolean plottable = !Double.isInfinite(y)  &&  !Double.isNaN(y);
            int y0;
            if (plottable)
            {            
                y0 = yaxis.getScreenCoord(y);
                gc.drawRectangle(x0 - half, y0 - half,
                                point_size, point_size);
                if (sample.haveMinMax())
                {
                    final int min = yaxis.getScreenCoord(sample.getMinY());
                    final int max = yaxis.getScreenCoord(sample.getMaxY());
                    gc.drawLine(x0, y0+half, x0, max);
                    gc.drawLine(x0, y0-half, x0, min);
                }
            }
            else
                y0 = xaxis.getRegion().y;
            if (sample.getType() == ChartSample.Type.Point)
                markPoint(gc, x0, y0);
        }
    }
    
    /** Draw given sample range with bars. */
    private void drawBars(final GC gc,
                                 final XAxis xaxis,
                                 final YAxis yaxis,
                                 final ChartSampleSequence samples,
                                 final int i0,
                                 final int i1)
    {
        // final int base = xaxis.getRegion().y;
        final int base = yaxis.getScreenCoord(0.0);
        for (int i = i0; i <= i1; ++i)
        {
            final ChartSample sample = samples.get(i);
            final int x0 = xaxis.getScreenCoord(sample.getX());
            final double y = sample.getY();
            final boolean plottable = !Double.isInfinite(y)  &&  !Double.isNaN(y);
            int y0;
            if (plottable)
            {
                y0 = yaxis.getScreenCoord(y);
                gc.drawLine(x0, base, x0, y0);
            }
            else
                y0 = xaxis.getRegion().y;
            if (sample.getType() == ChartSample.Type.Point)
                markPoint(gc, x0, y0);
        }
    }
    
    /** Mark given point. */
    private void markPoint(final GC gc, final int x0, final int y0)
    {
        final int half = marker_size / 2;
        switch (marker_type)
        {
        case 0:// Square
            gc.fillRectangle(x0 - half, y0 - half, marker_size, marker_size);
            break;
        default:
            // A ']' shape
            gc.drawLine(x0, y0 - half, x0 + half, y0 - half);
            gc.drawLine(x0 + half, y0 - half, x0 + half, y0 + half);
            gc.drawLine(x0 + half, y0 + half, x0, y0 + half);
        }
    }
}
