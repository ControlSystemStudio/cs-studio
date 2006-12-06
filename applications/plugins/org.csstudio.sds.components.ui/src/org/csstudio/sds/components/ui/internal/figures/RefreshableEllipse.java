package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.dataconnection.StatisticUtil;
import org.csstudio.sds.ui.editparts.IRefreshableFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * An ellipse figure.
 * 
 * @author Sven Wende
 * 
 */
public final class RefreshableEllipse extends Ellipse implements
		IRefreshableFigure {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;

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
		graphics.setBackgroundColor(ColorConstants.black);
		graphics.fillOval(getBounds());
		graphics.setBackgroundColor(ColorConstants.blue);
		graphics.fillOval(new Rectangle(bounds.getLocation(), new Dimension(
				newW, bounds.height)));
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
}
