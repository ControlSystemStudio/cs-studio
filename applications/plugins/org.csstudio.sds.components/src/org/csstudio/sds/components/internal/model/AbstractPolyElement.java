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
package org.csstudio.sds.components.internal.model;

import org.csstudio.sds.components.internal.localization.Messages;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.model.properties.PropertyTypeRegistry;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * @author Sven Wende
 */
public abstract class AbstractPolyElement extends AbstractElementModel {

	/**
	 * The ID of the points property.
	 */
	public static final String PROP_POINTS = "poly.points"; //$NON-NLS-1$

	/**
	 * The ID of the fill grade property.
	 */
	public static final String PROP_FILL_GRADE = "line.fillgrade"; //$NON-NLS-1$

	/**
	 * The ID of the background color property.
	 */
	public static final String PROP_BACKGROUND_COLOR = "color.background"; //$NON-NLS-1$

	/**
	 * The ID of the foreground color property.
	 */
	public static final String PROP_FOREGROUND_COLOR = "color.foreground"; //$NON-NLS-1$		

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 10;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 20;

	/**
	 * Constructor.
	 */
	public AbstractPolyElement() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void configureProperties() {
		addProperty(PROP_POINTS, Messages.PolyElement_POINTS,
				PropertyTypeRegistry.POINT_LIST, new PointList());
		addProperty(PROP_FILL_GRADE, Messages.PolyElement_FILL_GRADE,
				PropertyTypeRegistry.DOUBLE, 100.0);
		addProperty(PROP_BACKGROUND_COLOR,
				Messages.PolyElement_BACKGROUND_COLOR,
				PropertyTypeRegistry.COLOR, new RGB(100, 100, 100));
		addProperty(PROP_FOREGROUND_COLOR,
				Messages.PolyElement_FOREGROUND_COLOR,
				PropertyTypeRegistry.COLOR, new RGB(200, 100, 100));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getDoubleTestProperty() {
		return PROP_FILL_GRADE;
	}

	/**
	 * Sets the specified _points for the polygon.
	 * 
	 * @param points
	 *            the polygon points
	 */
	public final void setPoints(final PointList points) {
		PointList copy = points.getCopy();
		setPropertyValue(PROP_POINTS, copy);
		Rectangle bounds = copy.getBounds();
		setPropertyValue(PROP_X, bounds.x);
		setPropertyValue(PROP_Y, bounds.y);
		setPropertyValue(PROP_W, bounds.width);
		setPropertyValue(PROP_H, bounds.height);
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
	 * {@inheritDoc}
	 */
	@Override
	public final void setSize(final int width, final int height) {
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

	@Override
	public synchronized void setPropertyValue(String propertyID, Object value) {
		if (propertyID.equals(AbstractElementModel.PROP_X)
				&& ((Integer) value != getPoints().getBounds().x)) {
			setLocation((Integer) value, getY());
		} else if (propertyID.equals(AbstractElementModel.PROP_Y)
				&& ((Integer) value != getPoints().getBounds().y)) {
			setLocation(getX(), (Integer) value);
		} else if (propertyID.equals(AbstractElementModel.PROP_W)
				&& ((Integer) value != getPoints().getBounds().width)) {
			setSize((Integer) value, getHeight());
		} else if (propertyID.equals(AbstractElementModel.PROP_H)
				&& ((Integer) value != getPoints().getBounds().height)) {
			setSize(getWidth(),(Integer) value);
		} else {
			super.setPropertyValue(propertyID, value);
		}
	}
}
