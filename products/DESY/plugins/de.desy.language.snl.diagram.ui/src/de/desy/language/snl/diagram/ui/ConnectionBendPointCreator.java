package de.desy.language.snl.diagram.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;

import de.desy.language.snl.diagram.model.SNLModel;
import de.desy.language.snl.diagram.model.WhenConnection;

public class ConnectionBendPointCreator {

    private static final int TOLERANCE = 10;

    private int _separation;

    public void create(final WhenConnection whenCon, final int count) {
        final SNLModel source = whenCon.getSource();
        final SNLModel target = whenCon.getTarget();

        if (count > 0) {
            final List<Point> points = calculateNewPoints(source, target, count);
            for (int index = 0; index < points.size(); index++) {
                Point bendPoint = points.get(index);
                whenCon.addBendPoint(bendPoint, index);
            }
        }
    }

    /**
     * Calculates the points necessary for the re-routing of the
     * {@link Connection}.
     *
     * @param source
     *            The start point of the {@link Connection}
     * @param target
     *            The end point of the {@link Connection}
     * @param index
     *            The index of the connection between its source and target
     * @return A PointList containing the calculated points
     * @requires firstPoint != null
     * @requires lastPoint != null
     * @requires index >= 0
     */
    private List<Point> calculateNewPoints(final SNLModel source,
            final SNLModel target, final int index) {
        assert source != null : "source != null";
        assert target != null : "target != null";
        assert index >= 0 : "index >= 0";

        final Point firstAnchorPoint = source.getLocation().getCopy();
        final Point secondAnchorPoint = target.getLocation().getCopy();

        final double realAngle = calculateNormalizedAngle(firstAnchorPoint, secondAnchorPoint);
        final double firstAngle = realAngle - ((Math.PI / 30) * index);
        final double secondAngle = realAngle + (Math.PI / 2)
                + ((Math.PI / 30) * index);

        final double hypotenuse = calculateHypotenuse(_separation, index);

        final double firstOppositeLeg = Math.cos(firstAngle) * hypotenuse;
        final double firstAdjacentLeg = Math.sin(firstAngle) * hypotenuse;

        final double secondOppositeLeg = Math.cos(secondAngle) * hypotenuse;
        final double secondAdjacentLeg = Math.sin(secondAngle) * hypotenuse;

        final int deltaX = secondAnchorPoint.x - firstAnchorPoint.x;
        final int deltaY = secondAnchorPoint.y - firstAnchorPoint.y;

        int firstXOrientation = deltaX / Math.max(1, Math.abs(deltaX));
        if (firstXOrientation == 0) {
            firstXOrientation = 1;
        }
        Dimension firstRelativeDimension = new Dimension((int)(firstXOrientation * firstAdjacentLeg), -(int)(firstXOrientation * firstOppositeLeg));
        Dimension secondRelativeDimension = new Dimension(-(int)(firstXOrientation * secondAdjacentLeg), (int)(firstXOrientation * secondOppositeLeg));

        Point firstBendPoint = firstAnchorPoint.getCopy();
        firstBendPoint.translate(firstRelativeDimension);
        Point secondBendPoint = secondAnchorPoint.getCopy();
        secondBendPoint.translate(secondRelativeDimension);

        if (deltaX > TOLERANCE) {
            firstBendPoint.x = firstBendPoint.x + source.getSize().width;
        } else if (deltaX < -TOLERANCE) {
            secondBendPoint.x = secondBendPoint.x + target.getSize().width;
        }
        if (deltaY > TOLERANCE) {
            firstBendPoint.y = firstBendPoint.y + source.getSize().height;
        } else if (deltaY < -TOLERANCE) {
            secondBendPoint.y = secondBendPoint.y + target.getSize().height;
        } else {
            firstBendPoint.y = firstBendPoint.y + source.getSize().height / 2;
            secondBendPoint.y = secondBendPoint.y + target.getSize().height / 2;
        }

        final List<Point> result = new ArrayList<Point>(2);
        result.add(firstBendPoint);
        result.add(secondBendPoint);

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
