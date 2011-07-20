/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractLayoutModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;

/**Add widget to container 
 * @author Sven Wende & Stefan Hofer (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class AddWidgetCommand extends Command {

	private AbstractContainerModel containerModel;
	
	private AbstractWidgetModel widget;
	
	private Rectangle oldBounds;
	private Rectangle newBounds;
	
	public AddWidgetCommand(final AbstractContainerModel containerModel, 
			final AbstractWidgetModel widget, Rectangle newBounds){
		this.containerModel = containerModel;
		this.widget = widget;
		this.newBounds = newBounds;
	}
	
	
	@Override
	public void execute() {
		oldBounds = widget.getBounds();
		if(widget instanceof AbstractLayoutModel && containerModel
				.getLayoutWidget() != null){
			MessageDialog.openError(null, "Creating widget failed", 
					"There is already a layout widget in the container. " +
					"Please delete it before you can add a new layout widget.");
			return;
		}
		widget.setBounds(newBounds);
		containerModel.addChild(widget);
		
	}
	
	@Override
	public void undo() {
		widget.setBounds(oldBounds);
		containerModel.removeChild(widget);
	}
	
	
	
	
	
	
}
