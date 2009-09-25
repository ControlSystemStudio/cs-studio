package org.csstudio.diag.diles.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.diag.diles.commands.PathDeleteCommand;
import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.model.AbstractChartElement;
import org.csstudio.diag.diles.model.AnalogInput;
import org.csstudio.diag.diles.model.Path;
import org.csstudio.diag.diles.model.WireBendpoint;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class PathPart extends AbstractConnectionEditPart implements
		PropertyChangeListener {
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		getPath().addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());

		// Allows the removal of the connection model element
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new ConnectionEditPolicy() {
					@Override
					protected Command getDeleteCommand(GroupRequest request) {
						return new PathDeleteCommand((Path) getModel());
					}
				});

		refreshBendpointEditPolicy();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractConnectionEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return FigureFactory.createPathFigure();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		getPath().removePropertyChangeListener(this);
		super.deactivate();
	}

	protected Path getPath() {
		return (Path) getModel();
	}

	protected IFigure getPathFigure() {
		return getFigure();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AbstractChartElement.ACTIVITY_STATUS)) {
			refreshVisuals();
		}
		if (evt.getPropertyName().equals("bendpoint")) {
			refreshBendpoints();
		}
		if (Connection.PROPERTY_CONNECTION_ROUTER.equals(evt.getPropertyName())) {
			refreshBendpoints();
			refreshBendpointEditPolicy();
		}
	}

	private void refreshBendpointEditPolicy() {
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, null);
		else
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
					new WireBendpointEditPolicy());
	}

	protected void refreshBendpoints() {
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
			return;
		List modelConstraint = getPath().getBendpoints();
		List figureConstraint = new ArrayList();
		for (int i = 0; i < modelConstraint.size(); i++) {
			WireBendpoint wbp = (WireBendpoint) modelConstraint.get(i);
			RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
			rbp.setRelativeDimensions(wbp.getFirstRelativeDimension(), wbp
					.getSecondRelativeDimension());
			rbp.setWeight((i + 1) / ((float) modelConstraint.size() + 1));
			figureConstraint.add(rbp);
		}
		getConnectionFigure().setRoutingConstraint(figureConstraint);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	public void refreshVisuals() {
		refreshBendpoints();

		/*
		 * To set text TRUE or FALSE on the connection
		 * 
		 * PathFigure figure = (PathFigure) getFigure(); Path model = (Path)
		 * getModel(); figure.setPathText(model.getStatus());
		 */

		if (getPath().getSource() instanceof AnalogInput) {
			getPathFigure().setForegroundColor(
					new Color(Display.getDefault(), 0, 0, 0));
			return;
		}

		/*
		 * To set the color of the connection
		 */
		if (getPath().getStatus())
			getPathFigure().setForegroundColor(
					new Color(Display.getDefault(), 0, 74, 168));
		else
			getPathFigure().setForegroundColor(
					new Color(Display.getDefault(), 255, 50, 50));

	}
}