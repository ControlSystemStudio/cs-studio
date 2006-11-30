package org.csstudio.swt.chart.axes;

/** Factory for the default YAxis.
 *  @author Kay Kasemir
 */
public class YAxisFactory
{
    /** @return Returns a new YAxis. */
    public YAxis createYAxis(String label, YAxisListener listener)
    {
        return new YAxis(label, listener);
    }
}
