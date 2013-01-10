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

import org.csstudio.opibuilder.commands.SetWidgetPropertyCommand;
import org.csstudio.opibuilder.editparts.FixedPointsConnectionRouter;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy;
import org.eclipse.gef.handles.BendpointHandle;
import org.eclipse.gef.handles.BendpointMoveHandle;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.swt.graphics.Color;

/**
 * The editpolicy that allows to move the middle segment of a manhattan
 * conneciton.
 * 
 * @author Xihui Chen
 * 
 */
public class ManhattanBendpointEditPolicy extends SelectionHandlesEditPolicy {

//	private final static int MOVE_HANDLE_SIZE = 8;
	
	private ConnectionRouter originalRouter;
	private Object originalConstraint;

	@Override
	protected List<?> createSelectionHandles() {
		List<BendpointHandle> handles = new ArrayList<BendpointHandle>();
		final PointList points = getConnection().getPoints();
		if (points.size() < 4)
			return handles;
		for (int i = 1; i < points.size() - 2; i++) {
			handles.add(new BendpointMoveHandle(getConnectionEditPart(), i - 1, 
					new MidpointLocator(getConnection(), i)){
//					new MiddlePointLocator(getConnection(), i)) {
				
				@Override
				protected Color getBorderColor() {
					return (isPrimary()) ? ColorConstants.darkGreen : ColorConstants.white;
				}
				@Override
				protected Color getFillColor() {
					return ColorConstants.yellow;
				}
			});
		}
		return handles;
	}

	@Override
	public Command getCommand(Request request) {
		if (REQ_MOVE_BENDPOINT.equals(request.getType())) {
			return getMoveBendpointCommand((BendpointRequest) request);
		}
		return super.getCommand(request);
	}

	private Command getMoveBendpointCommand(BendpointRequest request) {
		PointList newPoints = getNewPoints(request);
		return new SetWidgetPropertyCommand(
				(AbstractWidgetModel) getConnectionEditPart().getModel(),
				ConnectionModel.PROP_POINTS, newPoints);
	}

	/**Get new Points based on bendpoint move request.
	 * @param request
	 * @return
	 */
	private PointList getNewPoints(BendpointRequest request) {
		PointList newPoints = getConnection().getPoints().getCopy();
		int aIndex = request.getIndex()+1;
		Point oldA = newPoints.getPoint(aIndex);
		Point oldB = newPoints.getPoint(aIndex+1);
		Point newM = request.getLocation().getCopy();
		Point newA, newB;
		getConnection().translateToRelative(newM);
		
		if(oldA.x == oldB.x){	//hozitontal move
			int dx = newM.x - oldA.x;
			newA = oldA.getTranslated(dx, 0);
			newB = oldB.getTranslated(dx, 0);
		}else { 		//vertical move
			int dy = newM.y - oldA.y;
			newA = oldA.getTranslated(0, dy);
			newB = oldB.getTranslated(0, dy);
		}
		newPoints.setPoint(newA, aIndex);
		newPoints.setPoint(newB, aIndex+1);		
		newPoints.removePoint(0);
		newPoints.removePoint(newPoints.size()-1);
		return newPoints;
	}
	
	@Override
	public void showSourceFeedback(Request request) {
		if (REQ_MOVE_BENDPOINT.equals(request.getType()))
			showMoveBendpointFeedback((BendpointRequest) request);
	}
	

	protected void showMoveBendpointFeedback(BendpointRequest request) {
		if(originalRouter == null && !(getConnection().getConnectionRouter() 
				instanceof FixedPointsConnectionRouter)){
			originalRouter = getConnection().getConnectionRouter();
			getConnection().setConnectionRouter(new FixedPointsConnectionRouter());
		}
		if(originalConstraint == null)
			originalConstraint = getConnection().getRoutingConstraint();
		getConnection().setRoutingConstraint(getNewPoints(request));
	}
	
	
	/**
	 * @see org.eclipse.gef.EditPolicy#eraseSourceFeedback(Request)
	 */
	public void eraseSourceFeedback(Request request) {
		if (REQ_MOVE_BENDPOINT.equals(request.getType()))
			eraseConnectionFeedback((BendpointRequest) request);
	}
	

	protected void eraseConnectionFeedback(BendpointRequest request) {
		if(originalRouter != null)
			getConnection().setConnectionRouter(originalRouter);
		getConnection().setRoutingConstraint(originalConstraint);
		originalConstraint = null;
		originalRouter = null;
	}
	
	
	
	/**
	 * Restores the original constraint that was saved before feedback began to
	 * show.
	 */
	protected void restoreOriginalConstraint() {
		getConnection().setRoutingConstraint(originalConstraint);
		
	}

	/**
	 * Convenience method for obtaining the host's <code>Connection</code>
	 * figure.
	 * 
	 * @return the Connection figure
	 */
	protected Connection getConnection() {
		return (Connection) ((ConnectionEditPart) getHost()).getFigure();
	}
	
	protected ConnectionEditPart getConnectionEditPart() {
		return (ConnectionEditPart) getHost();
	}

//	private final static class MiddlePointLocator implements Locator {
//
//		private Connection connection; 
//		int index; //index of start point
//
//		public MiddlePointLocator(Connection connection, int i) {
//			this.connection = connection;
//			this.index = i;
//		}
//
//		@Override
//		public void relocate(IFigure target) {
//			if(connection.getPoints().size() <= index +1)
//				return;
//			Point a = connection.getPoints().getPoint(index);
//			Point b = connection.getPoints().getPoint(index+1);
//			connection.translateToAbsolute(a);
//			connection.translateToAbsolute(b);
//			int x = (a.x + b.x) / 2;
//			int y = (a.y + b.y) / 2;
//			target.setBounds(new Rectangle(x - MOVE_HANDLE_SIZE / 2, y
//					- MOVE_HANDLE_SIZE / 2, MOVE_HANDLE_SIZE, MOVE_HANDLE_SIZE));
//		}
//
//	}

}
