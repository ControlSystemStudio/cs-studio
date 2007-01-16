package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.model.AbstractElementModel;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;

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
		return new RefreshableMeterFigure();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doRefreshFigure(final String propertyName,
			final Object newValue) {
		RefreshableMeterFigure meter = (RefreshableMeterFigure) getFigure();
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
			meter.setBackgroundColor((Color) newValue);
		}
		meter.repaint();

	}

}
