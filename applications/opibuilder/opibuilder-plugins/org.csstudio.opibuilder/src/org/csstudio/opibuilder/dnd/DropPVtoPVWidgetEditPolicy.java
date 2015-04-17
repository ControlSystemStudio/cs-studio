/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.dnd;

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

/**The editpolicy for dropping pv onto a PV widget.
 * @author Xihui Chen
 *
 */
public class DropPVtoPVWidgetEditPolicy extends AbstractEditPolicy {

	public final static String DROP_PV_ROLE = "DropPVEditPolicy"; //$NON-NLS-1$
	
	@Override
	public Command getCommand(Request request) {
		if(request.getType() == DropPVRequest.REQ_DROP_PV && request instanceof DropPVRequest){
			DropPVRequest dropPVRequest =(DropPVRequest)request; 
			if(dropPVRequest.getTargetWidget() != null)
					return new SetWidgetPropertyCommand(
							dropPVRequest.getTargetWidget().getWidgetModel(),
							AbstractPVWidgetModel.PROP_PVNAME, dropPVRequest.getPvNames()[0]);
		}
		return super.getCommand(request);
	}
	
	@Override
	public EditPart getTargetEditPart(Request request) {
		if(request.getType() == DropPVRequest.REQ_DROP_PV)
			return getHost();
		return super.getTargetEditPart(request);
	}
	
	
}
