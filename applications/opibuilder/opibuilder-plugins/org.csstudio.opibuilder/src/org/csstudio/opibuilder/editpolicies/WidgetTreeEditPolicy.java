/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
			ChangeBoundsRequest request = new ChangeBoundsRequest(REQ_MOVE_CHILDREN);
			//request.setEditParts(getHost());
			request.setEditParts(req.getEditParts());
			request.setLocation(req.getLocation());
			req.setType(""); //$NON-NLS-1$
			return parent.getCommand(request);
		}
		return UnexecutableCommand.INSTANCE;
	}
	
}
