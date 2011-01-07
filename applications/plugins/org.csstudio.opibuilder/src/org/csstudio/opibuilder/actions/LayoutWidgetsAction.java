package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editparts.AbstractLayoutEditpart;
import org.eclipse.jface.action.IAction;

/**
 * An action to layout widgets in a container.
 * @author Xihui Chen
 *
 */
public class LayoutWidgetsAction extends AbstractWidgetTargetAction {

	public void run(IAction action) {

		AbstractLayoutEditpart layoutWidget = getLayoutWidget();
		
		LayoutWidgetsImp.run(layoutWidget, getCommandStack());
		
	}

	protected AbstractLayoutEditpart getLayoutWidget(){
		return (AbstractLayoutEditpart)selection.getFirstElement();
	}
	
}
