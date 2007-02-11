package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.graphics.RGB;

/**
 * A line figure.
 * 
 * @author Sven Wende
 * 
 */
public final class RefreshablePolylineFigure extends Polyline implements
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
	public RefreshablePolylineFigure() {
		setFill(true);
		setBackgroundColor(ColorConstants.darkGreen);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(final Graphics graphics) {
		Rectangle figureBounds = getBounds();

		int newW = (int) Math.round(figureBounds.width * (getFill() / 100));
		
		graphics.setClip(new Rectangle(figureBounds.x, figureBounds.y, newW, figureBounds.height));
		graphics.setForegroundColor(getBackgroundColor());
		graphics.drawPolyline(getPoints());
		graphics.setClip(new Rectangle(figureBounds.x+newW, figureBounds.y, figureBounds.width-newW, figureBounds.height));
		graphics.setForegroundColor(getForegroundColor());
		graphics.drawPolyline(getPoints());
	}

	/**
	 * Overridden, to ensure that the bounds rectangle gets repainted each time,
	 * the points of the polygon change. {@inheritDoc}
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
		// TODO: swende: make some noise
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
