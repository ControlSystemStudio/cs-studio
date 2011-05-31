/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.PointListProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.swt.widgets.util.RotationUtil;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Sven Wende (original author)
 * @author Xihui Chen (import from SDS since 2009/9)
 */
public abstract class AbstractPolyModel extends AbstractShapeModel {
	
	

	/**
	 * Rotation angle of the widget.
	 */
	public static final String PROP_ROTATION = "rotation_angle"; //$NON-NLS-1$
	
	/**
	 * Points of the widget.
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
		if(getSize().width == width && getSize().height == height)
			return;
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
			}else if (value instanceof int[])
				this.setPoints(new PointList((int[]) value), true);
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
	
	@Override
	public void flipHorizontally() {	
		setPoints(RotationUtil.flipPointsHorizontally(getPoints()), true);		
	}
	
	@Override
	public void flipHorizontally(int centerX) {
		setPoints(RotationUtil.flipPointsHorizontally(getPoints(), centerX), true);		
	}
	
	
	@Override
	public void flipVertically() {	
		setPoints(RotationUtil.flipPointsVertically(getPoints()), true);		
	}
	
	@Override
	public void flipVertically(int centerY) {
		setPoints(RotationUtil.flipPointsVertically(getPoints(), centerY), true);
	}
	
	@Override
	public void rotate90(boolean clockwise) {
		setPoints(RotationUtil.rotatePoints(getPoints(), clockwise? 90:270), true);
	}

	
	@Override
	public void rotate90(boolean clockwise, Point center) {
		setPoints(RotationUtil.rotatePoints(getPoints(), clockwise? 90:270, center), true);
	}

	
	
	
}
