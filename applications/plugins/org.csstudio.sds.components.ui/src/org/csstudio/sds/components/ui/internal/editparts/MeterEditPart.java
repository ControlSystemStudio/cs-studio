package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for <code>MeterElement</code> elements.
 * 
 * @author Sven Wende
 * 
 */
public final class MeterEditPart extends AbstractElementEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		final RefreshableMeterFigure meter = new RefreshableMeterFigure();
		AbstractElementModel elementModel = getCastedModel();

		for (String key : elementModel.getPropertyNames()) {
			setFigureProperties(key, elementModel.getProperty(key)
					.getPropertyValue(), meter);
		}
		return meter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {

		RefreshableMeterFigure meter = (RefreshableMeterFigure) getFigure();
		setFigureProperties(propertyName, newValue, meter);
		meter.repaint();
	}

	/**
	 * Sets a property of a figure. Does not cause the figure to be (re-)painted!
	 * @param propertyName Required.
	 * @param newValue May be null.
	 * @param meter Required.
	 */
	private void setFigureProperties(final String propertyName, final Object newValue, final RefreshableMeterFigure meter) {
		assert propertyName != null : "Precondition violated: propertyName != null"; //$NON-NLS-1$
		assert meter != null : "Precondition violated: meter != null"; //$NON-NLS-1$
		
		if (propertyName.equals(MeterElement.PROP_VALUE)) {
			meter.setValue((Double) newValue);
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL1_LOWER_BORDER)) {
			meter.setInterval1LowerBorder((Double) newValue);
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL1_UPPER_BORDER)) {
			meter.setInterval1UpperBorder((Double) newValue);
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL2_LOWER_BORDER)) {
			meter.setInterval2LowerBorder((Double) newValue);
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL2_UPPER_BORDER)) {
			meter.setInterval2UpperBorder((Double) newValue);
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL3_LOWER_BORDER)) {
			meter.setInterval3LowerBorder((Double) newValue);
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL3_UPPER_BORDER)) {
			meter.setInterval3UpperBorder((Double) newValue);
		} else if (propertyName
				.equals(AbstractElementModel.PROP_BACKGROUND_COLOR)) {
			meter.setBackgroundColor(CustomMediaFactory.getInstance()
					.getColor((RGB) newValue));
		}
	}

}
