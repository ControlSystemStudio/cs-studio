package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;

/**The undo command for panning or zooming one axis.
 * @author Xihui Chen
 * @author Kay Kasemir (changed from AxisPanningCommand)
 */
public class AxisPanOrZoomCommand implements IUndoableCommand
{
    final private String name;

	private Axis axis;
	
	private Range beforeRange;
	
	private Range afterRange;
	
	public AxisPanOrZoomCommand(final String name, final Axis axis)
	{
	    this.name = name;
		this.axis = axis;
	}

	public void redo()
	{
		axis.setRange(afterRange);
	}

	public void undo()
	{
		axis.setRange(beforeRange);
	}
	
	public void savePreviousStates()
	{
		beforeRange = axis.getRange();
	}
	
	public void saveAfterStates()
	{
		afterRange = axis.getRange();
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
