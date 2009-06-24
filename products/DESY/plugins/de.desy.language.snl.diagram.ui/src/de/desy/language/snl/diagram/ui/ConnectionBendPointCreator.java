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
				whenCon.addBendPoint(points.get(index), index);
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
		
		final Point firstRawBendPoint = source.getLocation().getCopy();
		final Point lastRawBendPoint = target.getLocation().getCopy();

		// orientationUp is based on the visual representation
		final boolean orientationRight = firstRawBendPoint.x <= lastRawBendPoint.x;

		final double realAngle = calculateNormalizedAngle(firstRawBendPoint, lastRawBendPoint);
		final double firstAngle = realAngle - ((Math.PI / 30) * index);
		final double lastAngle = realAngle + (Math.PI / 2)
				+ ((Math.PI / 30) * index);

		final double hypotenuse = calculateHypotenuse(_separation, index);

		final double firstOppositeLeg = Math.cos(firstAngle) * hypotenuse;
		final double firstAdjacentLeg = Math.sin(firstAngle) * hypotenuse;

		final double lastOppositeLeg = Math.cos(lastAngle) * hypotenuse;
		final double lastAdjacentLeg = Math.sin(lastAngle) * hypotenuse;

		double firstX = 0;
		double firstY = 0;

		double lastX = 0;
		double lastY = 0;

		if (orientationRight) {
			firstX = firstRawBendPoint.x + firstAdjacentLeg;
			firstY = firstRawBendPoint.y - firstOppositeLeg;

			lastX = lastRawBendPoint.x - lastAdjacentLeg;
			lastY = lastRawBendPoint.y + lastOppositeLeg;
		} else {
			firstX = firstRawBendPoint.x - firstAdjacentLeg;
			firstY = firstRawBendPoint.y + firstOppositeLeg;

			lastX = lastRawBendPoint.x + lastAdjacentLeg;
			lastY = lastRawBendPoint.y - lastOppositeLeg;
		}
		double copyOfFirstX = firstX;
		double copyOfFirstY = firstY;
		
		final int deltaX = lastRawBendPoint.x - firstRawBendPoint.x;
		final int deltaY = lastRawBendPoint.y - firstRawBendPoint.y;
		if (deltaX > TOLERANCE) {
			firstX = firstX + source.getSize().width;
		} else if (deltaX < -TOLERANCE) {
			lastX = lastX + target.getSize().width;
		} 
		if (deltaY > TOLERANCE) {
			firstY = firstY + source.getSize().height;
		} else if (deltaY < -TOLERANCE) {
			lastY = lastY + target.getSize().height;
		} 
		
//		int xOrientation = deltaX / Math.max(1, Math.abs(deltaX));
//		if (xOrientation == 0) {
//			xOrientation = 1;
//		}
//		int yOrientation = -deltaY / Math.max(1, Math.abs(deltaY));
//		if (yOrientation == 0) {
//			yOrientation = 1;
//		}
//		Dimension firstDimension = new Dimension((int)(xOrientation * firstAdjacentLeg), (int)(yOrientation * firstOppositeLeg));
//		
//		Point copy = firstRawBendPoint.getCopy();
//		copy.translate(firstDimension);
//		
//		System.out.println("ConnectionBendPointCreator.calculateNewPoints()");
//		System.out.println("FirstPoint: "+firstRawBendPoint+" - SecondPoint: "+lastRawBendPoint);
//		System.out.println("deltaX: "+deltaX+" - deltaY: "+deltaY);
//		System.out.println("XOrientation: "+xOrientation+" - yOrientation: "+yOrientation);
//		System.out.println("FirstX / copy.x: "+copyOfFirstX+" / "+copy.x);
//		System.out.println("FirstY / copy.y: "+copyOfFirstY+" / "+copy.y);

		final Point firstBendPoint = new Point(firstX, firstY);
		final Point secondBendPoint = new Point(lastX, lastY);

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
