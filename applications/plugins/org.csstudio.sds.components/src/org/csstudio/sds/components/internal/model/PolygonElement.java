package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DataTypeEnum;
import org.csstudio.sds.model.DisplayModelElement;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A polygon model element.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class PolygonElement extends DisplayModelElement {

	/**
	 * The ID of the points property.
	 */
	public static final String PROP_POINTS = "polygon.points";

	/**
	 * The ID of the fill grade property.
	 */
	public static final String PROP_FILL_GRADE = "polygon.fillgrade";

	/**
	 * The ID of this model element.
	 */
	public static final String ID = "element.polygon";

	/**
	 * Standard constructor.
	 */
	public PolygonElement() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_POINTS, "points", DataTypeEnum.POINTLIST,
				new PointList());
		addProperty(PROP_FILL_GRADE, "fill grade", DataTypeEnum.DOUBLE, 1.0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_FILL_GRADE;
	}

	/**
	 * Sets the specified _points for the polygon.
	 * 
	 * @param points
	 *            the polygon points
	 */
	public void setPoints(final PointList points) {
		setPropertyValue(PROP_POINTS, points);
	}

	/**
	 * Gets the polygon _points.
	 * 
	 * @return the polygon _points
	 */
	public PointList getPoints() {
		return (PointList) getProperty(PROP_POINTS).getPropertyValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSize(final int width, final int height) {
		int targetW = Math.max(10, width);
		int targetH = Math.max(10, height);
		PointList pointList = getPoints();
		double oldW = pointList.getBounds().width;
		double oldH = pointList.getBounds().height;
		double topLeftX = pointList.getBounds().x;
		double topLeftY = pointList.getBounds().y;

		PointList newPoints = new PointList();

		for (int i = 0; i < pointList.size(); i++) {
			int x = pointList.getPoint(i).x;
			int y = pointList.getPoint(i).y;

			Point newPoint = new Point(x, y);
			if (oldW > 0 && oldH > 0) {
				double oldRelX = (x - topLeftX) / oldW;
				double oldRelY = (y - topLeftY) / oldH;

				double newX = topLeftX + (oldRelX * targetW);
				double newY = topLeftY + (oldRelY * targetH);
				newPoint = new Point((int) newX, (int) newY);
			}

			newPoints.addPoint(newPoint);
		}

		setPoints(newPoints);

		Rectangle newBounds = newPoints.getBounds();
		super.setSize(newBounds.width, newBounds.height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(final int x, final int y) {
		PointList points = getPoints();
		int oldX = points.getBounds().x;
		int oldY = points.getBounds().y;
		points.translate(x - oldX, y - oldY);

		setPoints(points);
		int newX = points.getBounds().x;
		int newY = points.getBounds().y;
		super.setLocation(newX, newY);
	}
}
