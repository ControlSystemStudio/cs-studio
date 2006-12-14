package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangle;
import org.csstudio.sds.dataconnection.StatisticUtil;
import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for <code>RectangleElement</code> elements.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class RectangleEditPart extends AbstractSDSEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RefreshableRectangle createFigure() {
		RefreshableRectangle rectangle = new RefreshableRectangle();
		DisplayModelElement modelElement = getCastedModel();

		for (String key : modelElement.getPropertyNames()) {
			setFigureProperties(key, modelElement.getProperty(key)
					.getPropertyValue(), rectangle);
		}
		return rectangle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		RefreshableRectangle rectangle = (RefreshableRectangle) getFigure();
		setFigureProperties(propertyName, newValue, rectangle);
		rectangle.repaint();
	}

	/**
	 * Sets a property of a figure.
	 * @param propertyName The property to set.
	 * @param newValue The value to set.
	 * @param rectangle The figure that is configured.
	 */
	private void setFigureProperties(final String propertyName, final Object newValue, final RefreshableRectangle rectangle) {
		if (propertyName.equals(RectangleElement.PROP_FILL_PERCENTAGE)) {
			rectangle.setFill((Double) newValue);
		} else if (propertyName.equals(RectangleElement.PROP_BACKGROUND_COLOR)) {
			rectangle.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		} else if (propertyName.equals(RectangleElement.PROP_FOREGROUND_COLOR)) {
			rectangle.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		}
	}

}
