package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Trace;
import org.eclipse.swt.graphics.GC;

/** Paints the samples of one trace.
 *  @author Kay Kasemir
 */
public class TracePainter
{
    private static final int marker_size = 10;

    /** Paint a trace over given X axis. */
    static public void paint(GC gc, Trace trace, XAxis xaxis)
    {
        AxisRangeLimiter limiter = new AxisRangeLimiter(xaxis);
        int i;
        int x0 = 0, y0 = 0, x1, y1;
        gc.setForeground(trace.getColor());
        gc.setBackground(trace.getColor());
        gc.setLineWidth(trace.getLineWidth());
        boolean need_x0y0 = true;
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
            for (i = i0; i <= i1; ++i)
            {
                ChartSample sample = samples.get(i);
                double y = sample.getY();
                boolean no_line = Double.isInfinite(y) || Double.isNaN(y);
                if (need_x0y0 || no_line)
                {   // Remember coordinates
                    x0 = xaxis.getScreenCoord(sample.getX());
                    y0 = yaxis.getScreenCoord(y);
                    // If we skip a line to/from this point,
                    // we'll still need another x0/y0.
                    // Otherwise, we are now ready to draw a line to the
                    // next sample.
                    need_x0y0 = no_line;
                }
                else
                {   // line from last to current point
                    x1 = xaxis.getScreenCoord(sample.getX());
                    y1 = yaxis.getScreenCoord(y);
                    if (false)
                    { // Simple line to the next sample
                        gc.drawLine(x0, y0, x1, y1);
                    }
                    else
                    {   // Staircase to the next sample
                        gc.drawLine(x0, y0, x1, y0);
                        gc.drawLine(x1, y0, x1, y1);
                    }
                    x0 = x1;
                    y0 = y1;
                }
                // TODO Move this into a 'SampleDecorator'?
                if (sample.getType() == ChartSample.Type.Point)
                {
                    if (true)
                    {   // Square
                        gc.fillRectangle(x0 - marker_size / 2,
                                         y0 - marker_size / 2,
                                         marker_size, marker_size);
                    }
                    if (false)
                    {   // A ']' shape
                        int d = marker_size/2;
                        gc.drawLine(x0, y0-d, x0+d, y0-d);
                        gc.drawLine(x0+d, y0-d, x0+d, y0+d);
                        gc.drawLine(x0+d, y0+d, x0, y0+d);
                    }
                }
            }
        }
    }
}
