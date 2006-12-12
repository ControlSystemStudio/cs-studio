package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.EllipseElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableEllipse;
import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for <code>EllipseElement</code> elements.
 * 
 * @author Stefan Hofer & Sven Wende
 * 
 */
public final class EllipseEditPart extends AbstractSDSEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		RefreshableEllipse ellipse = new RefreshableEllipse();
		DisplayModelElement modelElement = getCastedModel();

		for (String key : modelElement.getPropertyNames()) {
			setFigureProperties(key, modelElement.getProperty(key)
					.getPropertyValue(), ellipse);
		}
		return ellipse;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		RefreshableEllipse ellipse = (RefreshableEllipse) getFigure();
		setFigureProperties(propertyName, newValue, ellipse);
		ellipse.repaint();
	}

	/**
	 * Sets a property of a figure. Does not cause the figure to be (re-)painted! 
	 * @param propertyName The property to set.
	 * @param newValue The value to set.
	 * @param ellipse The figure that is configured.
	 */
	private void setFigureProperties(final String propertyName, final Object newValue, final RefreshableEllipse ellipse) {
		if (propertyName.equals(EllipseElement.PROP_FILL_PERCENTAGE)) {
			ellipse.setFill((Double) newValue);
		} else if (propertyName.equals(EllipseElement.PROP_BACKGROUND_COLOR)) {
			ellipse.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		} else if (propertyName.equals(EllipseElement.PROP_FOREGROUND_COLOR)) {
			ellipse.setForegroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		}
	}

}
