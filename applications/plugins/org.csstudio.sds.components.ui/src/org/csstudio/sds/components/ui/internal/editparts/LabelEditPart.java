package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.LabelElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for <code>LabelElement</code> elements.
 * 
 * @author Stefan Hofer & Sven Wende
 * 
 */
public final class LabelEditPart extends AbstractSDSEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new RefreshableLabelFigure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		if (propertyName.equals(LabelElement.PROP_LABEL)) {
			RefreshableLabelFigure label = (RefreshableLabelFigure) getFigure();
			label.setText("" + newValue); //$NON-NLS-1$
			label.repaint();
		}
	}

}
