package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.model.PolygonElement;
import org.csstudio.sds.components.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.BorderAdapter;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolygonFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolylineFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangleFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
import org.csstudio.sds.ui.figures.IRefreshableFigure;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
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
		RefreshablePolygonFigure polygon = new RefreshablePolygonFigure();
		PolygonElement elementModel = (PolygonElement) getCastedModel();
		polygon.setPoints(elementModel.getPoints());
		polygon.setFill(elementModel.getFill());
		
		return polygon;
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
				RefreshablePolygonFigure polygon = (RefreshablePolygonFigure) figure;
				polygon.setFill((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyElement.PROP_FILL, fillHandler);
		
		// points
		IElementPropertyChangeHandler pointsHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				RefreshablePolygonFigure polygon = (RefreshablePolygonFigure) figure;
				PointList points = (PointList) newValue;
				polygon.setPoints(points);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractPolyElement.PROP_POINTS, pointsHandler);
	}
}
