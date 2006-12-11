package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.PolygonElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolygonFigure;
import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for <code>PolygonElement</code> elements.
 * 
 * @author Sven Wende
 * 
 */
public final class PolygonEditPart extends AbstractSDSEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		RefreshablePolygonFigure polygon = new RefreshablePolygonFigure();
		DisplayModelElement modelElement = getCastedModel();

		for (String key : modelElement.getPropertyNames()) {
			polygon.refresh(key, modelElement.getProperty(key)
					.getPropertyValue());
		}

		return polygon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		RefreshablePolygonFigure polygon = (RefreshablePolygonFigure) getFigure();

		if (propertyName.equals(PolygonElement.PROP_POINTS)) {
			assert newValue instanceof PointList : "newValue instanceof PointList"; //$NON-NLS-1$
			PointList points = (PointList) newValue;
			polygon.setPoints(points);
		} else if (propertyName.equals(PolygonElement.PROP_FILL_GRADE)) {
			polygon.setFill((Double) newValue);
			polygon.repaint();
		} else if (propertyName.equals(PolygonElement.PROP_BACKGROUND_COLOR)) {
			polygon.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
			polygon.repaint();
		} else if (propertyName.equals(PolygonElement.PROP_FOREGROUND_COLOR)) {
			polygon.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
			polygon.repaint();
		}
	}
}
