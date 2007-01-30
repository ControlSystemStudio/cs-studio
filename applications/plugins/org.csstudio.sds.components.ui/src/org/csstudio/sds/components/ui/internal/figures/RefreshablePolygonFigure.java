package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 * A polygon figure.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class RefreshablePolygonFigure extends Polygon implements
		IRefreshableFigure, HandleBounds {

	/**
	 * The fill grade (0 - 100%).
	 */
	private double _fill = 100.0;

	/**
	 * A border adapter, which covers all border handlings.
	 */
	private IBorderEquippedWidget _borderAdapter;
	
	/**
	 * Constructor.
	 */
	public RefreshablePolygonFigure() {
		setFill(true);
		setBackgroundColor(ColorConstants.darkGreen);

	}
	
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
		graphics.fillPolygon(getPoints());
		graphics.setClip(new Rectangle(figureBounds.x + newW, figureBounds.y, figureBounds.width
				- newW, figureBounds.height));
		graphics.setBackgroundColor(getBackgroundColor());
		graphics.fillPolygon(getPoints());
	}

	/**
	 * Overridden, to ensure that the bounds rectangle gets repainted each time,
	 * the _points of the polygon change. {@inheritDoc}
	 */
	@Override
	public void setBounds(final Rectangle rect) {
		invalidate();
		fireFigureMoved();
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	public void randomNoiseRefresh() {
		setFill(Math.random() * 100);
		repaint();
	}

	/**
	 * {@inheritDoc}
	 */
	public Rectangle getHandleBounds() {
		return getPoints().getBounds();
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
