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
package org.csstudio.sds.components.model;

import org.csstudio.sds.components.internal.localization.Messages;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.PointlistProperty;
import org.csstudio.sds.util.RotationUtil;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author Sven Wende
 */
public abstract class AbstractPolyModel extends AbstractWidgetModel {

	/**
	 * The ID of the points property.
	 */
	public static final String PROP_POINTS = "points"; //$NON-NLS-1$

	/**
	 * The ID of the fill level property.
	 */
	public static final String PROP_FILL = "fill"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 10;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 20;
	
	/**
	 * Determines if the rotation values are initialized.
	 */
	private boolean _isRotationInitialized = false;

	/**
	 * Constructor.
	 */
	public AbstractPolyModel() {
		super(true);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_POINTS, new PointlistProperty(
				Messages.PolyElement_POINTS, WidgetPropertyCategory.Position,
				new PointList()));
		addProperty(PROP_FILL, new DoubleProperty(Messages.FillLevelProperty,
				WidgetPropertyCategory.Behaviour, 100.0, 0.0, 100.0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getDoubleTestProperty() {
		return PROP_FILL;
	}

	/**
	 * Sets the specified _points for the polygon.
	 * 
	 * @param points
	 *            the polygon points
	 */
	public final void setPoints(final PointList points) {
		if (points.size()>0) {
			PointList copy = points.getCopy();
			super.setPropertyValue(PROP_POINTS, copy);
			Rectangle bounds = copy.getBounds();
			super.setPropertyValue(PROP_POS_X, bounds.x);
			super.setPropertyValue(PROP_POS_Y, bounds.y);
			super.setPropertyValue(PROP_WIDTH, bounds.width);
			super.setPropertyValue(PROP_HEIGHT, bounds.height);	
		}
	}

	/**
	 * Gets the polygon _points.
	 * 
	 * @return the polygon _points
	 */
	public final PointList getPoints() {
		return (PointList) getProperty(PROP_POINTS).getPropertyValue();
	}

	/**
	 * Returns the fill grade.
	 * @return the fill grade
	 */
	public final double getFill() {
		return (Double) getProperty(PROP_FILL).getPropertyValue();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setSize(final int width, final int height) {
		int targetW = Math.max(1, width);
		int targetH = Math.max(1, height);
		PointList pointList = getPoints();
		double oldW = pointList.getBounds().width;
		double oldH = pointList.getBounds().height;
		double topLeftX = pointList.getBounds().x;
		double topLeftY = pointList.getBounds().y;

		if (oldW!=targetW || oldH!=targetH) {
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
			setPoints(newPoints);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setLocation(final int x, final int y) {
		PointList points = getPoints();
		int oldX = points.getBounds().x;
		int oldY = points.getBounds().y;
		points.translate(x - oldX, y - oldY);

		setPoints(points);
		int newX = points.getBounds().x;
		int newY = points.getBounds().y;
		super.setLocation(newX, newY);
	}
	
	/**
	 * Rotates all points.
	 * @param angle The angle to rotate
	 */
	public final void rotatePoints(final double angle) {
		if (_isRotationInitialized || (this.getRotationAngle()==0 && angle!=0)) {
			double rotationAngle = getRotationAngle();
			double trueAngle = angle - rotationAngle;
			
			Rectangle pointBounds = this.getPoints().getBounds();
			Point rotationPoint = pointBounds.getCenter();
			PointList newPoints = new PointList();
			for (int i=0;i<this.getPoints().size();i++) {
				newPoints.addPoint(RotationUtil.rotate(this.getPoints().getPoint(i), trueAngle, rotationPoint));
			}
			
			Rectangle newPointBounds = newPoints.getBounds();
			if (!rotationPoint.equals(newPointBounds.getCenter())) {
				Dimension difference = rotationPoint.getCopy().getDifference(newPointBounds.getCenter());
				newPoints.translate(difference.width, difference.height);
			}
			
			// sets the translated Points
			setPoints(newPoints);	
		}
		
		super.setPropertyValue(PROP_ROTATION, angle);
		_isRotationInitialized = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final synchronized void setPropertyValue(final String propertyID,
			final Object value) {
		if (propertyID.equals(AbstractPolyModel.PROP_POINTS)) {
			if (value instanceof PointList) {
				this.setPoints((PointList) value);
			}
		} else if (propertyID.equals(AbstractWidgetModel.PROP_POS_X)
				&& ((Integer) value != getPoints().getBounds().x)) {
			setLocation((Integer) value, getY());
		} else if (propertyID.equals(AbstractWidgetModel.PROP_POS_Y)
				&& ((Integer) value != getPoints().getBounds().y)) {
			setLocation(getX(), (Integer) value);
		} else if (propertyID.equals(AbstractWidgetModel.PROP_WIDTH)
				&& ((Integer) value != getPoints().getBounds().width)) {
			setSize((Integer) value, getHeight());
		} else if (propertyID.equals(AbstractWidgetModel.PROP_HEIGHT)
				&& ((Integer) value != getPoints().getBounds().height)) {
			setSize(getWidth(), (Integer) value);
		} else if (propertyID.equals(AbstractPolyModel.PROP_ROTATION)
				&& ((Double) value != getRotationAngle())) {
			rotatePoints((Double) value);
		} else {
			super.setPropertyValue(propertyID, value);
		}
	}
}
