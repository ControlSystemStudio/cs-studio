package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.components.internal.model.PolygonElement;
import org.csstudio.sds.dataconnection.StatisticUtil;
import org.csstudio.sds.ui.editparts.IRefreshableFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;

/**
 * A polygon figure.
 * 
 * @author Sven Wende
 * 
 */
public final class RefreshablePolygonFigure extends Polygon implements
		IRefreshableFigure, HandleBounds {
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
	public void refresh(final String propertyName, final Object propertyValue) {
		StatisticUtil.getInstance().recordWidgetRefresh(this);

		if (propertyName.equals(PolygonElement.PROP_POINTS)) {
			PointList points = (PointList) propertyValue;
			setPoints(points);
		}
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

}
