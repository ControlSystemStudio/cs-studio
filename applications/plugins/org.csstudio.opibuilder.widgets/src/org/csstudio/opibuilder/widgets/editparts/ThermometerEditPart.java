/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.ThermometerModel;
import org.csstudio.swt.widgets.figures.ThermometerFigure;
import org.csstudio.swt.widgets.figures.ThermometerFigure.TemperatureUnit;
import org.eclipse.draw2d.IFigure;

/**
 * EditPart controller for the Thermometer widget. The controller mediates between
 * {@link ThermometerModel} and {@link ThermometerFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class ThermometerEditPart extends AbstractMarkedWidgetEditPart {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ThermometerModel model = getWidgetModel();

		ThermometerFigure thermometer = new ThermometerFigure();
		
		initializeCommonFigureProperties(thermometer, model);		
		thermometer.setFillColor(model.getFillColor());
		thermometer.setTemperatureUnit(model.getUnit());
		thermometer.setShowBulb(model.isShowBulb());	
		thermometer.setFillBackgroundColor(model.getFillbackgroundColor());
		thermometer.setEffect3D(model.isEffect3D());
		return thermometer;

	}

	@Override
	public ThermometerModel getWidgetModel() {
		return (ThermometerModel)getModel();
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
				ThermometerFigure thermometer = (ThermometerFigure) refreshableFigure;
				thermometer.setFillColor(((OPIColor) newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_FILL_COLOR, fillColorHandler);	
		
		//fillBackgroundColor
		IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ThermometerFigure thermometer = (ThermometerFigure) refreshableFigure;
				thermometer.setFillBackgroundColor(((OPIColor) newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);	
		
		//show bulb
		IWidgetPropertyChangeHandler showBulbHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ThermometerFigure thermometer = (ThermometerFigure) refreshableFigure;
				thermometer.setShowBulb((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_SHOW_BULB, showBulbHandler);	
		
		//unit
		IWidgetPropertyChangeHandler fahrenheitHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ThermometerFigure thermometer = (ThermometerFigure) refreshableFigure;
				thermometer.setTemperatureUnit(TemperatureUnit.values()[(Integer)newValue]);
				return false;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_UNIT, fahrenheitHandler);
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ThermometerFigure thermo = (ThermometerFigure) refreshableFigure;
				thermo.setEffect3D((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_EFFECT3D, effect3DHandler);	
		
	}

}
