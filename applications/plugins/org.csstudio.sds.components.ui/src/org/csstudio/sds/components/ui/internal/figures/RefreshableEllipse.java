package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.internal.model.EllipseElement;
import org.csstudio.sds.dataconnection.StatisticUtil;
import org.csstudio.sds.ui.editparts.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * An ellipse figure.
 * 
 * @author Sven Wende, Alexander Will
 * 
 */
public final class RefreshableEllipse extends Ellipse implements
		IRefreshableFigure {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;

	/**
	 * The background color.
	 */
	private Color _backgroundColor;

	/**
	 * The foreground color.
	 */
	private Color _foregroundColor;

	/**
	 * {@inheritDoc}
	 */
	public void refresh(final String propertyName, final Object propertyValue) {
		StatisticUtil.getInstance().recordWidgetRefresh(this);

		if (propertyName.equals(EllipseElement.PROP_FILL_PERCENTAGE)) {
			Double fillGrade = (Double) propertyValue;
			setFill(fillGrade);
		} else if (propertyName.equals(EllipseElement.PROP_BACKGROUND_COLOR)) {
			setBackgroundColor(CustomMediaFactory.getInstance().getColor(
					(RGB) propertyValue));
		} else if (propertyName.equals(EllipseElement.PROP_FOREGROUND_COLOR)) {
			setForegroundColor(CustomMediaFactory.getInstance().getColor(
					(RGB) propertyValue));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillShape(final Graphics graphics) {
		Rectangle bounds = getBounds();

		int newW = (int) Math.round(bounds.width * (getFill() / 100));

		graphics
				.setClip(new Rectangle(bounds.x, bounds.y, newW, bounds.height));
		graphics.setBackgroundColor(getBackgroundColor());
		graphics.fillOval(bounds);
		graphics.setClip(new Rectangle(bounds.x + newW, bounds.y, bounds.width
				- newW, bounds.height));
		graphics.setBackgroundColor(getForegroundColor());
		graphics.fillOval(bounds);
	}

	/**
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
		setFill(Math.random() * 100);
		repaint();
	}

	/**
	 * Sets the fill grade.
	 * 
	 * @param fill
	 *            the fill grade.
	 */
	public void setFill(final double fill) {
		_fill = fill;
	}

	/**
	 * Gets the fill grade.
	 * 
	 * @return the fill grade
	 */
	public double getFill() {
		return _fill;
	}

	/**
	 * Gets the background color.
	 * 
	 * @return the background color.
	 */
	public Color getBackgroundColor() {
		return _backgroundColor;
	}

	/**
	 * Sets the background color.
	 * 
	 * @param backgroundColor
	 *            the background color.
	 */
	public void setBackgroundColor(final Color backgroundColor) {
		_backgroundColor = backgroundColor;
	}

	/**
	 * Gets the foreground color.
	 * 
	 * @return the foreground color.
	 */
	public Color getForegroundColor() {
		return _foregroundColor;
	}

	/**
	 * Sets the foreground color.
	 * 
	 * @param foregroundColor
	 *            the foreground color.
	 */
	public void setForegroundColor(final Color foregroundColor) {
		_foregroundColor = foregroundColor;
	}
}
