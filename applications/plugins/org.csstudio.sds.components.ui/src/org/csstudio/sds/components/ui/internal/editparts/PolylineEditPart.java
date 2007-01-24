package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolylineFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.eclipse.draw2d.geometry.PointList;

/**
 * EditPart controller for <code>PolylineElement</code> elements.
 * 
 * @author Sven Wende, Alexander Will
 * 
 */
public final class PolylineEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		RefreshablePolylineFigure polyline = new RefreshablePolylineFigure();
		AbstractElementModel elementModel = getCastedModel();

		for (String key : getCastedModel().getPropertyNames()) {
			polyline.refresh(key, elementModel.getProperty(key)
					.getPropertyValue());
		}

		return polyline;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure figure) {
		RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) figure;

		if (propertyName.equals(AbstractPolyElement.PROP_POINTS)) {
			assert newValue instanceof PointList : "newValue instanceof PointList"; //$NON-NLS-1$
			PointList points = (PointList) newValue;
			polyline.setPoints(points);
			return false; // Performance Optimization (setPoints() already
			// refreshes the figure)
		} else if (propertyName.equals(AbstractPolyElement.PROP_FILL)) {
			polyline.setFill((Double) newValue);
			return true;
		}
		
		return false;
	}
}
