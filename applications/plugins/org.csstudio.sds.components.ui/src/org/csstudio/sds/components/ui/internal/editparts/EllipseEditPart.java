package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.EllipseElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableEllipse;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;
import org.eclipse.draw2d.IFigure;

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
		return new RefreshableEllipse();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		if (propertyName.equals(EllipseElement.PROP_FILL_PERCENTAGE)) {
			RefreshableEllipse ellipse = (RefreshableEllipse) getFigure();
			ellipse.setFill((Double) newValue);
			ellipse.repaint();
		}
	}

}
