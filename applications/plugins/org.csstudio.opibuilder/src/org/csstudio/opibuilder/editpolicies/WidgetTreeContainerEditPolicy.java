/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editpolicies;

import java.util.List;

import org.csstudio.opibuilder.commands.ChangeOrderCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editpolicies.TreeContainerEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

/**
 * The edit policy for widgets operation on a tree.
 * @author Xihui Chen
 *
 */
public class WidgetTreeContainerEditPolicy extends TreeContainerEditPolicy {

	@Override
	protected Command getAddCommand(ChangeBoundsRequest request) {
		CompoundCommand cmd = new CompoundCommand();
		@SuppressWarnings("rawtypes")
		List editparts = request.getEditParts();
		int index = findIndexOfTreeItemAt(request.getLocation());
		for(int i=0; i< editparts.size(); i++){
			EditPart child = (EditPart)editparts.get(index >=0 ? editparts.size()-1-i : i);
			if(isAncestor(child, getHost())){
				cmd.add(UnexecutableCommand.INSTANCE);
			}else{
				AbstractWidgetModel childModel = (AbstractWidgetModel) child.getModel();
				cmd.add(createCreateCommand(
						childModel, 
						new Rectangle(new Point(), childModel.getSize()), 
						index, "Reparent Widgets"));
			}
		}
		return cmd;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		AbstractWidgetModel widgetModel = (AbstractWidgetModel) request.getNewObject();
		int index = findIndexOfTreeItemAt(request.getLocation());
		return createCreateCommand(widgetModel, null, index, "Create Widget");
	}

	@Override
	protected Command getMoveChildrenCommand(ChangeBoundsRequest request) {
		CompoundCommand command = new CompoundCommand();
		@SuppressWarnings("rawtypes")
		List editparts = request.getEditParts();
		@SuppressWarnings("rawtypes")
		List children = getHost().getChildren();
		int newIndex = findIndexOfTreeItemAt(request.getLocation());
		int tempIndex = newIndex;

		for(int i = 0; i < editparts.size(); i++){
			EditPart child = (EditPart)editparts.get(editparts.size()-1-i);
			
			int oldIndex = children.indexOf(child);
			if(oldIndex == tempIndex || oldIndex + 1 == tempIndex){
				command.add(UnexecutableCommand.INSTANCE);
				return command;
			} else if(oldIndex <= tempIndex){
				tempIndex--;
			}
			
			command.add(new ChangeOrderCommand(tempIndex, 
					(AbstractContainerModel)getHost().getModel(), 
					(AbstractWidgetModel) child.getModel()));
		}
		return command;
	}

	protected Command createCreateCommand(AbstractWidgetModel widgetModel, 
			Rectangle r, int index, String label){
		
		WidgetCreateCommand cmd = new WidgetCreateCommand(
				widgetModel, (AbstractContainerModel) getHost().getModel(), r, false, true);
		cmd.setLabel(label);
		cmd.setIndex(index);
		return cmd;
		
	}
	
	protected boolean isAncestor(EditPart source, EditPart target){
		if(source == target)
			return true;
		if(target.getParent() != null)
			return isAncestor(source, target.getParent());
		return false;
	}
	
}
