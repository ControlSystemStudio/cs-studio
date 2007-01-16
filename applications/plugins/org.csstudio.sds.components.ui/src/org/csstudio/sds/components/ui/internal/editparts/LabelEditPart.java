package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.LabelElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableLabelFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for <code>LabelElement</code> elements.
 * 
 * @author Stefan Hofer & Sven Wende
 * 
 */
public final class LabelEditPart extends AbstractElementEditPart {
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		RefreshableLabelFigure label = new RefreshableLabelFigure();
		AbstractElementModel elementModel = getCastedModel();

		for (String key : getCastedModel().getPropertyNames()) {
			setFigureProperties(key, elementModel.getProperty(key)
					.getPropertyValue(), label);
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
		setFigureProperties(propertyName, newValue, label);
		label.repaint();
	}

	/**
	 * Sets a property of a figure. Does not cause the figure to be (re-)painted! 
	 * @param propertyName The property to set.
	 * @param newValue The value to set.
	 * @param label The figure that is configured.
	 */
	private void setFigureProperties(final String propertyName, final Object newValue, final RefreshableLabelFigure label) {
		if (propertyName.equals(LabelElement.PROP_LABEL)) {
			label.setText(newValue.toString());
		} else if (propertyName.equals(LabelElement.PROP_BACKGROUND_COLOR)) {
			label.setBackgroundColor(CustomMediaFactory.getInstance().getColor(
					(RGB) newValue));
		} else if (propertyName.equals(LabelElement.PROP_FONT)) {
			FontData fontData = (FontData) newValue;
			label.setFont(CustomMediaFactory.getInstance().getFont(
					fontData.getName(), fontData.getHeight(),
					fontData.getStyle()));
		}
	}
}
