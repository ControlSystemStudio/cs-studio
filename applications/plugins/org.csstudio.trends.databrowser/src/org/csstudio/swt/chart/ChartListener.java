package org.csstudio.swt.chart;

import org.csstudio.swt.chart.axes.XAxis;
import org.csstudio.swt.chart.axes.XAxisListener;
import org.csstudio.swt.chart.axes.YAxis;
import org.csstudio.swt.chart.axes.YAxisListener;

/** Listener for a Chart.
 *  @see org.csstudio.swt.chart.Chart
 *  @author Kay Kasemir
 */
public interface ChartListener extends XAxisListener, YAxisListener
{
    /** A coordinate was selected.
     *  Somebody clicked the mouse on the graph area.
     */
    public void pointSelected(XAxis xaxis, YAxis yaxis, double x, double y);

    /** Nothing on the plot is selected.
     *  Somebody clicked the mouse in the chart area,
     *  but missed the graph.
     */
    public void nothingSelected();
}
