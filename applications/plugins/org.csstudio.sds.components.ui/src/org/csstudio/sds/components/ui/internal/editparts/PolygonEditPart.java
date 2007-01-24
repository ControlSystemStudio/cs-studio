package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolygonFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.geometry.PointList;

/**
 * EditPart controller for <code>PolygonElement</code> elements.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class PolygonEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		return new RefreshablePolygonFigure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure figure) {
		RefreshablePolygonFigure polygon = (RefreshablePolygonFigure) figure;

		if (propertyName.equals(AbstractPolyElement.PROP_POINTS)) {
			assert newValue instanceof PointList : "newValue instanceof PointList"; //$NON-NLS-1$
			PointList points = (PointList) newValue;
			polygon.setPoints(points);
			return false; // Performance Optimization (setPoints() already
			// refreshes the figure)
		} else if (propertyName.equals(AbstractPolyElement.PROP_FILL)) {
			polygon.setFill((Double) newValue);
			return true;
		}
		return false;
	}
}
