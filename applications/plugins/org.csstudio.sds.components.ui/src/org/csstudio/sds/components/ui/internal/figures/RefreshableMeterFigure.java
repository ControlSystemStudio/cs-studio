package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.dataconnection.StatisticUtil;
import org.csstudio.sds.ui.editparts.IRefreshableFigure;
import org.csstudio.sds.uil.AntialiasingUtil;
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
		IRefreshableFigure {

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
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
		setValue(Math.random() * 360);
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(final Graphics arg0) {
		super.paint(arg0);
		StatisticUtil.getInstance().recordWidgetRefresh(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintFigure(final Graphics g2) {
		AntialiasingUtil.getInstance().enableAntialiasing(g2);
		g2.setBackgroundColor(ColorConstants.gray);
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

}
