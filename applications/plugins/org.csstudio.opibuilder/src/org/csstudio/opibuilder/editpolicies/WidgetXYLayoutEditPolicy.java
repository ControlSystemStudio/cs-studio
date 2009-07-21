package org.csstudio.opibuilder.editpolicies;

import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.commands.WidgetSetConstraintCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditpart;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**
 * The EditPolicy for create/move/resize a widget.
 *
 * @author Xihui Chen
 *
 */
public class WidgetXYLayoutEditPolicy extends XYLayoutEditPolicy {

	
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		if(child instanceof AbstractBaseEditpart && constraint instanceof Rectangle){
			return new WidgetSetConstraintCommand(
					(AbstractWidgetModel) child.getModel(), request, (Rectangle)constraint);
		}		
		return super.createChangeConstraintCommand(request, child, constraint);
	}
	
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return null;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return new WidgetCreateCommand((AbstractWidgetModel)request.getNewObject(), 
					(AbstractContainerModel)getHost().getModel(), 
					(Rectangle)getConstraintFor(request));
	}

}
