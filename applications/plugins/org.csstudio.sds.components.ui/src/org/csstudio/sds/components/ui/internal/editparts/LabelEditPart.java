package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.internal.model.LabelElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.DisplayModelElement;
import org.csstudio.sds.ui.editparts.AbstractSDSEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

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
		RefreshableLabelFigure label = new RefreshableLabelFigure();
		DisplayModelElement modelElement = getCastedModel();

		for (String key : getCastedModel().getPropertyNames()) {
			label.refresh(key, modelElement.getProperty(key)
					.getPropertyValue());
		}
		
		return label;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		RefreshableLabelFigure label = (RefreshableLabelFigure) getFigure();
		if (propertyName.equals(LabelElement.PROP_LABEL)) {
			label.setText("" + newValue); //$NON-NLS-1$
			label.repaint();
		} else if (propertyName.equals(LabelElement.PROP_BACKGROUND_COLOR)) {
			label.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
			label.repaint();
		}
	}

}
