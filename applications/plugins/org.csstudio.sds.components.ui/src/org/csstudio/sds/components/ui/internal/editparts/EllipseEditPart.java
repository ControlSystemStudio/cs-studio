package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.EllipseElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableEllipseFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.figures.IRefreshableFigure;

/**
 * EditPart controller for <code>EllipseElement</code> elements.
 * 
 * @author Stefan Hofer & Sven Wende
 * 
 */
public final class EllipseEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IRefreshableFigure doCreateFigure() {
		return new RefreshableEllipseFigure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure figure) {
		RefreshableEllipseFigure ellipse = (RefreshableEllipseFigure) figure;
		
		if (propertyName.equals(EllipseElement.PROP_FILL)) {
			ellipse.setFill((Double) newValue);
			return true;
		}
		
		return false;
	}

}
