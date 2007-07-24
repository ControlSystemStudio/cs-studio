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
    enum Type
    {
        /** A normal sample, plot it together with the rest. */
        Normal,
        
        /** A gap, terminating a "line", displayed as a single point. */
        Point
    };
    
    /** @return One of the Type values. */
    public Type getType();
    
    /** @return The x value. */
    public double getX();

    /** @return The y value. */
    public double getY();

    /** @return <code>true</code> if this sample has Y error (range) info.
     *  @see #getMinY()
     *  @see #getMaxY()
     */
    public boolean haveMinMax();
    
    /** @return Minimum y value in case there is an Y error (range).
     *  @see #haveMinMax()
     */
    public double getMinY();

    /** @return Maximum y value in case there is an Y error (range).
     *  @see #haveMinMax()
     */
    public double getMaxY();

    /** @return Any informational string that might work as e.g. a Tooltip. */
    public String getInfo();
}
