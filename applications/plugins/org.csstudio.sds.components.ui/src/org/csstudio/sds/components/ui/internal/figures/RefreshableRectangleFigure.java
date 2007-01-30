package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * A rectangle figure.
 * 
 * @author Sven Wende
 * 
 */
public final class RefreshableRectangleFigure extends RectangleFigure implements
		IRefreshableFigure {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fillGrade = 100;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void fillShape(final Graphics graphics) {
		Rectangle figureBounds = getBounds();

		int newW = (int) Math.round(figureBounds.width * (getFill() / 100));

		graphics.setBackgroundColor(getBackgroundColor());
		graphics.fillRectangle(getBounds());
		graphics.setBackgroundColor(getForegroundColor());
		graphics.fillRectangle(new Rectangle(figureBounds.getLocation(),
				new Dimension(newW, figureBounds.height)));
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
		_fillGrade = fill;
	}

	/**
	 * Gets the fill grade.
	 * 
	 * @return the fill grade
	 */
	public double getFill() {
		return _fillGrade;
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
