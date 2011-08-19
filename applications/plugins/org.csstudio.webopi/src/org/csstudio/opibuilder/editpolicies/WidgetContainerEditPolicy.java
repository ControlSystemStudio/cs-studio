/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editpolicies;

import java.util.List;

import org.csstudio.opibuilder.commands.OrphanChildCommand;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ContainerEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;

/**Container edit policy which supports children orphan. 
 * @author Xihui Chen
 *
 */
public class WidgetContainerEditPolicy extends ContainerEditPolicy {

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}
	
	@Override
	protected Command getOrphanChildrenCommand(GroupRequest request) {
		@SuppressWarnings("rawtypes")
		List parts = request.getEditParts();
		CompoundCommand result = new CompoundCommand("Orphan Children");
		for(int i=0; i<parts.size(); i++){					
			OrphanChildCommand orphan = new OrphanChildCommand(
					(AbstractContainerModel)(getHost().getModel()),
					(AbstractWidgetModel)((EditPart)parts.get(i)).getModel());
			orphan.setLabel("Reparenting widget");
			result.add(orphan);
		}
		
		return result.unwrap();
	}

}
