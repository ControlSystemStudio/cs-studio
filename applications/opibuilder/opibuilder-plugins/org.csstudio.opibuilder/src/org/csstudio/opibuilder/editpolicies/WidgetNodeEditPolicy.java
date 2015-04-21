/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editpolicies;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.commands.ConnectionCreateCommand;
import org.csstudio.opibuilder.commands.ConnectionReconnectCommand;
import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.ConnectionModel.RouterType;
import org.csstudio.opibuilder.util.SchemaService;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.gef.requests.ReconnectRequest;

/**	The editpolicy that allows the creation of connections and 
	the reconnection of connections between widgets.
 * @author Xihui Chen
 *
 */
public class WidgetNodeEditPolicy extends GraphicalNodeEditPolicy {
	
	/**
	 * the List of handles
	 */
	protected List<AnchorHandle> handles;	

	@Override
	protected ConnectionRouter getDummyConnectionRouter(
			CreateConnectionRequest request) {
		int i = (Integer)SchemaService.getInstance().getDefaultPropertyValue(
				ConnectionModel.ID, ConnectionModel.PROP_ROUTER);
		RouterType routerType = RouterType.values()[i];
		switch (routerType) {
		case MANHATTAN:
			return new ManhattanConnectionRouter();
		case STRAIGHT_LINE:
		default:
			return super.getDummyConnectionRouter(request);
		}
		
	}
	
	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		ConnectionCreateCommand cmd = (ConnectionCreateCommand) request.getStartCommand();
		cmd.setTarget(getWidgetEditPart().getWidgetModel());
		ConnectionAnchor anchor = getWidgetEditPart().getTargetConnectionAnchor(request);
		if(anchor == null)
			return null;
		cmd.setTargetTerminal(getWidgetEditPart().getTerminalNameFromAnchor(anchor));
		return cmd;
	}

	
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		AbstractWidgetModel source = getWidgetEditPart().getWidgetModel();
		ConnectionAnchor anchor = getWidgetEditPart().getSourceConnectionAnchor(request);		
		String sourceTerminal = getWidgetEditPart().getTerminalNameFromAnchor(anchor);
		ConnectionCreateCommand cmd = new ConnectionCreateCommand(source, sourceTerminal);
		request.setStartCommand(cmd);
		return cmd;
	}

	
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		ConnectionModel connection = (ConnectionModel) request.getConnectionEditPart().getModel();
		AbstractWidgetModel newTarget = getWidgetEditPart().getWidgetModel();
		ConnectionAnchor anchor = getWidgetEditPart().getTargetConnectionAnchor(request);
		String newTerminal = getWidgetEditPart().getTerminalNameFromAnchor(anchor);
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(connection);
		cmd.setNewTarget(newTarget);
		cmd.setNewTargetTerminal(newTerminal);
		//clear point list		
		return cmd.chain(new SetWidgetPropertyCommand(
				connection, ConnectionModel.PROP_POINTS, new PointList()));
	}

	
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		ConnectionModel connection = (ConnectionModel) request.getConnectionEditPart().getModel();
		AbstractWidgetModel newSource = getWidgetEditPart().getWidgetModel();
		ConnectionAnchor anchor = getWidgetEditPart().getTargetConnectionAnchor(request);
		String newTerminal = getWidgetEditPart().getTerminalNameFromAnchor(anchor);
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(connection);
		cmd.setNewSource(newSource);
		cmd.setNewSourceTerminal(newTerminal);
		//clear point list		
		return cmd.chain(new SetWidgetPropertyCommand(
				connection, ConnectionModel.PROP_POINTS, new PointList()));
	}
	
	protected AbstractBaseEditPart getWidgetEditPart(){
		return (AbstractBaseEditPart)getHost();
	}

		
	@Override
	protected void showTargetConnectionFeedback(DropRequest request) {
		addAnchorHandles();
	}
	
	
	@Override
	protected void eraseTargetConnectionFeedback(DropRequest request) {
		removeAnchorHandles();
	}
			
	/**
	 * Adds the handles to the handle layer.
	 */
	protected void addAnchorHandles() {
		removeAnchorHandles();
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		handles = createAnchorHandles();
		for (int i = 0; i < handles.size(); i++)
			layer.add((IFigure) handles.get(i));
	}

	/**
	 * create the list of handles.
	 * 
	 * @return List of handles; cannot be <code>null</code>
	 */
	protected List<AnchorHandle> createAnchorHandles(){
		List<AnchorHandle> result = new ArrayList<AnchorHandle>();
		for(ConnectionAnchor anchor: getWidgetEditPart().getAnchorMap().values()){
			result.add(new AnchorHandle(getWidgetEditPart(), anchor));
		}
		return result;
	}
	

	/**
	 * removes the anchor handles
	 */
	protected void removeAnchorHandles() {
		if (handles == null)
			return;
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		for (int i = 0; i < handles.size(); i++)
			layer.remove((IFigure) handles.get(i));
		handles = null;
	}
}
