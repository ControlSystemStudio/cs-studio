package org.csstudio.diag.diles.policies;

import org.csstudio.diag.diles.commands.CreateCommand;
import org.csstudio.diag.diles.commands.SetConstraintCommand;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Chart;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

public class LayoutPolicy extends XYLayoutEditPolicy {
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	protected Command createAddCommand(EditPart childEditPart, Object constraint) {
		Activity part = (Activity) childEditPart.getModel();
		Rectangle rect = (Rectangle) constraint;
		CreateCommand create = new CreateCommand();
		create.setParent((Chart) (getHost().getModel()));
		create.setChild(part);
		create.setConstraint(rect);
		return create;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.requests.ChangeBoundsRequest, org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		SetConstraintCommand cmd = (SetConstraintCommand) createChangeConstraintCommand(
				child, constraint);
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		SetConstraintCommand locationCommand = new SetConstraintCommand();
		locationCommand.setPart((Activity) child.getModel());
		locationCommand.setLocation((Rectangle) constraint);
		return locationCommand;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.ConstrainedLayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		NonResizableEditPolicy policy = new NonResizableEditPolicy();
		return policy;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		CreateCommand create = new CreateCommand();
		create.setParent((Chart) (getHost().getModel()));
		create.setChild((Activity) (request.getNewObject()));
		Rectangle constraint = (Rectangle) getConstraintFor(request);
		create.setConstraint(constraint);
		return create;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	@Override
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}
}