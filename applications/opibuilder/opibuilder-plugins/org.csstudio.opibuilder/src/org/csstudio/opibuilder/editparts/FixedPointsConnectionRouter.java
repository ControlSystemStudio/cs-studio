package org.csstudio.opibuilder.editparts;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * The router that route a connection through fixed points
 * @author Xihui Chen
 *
 */
public class FixedPointsConnectionRouter extends AbstractRouter {

	private Map<Connection, Object> constraints = new HashMap<Connection, Object>(2);
	
	public FixedPointsConnectionRouter() {
	}

	public Object getConstraint(Connection connection) {
		return constraints.get(connection);
	}
	
	public void remove(Connection connection) {
		constraints.remove(connection);
	}
	
	public void setConstraint(Connection connection, Object constraint) {
		constraints.put(connection, constraint);
	}
	
	
	@Override
	public void route(Connection conn) {		
		PointList connPoints = conn.getPoints().getCopy();	
		PointList newPoints = (PointList) getConstraint(conn);
		connPoints.removeAllPoints();
		Point startPoint = getStartPoint(conn);
		conn.translateToRelative(startPoint);
		connPoints.addPoint(startPoint);
		connPoints.addAll(newPoints);
		Point endPoint = getEndPoint(conn);
		conn.translateToRelative(endPoint);
		connPoints.addPoint(endPoint);
		conn.setPoints(connPoints);
	}

}
