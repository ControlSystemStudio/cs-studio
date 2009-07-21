package org.csstudio.opibuilder.editparts;

import org.csstudio.opibuilder.editpolicies.WidgetXYLayoutEditPolicy;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

public abstract class AbstractContainerEditpart extends AbstractBaseEditpart {	

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new WidgetXYLayoutEditPolicy());

	}


}
