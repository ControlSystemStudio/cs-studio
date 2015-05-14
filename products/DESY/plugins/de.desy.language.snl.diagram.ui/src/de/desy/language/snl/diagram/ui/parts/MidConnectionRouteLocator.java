package de.desy.language.snl.diagram.ui.parts;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;

/**
 * This class locates the middle point of the middle segment of the {@link Connection}.
 *
 * @author Kai Meyer (C1 WPS)
 *
 */
public class MidConnectionRouteLocator extends ConnectionLocator {

    private static final int TOLERANCE = 10;
    private int _relativePosition = PositionConstants.SOUTH;

    /**
     * Constructs a MidpointLocator with associated Connection <i>c</i> and
     * index <i>i</i>. The points at index i and i+1 on the connection are used
     * to calculate the midpoint of the line segment.
     *
     * @param c
     *            the connection associated with the locator
     * @since 2.0
     */
    public MidConnectionRouteLocator(final Connection c) {
        super(c);
    }

    @Override
    public int getRelativePosition() {
        return _relativePosition;
    }

    /**
     * Returns the point of reference associated with this locator. This point
     * will be midway between middle points.
     *
     * @return the reference point
     * @since 2.0
     */
    @Override
    protected Point getReferencePoint() {
        final Connection conn = getConnection();
        final Point p = Point.SINGLETON;
        final int labelAnchorIndex = Math.max(0, (conn.getPoints().size()/2 - 1));
        final Point p1 = conn.getPoints().getPoint(labelAnchorIndex);
        final Point p2 = conn.getPoints().getPoint(labelAnchorIndex + 1);
        conn.translateToAbsolute(p1);
        conn.translateToAbsolute(p2);
        final int deltaX = p2.x - p1.x;
        final int deltaY = p2.y - p1.y;
        p.x = deltaX / 2 + p1.x;
        p.y = deltaY / 2 + p1.y;

        determineRelativePosition(deltaX, deltaY);

        return p;
    }

    /**
     * Determines the relative position of the label corresponding to the direction of the connection-segment.
     * @param deltaX The difference of the x coordinates
     * @param deltaY The difference of the y coordinates
     */
    private void determineRelativePosition(final int deltaX, final int deltaY) {
        if (deltaX > TOLERANCE) {
            if (deltaY > TOLERANCE) {
                _relativePosition = PositionConstants.NORTH_EAST;
            } else if (deltaY < -TOLERANCE) {
                _relativePosition = PositionConstants.NORTH_WEST;
            } else {
                _relativePosition = PositionConstants.NORTH;
            }
        } else if (deltaX < -TOLERANCE) {
            if (deltaY > TOLERANCE) {
                _relativePosition = PositionConstants.SOUTH_EAST;
            } else if (deltaY < -TOLERANCE) {
                _relativePosition = PositionConstants.SOUTH_WEST;
            } else {
                _relativePosition = PositionConstants.SOUTH;
            }
        } else {
            if (deltaY > TOLERANCE) {
                _relativePosition = PositionConstants.EAST;
            } else if (deltaY < -TOLERANCE) {
                _relativePosition = PositionConstants.WEST;
            } else {
                _relativePosition = PositionConstants.CENTER;
            }
        }
    }

}
