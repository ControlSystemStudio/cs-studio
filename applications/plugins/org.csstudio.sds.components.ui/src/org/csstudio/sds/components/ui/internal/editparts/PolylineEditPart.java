package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.model.PolylineElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolylineFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
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
		PolylineElement elementModel = (PolylineElement) getCastedModel();

		polyline.setPoints(elementModel.getPoints());
		polyline.setFill(elementModel.getFill());

		return polyline;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure f) {
		RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) f;

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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// fill
		IElementPropertyChangeHandler fillHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) figure;
				polyline.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyElement.PROP_FILL, fillHandler);
		
		// points
		IElementPropertyChangeHandler pointsHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) figure;
				PointList points = (PointList) newValue;
				polyline.setPoints(points);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyElement.PROP_POINTS, pointsHandler);
	}
}
