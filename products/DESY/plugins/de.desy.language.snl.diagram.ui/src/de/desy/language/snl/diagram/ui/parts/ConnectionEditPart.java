/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *******************************************************************************/
package de.desy.language.snl.diagram.ui.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import de.desy.language.snl.diagram.model.ModelElement;
import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.ui.command.CreateBendPointCommand;
import de.desy.language.snl.diagram.ui.command.DeleteBendPointCommand;
import de.desy.language.snl.diagram.ui.command.MoveBendPointCommand;
import de.desy.language.snl.diagram.ui.figures.MidConnectionRouteLocator;

/**
 * Edit part for Connection model elements.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 * 
 */
class ConnectionEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {

	/**
	 * Upon activation, attach to the model element as a property change
	 * listener.
	 */
	public void activate() {
		if (!isActive()) {
			super.activate();
			((ModelElement) getModel()).addPropertyChangeListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		// Selection handle edit policy.
		// Makes the connection show a feedback, when selected by the user.
		//installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
		//		new ConnectionEndpointEditPolicy());
		// Allows the removal of the connection model element
//		installEditPolicy(EditPolicy.CONNECTION_ROLE,
//				new ConnectionEditPolicy() {
//					protected Command getDeleteCommand(GroupRequest request) {
//						return new ConnectionDeleteCommand(getCastedModel());
//					}
//				});
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, new BendpointEditPolicy() {
		
			@Override
			protected Command getMoveBendpointCommand(BendpointRequest request) {
				int index = request.getIndex();
				Point location = request.getLocation();
				MoveBendPointCommand moveBendPointCommand = new MoveBendPointCommand();
				moveBendPointCommand.setIndex(index);
				moveBendPointCommand.setLocation(location);
				moveBendPointCommand.setConnectionModel(getCastedModel());
				
				return moveBendPointCommand;
			}
		
			@Override
			protected Command getDeleteBendpointCommand(BendpointRequest request) {
				int index = request.getIndex();
				Point location = request.getLocation();
				DeleteBendPointCommand deleteBendPointCommand = new DeleteBendPointCommand();
				deleteBendPointCommand.setIndex(index);
				deleteBendPointCommand.setLocation(location);
				deleteBendPointCommand.setConnectionModel(getCastedModel());
				
				return deleteBendPointCommand;
			}
		
			@Override
			protected Command getCreateBendpointCommand(BendpointRequest request) {
				int index = request.getIndex();
				Connection connection = getConnection();
				Point location = request.getLocation();
				Point firstPoint = connection.getPoints().getFirstPoint();
				Point lastPoint = connection.getPoints().getLastPoint();
				
				System.out.println("Location: "+location);
				System.out.println("FirstPoint: "+firstPoint);
				System.out.println("LastPoint: "+lastPoint);
				System.out.println("Connection: "+connection.getBounds());
				
				connection.translateToRelative(location);
				System.out.println("Location: "+location);
				System.out.println("FirstPoint: "+firstPoint);
				System.out.println("LastPoint: "+lastPoint);
				System.out.println("Connection: "+connection.getBounds());
				
				connection.translateToRelative(firstPoint);
				System.out.println("Location: "+location);
				System.out.println("FirstPoint: "+firstPoint);
				System.out.println("LastPoint: "+lastPoint);
				System.out.println("Connection: "+connection.getBounds());
				
				connection.translateToRelative(lastPoint);
				System.out.println("Location: "+location);
				System.out.println("FirstPoint: "+firstPoint);
				System.out.println("LastPoint: "+lastPoint);
				System.out.println("Connection: "+connection.getBounds());
				
				Dimension dim1 = location.getDifference(firstPoint);
				Dimension dim2 = location.getDifference(lastPoint);
				
				System.out.println("Dimension1: "+dim1);
				System.out.println("Dimension2: "+dim2);
				
				CreateBendPointCommand createBendPointCommand = new CreateBendPointCommand();
				createBendPointCommand.setIndex(index);
				createBendPointCommand.setLocation(location);
				createBendPointCommand.setConnectionModel(getCastedModel());
				createBendPointCommand.setRelativeDimensions(dim1, dim2);
				
				return createBendPointCommand;
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection();
		connection.setTargetDecoration(new PolygonDecoration()); // arrow at
																	// target
																	// endpoint
		connection.setLineStyle(getCastedModel().getLineStyle()); // line
																	// drawing
																	// style
		getCastedModel().addPropertyChangeListener(this);
		
		final Label midLabel = new Label(getCastedModel().getWhenNode().getSourceIdentifier());
			
//		final Label tooltipLabel = new Label(getCastedModel().getWhenNode().getSourceIdentifier());
//		tooltipLabel.setBackgroundColor(ColorConstants.tooltipBackground);
//		connection.setToolTip(tooltipLabel);
		
		ConnectionLocator locator = new MidConnectionRouteLocator(connection);
		locator.setRelativePosition(PositionConstants.SOUTH);
		connection.add(midLabel, locator);
		
		connection.setConnectionRouter(new BendpointConnectionRouter());
		
		return connection;
	}
	
	private void refreshBendPoints() {
		List<Point> modelConstraint = getCastedModel().getBendPoints();
		List<Point> figureConstraint = new ArrayList<Point>();
		if (modelConstraint != null) {
			for (Point current : modelConstraint) {
//				RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
				AbsoluteBendpoint abp = new AbsoluteBendpoint(current);
				figureConstraint.add(abp);
			}
			getConnectionFigure().setRoutingConstraint(figureConstraint);
		}
	}
	
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		refreshBendPoints();
	}
	
	/**
	 * Upon deactivation, detach from the model element as a property change
	 * listener.
	 */
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	private WhenConnection getCastedModel() {
		return (WhenConnection) getModel();
	}

	public void propertyChange(PropertyChangeEvent event) {
		System.out.println("ConnectionEditPart.propertyChange()");
//		System.out.println("ID: "+event.getPropagationId());
//		System.out.println("Name: "+event.getPropertyName());
		refreshBendPoints();
	}

}