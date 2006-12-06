package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.components.internal.model.RectangleElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableRectangle;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;

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
				"Creating RefreshableRectangle...");
		return new RefreshableRectangle();
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
		}
	}

}
