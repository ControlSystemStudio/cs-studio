package org.csstudio.swt.chart.axes;


/** Listener for XAxis.
 * 
 *  @see org.csstudio.swt.chart.axes.XAxis
 *  @author Kay Kasemir
 */
public interface XAxisListener
{
    /** Something changed the X axis range. */ 
    public void changedXAxis(XAxis xaxis);
}
