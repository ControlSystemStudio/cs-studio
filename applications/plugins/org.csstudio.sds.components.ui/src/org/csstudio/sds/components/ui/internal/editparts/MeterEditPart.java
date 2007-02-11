package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.EllipseElement;
import org.csstudio.sds.components.model.MeterElement;
import org.csstudio.sds.components.ui.internal.figures.RefreshableEllipseFigure;
import org.csstudio.sds.components.ui.internal.figures.RefreshableMeterFigure;
import org.csstudio.sds.ui.editparts.AbstractElementEditPart;
import org.csstudio.sds.ui.editparts.IElementPropertyChangeHandler;
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
		MeterElement model = (MeterElement) getCastedModel();

		RefreshableMeterFigure meter = new RefreshableMeterFigure();

		meter.setInterval1LowerBorder(model.getInterval1LowerBorder());
		meter.setInterval1UpperBorder(model.getInterval1UpperBorder());
		meter.setInterval2LowerBorder(model.getInterval2LowerBorder());
		meter.setInterval2UpperBorder(model.getInterval2UpperBorder());
		meter.setInterval3LowerBorder(model.getInterval3LowerBorder());
		meter.setInterval3UpperBorder(model.getInterval3UpperBorder());
		meter.setValue(model.getValue());
		
		return meter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		// value
		IElementPropertyChangeHandler valueHandler = new IElementPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue,
					IRefreshableFigure figure) {
				RefreshableMeterFigure meter = (RefreshableMeterFigure) figure;
				meter.setValue((Double) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(MeterElement.PROP_VALUE, valueHandler);
		
		//TODO: Register change handlers for upper and lower intervals
	}

}
