package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.PolylineElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolylineFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.RGB;

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
	protected IFigure createFigure() {
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
		} else if (propertyName.equals(PolylineElement.PROP_BACKGROUND_COLOR)) {
			polyline.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
			polyline.repaint();
		} else if (propertyName.equals(PolylineElement.PROP_FOREGROUND_COLOR)) {
			polyline.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
			polyline.repaint();
		}
	}
}
