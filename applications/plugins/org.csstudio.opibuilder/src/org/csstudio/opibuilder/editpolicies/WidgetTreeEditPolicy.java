package org.csstudio.opibuilder.editpolicies;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**An edit policy that pass MOVE request to its parent.
 * @author Xihui Chen
 *
 */
public class WidgetTreeEditPolicy extends AbstractEditPolicy {

	@Override
	public Command getCommand(Request req) {
		if (REQ_MOVE.equals(req.getType()))
			return getMoveCommand((ChangeBoundsRequest)req);
		return null;
	}
	
	protected Command getMoveCommand(ChangeBoundsRequest req){
		EditPart parent = getHost().getParent();
		if(parent != null){
			req.setType(REQ_MOVE_CHILDREN);
//			ChangeBoundsRequest request = new ChangeBoundsRequest(REQ_MOVE_CHILDREN);
//			request.setEditParts(getHost());
//			request.setLocation(req.getLocation());
			return parent.getCommand(req);
		}
		return UnexecutableCommand.INSTANCE;
	}
	
}
