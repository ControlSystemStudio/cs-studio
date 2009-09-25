package org.csstudio.diag.diles.policies;

import org.csstudio.diag.diles.commands.DeleteCommand;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Chart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public class ComponentPolicy extends ComponentEditPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(
	 * org.eclipse.gef.requests.GroupRequest)
	 */
	@Override
	protected Command createDeleteCommand(GroupRequest request) {
		Object parent = getHost().getParent().getModel();
		DeleteCommand deleteCmd = new DeleteCommand();
		deleteCmd.setParent((Chart) parent);
		deleteCmd.setChild((Activity) getHost().getModel());
		return deleteCmd;
	}
}