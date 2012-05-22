/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;

/**The command to support move/resize.
 * @author Xihui Chen
 *
 */
public class WidgetSetConstraintCommand extends Command {
	
	/** Stores the new size and location. */
	private final Rectangle newBounds;
	/** Stores the old size and location. */
	private Rectangle oldBounds;
	/** A request to move/resize an edit part. */
	//private final ChangeBoundsRequest request;
	
	private final AbstractWidgetModel widget;

	public WidgetSetConstraintCommand(AbstractWidgetModel widgetModel, 
			ChangeBoundsRequest req, Rectangle newBounds) {
		if (widgetModel == null || newBounds == null)
			throw new IllegalArgumentException();
		this.widget = widgetModel;
		this.newBounds = newBounds;
		//this.request = req;
		setLabel("move/resize");
	}
	/*
	@Override
	public boolean canExecute() {
		Object type = request.getType();
		// make sure the Request is of a type we support:
		return (RequestConstants.REQ_MOVE.equals(type)
				|| RequestConstants.REQ_MOVE_CHILDREN.equals(type) 
				|| RequestConstants.REQ_RESIZE.equals(type)
				|| RequestConstants.REQ_RESIZE_CHILDREN.equals(type)
				|| RequestConstants.REQ_ALIGN.equals(type));
	}
	*/
	@Override
	public void execute() {
		oldBounds = new Rectangle(widget.getLocation(), widget.getSize());
		redo();
	}
	
	@Override
	public void redo() {
		widget.setLocation(newBounds.x, newBounds.y);
		widget.setSize(newBounds.width, newBounds.height);
	}
	
	@Override
	public void undo() {
		widget.setLocation(oldBounds.x, oldBounds.y);
		widget.setSize(oldBounds.width, oldBounds.height);
	}
	
}
