package org.csstudio.swt.xygraph.undo;

import org.csstudio.swt.xygraph.figures.Axis;
import org.csstudio.swt.xygraph.linearscale.Range;

/**The undo command for axis panning.
 * @author Xihui Chen
 *
 */
public class AxisPanningCommand implements IUndoableCommand {

	private Axis axis;
	
	private Range beforeRange;
	
	private Range afterRange;
	
	
	public AxisPanningCommand(Axis axis) {
		this.axis = axis;
	}

	public void redo() {
		axis.setRange(afterRange);
	}

	public void undo() {
		axis.setRange(beforeRange);
	}
	
	public void savePreviousStates(){
		beforeRange = axis.getRange();
	}
	
	public void saveAfterStates(){
		afterRange = axis.getRange();
	}
	
	@Override
	public String toString() {
		return "Axis Panning";
	}
	

}
