package org.csstudio.sds.components.internal.model;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * A transformator utility for {@link PointList} objects.
 * 
 * @author Sven Wende
 */
public final class PointListHelper {
	
	/**
	 * Private constructor to prevent instantiation.
	 *
	 */
	private PointListHelper(){
		
	}
	/**
	 * Transforms the points in the specified point list to fit the given size.
	 * All point coordinates are transformed relatively to the new size.
	 * 
	 * @param points
	 *            the point list
	 * @param width
	 *            the new width
	 * @param height
	 *            the new height
	 * @return the transformed point list
	 */
	public static PointList moveToSize(final PointList points, final int width,
			final int height) {
		double oldW = points.getBounds().width;
		double oldH = points.getBounds().height;
		double topLeftX = points.getBounds().x;
		double topLeftY = points.getBounds().y;

		PointList newPoints = new PointList();

		for (int i = 0; i < points.size(); i++) {
			int x = points.getPoint(i).x;
			int y = points.getPoint(i).y;

			Point newPoint = new Point(x, y);
			if (oldW != 0 && oldH != 0) {
				double oldRelX = (x - topLeftX) / oldW;
				double oldRelY = (y - topLeftY) / oldH;

				double newX = topLeftX + (oldRelX * width);
				double newY = topLeftY + (oldRelY * height);
				newPoint = new Point((int) newX, (int) newY);
			}

			newPoints.addPoint(newPoint);
		}

		return newPoints;
	}

	/**
	 * Moves the origin (0,0) of the coordinate system of all the points in the
	 * specified point list to the Point (x,y). This updates the position of all
	 * the points in the point list.
	 * 
	 * @param points
	 *            the point list
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return the transformed point list
	 */
	public static PointList moveToLocation(final PointList points, final int x,
			final int y) {
		int oldX = points.getBounds().x;
		int oldY = points.getBounds().y;
		points.translate(x - oldX, y - oldY);
		return points;
	}
}
