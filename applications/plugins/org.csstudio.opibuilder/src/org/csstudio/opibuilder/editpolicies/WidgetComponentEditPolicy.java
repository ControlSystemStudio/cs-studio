/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editpolicies;

import org.csstudio.opibuilder.commands.WidgetDeleteCommand;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**Component Editpolicy for widgets.
 * @author Xihui Chen
 *
 */
public class WidgetComponentEditPolicy extends ComponentEditPolicy {

	
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Object containerModel = getHost().getParent().getModel();
		Object widget = getHost().getModel();
		
		if(containerModel instanceof AbstractContainerModel && 
				widget instanceof AbstractWidgetModel)
			return new WidgetDeleteCommand((AbstractContainerModel)containerModel,
					(AbstractWidgetModel)widget);				
		return super.createDeleteCommand(deleteRequest);
	}
	
}
