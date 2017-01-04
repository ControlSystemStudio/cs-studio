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
        PointList newPoints = (PointList) getConstraint(conn);
        connPoints.removeAllPoints();
        Point startPoint = getStartPoint(conn);
        conn.translateToRelative(startPoint);

        Point endPoint = getEndPoint(conn);
        conn.translateToRelative(endPoint);

        connPoints.addPoint(startPoint);
        for(int i=0; i<newPoints.size(); i++) {
            Point point = newPoints.getPoint(i);
            // updateBendpoints only from inside linking container
            if(scrollPane != null && connectionFigure != null) {
                scrollPane.translateToAbsolute(point);
                connectionFigure.translateToRelative(point);
                Rectangle bounds = scrollPane.getBounds();
                point = new Point(point.x() + bounds.x(), point.y() + bounds.y());
            }
            connPoints.addPoint(point);
        }

        connPoints.addPoint(endPoint);
        conn.setPoints(connPoints);
    }

}
