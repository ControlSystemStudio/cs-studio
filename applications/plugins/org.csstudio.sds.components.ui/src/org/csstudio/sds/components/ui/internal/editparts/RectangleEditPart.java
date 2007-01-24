package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangleFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for <code>RectangleElement</code> elements.
 * 
 * @author Sven Wende & Stefan Hofer
 * 
 */
public final class RectangleEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RefreshableRectangleFigure createFigure() {
		RefreshableRectangleFigure rectangle = new RefreshableRectangleFigure();
		AbstractElementModel elementModel = getCastedModel();

		for (String key : elementModel.getPropertyNames()) {
			setFigureProperties(key, elementModel.getProperty(key)
					.getPropertyValue(), rectangle);
		}
		return rectangle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void doRefreshFigure(final String propertyName,
			final Object newValue) {
		RefreshableRectangleFigure rectangle = (RefreshableRectangleFigure) getFigure();
		setFigureProperties(propertyName, newValue, rectangle);
		rectangle.revalidate();
		rectangle.repaint();
	}

	/**
	 * Sets a property of a figure. Does not perform (re-)paint! 
	 * @param propertyName The property to set. Required.
	 * @param newValue The value to set.
	 * @param rectangle The figure that is configured. Required.
	 */
	private void setFigureProperties(final String propertyName, final Object newValue, final RefreshableRectangleFigure rectangle) {
		assert propertyName != null : "Precondition violated: propertyName != null"; //$NON-NLS-1$
		assert rectangle != null : "Precondition violated: rectangle != null"; //$NON-NLS-1$
		
		if (propertyName.equals(RectangleElement.PROP_FILL_PERCENTAGE)) {
			rectangle.setFill((Double) newValue);
		} else if (propertyName.equals(AbstractElementModel.PROP_COLOR_BACKGROUND)) {
			rectangle.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		} else if (propertyName.equals(AbstractElementModel.PROP_COLOR_FOREGROUND)) {
			rectangle.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		}
	}

}
