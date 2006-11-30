package org.csstudio.swt.chart.axes;

/** Factory for the TraceNameYAxis.
 *  @author Kay Kasemir
 */
public class TraceNameYAxisFactory extends YAxisFactory
{
    /** @return Returns a new YAxis. */
    @Override
    public YAxis createYAxis(String label, YAxisListener listener)
    {
        return new TraceNameYAxis(label, listener);
    }
}
