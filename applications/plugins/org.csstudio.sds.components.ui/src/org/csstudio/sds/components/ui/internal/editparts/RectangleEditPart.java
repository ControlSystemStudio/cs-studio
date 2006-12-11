package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.internal.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangle;
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
		CentralLogger.getInstance().debug(this,
				"Creating RefreshableRectangle..."); //$NON-NLS-1$

		RefreshableRectangle rectangle = new RefreshableRectangle();

		RGB backRgb = (RGB) getCastedModel().getProperty(
				RectangleElement.PROP_BACKGROUND_COLOR).getPropertyValue();
		RGB foreRgb = (RGB) getCastedModel().getProperty(
				RectangleElement.PROP_FOREGROUND_COLOR).getPropertyValue();

		rectangle.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
				backRgb));
		rectangle.setForegroundColor(CustomMediaFactory.getInstance().getColor(
				foreRgb));

		return rectangle;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		if (propertyName.equals(RectangleElement.PROP_FILL_PERCENTAGE)) {
			RefreshableRectangle rectangle = (RefreshableRectangle) getFigure();
			rectangle.setFill((Double) newValue);
			rectangle.repaint();
		} else if (propertyName.equals(RectangleElement.PROP_BACKGROUND_COLOR)) {
			RefreshableRectangle rectangle = (RefreshableRectangle) getFigure();
			rectangle.setBackgroundColor(CustomMediaFactory.getInstance().getColor((RGB) newValue));
			rectangle.repaint();
		} else if (propertyName.equals(RectangleElement.PROP_FOREGROUND_COLOR)) {
			RefreshableRectangle rectangle = (RefreshableRectangle) getFigure();
			rectangle.setForegroundColor(CustomMediaFactory.getInstance().getColor((RGB) newValue));
			rectangle.repaint();
		}
	}

}
