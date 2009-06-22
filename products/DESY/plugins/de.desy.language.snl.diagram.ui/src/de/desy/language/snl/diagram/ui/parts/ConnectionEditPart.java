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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.desy.language.snl.diagram.model.ModelElement;
import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.ui.commands.ConnectionDeleteCommand;
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
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
		// Allows the removal of the connection model element
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new ConnectionEditPolicy() {
					protected Command getDeleteCommand(GroupRequest request) {
						return new ConnectionDeleteCommand(getCastedModel());
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection() {
//			@Override
//			public boolean equals(Object obj) {
//				boolean result = false;
//				if (obj instanceof PolylineConnection) {
//					PolylineConnection that = (PolylineConnection)obj;
//					
//					IFigure thisTooltip = this.getToolTip();
//					IFigure thatTooltip = that.getToolTip();
//					
//					System.out.println(".equals()");
//					System.out.println("this.getSourceAnchor(): "+this.getSourceAnchor());
//					System.out.println("that.getSourceAnchor(): "+that.getSourceAnchor());
//					System.out.println("this.getTargetAnchor(): "+this.getTargetAnchor());
//					System.out.println("that.getTargetAnchor(): "+that.getTargetAnchor());
//					System.out.println("thisTooltip " + thisTooltip);
//					System.out.println("thatTooltip " + thatTooltip);
//					
//					if (this.getSourceAnchor() != null && this.getSourceAnchor().equals(that.getSourceAnchor()) &&
//							this.getTargetAnchor() != null && this.getTargetAnchor().equals(that.getTargetAnchor()) &&
//							thisTooltip != null && thisTooltip.equals(thatTooltip)) {
//						result = true;
//					}
//					System.out.println(".equals() "+result);
//				}
//				return result;
//			}
		};
		connection.setTargetDecoration(new PolygonDecoration()); // arrow at
																	// target
																	// endpoint
		connection.setLineStyle(getCastedModel().getLineStyle()); // line
																	// drawing
																	// style
		
		
		final Label midLabel = new Label(getCastedModel().getWhenNode().getSourceIdentifier());
			
		final Label tooltipLabel = new Label(getCastedModel().getWhenNode().getSourceIdentifier());
		tooltipLabel.setBackgroundColor(ColorConstants.tooltipBackground);
		connection.setToolTip(tooltipLabel);
		
		ConnectionLocator locator = new MidConnectionRouteLocator(connection);
		locator.setRelativePosition(PositionConstants.SOUTH);
		connection.add(midLabel, locator);
		
		return connection;
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

	public void propertyChange(PropertyChangeEvent arg0) {
	}

}