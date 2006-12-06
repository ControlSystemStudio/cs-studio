package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.model.DataTypeEnum;
import org.csstudio.sds.model.DisplayModelElement;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * A polygon model element.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class PolygonElement extends DisplayModelElement {

	/**
	 * The ID of the fill points property.
	 */
	public static final String PROP_POINTS = "polygon.points";

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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		// FIXME: swende: kein Null zurückgeben
		return null;
	}

	/**
	 * Sets the specified points for the polygon.
	 * 
	 * @param points
	 *            the polygon points
	 */
	public void setPoints(final PointList points) {
		setPropertyValue(PROP_POINTS, points);
	}

	/**
	 * Gets the polygon points.
	 * 
	 * @return the polygon points
	 */
	public PointList getPoints() {
		return (PointList) getProperty(PROP_POINTS).getPropertyValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSize(final int width, final int height) {

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
			if (oldW != 0 && oldH != 0) {
				double oldRelX = (x - topLeftX) / oldW;
				double oldRelY = (y - topLeftY) / oldH;

				double newX = topLeftX + (oldRelX * width);
				double newY = topLeftY + (oldRelY * height);
				newPoint = new Point((int) newX, (int) newY);
			}

			newPoints.addPoint(newPoint);
		}

		System.out.println("OLD " + width + ":" + height + " NEW "
				+ newPoints.getBounds().width + ":"
				+ newPoints.getBounds().height);
		setPoints(newPoints);
		super.setSize(width, height);
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
