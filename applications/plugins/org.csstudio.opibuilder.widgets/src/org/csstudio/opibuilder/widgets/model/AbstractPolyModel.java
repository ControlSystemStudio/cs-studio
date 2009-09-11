package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.PointListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.widgets.util.RotationUtil;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author Xihui Chen
 */
public abstract class AbstractPolyModel extends AbstractShapeModel {
	
	

	/**
	 * The ID of the rotation property. Is only used if the widget is rotatable.
	 * 
	 * @see AbstractWidgetModel#isRotatable()
	 */
	public static final String PROP_ROTATION = "rotation"; //$NON-NLS-1$
	
	/**
	 * The ID of the points property.
	 */
	public static final String PROP_POINTS = "points"; //$NON-NLS-1$	
	

	/**
	 * The original Points without rotation.
	 */
	private PointList originalPoints;

	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new DoubleProperty(PROP_ROTATION, "Rotation Angle",
				WidgetPropertyCategory.Display, 0, 0, 360));	
		addProperty(new PointListProperty(PROP_POINTS, 
				"Points", WidgetPropertyCategory.Display, new PointList()));		
	}


	/**
	 * Sets the specified _points for the polygon.
	 * 
	 * @param points
	 *            the polygon points
	 * @param rememberPoints true if the zero relative points should be remembered, false otherwise.
	 */
	public void setPoints(final PointList points,
			final boolean rememberPoints) {
		if (points.size() > 0) {
			PointList copy = points.getCopy();
			if (rememberPoints) {
				this.rememberZeroDegreePoints(copy);
			}
			super.setPropertyValue(PROP_POINTS, copy);
			Rectangle bounds = copy.getBounds();
			super.setPropertyValue(PROP_XPOS, bounds.x);
			super.setPropertyValue(PROP_YPOS, bounds.y);
			super.setPropertyValue(PROP_WIDTH, bounds.width);
			super.setPropertyValue(PROP_HEIGHT, bounds.height);
		}
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
		int targetW = Math.max(1, width);
		int targetH = Math.max(1, height);
		PointList pointList = getPoints();
		double oldW = pointList.getBounds().width;
		double oldH = pointList.getBounds().height;
		double topLeftX = pointList.getBounds().x;
		double topLeftY = pointList.getBounds().y;

		if (oldW != targetW || oldH != targetH) {
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
					long roundedX = Math.round(newX);
					long roundedY = Math.round(newY);
					newPoint = new Point(roundedX, roundedY);
				}

				newPoints.addPoint(newPoint);
			}
			setPoints(newPoints, true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(final int x, final int y) {
		PointList points = getPoints();
		int oldX = getLocation().x;
		int oldY = getLocation().y;
		points.translate(x - oldX, y - oldY);

		setPoints(points, true);
	}

	/**
	 * Rotates all points.
	 * 
	 * @param points The PoinList, which points should be rotated
	 * @param angle
	 *            The angle to rotate
	 * @return The rotated PointList
	 */
	public PointList rotatePoints(final PointList points, final double angle) {		
		Rectangle pointBounds = points.getBounds();
		Point rotationPoint = pointBounds.getCenter();
		PointList newPoints = new PointList();

		for (int i = 0; i < points.size(); i++) {
			newPoints.addPoint(RotationUtil.rotate(points.getPoint(i), angle,
					rotationPoint));
		}

		Rectangle newPointBounds = newPoints.getBounds();
		if (!rotationPoint.equals(newPointBounds.getCenter())) {
			Dimension difference = rotationPoint.getCopy().getDifference(
					newPointBounds.getCenter());
			newPoints.translate(difference.width, difference.height);
		}

		return newPoints;
	}
	
	/**
	 * Rotates the given points to 0 degrees and sets them as <code>_originalPoints</code>.
	 * @param points The current {@link PointList}
	 */
	protected void rememberZeroDegreePoints(final PointList points) {
		if (this.getRotationAngle()==0) {
			originalPoints = points.getCopy();
		} else {
			originalPoints = this.rotatePoints(points, -this.getRotationAngle());
		}
	}
	
	/**
	 * Returns the rotation angle for this widget. Returns 0 if this widget is
	 * not rotatable
	 * 
	 * @return The rotation angle
	 */
	public final double getRotationAngle() {
			return (Double) getProperty(PROP_ROTATION).getPropertyValue();
	}

	/**
	 * Sets the rotation angle for this widget, only when this widget is
	 * rotatable.
	 * 
	 * @param angle
	 *            The angle
	 */
	public final void setRotationAngle(final double angle) {
		setPropertyValue(PROP_ROTATION, angle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized void setPropertyValue(Object propertyID,
			Object value) {
		if (propertyID.equals(AbstractPolyModel.PROP_POINTS)) {
			if (value instanceof PointList) {
				this.setPoints((PointList) value, true);
			}
		} 
		else if (propertyID.equals(AbstractWidgetModel.PROP_XPOS) ||
				propertyID.equals(AbstractWidgetModel.PROP_YPOS) ||
				propertyID.equals(AbstractWidgetModel.PROP_WIDTH) ||
				propertyID.equals(AbstractWidgetModel.PROP_HEIGHT)){
			int newValue = Integer.parseInt(value.toString());
			if (propertyID.equals(AbstractWidgetModel.PROP_XPOS)
					&& (newValue != getPoints().getBounds().x)) {
				setLocation(newValue, getLocation().y);
			} else if (propertyID.equals(AbstractWidgetModel.PROP_YPOS)
					&& ((newValue != getPoints().getBounds().y))) {
				setLocation(getLocation().x, newValue);
			} else if (propertyID.equals(AbstractWidgetModel.PROP_WIDTH)
					&& (newValue != getPoints().getBounds().width)) {
				setSize(newValue, getSize().height);
			} else if (propertyID.equals(AbstractWidgetModel.PROP_HEIGHT)
					&& (newValue != getPoints().getBounds().height)) {
				setSize(getSize().width, newValue);
			}
		}else {
			super.setPropertyValue(propertyID, value);
		}
	}
	
	public PointList getOriginalPoints() {
		return originalPoints;
	}
	
	
}
