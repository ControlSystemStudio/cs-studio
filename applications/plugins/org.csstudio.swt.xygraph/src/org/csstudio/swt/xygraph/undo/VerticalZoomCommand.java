package org.csstudio.swt.xygraph.undo;

import java.util.List;

import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.Range;

/** Undoable command to zoom in/out vertically.
 *  @author Kay Kasemir
 */
public class VerticalZoomCommand implements IUndoableCommand
{
    final private static double HUGE_NEGATIVE=-1e100;
	final private static double ZOOM_FACTOR = 1.5;

	final private XYGraph xyGraph;
    final private String description;
    final private double factor;
	
    /** Initialize
     *  @param xyGraph Graph to zoom vertically
     *  @param description Description for UI
     *  @param in Zoom in? Otherwise: out
     */
	public VerticalZoomCommand(final XYGraph xyGraph,
	        final String description, final boolean in)
	{
		this.xyGraph = xyGraph;
		this.description = description;
		factor = in ? 1.0 / ZOOM_FACTOR : ZOOM_FACTOR;
	}

	/** Perform the zoom */
	public void redo()
	{
	    final List<Axis> axes = xyGraph.getYAxisList();
	    for (Axis axis : axes)
            zoom(axis, 1.0/factor);
	}

	/** Revert by zooming "the other way" */
    public void undo()
	{
       final List<Axis> axes = xyGraph.getYAxisList();
       for (Axis axis : axes)
           zoom(axis, factor);
	}

    /** Zoom axis by factor */
    private void zoom(final Axis axis, final double factor)
    {
        final Range range = axis.getRange();
        double low = range.getLower();
        double high = range.getUpper();
        if (axis.isLogScaleEnabled())
        {
            low = log10(low);
            high = log10(high);
        }
        double center = (high + low)/2.0;
        double zoomed = (high - low) / factor / 2.0;  
        low = center - zoomed;
        high = center + zoomed;
        if (axis.isLogScaleEnabled())
        {
            low = Math.pow(10.0, low);
            high = Math.pow(10.0, high);
        }
        // Don't update range if the result would be garbage.
        if (Double.isInfinite(low) || Double.isInfinite(high))
            return;
        axis.setRange(low, high);
    }
    
    /** Adjusted log10 to handle values less or equal to zero.
     *  <p>
     *  The logarithm does not result in real numbers for arguments
     *  less or equal to zero, but the plot should still somehow handle
     *  such values without crashing.
     *  So anything &le; 0 is mapped to a 'really big negative' number
     *  just for the sake of plotting.
     *  <p>
     *  Note that LogarithmicAxis.java in the JFreeChart has another interesting
     *  idea for modifying the log10 of values &le; 10, resuling in a smooth
     *  plot for the full real argument range.
     *  Unfortunately that clobbers values like 1e-7, which might be a 
     *  very real vacuum reading.
     *   
     *  @param val  value for which log<sub>10</sub> should be calculated.
     *
     *  @return an adjusted log<sub>10</sub>(val).
     */
    private static double log10(double val)
    {
        if (val > 0.0)
            return Math.log10(val);
        return HUGE_NEGATIVE;
    }

    @Override
	public String toString()
	{
		return description;
	}
}
