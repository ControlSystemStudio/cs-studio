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

/**The command to replace a widget to the target widget..
 * @author Xihui Chen
 *
 */
public class ReplaceWidgetCommand extends Command {

	private final AbstractContainerModel container;
	
	private int index;
	
	private final AbstractWidgetModel srcWidget, targetWidget;
	
	private List<ConnectionModel> sourceConnections, targetConnections;
	

	public ReplaceWidgetCommand(AbstractContainerModel container,
			AbstractWidgetModel srcWidget, AbstractWidgetModel targetWidget) {
		assert container != null;
		assert srcWidget != null;	
		assert targetWidget != null;
		this.container = container;
		this.srcWidget = srcWidget;
		this.targetWidget = targetWidget;
	}
	
	
	@Override
	public void execute() {
		sourceConnections = getAllConnections(srcWidget, true);
		targetConnections = getAllConnections(srcWidget, false);
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
		index = container.getIndexOf(srcWidget);
		container.removeChild(srcWidget);
		container.addChild(index, targetWidget);
		for(ConnectionModel conn : sourceConnections){
			if(conn.getSource()==srcWidget)
				conn.setSource(targetWidget);			
		}
		for(ConnectionModel conn:targetConnections){
			if(conn.getTarget()==srcWidget)
				conn.setTarget(targetWidget);
		}
		removeConnections(sourceConnections);
		removeConnections(targetConnections);
		List<AbstractWidgetModel> allDescendants = container.getAllDescendants();
		for(ConnectionModel conn: sourceConnections){			
			if(allDescendants.contains(conn.getSource()))
				conn.reconnect();
		}
		
		for(ConnectionModel conn: targetConnections){			
			if(allDescendants.contains(conn.getTarget()))
				conn.reconnect();
		}
	}
	
	@Override
	public void undo() {
		container.removeChild(targetWidget);
		container.addChild(index, srcWidget);
		for(ConnectionModel conn : sourceConnections){
			if(conn.getSource()==targetWidget)
				conn.setSource(srcWidget);			
		}
		for(ConnectionModel conn:targetConnections){
			if(conn.getTarget()==targetWidget)
				conn.setTarget(srcWidget);
		}
		removeConnections(sourceConnections);
		removeConnections(targetConnections);
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
