package org.csstudio.swt.chart.axes;

import org.csstudio.swt.chart.ChartSample;
import org.csstudio.swt.chart.ChartSampleSequence;
import org.csstudio.swt.chart.Trace;
import org.eclipse.swt.graphics.GC;
import java.util.ArrayList;

/** Paints the samples of one trace.
 *  <p>
 *  Specifically, it draws a 'staircase' type line
 *  from one sample to the next:
 *  The 'y' value of a previous sample is considered valid
 *  until the next sample.
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
        int i0, i1;
        gc.setForeground(trace.getColor());
        gc.setBackground(trace.getColor());
        gc.setLineWidth(trace.getLineWidth());
        boolean new_line = true;
        YAxis yaxis = trace.getYAxis();
        ChartSampleSequence samples = trace.getSampleSequence();
        
        i0 = limiter.getLowIndex(samples);
		i1 = limiter.getHighIndex(samples);

//		 ** Lock the samples, so they don't change on us! **  
        synchronized (samples)
        {
        	switch(trace.getType()) {
        	
        	case HighLowArea :
        		
        		ArrayList<Integer> maxPoints = new ArrayList<Integer>();
            	ArrayList<Integer> minPoints = new ArrayList<Integer>();
            	
            	i0 = limiter.getLowIndex(samples);
                i1 = limiter.getHighIndex(samples);
                
                // We'll have to find max and min y values at same x value.
                int minY = Integer.MAX_VALUE;
                int maxY = Integer.MIN_VALUE;
                
                x1 = Integer.MIN_VALUE;
                y1 = Integer.MIN_VALUE;
                
                for (i = i0; i <= i1; ++i)
                {
                    ChartSample sample = samples.get(i);
                    
                    x0 = xaxis.getScreenCoord(sample.getX());
                    y0 = yaxis.getScreenCoord(sample.getY());
                    
                    if (x1 != x0) 
                    {
                    	// We add previous min/max values to array.
                    	if(x1 != Integer.MIN_VALUE)
                    	{
                    		maxPoints.add(x1);
                    		maxPoints.add(maxY);
                    		minPoints.add(x1);
                    		minPoints.add(minY);
                    	}
                    	
                    	// Set new values.
                    	x1 = x0;
                    	minY = y0;
                        maxY = y0;
                    }
                    else {
                    	if(y0 > maxY)
                    		maxY = y0;
                    	if(y0 < minY)
                    		minY = y0;
                    }
                    
                    int[] points = new int[maxPoints.size()];
                    int pointer = 0;
                    
                    for(Integer integer : maxPoints) {
                    	points[pointer]  = (int)integer;
                    	pointer++;
                    }
                    
                    gc.drawPolyline(points);

                    points = new int[minPoints.size()];
                    pointer = 0;
                    
                    for(Integer integer : minPoints) {
                    	points[pointer]  = (int)integer;
                    	pointer++;
                    }
                    
                    gc.drawPolyline(points);
                }
        		
        		
        		break;
        		
        	case Markers:
        		
        		int d1 = trace.getLineWidth();
        		
        		for (i = i0; i <= i1; ++i)
                {
                    ChartSample sample = samples.get(i);
                    
                    x0 = xaxis.getScreenCoord(sample.getX());
                    y0 = yaxis.getScreenCoord(sample.getY());
                    
                    if(d1 <= 0)
                    	gc.drawPoint(x0, y0);
                    else
                    	gc.drawRectangle(x0 - (int)Math.floor(d1/2), y0 - (int)Math.floor(d1/2), d1, d1);
                    
                }
        		break;
        		
        	case Candlestick:
        		
                x1 = Integer.MIN_VALUE;
                y1 = Integer.MIN_VALUE;
                
                //int minY0, maxY0;
                //int minY1, maxY1;
                
                for (i = i0; i <= i1; ++i)
                {
                    ChartSample sample = samples.get(i);
                    
                    x0 = xaxis.getScreenCoord(sample.getX());
                    y0 = yaxis.getScreenCoord(sample.getY());
                    
                    if (x1 != x0) {
                    	x1 = x0;
                    	y1 = y0;
                    	gc.fillRectangle(x0 - 1, y0 - 1, 3, 3);
                    }
                    	
                    else {
                    	gc.drawLine(x0, y1, x0, y0);
                    	gc.fillRectangle(x0 - 1, y0 - 1, 3, 3);
                    	y1 = y0;
                    }
                }

                break;
        	
        	case Lines:
        		
				// Instead of painting the whole trace, find the
				// first and last sample that's actually visible on this x axis.
				// Of course this requires the x values to be rising.
				
				double y;
				for (i = i0; i <= i1; ++i) {
					ChartSample sample = samples.get(i);
					y = sample.getY();
					boolean plottable = !Double.isInfinite(y)
							&& !Double.isNaN(y);
					if (new_line) { // Sample starts a new line. Don't have a previous
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
					} else { // line from last to current point
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
					if (sample.getType() == ChartSample.Type.Point) {
						if (true) { // Square
							gc
									.fillRectangle(x0 - marker_size / 2, y0
											- marker_size / 2, marker_size,
											marker_size);
						}
						if (false) { // A ']' shape
							int d = marker_size / 2;
							gc.drawLine(x0, y0 - d, x0 + d, y0 - d);
							gc.drawLine(x0 + d, y0 - d, x0 + d, y0 + d);
							gc.drawLine(x0 + d, y0 + d, x0, y0 + d);
						}
					}
				}
				break;
			}
        }
    }
}
