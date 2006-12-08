package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.internal.model.PolylineElement;
import org.csstudio.sds.dataconnection.StatisticUtil;
import org.csstudio.sds.ui.editparts.IRefreshableFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

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
	 * Constructor.
	 */
	public RefreshablePolylineFigure() {
		setFill(true);
		setBackgroundColor(ColorConstants.darkGreen);
		setLineWidth(4);
	}

	/**
	 * {@inheritDoc}
	 */
	public void refresh(final String propertyName, final Object propertyValue) {
		StatisticUtil.getInstance().recordWidgetRefresh(this);

		if (propertyName.equals(PolylineElement.PROP_POINTS)) {
			PointList points = (PointList) propertyValue;
			setPoints(points);
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void outlineShape(final Graphics graphics) {
		Rectangle bounds = getBounds();

		int newW = (int) Math.round(bounds.width * (getFill() / 100));

		graphics.setClip(new Rectangle(bounds.x, bounds.y, newW, bounds.height));
		graphics.setForegroundColor(ColorConstants.black);
		graphics.drawPolyline(getPoints());
		graphics.setClip(new Rectangle(bounds.x+newW, bounds.y, bounds.width-newW, bounds.height));
		graphics.setForegroundColor(ColorConstants.blue);
		graphics.drawPolyline(getPoints());
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

}
