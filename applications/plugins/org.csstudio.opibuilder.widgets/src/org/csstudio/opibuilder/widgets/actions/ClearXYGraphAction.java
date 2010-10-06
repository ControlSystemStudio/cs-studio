package org.csstudio.opibuilder.widgets.actions;

import org.csstudio.opibuilder.actions.AbstractWidgetTargetAction;
import org.csstudio.opibuilder.widgets.editparts.XYGraphEditPart;
import org.eclipse.jface.action.IAction;

/**Clear XY Graph.
 * @author Xihui Chen
 *
 */
public class ClearXYGraphAction extends AbstractWidgetTargetAction {
	
	public void run(IAction action) {
		getSelectedXYGraph().clearGraph();
		
	}
	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final XYGraphEditPart getSelectedXYGraph() {
		return (XYGraphEditPart)selection.getFirstElement();
	}
}
