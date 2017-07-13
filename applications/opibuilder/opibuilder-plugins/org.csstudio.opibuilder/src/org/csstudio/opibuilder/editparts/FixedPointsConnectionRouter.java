package org.csstudio.opibuilder.editparts;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.editparts.AbstractOpiBuilderAnchor.ConnectorOrientation;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;

/**
 * The router that route a connection through fixed points
 * @author Xihui Chen
 *
 */
public class FixedPointsConnectionRouter extends AbstractRouter {

    private static final Logger LOGGER = Logger.getLogger(FixedPointsConnectionRouter.class.getCanonicalName());

    private Map<Connection, Object> constraints = new HashMap<Connection, Object>(2);

    private ConnectionModel connectionModel;

    private ScrollPane scrollPaneCached;
    private boolean scrollPaneInitialized;

    public FixedPointsConnectionRouter() {
        scrollPaneInitialized = false;
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

    public void setConnectionModel(ConnectionModel connectionModel) {
        this.connectionModel = connectionModel;
    }

    @Override
    public void route(Connection conn) {
        PointList connPoints = new PointList();
        PointList constraintPoints = (PointList) getConstraint(conn);

        // we get the absolute and relative start points of the connection
        // absolute: on screen, relative: according to parent. May be different if the OPI is being scrolled
        Point startPoint = getStartPoint(conn);
        Point startPointRel = startPoint.getCopy();
        conn.translateToRelative(startPointRel);

        // we get the absolute and relative end points of the connection
        Point endPoint = getEndPoint(conn);
        Point endPointRel = endPoint.getCopy();
        conn.translateToRelative(endPointRel);

        connPoints.addPoint(startPointRel);

        AbstractOpiBuilderAnchor anchor = (AbstractOpiBuilderAnchor)conn.getSourceAnchor();
        final ConnectorOrientation startDirection = anchor.getOrientation();

        anchor = (AbstractOpiBuilderAnchor)conn.getTargetAnchor();
        final ConnectorOrientation endDirection = anchor.getOrientation();

        PointList newPoints = constraintPoints.getCopy();
        final ScrollPane sp = getScrollPane();
        if (connectionModel != null && sp != null) {
            for (int i = 0; i < newPoints.size(); ++i) {
                final Point point = newPoints.getPoint(i);
                sp.translateToAbsolute(point);
                conn.translateToRelative(point);
                Rectangle bounds = sp.getBounds();
                newPoints.setPoint(new Point(point.x() + bounds.x(), point.y() + bounds.y()), i);
            }
        }
        LOGGER.log(Level.FINEST, buildPointDebug("newPoints", newPoints));

        connPoints.addAll(adjustRouteEndsToAnchors(newPoints, startDirection, endDirection, startPointRel, endPointRel));

        connPoints.addPoint(endPointRel);

        conn.setPoints(connPoints);
    }

    private PointList adjustRouteEndsToAnchors(PointList oldPoints, ConnectorOrientation startDirection, ConnectorOrientation endDirection, Point startPointRel, Point endPointRel) {
        simpleMove(oldPoints, startDirection, endDirection, startPointRel, endPointRel);

        return oldPoints;
    }

    //
    //--------------------------------------------------------------- One or two point connections -------------------------------
    //
    private void simpleMove(PointList translatedPoints, ConnectorOrientation startDirection, ConnectorOrientation endDirection, Point startPointRel, Point endPointRel) {
        // Handle the start point
        final Point firstPoint = translatedPoints.getFirstPoint();
        onePointMove(firstPoint, startDirection, startPointRel);
        translatedPoints.setPoint(firstPoint, 0);

        // Handle the end point
        final int lastIndex = translatedPoints.size() - 1;
        final Point lastPoint = translatedPoints.getPoint(lastIndex);
        onePointMove(lastPoint, endDirection, endPointRel);
        translatedPoints.setPoint(lastPoint, lastIndex);
    }

    private void onePointMove(Point pointToMove, ConnectorOrientation connectorDirection, Point anchor) {
        if (connectorDirection == ConnectorOrientation.VERTICAL) {
            // Only horizontal move affects the connection
            pointToMove.setX(anchor.x());
        } else {
            // Only vertical move affects the connection
            pointToMove.setY(anchor.y());
        }
    }

    //
    //--------------------------------------------------------------- Other methods -------------------------------
    //

    private String buildPointDebug(String name, PointList points) {
        final StringBuilder sb = new StringBuilder(points.size() * 8 + 32);
        sb.append(name).append(": [");
        for(int i = 0; i < points.size(); ++i) {
            final Point p = points.getPoint(i);
            sb.append(p.toString()).append(i>=points.size()-1?"":", ");
        }
        return sb.append(']').toString();
    }

    private ScrollPane getScrollPane() {
        if (!scrollPaneInitialized) {
            scrollPaneCached = getScrollPaneImpl();
            scrollPaneInitialized = true;
        }
        return scrollPaneCached;
    }

    private ScrollPane getScrollPaneImpl() {
        if (connectionModel != null) {
            if (connectionModel.getScrollPane() != null) {
                return connectionModel.getScrollPane();
            } else {
                // are both connected widgets defined - this should be always true
                if ((connectionModel.getSource() == null) || (connectionModel.getTarget() == null))
                    return null;
                AbstractContainerModel sourceModel =  connectionModel.getSource().getParent();
                AbstractContainerModel targetModel =  connectionModel.getTarget().getParent();
                // if one of them is null, then at least one end if the connection is in the top-most container. No translation.
                if ((sourceModel == null) || (targetModel == null))
                    return null;
                // otherwise, see if any of them is scrollable
                AbstractScrollableEditpart sourceEditpart = getScrollable(sourceModel.getEditPart());
                AbstractScrollableEditpart targetEditpart = getScrollable(targetModel.getEditPart());

                // if one of them is not linking container, then return null
                if ((sourceEditpart == null) || (targetEditpart == null))
                    return null;

                // now we have two options:
                // - one linking container is inside the other
                // - they are not one inside the other, but they have common parent

                // option one?
                ScrollPane scrollPane = getScrollPaneForContained(sourceEditpart, targetEditpart);
                if (scrollPane != null)
                    return scrollPane;

                // option two
                return getCommonScrollableParent(sourceEditpart, targetEditpart);
            }
        }
        return null;
    }

    private AbstractScrollableEditpart getScrollable(EditPart container) {
        EditPart c = container;
        while (c != null) {
            if (c instanceof AbstractScrollableEditpart)
                return (AbstractScrollableEditpart) c;
            c = c.getParent();
        }
        return null;
    }

    private ScrollPane getScrollPaneForContained(AbstractScrollableEditpart one, AbstractScrollableEditpart two) {
        if (isAAncestorOfB(one, two))
            return one.getScrollPane();
        else if (isAAncestorOfB(two, one))
            return two.getScrollPane();
        else
            return null;
    }

    /**
     * @param a scrollable A
     * @param b scrollable B
     * @return <code>true</code> if A is ancestor of B, <code>false</code> otherwise
     */
    private boolean isAAncestorOfB(AbstractScrollableEditpart a, AbstractScrollableEditpart b) {
        EditPart part = b;
        // is two child of one?
        while (part != null) {
            if (part == a)
                return true;
            part = part.getParent();
        }
        return false;
    }

    private ScrollPane getCommonScrollableParent(AbstractScrollableEditpart one, AbstractScrollableEditpart two) {
        AbstractScrollableEditpart partOne = one;
        // for each scrollable parent of one, check all scrollable parents of two
        while (partOne != null) {
            AbstractScrollableEditpart partTwo = two;
            while (partTwo != null) {
                if (partOne == partTwo)
                    return partOne.getScrollPane();             // common parent found
                partTwo = getScrollable(partTwo.getParent());
            }
            partOne = getScrollable(partOne.getParent());
        }
        // no common parent
        return null;
    }
}
