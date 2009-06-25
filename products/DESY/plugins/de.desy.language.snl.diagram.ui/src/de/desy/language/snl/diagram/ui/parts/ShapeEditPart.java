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
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.desy.language.snl.diagram.model.ModelElement;
import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.StateModel;
import de.desy.language.snl.diagram.model.StateSetModel;
import de.desy.language.snl.diagram.model.WhenConnection;
import de.desy.language.snl.diagram.ui.commands.ConnectionCreateCommand;
import de.desy.language.snl.diagram.ui.commands.ConnectionReconnectCommand;
import de.desy.language.snl.diagram.ui.figures.StateFigure;

/**
 * EditPart used for Shape instances (more specific for EllipticalShape and
 * RectangularShape instances).
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Elias Volanakis
 */
class ShapeEditPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener, NodeEditPart {

	private ConnectionAnchor anchor;

	/**
	 * Upon activation, attach to the model element as a property change
	 * listener.
	 */
	@Override
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
	@Override
	protected void createEditPolicies() {
		// allow removal of the associated model element
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ShapeComponentEditPolicy());
		// allow the creation of connections and
		// and the reconnection of connections between Shape instances
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new GraphicalNodeEditPolicy() {
					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
					 */
					@Override
					protected Command getConnectionCompleteCommand(
							final CreateConnectionRequest request) {
						final ConnectionCreateCommand cmd = (ConnectionCreateCommand) request
								.getStartCommand();
						cmd.setTarget((SNLModel) getHost().getModel());
						return cmd;
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
					 */
					@Override
					protected Command getConnectionCreateCommand(
							final CreateConnectionRequest request) {
						final SNLModel source = (SNLModel) getHost().getModel();
						final ConnectionCreateCommand cmd = new ConnectionCreateCommand(
								source);
						request.setStartCommand(cmd);
						return cmd;
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
					 */
					@Override
					protected Command getReconnectSourceCommand(
							final ReconnectRequest request) {
						final WhenConnection conn = (WhenConnection) request
								.getConnectionEditPart().getModel();
						final SNLModel newSource = (SNLModel) getHost().getModel();
						final ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(
								conn);
						cmd.setNewSource(newSource);
						return cmd;
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
					 */
					@Override
					protected Command getReconnectTargetCommand(
							final ReconnectRequest request) {
						final WhenConnection conn = (WhenConnection) request
								.getConnectionEditPart().getModel();
						final SNLModel newTarget = (SNLModel) getHost().getModel();
						final ConnectionReconnectCommand cmd = new ConnectionReconnectCommand(
								conn);
						cmd.setNewTarget(newTarget);
						return cmd;
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		final IFigure f = createFigureForModel();
		f.setOpaque(true); // non-transparent figure
		// f.setBackgroundColor(ColorConstants.green);
		return f;
	}

	/**
	 * Return a IFigure depending on the instance of the current model element.
	 * This allows this EditPart to be used for both sublasses of Shape.
	 */
	private IFigure createFigureForModel() {
		if (getModel() instanceof StateModel) {
			final StateModel sm = (StateModel) getModel();
			return new StateFigure(sm.getStateNode());
		} else if (getModel() instanceof StateSetModel) {
			final RectangleFigure rectangleFigure = new RectangleFigure();
			rectangleFigure.setBackgroundColor(ColorConstants.green);
			return rectangleFigure;
		} else {
			// if Shapes gets extended the conditions above must be updated
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Upon deactivation, detach from the model element as a property change
	 * listener.
	 */
	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ModelElement) getModel()).removePropertyChangeListener(this);
		}
	}

	private SNLModel getCastedModel() {
		return (SNLModel) getModel();
	}

	protected ConnectionAnchor getConnectionAnchor() {
		if (anchor == null) {
			if (getModel() instanceof StateModel)
				anchor = new ChopboxAnchor(getFigure());
			else if (getModel() instanceof StateSetModel)
				anchor = new ChopboxAnchor(getFigure());
			else
				// if Shapes gets extended the conditions above must be updated
				throw new IllegalArgumentException("unexpected model");
		}
		return anchor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections()
	 */
	@Override
	protected List getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections()
	 */
	@Override
	protected List getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			final ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			final ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef.Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(final PropertyChangeEvent evt) {
		final String prop = evt.getPropertyName();
		if (SNLModel.SIZE_PROP.equals(prop)
				|| SNLModel.LOCATION_PROP.equals(prop)) {
			refreshVisuals();
		} else if (SNLModel.SOURCE_CONNECTIONS_PROP.equals(prop)) {
			refreshSourceConnections();
		} else if (SNLModel.TARGET_CONNECTIONS_PROP.equals(prop)) {
			refreshTargetConnections();
		}
	}

	@Override
	protected void refreshVisuals() {
		// notify parent container of changed position & location
		// if this line is removed, the XYLayoutManager used by the parent
		// container
		// (the Figure of the ShapesDiagramEditPart), will not know the bounds
		// of this figure
		// and will not draw it correctly.
		final Rectangle bounds = new Rectangle(getCastedModel().getLocation(),
				getCastedModel().getSize());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), bounds);
	}
	
}