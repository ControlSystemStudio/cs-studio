/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.commands;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.eclipse.gef.commands.Command;

/**The command to delete a widget.
 * @author Xihui Chen
 *
 */
public class WidgetDeleteCommand extends Command {

	private final AbstractContainerModel container;
	
	private int index;
	
	private final AbstractWidgetModel widget;
	
	private List<ConnectionModel> sourceConnections, targetConnections;

	public WidgetDeleteCommand(AbstractContainerModel container,
			AbstractWidgetModel widget) {
		assert container != null;
		assert widget != null;		
		this.container = container;
		this.widget = widget;
	}
	
	
	@Override
	public void execute() {
		sourceConnections = getAllConnections(widget, true);
		targetConnections = getAllConnections(widget, false);
		redo();
	}
	
	private List<ConnectionModel> getAllConnections(AbstractWidgetModel widget, boolean source){
			List<ConnectionModel> result = new ArrayList<ConnectionModel>();
			result.addAll(source ? widget.getSourceConnections() : widget.getTargetConnections());
			if(widget instanceof AbstractContainerModel){
				for(AbstractWidgetModel child : 
					((AbstractContainerModel)widget).getAllDescendants()){
					result.addAll(
							source ? child.getSourceConnections() : 
								child.getTargetConnections());
				}
			}
			return result;
	}
	
	@Override
	public void redo() {		
		index = container.getIndexOf(widget);
		container.removeChild(widget);
		removeConnections(sourceConnections);
		removeConnections(targetConnections);
	}
	
	@Override
	public void undo() {
		container.addChild(index, widget);
		addConnections(sourceConnections);
		addConnections(targetConnections);
	}
	
	private void removeConnections(List<ConnectionModel> connections){
		for(ConnectionModel conn : connections){
			conn.disconnect();
		}
	}
	
	private void addConnections(List<ConnectionModel> connections){
		for(ConnectionModel conn: connections){
			conn.reconnect();
		}
	}
	
}
