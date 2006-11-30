package org.csstudio.swt.chart;

/** Interface for one sample, i.e. one "point" on the chart.
 *  <p>
 *  The user of the chart must provide each sample via this interface.
 *  
 *  @see ChartSampleContainer
 *  @see ChartSampleSequence
 *  
 *  @author Kay Kasemir
 */
public interface ChartSample
{
    /** A normal sample, plot it together with the rest. */
    public static final int TYPE_NORMAL = 0;
    
    /** A gap, terminating a "line",displayed as a single point. */
    public static final int TYPE_POINT = 1;
    
    /** @return One of the TYPE_... values. */
    public int getType();
    
    /** @return The x value. */
    public double getX();

    /** @return The y value. */
    public double getY();

    /** @return Any informational string that might work as e.g. a Tooltip. */
    public String getInfo();
}
