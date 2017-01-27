package org.csstudio.opibuilder.editparts;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * The router that route a connection through fixed points
 * @author Xihui Chen
 *
 */
public class FixedPointsConnectionRouter extends AbstractRouter {

    private Map<Connection, Object> constraints = new HashMap<Connection, Object>(2);

    private PolylineConnection connectionFigure;

    private ScrollPane scrollPane;

    public FixedPointsConnectionRouter() {
    }

    @Override
    public Object getConstraint(Connection connection) {
        return constraints.get(connection);
    }

    @Override
    public void remove(Connection connection) {
        constraints.remove(connection);
    }

    @Override
    public void setConstraint(Connection connection, Object constraint) {
        constraints.put(connection, constraint);
    }

    public void setConnectionFigure(PolylineConnection connectionFigure) {
        this.connectionFigure = connectionFigure;
    }

    public void setScrollPane(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }


    @Override
    public void route(Connection conn) {
        PointList connPoints = conn.getPoints().getCopy();
        PointList constraintPoints = (PointList) getConstraint(conn);
        connPoints.removeAllPoints();
        Point startPoint = getStartPoint(conn);
        conn.translateToRelative(startPoint);

        Point endPoint = getEndPoint(conn);
        conn.translateToRelative(endPoint);

        connPoints.addPoint(startPoint);
        PointList newPoints = new PointList();
        for(int i=0; i<constraintPoints.size(); i++) {
            Point point = constraintPoints.getPoint(i);
            // updateBendpoints only from inside linking container
            if(scrollPane != null && connectionFigure != null) {
                scrollPane.translateToAbsolute(point);
                connectionFigure.translateToRelative(point);
                Rectangle bounds = scrollPane.getBounds();
                point = new Point(point.x() + bounds.x(), point.y() + bounds.y());
            }
            newPoints.addPoint(point);
        }

        FixedPositionAnchor anchor = (FixedPositionAnchor)conn.getSourceAnchor();
        Point sourcePoint = anchor.getSlantDifference(startPoint, newPoints.getFirstPoint());

        anchor = (FixedPositionAnchor)conn.getTargetAnchor();
        Point targetPoint = anchor.getSlantDifference(endPoint, newPoints.getLastPoint());

        connPoints.addAll(staticRoute(newPoints, sourcePoint, targetPoint));

        connPoints.addPoint(endPoint);
        conn.setPoints(connPoints);
    }

    private PointList staticRoute(PointList oldPoints, Point sourcePoint, Point targetPoint) {
        int xDiff = 0, yDiff = 0;
        if (sourcePoint.x() != 0) {
            xDiff = sourcePoint.x();
            yDiff = targetPoint.y();
        } else {
            xDiff = targetPoint.x();
            yDiff = sourcePoint.y();
        }

        PointList newPoints = new PointList();
        for(int i=0; i<oldPoints.size(); i++) {
            Point point = oldPoints.getPoint(i);
            point.setX(point.x() + xDiff);
            point.setY(point.y() + yDiff);
            newPoints.addPoint(point);
        }
        return newPoints;
    }
}
