package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.XMeterModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableXMeterFigure;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the Gauge widget. The controller mediates between
 * {@link XMeterModel} and {@link RefreshableXMeterFigure}.
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
		final XMeterModel model = (XMeterModel) getWidgetModel();

		RefreshableXMeterFigure xMeter = new RefreshableXMeterFigure();
		
		initializeCommonFigureProperties(xMeter, model);		
		xMeter.setNeedleColor((model.getNeedleColor()));
		xMeter.setGradient(model.isRampGradient());
		
		return xMeter;

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
				RefreshableXMeterFigure xMeter = (RefreshableXMeterFigure) refreshableFigure;
				xMeter.setNeedleColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XMeterModel.PROP_NEEDLE_COLOR, needleColorColorHandler);	
		
		
		
		//Ramp gradient
		IWidgetPropertyChangeHandler gradientHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableXMeterFigure xMeter = (RefreshableXMeterFigure) refreshableFigure;
				xMeter.setGradient((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(XMeterModel.PROP_RAMP_GRADIENT, gradientHandler);	
		
		
		
	}

}
