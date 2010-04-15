package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;

/**The undo command for panning or zooming one axis.
 * @author Xihui Chen
 * @author Kay Kasemir (changed from AxisPanningCommand)
 */
public class AxisPanOrZoomCommand extends SaveStateCommand
{
    final private Axis axis;
	
	final private Range beforeRange;
	
	private Range afterRange;
	
	public AxisPanOrZoomCommand(final String name, final Axis axis)
	{
	    super(name);
		this.axis = axis;
        beforeRange = axis.getRange();
	}

	public void redo()
	{
		axis.setRange(afterRange);
	}

	public void undo()
	{
		axis.setRange(beforeRange);
	}
	
	@Override
    public void saveState()
	{
		afterRange = axis.getRange();
	}
}
