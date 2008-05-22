package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.Chart;
import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Trace;
import org.eclipse.swt.graphics.GC;

/** Paints the samples of one trace.
 *  <p>
 *  Specifically, it draws a 'staircase' type line from one sample to the next:
 *  The 'y' value of a previous sample is considered valid until the next sample.
 * 
 *  @author Blaz Lipuscek 
 *  @author Kay Kasemir
 */
public class TracePainter
{
    private static final int marker_size = 10;

    /** Paint a trace over given X axis. */
    static public void paint(GC gc, Trace trace, XAxis xaxis)
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
        synchronized (samples)
        {
            // Instead of painting the whole trace, find the
            // first and last sample that's actually visible on this x axis.
            // Of course this requires the x values to be rising.
            final int i0 = limiter.getLowIndex(samples);
            final int i1 = limiter.getHighIndex(samples);
            switch (trace.getType())
            {
            case Lines:
                drawLines(gc, xaxis, yaxis, samples, i0, i1);
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
    }

    /** Draw given sample range with lines.
     *  <p>
     *  For samples with min/max info,
     *  draw separate min, average and max lines.
     *  <br>
     *  For 'original' samples without min/max info,
     *  a staircase is drawn since we assume each
     *  sample remains 'valid' until the next sample.
     *  <br>
     *  Mixed cases like min/max sample to/from original sample,
     *  we still use 3 lines.
     *  From min/max sample to a gap, we continue the last
     *  min/max to the gap.
     *  <p>
     *  (Basically, this automatically switched between the old 'Lines'
     *   and Blaz's 'MinMaxAverage' methods)
     */
    private static void drawLines(final GC gc,
                                  final XAxis xaxis,
                                  final YAxis yaxis,
                                  final ChartSampleSequence samples,
                                  final int i0,
                                  final int i1)
    {
        int x0=0, y0=0, y0_min=0, y0_max=0; // x/y of the previous point if !new_line
        boolean new_line = true;            // Start of new line, or is x0/y0 set?
        boolean have_pre_min_max = false;   // Are y0_min/max set?
        for (int i = i0; i <= i1; ++i)
        {
            final ChartSample sample = samples.get(i);
            final double y = sample.getY();
            final boolean plottable = !Double.isInfinite(y)  &&  !Double.isNaN(y);
            final boolean have_min_max = sample.haveMinMax();
            // No previous sample from which to draw a connection?
            if (new_line)
            {
                if (have_pre_min_max) throw new Error("Invalid state"); //$NON-NLS-1$
                // Immediately set x/y of the 'previous' point.
                x0 = xaxis.getScreenCoord(sample.getX());
                y0 = yaxis.getScreenCoord(y);
                if (plottable)
                {
                    new_line = false;
                    if (have_min_max)
                    {   // Show and remember min/max of this sample
                        y0_min = yaxis.getScreenCoord(sample.getMinY());
                        y0_max = yaxis.getScreenCoord(sample.getMaxY());
                        gc.drawLine(x0, y0_min, x0, y0_max);
                        have_pre_min_max = true;
                    }
                    else // Draw point. have_pre_min_max stays false
                        gc.drawPoint(x0, y0);
                }
                // else new_line stays true
                // No 'continue', since we might still draw a Point decorator
            }
            else
            {   // new_line=false; x0/y0 are set.
                final int x1 = xaxis.getScreenCoord(sample.getX());
                if (plottable)
                {
                    final int y1 = yaxis.getScreenCoord(y);
                    if (have_min_max)
                    {
                        final int y1_min = yaxis.getScreenCoord(sample.getMinY());
                        final int y1_max = yaxis.getScreenCoord(sample.getMaxY());
                        if (have_pre_min_max)
                        {   // Connect old and new min/max/average
                            gc.drawLine(x0, y0_min, x1, y1_min);
                            gc.drawLine(x0, y0_max, x1, y1_max);
                            gc.drawLine(x0, y0,     x1, y1);
                        }
                        else
                        {   // Connect old sample to new min/max/average
                            gc.drawLine(x0, y0, x1, y1_min);
                            gc.drawLine(x0, y0, x1, y1_max);
                            gc.drawLine(x0, y0, x1, y1);
                        }
                        y0_min = y1_min;
                        y0_max = y1_max;
                        have_pre_min_max = true;
                    }
                    else
                    {
                        if (have_pre_min_max)
                        {   // Connect old min/max/avg to new sample
                            gc.drawLine(x0, y0_min, x1, y1);
                            gc.drawLine(x0, y0_max, x1, y1);
                            gc.drawLine(x0, y0,     x1, y1);
                            have_pre_min_max = false;
                        }
                        else
                        {   // Connect old and new sample with staircase(!)
                            gc.drawLine(x0, y0, x1, y0);
                            gc.drawLine(x1, y0, x1, y1);
                        }
                    }
                    // Remember y value
                    y0 = y1;
                }
                else
                {   // Current sample not plotable.
                    // Extend last sample's value...
                    gc.drawLine(x0, y0, x1, y0);
                    if (have_pre_min_max)
                    {   // ... and min/max
                        gc.drawLine(x0, y0_min, x1, y0_min);
                        gc.drawLine(x0, y0_max, x1, y0_max);
                        have_pre_min_max = false;
                    }
                    new_line = true;
                    y0 = xaxis.getRegion().y;
                }
                // Remember x value
                x0 = x1;
            }
            if (sample.getType() == ChartSample.Type.Point)
                markPoint(gc, x0, y0);
        }
    }

    /** Draw given sample range with markers. */
    private static void drawMarkers(final GC gc,
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
    private static void drawBars(final GC gc,
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
    private static void markPoint(final GC gc, final int x0, final int y0)
    {
        final int half = marker_size / 2;
        if (true)
        {   // Square
            gc.fillRectangle(x0 - half, y0 - half, marker_size, marker_size);
        }
        if (false)
        { // A ']' shape
            gc.drawLine(x0, y0 - half, x0 + half, y0 - half);
            gc.drawLine(x0 + half, y0 - half, x0 + half, y0 + half);
            gc.drawLine(x0 + half, y0 + half, x0, y0 + half);
        }
    }
}
