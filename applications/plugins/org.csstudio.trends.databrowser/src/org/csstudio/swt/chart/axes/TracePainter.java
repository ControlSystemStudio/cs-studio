package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Trace;
import org.eclipse.swt.graphics.GC;

/**
 * Paints the samples of one trace.
 * <p>
 * Specifically, it draws a 'staircase' type line from one sample to the next:
 * The 'y' value of a previous sample is considered valid until the next sample.
 * 
 * @author Kay Kasemir
 */
public class TracePainter
{
    private static final int marker_size = 10;

    /** Paint a trace over given X axis. */
    static public void paint(GC gc, Trace trace, XAxis xaxis)
    {
        final AxisRangeLimiter limiter = new AxisRangeLimiter(xaxis);
        int i, x0 = 0, y0 = 0, x1, y1;
        gc.setForeground(trace.getColor());
        gc.setBackground(trace.getColor());
        gc.setLineWidth(trace.getLineWidth());
        YAxis yaxis = trace.getYAxis();
        ChartSampleSequence samples = trace.getSampleSequence();
        // ** Lock the samples, so they don't change on us! **
        synchronized (samples)
        {
            // Instead of painting the whole trace, find the
            // first and last sample that's actually visible on this x axis.
            // Of course this requires the x values to be rising.
            int i0 = limiter.getLowIndex(samples);
            int i1 = limiter.getHighIndex(samples);
    
            switch (trace.getType())
            {
            case Lines:
            {
                boolean new_line = true;
                for (i = i0; i <= i1; ++i)
                {
                    ChartSample sample = samples.get(i);
                    double y = sample.getY();
                    boolean plottable = !Double.isInfinite(y)
                                    && !Double.isNaN(y);
                    if (new_line)
                    {   // Sample starts a new line. Don't have a previous
                        // sample from which to draw a staircase connection.
                        // Remember coordinates
                        x0 = xaxis.getScreenCoord(sample.getX());
                        y0 = yaxis.getScreenCoord(y);
                        if (plottable)
                            gc.drawPoint(x0, y0);
                        // If we skip a line to/from this point,
                        // we'll still need another x0/y0.
                        // Otherwise, we are now ready to draw a line to the
                        // next sample.
                        new_line = !plottable;
                    }
                    else
                    {   // line from last to current point
                        x1 = xaxis.getScreenCoord(sample.getX());
                        y1 = yaxis.getScreenCoord(y);
                        // Staircase: Line from the last sample...
                        gc.drawLine(x0, y0, x1, y0);
                        if (plottable) // to this one
                            gc.drawLine(x1, y0, x1, y1);
                        else
                            new_line = true; // or end current line, start new one
                        x0 = x1;
                        y0 = y1;
                    }
                    // TODO Move this into a 'SampleDecorator'?
                    if (sample.getType() == ChartSample.Type.Point)
                    {
                        int half = marker_size / 2;
                        if (true)
                        {   // Square
                            gc.fillRectangle(x0 - half, y0 - half,
                                            marker_size, marker_size);
                        }
                        if (false)
                        { // A ']' shape
                            gc.drawLine(x0, y0 - half, x0 + half, y0 - half);
                            gc.drawLine(x0 + half, y0 - half, x0 + half, y0 + half);
                            gc.drawLine(x0 + half, y0 + half, x0, y0 + half);
                        }
                    }
                }
                break;
            }
            case Bars:
                int top_of_x_axis = xaxis.getRegion().y;
                for (i = i0; i <= i1; ++i)
                {
                    ChartSample sample = samples.get(i);
                    x0 = xaxis.getScreenCoord(sample.getX());
                    y0 = yaxis.getScreenCoord(sample.getY());
                    gc.drawLine(x0, y0, x0, top_of_x_axis);
                }
                break;
                /*
            case MinMaxAverage:
                x1 = Integer.MIN_VALUE;
                y1 = Integer.MIN_VALUE;
                i = i0;
                int[] triplet = new int[]
                { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
                int k = 0;
                int x2 = 0;

                double realX0,
                realX1;
                realX1 = samples.get(0).getX();
                x1 = xaxis.getScreenCoord(realX1);

                while (i <= i1)
                {
                    // Previous value was not a triplet so we ignore it.
                    if (k != 3)
                    {
                        triplet = new int[]
                        { Integer.MIN_VALUE, Integer.MIN_VALUE,
                                Integer.MIN_VALUE };
                    }

                    k = 0;
                    while (true)
                    {
                        ChartSample sample = samples.get(i);
                        realX0 = sample.getX();

                        x0 = xaxis.getScreenCoord(realX0);
                        y0 = yaxis.getScreenCoord(sample.getY());

                        // We'll check if we are on the same triplet here.
                        if (realX0 != realX1 || k > 2)
                            break;

                        // Connect previous value of triplet with current one
                        // using line.
                        if (triplet[k] != Integer.MIN_VALUE)
                            gc.drawLine(x2, triplet[k], x0, y0);

                        // Let's set new value.
                        triplet[k] = y0;

                        // Move foward.
                        k++;
                        i++;
                    }

                    // Assign new values.
                    x2 = x1;
                    x1 = x0;
                    realX1 = realX0;
                }
                break;
*/
            // Paints each point.
            case Markers:
            {
                int point_size = trace.getLineWidth();
                if (point_size < 2)
                    point_size = 2;
                int half = point_size / 2;
                for (i = i0; i <= i1; ++i)
                {
                    ChartSample sample = samples.get(i);
                    x0 = xaxis.getScreenCoord(sample.getX());
                    y0 = yaxis.getScreenCoord(sample.getY());
                    gc.drawRectangle(x0 - half,
                                     y0 - half,
                                     point_size, point_size);
                }
                break;
            }
            // Paints values on same x as candlesticks.
            /*
            case Candlestick:
            {
                x1 = Integer.MIN_VALUE;
                y1 = Integer.MIN_VALUE;
                for (i = i0; i <= i1; ++i)
                {
                    ChartSample sample = samples.get(i);
                    x0 = xaxis.getScreenCoord(sample.getX());
                    y0 = yaxis.getScreenCoord(sample.getY());
                    if (x1 != x0)
                    {
                        x1 = x0;
                        y1 = y0;
                        gc.fillRectangle(x0 - 1, y0 - 1, 3, 3);
                    }
                    else
                    {
                        gc.drawLine(x0, y1, x0, y0);
                        gc.fillRectangle(x0 - 1, y0 - 1, 3, 3);
                        y1 = y0;
                    }
                }
                break;
            }
            */
            }
        }
    }
}
