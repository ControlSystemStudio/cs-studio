package org.csstudio.sds.components.ui.internal.editparts;

import org.csstudio.sds.components.model.ThermometerModel;
import org.csstudio.sds.components.ui.internal.figures.RefreshableThermoFigure;
import org.csstudio.sds.ui.editparts.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the Thermometer widget. The controller mediates between
 * {@link ThermometerModel} and {@link RefreshableThermoFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class ThermometerEditPart extends AbstractScaledWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ThermometerModel model = (ThermometerModel) getWidgetModel();

		RefreshableThermoFigure thermometer = new RefreshableThermoFigure();
		
		initializeCommonFigureProperties(thermometer, model);		
		thermometer.setFillColor(model.getFillColor());
		thermometer.setFahrenheit(model.isFahrenheit());
		thermometer.setShowBulb(model.isShowBulb());	
		thermometer.setFillBackgroundColor(model.getFillbackgroundColor());
		thermometer.setEffect3D(model.isEffect3D());
		return thermometer;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		//fillColor
		IWidgetPropertyChangeHandler fillColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableThermoFigure thermometer = (RefreshableThermoFigure) refreshableFigure;
				thermometer.setFillColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_FILL_COLOR, fillColorHandler);	
		
		//fillBackgroundColor
		IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableThermoFigure thermometer = (RefreshableThermoFigure) refreshableFigure;
				thermometer.setFillBackgroundColor((RGB) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);	
		
		//show bulb
		IWidgetPropertyChangeHandler showBulbHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableThermoFigure thermometer = (RefreshableThermoFigure) refreshableFigure;
				thermometer.setShowBulb((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_SHOW_BULB, showBulbHandler);	
		
		//fahrenheit
		IWidgetPropertyChangeHandler fahrenheitHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableThermoFigure thermometer = (RefreshableThermoFigure) refreshableFigure;
				thermometer.setFahrenheit((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_FAHRENHEIT, fahrenheitHandler);
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				RefreshableThermoFigure thermo = (RefreshableThermoFigure) refreshableFigure;
				thermo.setEffect3D((Boolean) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_EFFECT3D, effect3DHandler);	
		
	}

}
