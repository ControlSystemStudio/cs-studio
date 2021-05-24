/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.internal;

import org.csstudio.swt.rtplot.Axis;
import org.csstudio.swt.rtplot.PointType;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.swt.rtplot.Trace;
import org.csstudio.swt.rtplot.TraceType;
import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.csstudio.swt.rtplot.data.PlotDataProvider;
import org.csstudio.swt.rtplot.internal.util.IntList;
import org.csstudio.swt.rtplot.internal.util.ScreenTransform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/** Helper for painting a {@link Trace}
 *  @param <XTYPE> Data type of horizontal {@link Axis}
 *  @author Kay Kasemir
 */
public class TracePainter<XTYPE extends Comparable<XTYPE>>
{
    // Implementation notes:
    // gc.drawPolyline() is faster than gc.drawLine() calls
    // plus it works better when using dashed or wide lines,
    // but it requires an int[] array of varying size.
    // IntList turned out to be about 3x faster than ArrayList<Integer>.

    /** Initial {@link IntList} size */
    private static final int INITIAL_ARRAY_SIZE = 2048;

    /** Fudge to avoid clip errors
     *
     *  <p>When coordinates are way outside the clip region,
     *  clipping fails and graphics are 'aliases' into the visible range.
     *  By moving clipped coordinates just 'OUTSIDE' the allowed region,
     *  rounding errors inside the clipping implementation are avoided.
     *  Strictly speaking, we'd have to compute the intersection of
     *  lines with the clip region, but this is much easier to implement.
     */
    final private static int OUTSIDE = 1000;
    private int x_min, x_max, y_min, y_max;

    final private int clipX(final double x)
    {
        if (x < x_min)
            return x_min;
        if (x > x_max)
            return x_max;
        return (int)x;
    }

    final private int clipY(final int y)
    {
        if (y < y_min)
            return y_min;
        if (y > y_max)
            return y_max;
        return y;
    }

    /** @param gc GC
     *  @param media
     *  @param bounds Clipping bounds within which to paint
     *  @param opacity Opacity (0 .. 100 %) of 'area'
     *  @param x_transform Coordinate transform used by the x axis
     *  @param trace Trace, has reference to its value axis
     */
    final public void paint(final GC gc, final SWTMediaPool media, final Rectangle bounds, final int opacity,
                            final ScreenTransform<XTYPE> x_transform, final YAxisImpl<XTYPE> y_axis, final Trace<XTYPE> trace)
    {
        if (! trace.isVisible() )
            return;

        x_min = bounds.x - OUTSIDE;
        x_max = bounds.x + bounds.width + OUTSIDE;
        y_min = bounds.y - OUTSIDE;
        y_max = bounds.y + bounds.height + OUTSIDE;

        final Color old_color = gc.getForeground();
        final Color old_bg = gc.getBackground();
        final int old_width = gc.getLineWidth();

        final int alpha = (opacity * 255) / 100;

        final Color color = media.get(trace.getColor());
        gc.setBackground(color);
        gc.setForeground(color);

        // TODO Use anti-alias?
        gc.setAdvanced(true);
        gc.setAntialias(SWT.ON);

        // TODO Optimize drawing
        //
        // Determine first sample to draw via PlotDataSearch.findSampleLessOrEqual(),
        // then end drawing when reaching right end of area.
        //
        // Loop only once, performing drawMinMax, drawStdDev, drawValueStaircase in one loop
        //
        // For now, main point is that this happens in non-UI thread,
        // so the slower the better to test UI responsiveness.
        final PlotDataProvider<XTYPE> data = trace.getData();
        data.getLock().lock();
        try
        {
            final TraceType type = trace.getType();
            switch (type)
            {
            case NONE:
                break;
            case AREA:
                gc.setAlpha(alpha);
                drawMinMaxArea(gc, x_transform, y_axis, data);
                gc.setAlpha(255);
                drawValueStaircase(gc, x_transform, y_axis, data, trace.getWidth());
                break;
            case AREA_DIRECT:
                gc.setAlpha(alpha);
                drawMinMaxArea(gc, x_transform, y_axis, data);
                gc.setAlpha(255);
                drawValueLines(gc, x_transform, y_axis, data, trace.getWidth());
                break;
            case LINES:
                drawMinMaxLines(gc, x_transform, y_axis, data, trace.getWidth());
                gc.setAlpha(alpha);
                gc.setAlpha(255);
                drawValueStaircase(gc, x_transform, y_axis, data, trace.getWidth());
                break;
            case LINES_DIRECT:
                drawMinMaxLines(gc, x_transform, y_axis, data, trace.getWidth());
                gc.setAlpha(alpha);
                gc.setAlpha(255);
                drawValueLines(gc, x_transform, y_axis, data, trace.getWidth());
                break;
            case SINGLE_LINE:
                drawValueStaircase(gc, x_transform, y_axis, data, trace.getWidth());
                break;
            case SINGLE_LINE_DIRECT:
                drawValueLines(gc, x_transform, y_axis, data, trace.getWidth());
                break;
            }

            final PointType point_type = trace.getPointType();
            switch (point_type)
            {
            case NONE:
                break;
            case SQUARES:
            case CIRCLES:
            case DIAMONDS:
            case XMARKS:
            case TRIANGLES:
                drawPoints(gc, x_transform, y_axis, data, point_type, trace.getPointSize());
                break;
            }
        }
        finally
        {
            data.getLock().unlock();
        }
        gc.setLineWidth(old_width);
        gc.setBackground(old_bg);
        gc.setForeground(old_color);
    }

    /** Draw values of data as staircase line
     *  @param gc GC
     *  @param x_transform Horizontal axis
     *  @param y_axis Value axis
     *  @param data Data
     *  @param line_width
     */
    final private void drawValueStaircase(final GC gc,
            final ScreenTransform<XTYPE> x_transform, final YAxisImpl<XTYPE> y_axis,
            final PlotDataProvider<XTYPE> data, final int line_width)
    {
        final IntList value_poly = new IntList(INITIAL_ARRAY_SIZE);
        final int N = data.size();
        int last_x = -1, last_y = -1;
        gc.setLineWidth(line_width);
        for (int i=0; i<N; ++i)
        {
            final PlotDataItem<XTYPE> item = data.get(i);
            final int x = clipX(Math.round(x_transform.transform(item.getPosition())));
            final double value = item.getValue();
            if (value_poly.size() > 0  && x != last_x)
            {   // Staircase from last 'y'..
                value_poly.add(x);
                value_poly.add(last_y);
                last_x = x;
            }
            if (Double.isNaN(value))
            {
                flushPolyLine(gc, value_poly, line_width);
                last_x = last_y = -1;
            }
            else
            {
                final int y = clipY(y_axis.getScreenCoord(value));
                if (last_x == x  &&  last_y == y)
                    continue;
                value_poly.add(x);
                value_poly.add(y);
                last_y = y;
            }
        }
        flushPolyLine(gc, value_poly, line_width);
    }

    /** Draw values of data as direct line
     *  @param gc GC
     *  @param x_transform Horizontal axis
     *  @param y_axis Value axis
     *  @param data Data
     *  @param line_width
     */
    final private void drawValueLines(final GC gc,
            final ScreenTransform<XTYPE> x_transform, final YAxisImpl<XTYPE> y_axis,
            final PlotDataProvider<XTYPE> data, final int line_width)
    {
        final IntList value_poly = new IntList(INITIAL_ARRAY_SIZE);
        final int N = data.size();
        gc.setLineWidth(line_width);
        int last_x = -1, last_y = -1;
        for (int i=0; i<N; ++i)
        {
            final PlotDataItem<XTYPE> item = data.get(i);
            final int x = clipX(Math.round(x_transform.transform(item.getPosition())));
            final double value = item.getValue();
            if (Double.isNaN(value))
            {
                flushPolyLine(gc, value_poly, line_width);
                last_x = last_y = -1;
            }
            else
            {
                final int y = clipY(y_axis.getScreenCoord(value));
                if (x == last_x  &&  y == last_y)
                    continue;
                value_poly.add(x);
                value_poly.add(y);
                last_x = x;
                last_y = y;
            }
        }
        flushPolyLine(gc, value_poly, line_width);
    }

    /** Draw min/max outline
     *  @param gc GC
     *  @param x_transform Horizontal axis
     *  @param y_axis Value axis
     *  @param data Data
     */
    final private void drawMinMaxArea(final GC gc,
            final ScreenTransform<XTYPE> x_transform, final YAxisImpl<XTYPE> y_axis,
            final PlotDataProvider<XTYPE> data)
    {
        final IntList pos = new IntList(INITIAL_ARRAY_SIZE);
        final IntList min = new IntList(INITIAL_ARRAY_SIZE);
        final IntList max = new IntList(INITIAL_ARRAY_SIZE);

        final int N = data.size();
        for (int i = 0;  i < N;  ++i)
        {
            final PlotDataItem<XTYPE> item = data.get(i);
            double ymin = item.getMin();
            double ymax = item.getMax();
            if (Double.isNaN(ymin)  ||  Double.isNaN(ymax))
                flushPolyFill(gc, pos, min, max);
            else
            {
                final int x1 = clipX(x_transform.transform(item.getPosition()));
                final int y1min = clipY(y_axis.getScreenCoord(ymin));
                final int y1max = clipY(y_axis.getScreenCoord(ymax));
                pos.add(x1);
                min.add(y1min);
                max.add(y1max);
            }
        }
        flushPolyFill(gc, pos, min, max);
    }

    /** Draw min/max outline
     *  @param gc GC
     *  @param x_transform Horizontal axis
     *  @param y_axis Value axis
     *  @param data Data
     */
    final private void drawMinMaxLines(final GC gc,
            final ScreenTransform<XTYPE> x_transform, final YAxisImpl<XTYPE> y_axis,
            final PlotDataProvider<XTYPE> data, final int line_width)
    {
        final IntList min = new IntList(INITIAL_ARRAY_SIZE);
        final IntList max = new IntList(INITIAL_ARRAY_SIZE);

        final int N = data.size();
        for (int i = 0;  i < N;  ++i)
        {
            final PlotDataItem<XTYPE> item = data.get(i);
            double ymin = item.getMin();
            double ymax = item.getMax();
            if (Double.isNaN(ymin)  ||  Double.isNaN(ymax))
            {
                flushPolyLine(gc, min, line_width);
                flushPolyLine(gc, max, line_width);
            }
            else
            {
                final int x1 = clipX(x_transform.transform(item.getPosition()));
                final int y1min = clipY(y_axis.getScreenCoord(ymin));
                final int y1max = clipY(y_axis.getScreenCoord(ymax));
                min.add(x1);   min.add(y1min);
                max.add(x1);   max.add(y1max);
            }
        }
        flushPolyLine(gc, min, line_width);
        flushPolyLine(gc, max, line_width);
    }

    /** @param gc GC
     *  @param poly Points of poly line, will be cleared
     *  @param line_width
     */
    final private void flushPolyLine(final GC gc, final IntList poly, final int line_width)
    {
        final int N = poly.size();
        if (N == 2)
            drawPoint(gc, poly.get(0), poly.get(1), line_width);
        else if (N > 1)
            gc.drawPolyline(poly.toArray());
        poly.clear();
    }

    /** Draw values of data as direct line
     *  @param gc GC
     *  @param x_transform Horizontal axis
     *  @param y_axis Value axis
     *  @param data Data
     *  @param point_type
     *  @param size
     */
    final private void drawPoints(final GC gc,
            final ScreenTransform<XTYPE> x_transform, final YAxisImpl<XTYPE> y_axis,
            final PlotDataProvider<XTYPE> data, PointType point_type, final int size)
    {
        final int N = data.size();
        int last_x = -1, last_y = -1;
        for (int i=0; i<N; ++i)
        {
            final PlotDataItem<XTYPE> item = data.get(i);
            final int x = clipX(Math.round(x_transform.transform(item.getPosition())));
            final double value = item.getValue();
            if (!Double.isNaN(value))
            {
                final int y = clipY(y_axis.getScreenCoord(value));
                if (x == last_x  &&  y == last_y)
                    continue;
                switch (point_type)
                {
                case SQUARES:
                    gc.fillRectangle(x-size/2, y-size/2, size, size);
                    break;
                case DIAMONDS:
                    gc.fillPolygon(new int[]
                    {
                        x, y-size/2,
                        x+size/2, y,
                        x, y+size/2,
                        x-size/2, y,
                    });
                    break;
                case XMARKS:
                    gc.drawLine(x-size/2, y-size/2, x+size/2, y+size/2);
                    gc.drawLine(x-size/2, y+size/2, x+size/2, y-size/2);
                    break;
                case TRIANGLES:
                    gc.fillPolygon(new int[]
                    {
                        x, y-size/2,
                        x+size/2, y+size/2,
                        x-size/2, y+size/2,
                    });
                    break;
                case CIRCLES:
                default:
                    drawPoint(gc, x, y, size);
                }
                last_x = x;
                last_y = y;
            }
        }
    }

    /** @param gc GC
     *  @param x Coordinate
     *  @param y .. of point on screen
     *  @param size
     */
    final private void drawPoint(final GC gc, final int x, final int y, final int size)
    {
        gc.fillOval(x-size/2, y-size/2, size, size);
    }

    /** Fill area. All lists will be cleared.
     *  @param gc GC
     *  @param pos Horizontal screen positions
     *  @param min Minimum 'y' values in screen coords
     *  @param max .. maximum
     */
    @SuppressWarnings("unused")
    final private void flushPolyFill(final GC gc, final IntList pos, final IntList min, final IntList max)
    {
        final int N = pos.size();
        if (N <= 0)
            return;

        if (true)
        {
            // 'direct' outline, point-to-point
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
        else
        {
            // 'staircase' outline
            final int points[] = new int[8*N-4];

            int p = 0;
            points[p++] = pos.get(0);
            int ly = points[p++] = min.get(0);
            for (int i=1; i<N; ++i)
            {
                points[p++] = pos.get(i);
                points[p++] = ly;
                points[p++] = pos.get(i);
                ly = points[p++] = min.get(i);
            }

            int lx = points[p++] = pos.get(N-1);
            points[p++] = max.get(N-1);
            for (int i=N-2; i>=0; --i)
            {
                points[p++] = lx;
                points[p++] = max.get(i);
                lx = points[p++] = pos.get(i);
                points[p++] = max.get(i);
            }
            gc.fillPolygon(points);
        }

        pos.clear();
        min.clear();
        max.clear();
    }
}
