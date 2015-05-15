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

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.PropertyChangeAdapter;
import org.csstudio.sds.model.WidgetPropertyCategory;
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
     * The original Points without rotation.
     */
    private PointList _originalPoints;

    /**
     * Constructor.
     */
    public AbstractPolyModel() {
        super(true);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        addPropertyChangeListener(PROP_ROTATION, new PropertyChangeAdapter() {
            @Override
            public void propertyValueChanged(final Object oldValue, final Object newValue) {
                setPoints(rotatePoints(_originalPoints.getCopy(), (Double) newValue), false);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureProperties() {
        addPointlistProperty(PROP_POINTS, "Points", WidgetPropertyCategory.POSITION, new PointList(), false);
        addDoubleProperty(PROP_FILL, "Value", WidgetPropertyCategory.DISPLAY, 100.0, 0.0, 100.0, true, PROP_TOOLTIP);
    }

    /**
     * Sets the specified _points for the polygon.
     *
     * @param points
     *            the polygon points
     * @param rememberPoints
     *            true if the zero relative points should be remembered, false
     *            otherwise.
     */
    public final void setPoints(final PointList points, final boolean rememberPoints) {
        if (points.size() > 0) {
            final PointList copy = points.getCopy();
            if (rememberPoints) {
                this.rememberZeroDegreePoints(copy);
            }
            super.setPropertyValue(PROP_POINTS, copy);
            final Rectangle bounds = copy.getBounds();
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
        return getPointlistProperty(PROP_POINTS);
    }

    /**
     * Returns the fill grade.
     *
     * @return the fill grade
     */
    public final double getFill() {
        return getDoubleProperty(PROP_FILL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setSize(final int width, final int height) {
        final int targetW = Math.max(1, width);
        final int targetH = Math.max(1, height);
        final PointList pointList = getPoints();
        final double oldW = pointList.getBounds().width;
        final double oldH = pointList.getBounds().height;
        final double topLeftX = pointList.getBounds().x;
        final double topLeftY = pointList.getBounds().y;

        if (oldW != targetW || oldH != targetH) {
            final PointList newPoints = new PointList();
            for (int i = 0; i < pointList.size(); i++) {
                final int x = pointList.getPoint(i).x;
                final int y = pointList.getPoint(i).y;

                Point newPoint = new Point(x, y);
                if (oldW > 0 && oldH > 0) {
                    final double oldRelX = (x - topLeftX) / oldW;
                    final double oldRelY = (y - topLeftY) / oldH;

                    final double newX = topLeftX + oldRelX * targetW;
                    final double newY = topLeftY + oldRelY * targetH;
                    final long roundedX = Math.round(newX);
                    final long roundedY = Math.round(newY);
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
    public final void setLocation(final int x, final int y) {
        final PointList points = getPoints();
        final int oldX = points.getBounds().x;
        final int oldY = points.getBounds().y;
        points.translate(x - oldX, y - oldY);

        setPoints(points, true);
        final int newX = points.getBounds().x;
        final int newY = points.getBounds().y;
        super.setLocation(newX, newY);
    }

    /**
     * Rotates all points.
     *
     * @param points
     *            The PoinList, which points should be rotated
     * @param angle
     *            The angle to rotate
     * @return The rotated PointList
     */
    public final PointList rotatePoints(final PointList points, final double angle) {
        final Rectangle pointBounds = points.getBounds();
        final Point rotationPoint = pointBounds.getCenter();
        final PointList newPoints = new PointList();

        for (int i = 0; i < points.size(); i++) {
            newPoints.addPoint(RotationUtil.rotate(points.getPoint(i), angle, rotationPoint));
        }

        final Rectangle newPointBounds = newPoints.getBounds();
        if (!rotationPoint.equals(newPointBounds.getCenter())) {
            final Dimension difference = rotationPoint.getCopy().getDifference(newPointBounds.getCenter());
            newPoints.translate(difference.width, difference.height);
        }

        return newPoints;
    }

    /**
     * Rotates the given points to 0 degrees and sets them as
     * <code>_originalPoints</code>.
     *
     * @param points
     *            The current {@link PointList}
     */
    private void rememberZeroDegreePoints(final PointList points) {
        if (this.getRotationAngle() == 0) {
            _originalPoints = points.getCopy();
        } else {
            _originalPoints = this.rotatePoints(points, -this.getRotationAngle());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setPropertyValue(final String propertyID, final Object value) {
        if (propertyID.equals(AbstractPolyModel.PROP_POINTS)) {
            if (value instanceof PointList) {
                this.setPoints((PointList) value, true);
            }
        } else if (propertyID.equals(AbstractWidgetModel.PROP_POS_X) && (Integer) value != getPoints().getBounds().x) {
            setLocation((Integer) value, getY());
        } else if (propertyID.equals(AbstractWidgetModel.PROP_POS_Y) && (Integer) value != getPoints().getBounds().y) {
            setLocation(getX(), (Integer) value);
        } else if (propertyID.equals(AbstractWidgetModel.PROP_WIDTH) && (Integer) value != getPoints().getBounds().width) {
            setSize((Integer) value, getHeight());
        } else if (propertyID.equals(AbstractWidgetModel.PROP_HEIGHT) && (Integer) value != getPoints().getBounds().height) {
            setSize(getWidth(), (Integer) value);
        } else {
            super.setPropertyValue(propertyID, value);
        }
    }
}
