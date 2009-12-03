package org.csstudio.swt.xygraph.undo;

import java.util.List;

import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.figures.XYGraph;
import org.csstudio.swt.xygraph.linearscale.Range;
import org.csstudio.swt.xygraph.util.Log10;

/** Undoable command to zoom in/out vertically.
 *  @author Kay Kasemir
 */
public class VerticalZoomCommand implements IUndoableCommand
{
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
            low = Log10.log10(low);
            high = Log10.log10(high);
        }
        double center = (high + low)/2.0;
        double zoomed = (high - low) / factor / 2.0;  
        low = center - zoomed;
        high = center + zoomed;
        if (axis.isLogScaleEnabled())
        {
            low = Log10.pow10(low);
            high = Log10.pow10(high);
        }
        // Don't update range if the result would be garbage.
        if (Double.isInfinite(low) || Double.isInfinite(high))
            return;
        axis.setRange(low, high);
    }

    @Override
	public String toString()
	{
		return description;
	}
}
