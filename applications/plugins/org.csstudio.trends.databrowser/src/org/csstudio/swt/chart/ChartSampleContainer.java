package org.csstudio.swt.chart;

/** A simple <code>Sample</code> container.
 *  <p>
 *  Users can use this to store samples, or implement the Sample interface
 *  otherwise.
 *  
 *  @see ChartSample
 *  @see ChartSampleSequence
 *  
 *  @author Kay Kasemir
 */
public class ChartSampleContainer implements ChartSample
{
    private int type;
    private double x;
    private double y;
    private String info;
    
    /** Construct new sample from values.
     *  @see #ChartSampleContainer(int, double, double, String)
     */
    public ChartSampleContainer(double x, double y)
    {
        this(ChartSample.TYPE_NORMAL, x, y, null);
    }

    /** Construct new sample from values.
     *  @see #ChartSampleContainer(int, double, double, String)
     */
    public ChartSampleContainer(int type, double x, double y)
    {
        this(type, x, y, null);
    }

    /** Construct new sample from values.
     *  @param type One of the Sample.TYPE_... values
     *  @param x X coordinate
     *  @param y Y coordinate
     *  @param info Info string, e.g. for tooltip, or <code>null</code>.
     */
    public ChartSampleContainer(int type, double x, double y, String info)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.info = info;
    }
    
    /** @return Returns one of the TYPE_... values. */
    public int getType()
    {
        return type;
    }
    
    /** @return Returns the x value. */
    public double getX()
    {
        return x;
    }
    
    /** @return Returns the y value. */
    public double getY()
    {
        return y;
    }
    
    /** @return Any informational string that might work as e.g. a Tooltip. */
    public String getInfo()
    {
        return info;
    }
}
