/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**The command to delete a widget.
 * @author Xihui Chen
 *
 */
public class WidgetDeleteCommand extends Command {

	private final AbstractContainerModel container;
	
	private int index;
	
	private final AbstractWidgetModel widget;

	public WidgetDeleteCommand(AbstractContainerModel container,
			AbstractWidgetModel widget) {
		assert container != null;
		assert widget != null;		
		this.container = container;
		this.widget = widget;
	}
	
	
	@Override
	public void execute() {
		index = container.getIndexOf(widget);
		container.removeChild(widget);
	}
	
	@Override
	public void undo() {
		container.addChild(index, widget);
	}
	
	
	
}
