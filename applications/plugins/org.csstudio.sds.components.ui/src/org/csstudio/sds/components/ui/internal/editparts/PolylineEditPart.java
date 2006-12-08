package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.PolylineElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolylineFigure;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PointList;

/**
 * EditPart controller for <code>PolylineElement</code> elements.
 * 
 * @author Sven Wende
 * 
 */
public final class PolylineEditPart extends AbstractSDSEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		RefreshablePolylineFigure polyline = new RefreshablePolylineFigure();

		for (String key : getCastedModel().getPropertyNames()) {
			polyline.refresh(key, getCastedModel().getProperty(key)
					.getPropertyValue());
		}

		return polyline;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		RefreshablePolylineFigure polyline = (RefreshablePolylineFigure) getFigure();

		if (propertyName.equals(PolylineElement.PROP_POINTS)) {
			assert newValue instanceof PointList : "newValue instanceof PointList"; //$NON-NLS-1$
			PointList points = (PointList) newValue;
			polyline.setPoints(points);
		} else if (propertyName.equals(PolylineElement.PROP_FILL_GRADE)) {
			polyline.setFill((Double) newValue);
			polyline.repaint();
		}
	}
}
