package de.desy.language.snl.diagram.ui;

import org.eclipse.draw2d.AutomaticRouter;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

public class CustomFanRouter extends AutomaticRouter {

	private int _separation = 10;

	@Override
	protected void handleCollision(PointList list, int index) {
		Point firstPoint = list.getFirstPoint();
		Point lastPoint = list.getLastPoint();

		if (index != 1) {
			list.removeAllPoints();
			list.addAll(calculateNewPoints(firstPoint, lastPoint, index-1));
		}
	}

	private PointList calculateNewPoints(Point firstPoint, Point lastPoint,
			int index) {
		// orientationUp is based on the visual representation
		boolean orientationRightUp = firstPoint.x <= lastPoint.x;

		double realAngle = calculateNormalizedAngle(firstPoint, lastPoint);
		double firstAngle = realAngle - ((Math.PI / 30) * index);
		double lastAngle = realAngle + (Math.PI / 2) + ((Math.PI / 30) * index);

		double hypotenuse = calculateHypotenuse(_separation, index);

		double firstOppositeLeg = Math.cos(firstAngle) * hypotenuse;
		double firstAdjacentLeg = Math.sin(firstAngle) * hypotenuse;

		double lastOppositeLeg = Math.cos(lastAngle) * hypotenuse;
		double lastAdjacentLeg = Math.sin(lastAngle) * hypotenuse;

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

		Point firstBendPoint = new Point(newFirstX, newFirstY);
		Point secondBendPoint = new Point(newLastX, newLastY);

		PointList result = new PointList();
		result.addPoint(firstPoint);
		result.addPoint(firstBendPoint);
		result.addPoint(secondBendPoint);
		result.addPoint(lastPoint);

		return result;
	}

	private double calculateNormalizedAngle(Point firstPoint, Point lastPoint) {
		double angle = 0.0;
		double deltaX = lastPoint.x - firstPoint.x;
		double deltaY = lastPoint.y - firstPoint.y;
		double gradient = deltaY / deltaX;

		angle = Math.atan(gradient); // 0 - 2Pi
		angle = angle + Math.PI / 4;

		if (angle >= 2 * Math.PI) {
			angle = angle - 2 * Math.PI;
		}

		return angle;
	}

	private double calculateHypotenuse(int distance, int index) {
		double longDistance = distance * index;
		double result = Math.sqrt((distance * distance) + (distance * distance));
		result = result * index;
		return result;
	}

	public void setSeparation(int separation) {
		_separation = separation;
	}

	public int getSeparation() {
		return _separation;
	}

}
