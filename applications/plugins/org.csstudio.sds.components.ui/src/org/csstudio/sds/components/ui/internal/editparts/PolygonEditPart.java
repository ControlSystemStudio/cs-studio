package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.AbstractPolyElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshablePolygonFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.RGB;

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
	protected IFigure createFigure() {
		RefreshablePolygonFigure polygon = new RefreshablePolygonFigure();
		AbstractElementModel elementModel = getCastedModel();

		for (String key : elementModel.getPropertyNames()) {
			setFigureProperties(key, elementModel.getProperty(key)
					.getPropertyValue(), polygon);
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
		setFigureProperties(propertyName, newValue, polygon);
		
		// performance optimization: super.setPoints() already performs repaint
		if (!propertyName.equals(AbstractPolyElement.PROP_POINTS)) {
			polygon.repaint();
		}
	}

	/**
	 * Sets a property of a figure. Does not cause the figure to be (re-)painted!
	 * @param propertyName Required.
	 * @param newValue May be null (depends on model implementation).
	 * @param polygon Required.
	 */
	private void setFigureProperties(final String propertyName, final Object newValue, final RefreshablePolygonFigure polygon) {
		assert propertyName != null : "Precondition violated: propertyName != null"; //$NON-NLS-1$
		assert polygon != null : "Precondition violated: polygon != null"; //$NON-NLS-1$
		
		if (propertyName.equals(AbstractPolyElement.PROP_POINTS)) {
			assert newValue instanceof PointList : "newValue instanceof PointList"; //$NON-NLS-1$
			PointList points = (PointList) newValue;
			polygon.setPoints(points);
		} else if (propertyName.equals(AbstractPolyElement.PROP_FILL_GRADE)) {
			polygon.setFill((Double) newValue);
		} else if (propertyName.equals(AbstractElementModel.PROP_BACKGROUND_COLOR)) {
			polygon.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		} else if (propertyName.equals(AbstractElementModel.PROP_FOREGROUND_COLOR)) {
			polygon.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		}
	}
}
