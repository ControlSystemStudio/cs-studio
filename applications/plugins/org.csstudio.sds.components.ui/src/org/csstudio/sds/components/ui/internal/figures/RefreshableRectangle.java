package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.dataconnection.StatisticUtil;
import org.csstudio.sds.ui.editparts.IRefreshableFigure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * A rectangle figure.
 * 
 * @author Sven Wende
 * 
 */
public final class RefreshableRectangle extends RectangleFigure implements
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
	@Override
	public void repaint() {
		super.repaint();
		StatisticUtil.getInstance().recordWidgetRefresh(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillShape(final Graphics graphics) {
		Rectangle bounds = getBounds();

		int newW = (int) Math.round(bounds.width * (getFill() / 100));

		graphics.setBackgroundColor(getBackgroundColor());
		graphics.fillRectangle(getBounds());
		graphics.setBackgroundColor(getForegroundColor());
		graphics.fillRectangle(new Rectangle(bounds.getLocation(),
				new Dimension(newW, bounds.height)));
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
