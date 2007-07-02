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
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.AntialiasingUtil;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A figure for meters.
 * 
 * @author Sven Wende
 * 
 */
public final class RefreshableMeterFigure extends ImageFigure implements
		IAdaptable {

	/** The default border size. */
	private static final float DEFAULT_BORDER_SIZE = 3f;

	/** The default circle size. */
	private static final float DEFAULT_CIRCLE_SIZE = 10f;

	/** The lower bound of the overall range of data values on the dial. */
	private double _rangeLowerBound = 0.0;

	/** The upper bound of the overall range of data values on the dial. */
	private double _rangeUpperBound = 80.0;

	/** The dial extent (measured in degrees). */
	private int _meterAngle = 270;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * The current value.
	 */
	private double _value = 10.0;

	/**
	 * Lower border of intervall 1.
	 */
	private double _interval1LowerBorder;

	/**
	 * Upper border of intervall 1.
	 */
	private double _interval1UpperBorder;

	/**
	 * Lower border of intervall 2.
	 */
	private double _interval2LowerBorder;

	/**
	 * Upper border of intervall 2.
	 */
	private double _interval2UpperBorder;

	/**
	 * Lower border of intervall 3.
	 */
	private double _interval3LowerBorder;

	/**
	 * Upper border of intervall 3.
	 */
	private double _interval3UpperBorder;

	/**
	 * Constructor.
	 */
	public RefreshableMeterFigure() {
		// refreshChartImage();
		addFigureListener(new FigureListener() {
			public void figureMoved(final IFigure arg0) {
				// refreshChartImage();
			}
		});
	}

	/**
	 * This method is a tribute to unit tests, which need a way to test the
	 * performance of the figure implementation. Implementors should produce
	 * some random changes and refresh the figure, when this method is called.
	 * 
	 */
	public void randomNoiseRefresh() {
		setValue(Math.random() * 360);
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintFigure(final Graphics g2) {
		AntialiasingUtil.getInstance().enableAntialiasing(g2);
		g2.setBackgroundColor(getBackgroundColor());
		g2.fillOval(getBounds().getCropped(new Insets(2)));
		AntialiasingUtil.getInstance().disableAntialiasing(g2);
		drawAngle(g2);
	}

	/**
	 * Draws the angle of the meter.
	 * 
	 * @param graphics
	 *            the graphics
	 */
	private void drawAngle(final Graphics graphics) {
		Rectangle area = getBounds();
		graphics.setBackgroundColor(ColorConstants.black);
		double meterMiddleX = area.getCenter().x;
		double meterMiddleY = area.getCenter().y;

		double radius = (area.width / 2) + DEFAULT_BORDER_SIZE + 15;
		double valueAngle = valueToAngle(getValue());
		double valueP1 = meterMiddleX
				+ (radius * Math.cos(Math.PI * (valueAngle / 180)));
		double valueP2 = meterMiddleY
				- (radius * Math.sin(Math.PI * (valueAngle / 180)));

		PointList arrow = new PointList();
		if ((valueAngle > 135 && valueAngle < 225)
				|| (valueAngle < 45 && valueAngle > -45)) {

			double valueP3 = (meterMiddleY - DEFAULT_CIRCLE_SIZE / 4);
			double valueP4 = (meterMiddleY + DEFAULT_CIRCLE_SIZE / 4);
			arrow.addPoint((int) meterMiddleX, (int) valueP3);
			arrow.addPoint((int) meterMiddleX, (int) valueP4);

		} else {
			arrow.addPoint((int) (meterMiddleX - DEFAULT_CIRCLE_SIZE / 4),
					(int) meterMiddleY);
			arrow.addPoint((int) (meterMiddleX + DEFAULT_CIRCLE_SIZE / 4),
					(int) meterMiddleY);
		}
		arrow.addPoint((int) valueP1, (int) valueP2);

		graphics.fillPolygon(arrow);

	}

	/**
	 * Translates a data value to an angle on the dial.
	 * 
	 * @param value
	 *            the value.
	 * 
	 * @return The angle on the dial.
	 */
	public double valueToAngle(final double value) {
		double v = value - _rangeLowerBound;
		double baseAngle = 180 + ((_meterAngle - 180) / 2);
		return baseAngle - ((v / _rangeUpperBound) * _meterAngle);
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value
	 */
	public void setValue(final double value) {
		_value = value;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public double getValue() {
		return _value;
	}

	/**
	 * Sets the lower border for interval 1.
	 * 
	 * @param interval1LowerBorder
	 *            the lower border for interval 1
	 */
	public void setInterval1LowerBorder(final double interval1LowerBorder) {
		_interval1LowerBorder = interval1LowerBorder;
	}

	/**
	 * Gets the lower border for interval 1.
	 * 
	 * @return the lower border for interval 1
	 */
	public double getInterval1LowerBorder() {
		return _interval1LowerBorder;
	}

	/**
	 * Sets the upper border for interval 1.
	 * 
	 * @param interval1UpperBorder
	 *            the lower border for interval 1
	 */
	public void setInterval1UpperBorder(final double interval1UpperBorder) {
		_interval1UpperBorder = interval1UpperBorder;
	}

	/**
	 * Gets the upper border for interval 1.
	 * 
	 * @return the lower border for interval 1
	 */
	public double getInterval1UpperBorder() {
		return _interval1UpperBorder;
	}

	/**
	 * Sets the lower border for interval 2.
	 * 
	 * @param interval2LowerBorder
	 *            the lower border for interval 2
	 */
	public void setInterval2LowerBorder(final double interval2LowerBorder) {
		_interval2LowerBorder = interval2LowerBorder;
	}

	/**
	 * Gets the lower border for interval 2.
	 * 
	 * @return the lower border for interval 2
	 */
	public double getInterval2LowerBorder() {
		return _interval2LowerBorder;
	}

	/**
	 * Sets the upper border for interval 2.
	 * 
	 * @param interval2UpperBorder
	 *            the lower border for interval 2
	 */
	public void setInterval2UpperBorder(final double interval2UpperBorder) {
		_interval2UpperBorder = interval2UpperBorder;
	}

	/**
	 * Gets the upper border for interval 2.
	 * 
	 * @return the lower border for interval 2
	 */
	public double getInterval2UpperBorder() {
		return _interval2UpperBorder;
	}

	/**
	 * Sets the lower border for interval 3.
	 * 
	 * @param interval3LowerBorder
	 *            the lower border for interval 3
	 */
	public void setInterval3LowerBorder(final double interval3LowerBorder) {
		_interval3LowerBorder = interval3LowerBorder;
	}

	/**
	 * Gets the lower border for interval 3.
	 * 
	 * @return the lower border for interval 3
	 */
	public double getInterval3LowerBorder() {
		return _interval3LowerBorder;
	}

	/**
	 * Sets the upper border for interval 3.
	 * 
	 * @param interval3UpperBorder
	 *            the lower border for interval 3
	 */
	public void setInterval3UpperBorder(final double interval3UpperBorder) {
		_interval3UpperBorder = interval3UpperBorder;
	}

	/**
	 * Gets the upper border for interval 3.
	 * 
	 * @return the lower border for interval 3
	 */
	public double getInterval3UpperBorder() {
		return _interval3UpperBorder;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Class adapter) {
		if (adapter == IBorderEquippedWidget.class) {
			if(_borderAdapter==null) {
				_borderAdapter = new BorderAdapter(this);
			}
			return _borderAdapter;
		}
		return null;
	}

}
