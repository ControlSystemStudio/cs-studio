package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * An ellipse figure.
 * 
 * @author Sven Wende, Alexander Will
 * 
 */
public final class RefreshableEllipseFigure extends Ellipse implements
		IRefreshableFigure {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;
	
	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fillShape(final Graphics graphics) {
		Rectangle figureBounds = getBounds();

		int newW = (int) Math.round(figureBounds.width * (getFill() / 100));

		graphics
				.setClip(new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height));
		graphics.setBackgroundColor(getForegroundColor());
		graphics.fillOval(figureBounds);
		graphics.setClip(new Rectangle(figureBounds.x + newW, figureBounds.y, figureBounds.width
				- newW, figureBounds.height));
		graphics.setBackgroundColor(getBackgroundColor());
		graphics.fillOval(figureBounds);
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
