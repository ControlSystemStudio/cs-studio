package de.desy.language.snl.diagram.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * Customized router for {@link Connection}s.
 *
 * @author Kai Meyer, Sebastian Middeke (C1 WPS)
 *
 */
@Deprecated
public class CustomFanRouter extends AutomaticRouter {

    /**
     * Internal class used as keys for the map.
     *
     * A MapKey holds the source and target {@link ConnectionAnchor} of a
     * {@link Connection}.
     *
     * @author Kai Meyer (C1 WPS)
     *
     */
    private class MapKey {
        /**
         * The source {@link ConnectionAnchor}.
         */
        private final ConnectionAnchor _source;
        /**
         * The target {@link ConnectionAnchor}.
         */
        private final ConnectionAnchor _target;

        /**
         * Constructor.
         *
         * @param source
         *            The source {@link ConnectionAnchor}
         * @param target
         *            The Target {@link ConnectionAnchor}
         *
         * @requires source != null
         * @requires target != null
         */
        public MapKey(final ConnectionAnchor source,
                final ConnectionAnchor target) {
            assert source != null : "source != null";
            assert target != null : "target != null";

            _source = source;
            _target = target;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result
                    + ((_source == null) ? 0 : _source.hashCode());
            result = prime * result
                    + ((_target == null) ? 0 : _target.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final MapKey other = (MapKey) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (_source == null) {
                if (other._source != null)
                    return false;
            } else if (!_source.equals(other._source))
                return false;
            if (_target == null) {
                if (other._target != null)
                    return false;
            } else if (!_target.equals(other._target))
                return false;
            return true;
        }

        private CustomFanRouter getOuterType() {
            return CustomFanRouter.this;
        }

    }

    /**
     * The space between two {@link Connection}s in pixel.
     */
    private int _separation = 10;
    /**
     * THe {@link Map} of all known {@link Connection}s.
     */
    private final Map<MapKey, List<Connection>> _connectionMap;

    /**
     * Constructor.
     */
    public CustomFanRouter() {
        _connectionMap = new HashMap<MapKey, List<Connection>>();
    }

    /**
     * {@inheritDoc}
     *
     * @requires conn != null
     */
    @Override
    public void route(final Connection conn) {
        assert conn != null : "conn != null";

        cleanMap();
        final ConnectionAnchor sourceAnchor = conn.getSourceAnchor();
        final ConnectionAnchor targetAnchor = conn.getTargetAnchor();

        if (sourceAnchor != null && targetAnchor != null) {
            final MapKey key = new MapKey(sourceAnchor, targetAnchor);
            final MapKey reverseKey = new MapKey(targetAnchor, sourceAnchor);

            if (_connectionMap.containsKey(key)
                    || _connectionMap.containsKey(reverseKey)) {
                final List<Connection> connections = _connectionMap.get(key);
                if (!connections.contains(conn)) {
                    connections.add(conn);
                }
                final int index = connections.indexOf(conn);
                handleCollision(conn, index);
            } else {
                final List<Connection> list = new LinkedList<Connection>();
                list.add(conn);
                final List<Connection> reverseList = new LinkedList<Connection>();
                reverseList.add(conn);
                _connectionMap.put(key, list);
                _connectionMap.put(reverseKey, reverseList);
            }
        }

        super.route(conn);
    }

    /**
     * Removes all {@link Connection}s with no target or source
     * {@link ConnectionAnchor} from the {@link Map}.
     */
    private void cleanMap() {
        final Set<MapKey> iterkeySet = new HashSet<MapKey>(_connectionMap
                .keySet());
        for (final MapKey key : iterkeySet) {
            final List<Connection> list = _connectionMap.get(key);
            final List<Connection> iterList = new ArrayList<Connection>(list);
            for (final Connection conn : iterList) {
                final ConnectionAnchor sourceAnchor = conn.getSourceAnchor();
                final ConnectionAnchor targetAnchor = conn.getTargetAnchor();
                if (sourceAnchor == null || targetAnchor == null) {
                    list.remove(conn);
                }
            }
            if (list.isEmpty()) {
                _connectionMap.remove(key);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleCollision(final PointList list, final int index) {
        // nothing to do
    }

    /**
     * Handles the re-routing of a {@link Connection}.
     *
     * @param conn
     *            The {@link Connection}
     * @param index
     *            The index of the connection between its source and target
     * @requires conn != null;
     * @requires index >= 0
     */
    protected void handleCollision(final Connection conn, final int index) {
        assert conn != null : "conn != null";
        assert index >= 0 : "index >= 0";

        final PointList list = conn.getPoints();
        final Point firstPoint = list.getFirstPoint();
        final Point lastPoint = list.getLastPoint();

        if (index != 0) {
            list.removeAllPoints();
            list.addAll(calculateNewPoints(firstPoint, lastPoint, index));
        }
    }

//    private void refreshBendPoints(Connection conn) {
//        final List<Point> modelConstraint = getCastedModel().getBendPoints();
//        final List<Bendpoint> figureConstraint = new ArrayList<Bendpoint>();
//        if (modelConstraint != null) {
//            for (final Point current : modelConstraint) {
//                final AbsoluteBendpoint abp = new AbsoluteBendpoint(current);
//                figureConstraint.add(abp);
//            }
//            conn.setRoutingConstraint(figureConstraint);
//        }
//    }

    /**
     * Calculates the points necessary for the re-routing of the
     * {@link Connection}.
     *
     * @param firstPoint
     *            The start point of the {@link Connection}
     * @param lastPoint
     *            The end point of the {@link Connection}
     * @param index
     *            The index of the connection between its source and target
     * @return A PointList containing the calculated points
     * @requires firstPoint != null
     * @requires lastPoint != null
     * @requires index >= 0
     */
    private PointList calculateNewPoints(final Point firstPoint,
            final Point lastPoint, final int index) {
        assert firstPoint != null : "firstPoint != null";
        assert lastPoint != null : "lastPoint != null";
        assert index >= 0 : "index >= 0";

        // orientationUp is based on the visual representation
        final boolean orientationRightUp = firstPoint.x <= lastPoint.x;

        final double realAngle = calculateNormalizedAngle(firstPoint, lastPoint);
        final double firstAngle = realAngle - ((Math.PI / 30) * index);
        final double lastAngle = realAngle + (Math.PI / 2)
                + ((Math.PI / 30) * index);

        final double hypotenuse = calculateHypotenuse(_separation, index);

        final double firstOppositeLeg = Math.cos(firstAngle) * hypotenuse;
        final double firstAdjacentLeg = Math.sin(firstAngle) * hypotenuse;

        final double lastOppositeLeg = Math.cos(lastAngle) * hypotenuse;
        final double lastAdjacentLeg = Math.sin(lastAngle) * hypotenuse;

        double newFirstX = 0;
        double newFirstY = 0;

        double newLastX = 0;
        double newLastY = 0;

        if (orientationRightUp) {
            newFirstX = firstPoint.x + firstAdjacentLeg;
            newFirstY = firstPoint.y - firstOppositeLeg;

            newLastX = lastPoint.x - lastAdjacentLeg;
            newLastY = lastPoint.y + lastOppositeLeg;
        } else {
            newFirstX = firstPoint.x - firstAdjacentLeg;
            newFirstY = firstPoint.y + firstOppositeLeg;

            newLastX = lastPoint.x + lastAdjacentLeg;
            newLastY = lastPoint.y - lastOppositeLeg;
        }

        final Point firstBendPoint = new Point(newFirstX, newFirstY);
        final Point secondBendPoint = new Point(newLastX, newLastY);

        final PointList result = new PointList();
        result.addPoint(firstPoint);
        result.addPoint(firstBendPoint);
        result.addPoint(secondBendPoint);
        result.addPoint(lastPoint);

        return result;
    }

    /**
     * Calculates the angle of the {@link Connection}.
     *
     * @param firstPoint
     *            The start point of the {@link Connection}.
     * @param lastPoint
     *            The end point of the {@link Connection}.
     * @return The angle of the {@link Connection}
     * @requires firstPoint != null
     * @requires lastPoint != null
     */
    private double calculateNormalizedAngle(final Point firstPoint,
            final Point lastPoint) {
        assert firstPoint != null : "firstPoint != null";
        assert lastPoint != null : "lastPoint != null";

        double angle = 0.0;
        final double deltaX = lastPoint.x - firstPoint.x;
        final double deltaY = lastPoint.y - firstPoint.y;
        final double gradient = deltaY / deltaX;

        angle = Math.atan(gradient); // 0 - 2Pi
        angle = angle + Math.PI / 4;

        if (angle >= 2 * Math.PI) {
            angle = angle - 2 * Math.PI;
        }

        return angle;
    }

    /**
     * Calculates the length of the first re-routing segment.
     *
     * @param distance
     *            The default distance between two {@link Connection}s
     * @param index
     *            The index of the {@link Connection} between its source and
     *            target
     * @return The length of the segment
     * @requires index >= 0
     */
    private double calculateHypotenuse(final int distance, final int index) {
        assert index >= 0 : "index >= 0";

        final double result = Math.sqrt(2 * (distance * distance)) * index;
        return result;
    }

    /**
     * Sets the space between two {@link Connection}s.
     *
     * @param separation
     * @requires separation >= 0
     */
    public void setSeparation(final int separation) {
        assert separation >= 0 : "separation >= 0";

        _separation = separation;
    }

    /**
     * Returns the space between two {@link Connection}s.
     *
     * @return The space.
     */
    public int getSeparation() {
        return _separation;
    }

}
