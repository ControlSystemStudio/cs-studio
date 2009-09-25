package org.csstudio.diag.diles.policies;

import org.csstudio.diag.diles.commands.PathCommand;
import org.csstudio.diag.diles.editpart.ActivityPart;
import org.csstudio.diag.diles.figures.ActivityFigure;
import org.csstudio.diag.diles.figures.FigureFactory;
import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.model.Path;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public class NodePolicy extends GraphicalNodeEditPolicy {
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#createDummyConnection(org.eclipse.gef.Request)
	 */
	@Override
	protected Connection createDummyConnection(Request req) {
		PolylineConnection conn = FigureFactory.createPathFigure();
		return conn;
	}

	protected Activity getActivity() {
		return (Activity) getHost().getModel();
	}

	protected ActivityFigure getActivityFigure() {
		return (ActivityFigure) ((GraphicalEditPart) getHost()).getFigure();
	}

	protected ActivityPart getActivityPart() {
		return (ActivityPart) getHost();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		PathCommand command = (PathCommand) request.getStartCommand();
		command.setTarget(getActivity());
		ConnectionAnchor ctor = getActivityPart().getTargetConnectionAnchor(
				request);
		if (ctor == null)
			return null;
		command.setTargetName(getActivityPart()
				.mapTargetConnectionAnchorToName(ctor));
		return command;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		PathCommand command = new PathCommand();
		command.setPath(new Path());
		command.setSource(getActivity());
		ConnectionAnchor ctor = getActivityPart().getSourceConnectionAnchor(
				request);
		command.setSourceName(getActivityPart()
				.mapSourceConnectionAnchorToName(ctor));
		request.setStartCommand(command);
		return command;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		PathCommand cmd = new PathCommand();
		cmd.setPath((Path) request.getConnectionEditPart().getModel());
		ConnectionAnchor ctor = getActivityPart().getSourceConnectionAnchor(
				request);
		cmd.setSource(getActivity());
		cmd.setSourceName(getActivityPart().mapSourceConnectionAnchorToName(
				ctor));
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		PathCommand cmd = new PathCommand();
		cmd.setPath((Path) request.getConnectionEditPart().getModel());
		ConnectionAnchor ctor = getActivityPart().getTargetConnectionAnchor(
				request);
		cmd.setTarget(getActivity());
		cmd.setTargetName(getActivityPart().mapTargetConnectionAnchorToName(
				ctor));
		return cmd;
	}
}