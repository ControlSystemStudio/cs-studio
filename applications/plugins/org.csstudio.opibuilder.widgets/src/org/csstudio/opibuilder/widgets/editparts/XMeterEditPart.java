package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.figures.MeterFigure;
import org.csstudio.opibuilder.widgets.model.XMeterModel;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Gauge widget. The controller mediates between
 * {@link XMeterModel} and {@link MeterFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class XMeterEditPart extends AbstractMarkedWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final XMeterModel model = getWidgetModel();

		MeterFigure xMeter = new MeterFigure();
		
		initializeCommonFigureProperties(xMeter, model);		
		xMeter.setNeedleColor((model.getNeedleColor()));
		xMeter.setGradient(model.isRampGradient());
		
		return xMeter;

	}
	
	@Override
	public XMeterModel getWidgetModel() {
		return (XMeterModel)getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//needle Color
		IWidgetPropertyChangeHandler needleColorColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				MeterFigure xMeter = (MeterFigure) refreshableFigure;
				xMeter.setNeedleColor(((OPIColor) newValue).getRGBValue());
				return true;
			}
		};
		setPropertyChangeHandler(XMeterModel.PROP_NEEDLE_COLOR, needleColorColorHandler);	
		
		
		
		//Ramp gradient
		IWidgetPropertyChangeHandler gradientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				MeterFigure xMeter = (MeterFigure) refreshableFigure;
				xMeter.setGradient((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XMeterModel.PROP_RAMP_GRADIENT, gradientHandler);	
		
		
		
	}

}
