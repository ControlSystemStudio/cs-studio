package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.LEDFigure;
import org.csstudio.opibuilder.widgets.model.LEDModel;
import org.eclipse.draw2d.IFigure;

/**
 * LED EditPart
 * @author Xihui Chen
 *
 */
public class LEDEditPart extends AbstractBoolEditPart{

	@Override
	protected IFigure doCreateFigure() {
		final LEDModel model = getWidgetModel();

		LEDFigure led = new LEDFigure();
		
		initializeCommonFigureProperties(led, model);			
		led.setEffect3D(model.isEffect3D());
		led.setSquareLED(model.isSquareLED());
		return led;
		
		
	}

	@Override
	public LEDModel getWidgetModel() {
		return (LEDModel)getModel();
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//effect 3D
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				LEDFigure led = (LEDFigure) refreshableFigure;
				led.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LEDModel.PROP_EFFECT3D, handler);	
		
		//Sqaure LED
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				LEDFigure led = (LEDFigure) refreshableFigure;
				led.setSquareLED((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LEDModel.PROP_SQUARE_LED, handler);	
		
	}

}
