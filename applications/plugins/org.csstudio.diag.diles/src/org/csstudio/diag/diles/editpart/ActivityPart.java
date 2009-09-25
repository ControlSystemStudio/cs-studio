package org.csstudio.diag.diles.editpart;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.csstudio.diag.diles.figures.ActivityFigure;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Path;
import org.csstudio.diag.diles.policies.ComponentPolicy;
import org.csstudio.diag.diles.policies.NodePolicy;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DropRequest;

abstract public class ActivityPart extends AppAbstractEditPart implements
		NodeEditPart {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodePolicy());
	}

	protected Activity getActivity() {
		return (Activity) getModel();
	}

	protected ActivityFigure getActivityFigure() {
		return (ActivityFigure) getFigure();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections
	 * ()
	 */
	@Override
	protected List getModelSourceConnections() {
		return getActivity().getSourceConnections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections
	 * ()
	 */
	@Override
	protected List getModelTargetConnections() {
		return getActivity().getTargetConnections();
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {
		Path Path = (Path) connEditPart.getModel();
		return getActivityFigure().getSourceConnectionAnchor(
				Path.getSourceName());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getActivityFigure().getSourceConnectionAnchorAt(pt);
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart) {
		Path Path = (Path) connEditPart.getModel();
		return getActivityFigure().getTargetConnectionAnchor(
				Path.getTargetName());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getActivityFigure().getTargetConnectionAnchorAt(pt);
	}

	public String mapSourceConnectionAnchorToName(ConnectionAnchor c) {
		return getActivityFigure().getSourceAnchorName(c);
	}

	public String mapTargetConnectionAnchorToName(ConnectionAnchor c) {
		return getActivityFigure().getTargetAnchorName(c);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (Activity.CHILD.equals(prop))
			refreshChildren();
		else if (Activity.TARGETS.equals(prop))
			refreshTargetConnections();
		else if (Activity.SOURCES.equals(prop)) {
			refreshSourceConnections();
		} else if (Activity.SIZE.equals(prop) || Activity.LOC.equals(prop)
				|| Activity.NAME.equals(prop))
			refreshVisuals();
		else if (Activity.ACTIVITY_STATUS.equals(prop)) {
			refreshVisuals();
		} else if (Activity.DELAY.equals(prop)) {
			refreshVisuals();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		getActivityFigure().setName(getActivity().getName());
		Point loc = getActivity().getLocation();
		Dimension size = getActivity().getSize();
		Rectangle r = new Rectangle(loc, size);
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}
}