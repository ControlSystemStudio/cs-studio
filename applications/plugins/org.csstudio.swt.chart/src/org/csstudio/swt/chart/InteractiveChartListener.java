package org.csstudio.swt.chart;

/** Events that the InteractiveChart sends.
 *  @author Kay Kasemir
 */
public interface InteractiveChartListener
{
    /** The button bar became visible or hidden
     *  @param visible <code>true</code> if it's not visible
     */
    public void buttonBarChanged(boolean visible);
}
