package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.figures.IRefreshableFigure;

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
	protected IRefreshableFigure doCreateFigure() {
		return  new RefreshableMeterFigure();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doRefreshFigure(final String propertyName,
			final Object newValue, final IRefreshableFigure f) {

		RefreshableMeterFigure meter = (RefreshableMeterFigure) f;
		
		if (propertyName.equals(MeterElement.PROP_VALUE)) {
			meter.setValue((Double) newValue);
			return true;
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL1_LOWER_BORDER)) {
			meter.setInterval1LowerBorder((Double) newValue);
			return true;
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL1_UPPER_BORDER)) {
			meter.setInterval1UpperBorder((Double) newValue);
			return true;
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL2_LOWER_BORDER)) {
			meter.setInterval2LowerBorder((Double) newValue);
			return true;
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL2_UPPER_BORDER)) {
			meter.setInterval2UpperBorder((Double) newValue);
			return true;
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL3_LOWER_BORDER)) {
			meter.setInterval3LowerBorder((Double) newValue);
			return true;
		} else if (propertyName
				.equals(MeterElement.PROP_INTERVAL3_UPPER_BORDER)) {
			meter.setInterval3UpperBorder((Double) newValue);
			return true;
		} 
		
		return false;
	}

}
