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

        // Detect if figure has rule to animate
        if(((PolylineJumpConnection)conn).getInitialStartPoint() == null) {
            ((PolylineJumpConnection)conn).setInitialStartPoint(startPoint);
        }

        int xFromStart = 0, yFromStart = 0;
        xFromStart = ((PolylineJumpConnection)conn).getInitialStartPoint().x() - startPoint.x();
        yFromStart = ((PolylineJumpConnection)conn).getInitialStartPoint().y() - startPoint.y();

        if(((PolylineJumpConnection)conn).getInitialEndPoint() == null) {
            ((PolylineJumpConnection)conn).setInitialEndPoint(endPoint);
        }

        int xFromEnd = 0, yFromEnd = 0;
        xFromEnd = ((PolylineJumpConnection)conn).getInitialEndPoint().x() - endPoint.x();
        yFromEnd = ((PolylineJumpConnection)conn).getInitialEndPoint().y() - endPoint.y();

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

        AbstractOpiBuilderAnchor anchor = (AbstractOpiBuilderAnchor)conn.getSourceAnchor();
        Point sourcePoint = anchor.getSlantDifference(startPoint, newPoints.getFirstPoint());

        anchor = (AbstractOpiBuilderAnchor)conn.getTargetAnchor();
        Point targetPoint = anchor.getSlantDifference(endPoint, newPoints.getLastPoint());

        // figures with rules to move horizontally/vertically
        if(xFromStart != 0 || yFromStart != 0 || xFromEnd != 0 || yFromEnd != 0) {
            connPoints.addAll(animatedRoute(newPoints, sourcePoint, targetPoint));
        } else {
            connPoints.addAll(staticRoute(newPoints, sourcePoint, targetPoint));
        }

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

    private PointList animatedRoute(PointList oldPoints, Point sourcePoint, Point targetPoint) {
        PointList newPoints = new PointList();
        for(int i=0; i<oldPoints.size(); i++) {
            Point point = oldPoints.getPoint(i);
            if(i == 0) {
                point.setX(point.x() + sourcePoint.x());
                point.setY(point.y() + sourcePoint.y());
            } else if(i == oldPoints.size()-1) {
                point.setX(point.x() + targetPoint.x());
                point.setY(point.y() + targetPoint.y());
            }
            newPoints.addPoint(point);
        }
        return newPoints;
    }
}
