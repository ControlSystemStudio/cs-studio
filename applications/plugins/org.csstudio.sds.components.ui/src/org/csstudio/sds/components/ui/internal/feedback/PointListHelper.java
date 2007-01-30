package org.csstudio.sds.components.ui.internal.feedback;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

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
	 * @return a point list copy, which has been scaled to the new size
	 */
	public static PointList scaleToSize(final PointList points, final int width,
			final int height) {
		assert points != null;
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(
					"Illegal dimensions. Width and height must be > 0."); //$NON-NLS-1$
		}
		
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
	 * specified point list to the Point (x,y).
	 * 
	 * @param points
	 *            the point list
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return a point list copy, which has been scaled to the new location
	 */
	public static PointList scaleToLocation(final PointList points, final int x,
			final int y) {
		int oldX = points.getBounds().x;
		int oldY = points.getBounds().y;
		
		PointList result = points.getCopy();
		result.translate(x - oldX, y - oldY);
		return result;
	}
	
	/**
	 * Scales the point list to the new bounds.
	 * 
	 * @param points the point list
	 * @param targetBounds the target bounds
	 * @return a point list copy, which has been scaled to the new bounds
	 */
	public static PointList scaleTo(final PointList points, final Rectangle targetBounds) {
		PointList result = scaleToLocation(points, targetBounds.x,
				targetBounds.y);
		result = scaleToSize(result, targetBounds.width, targetBounds.height);

		return result;

	}
}
