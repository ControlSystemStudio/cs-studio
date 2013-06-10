/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.ProgressBarModel;
import org.csstudio.opibuilder.widgets.model.ThermometerModel;
import org.csstudio.swt.widgets.figures.ThermometerFigure;
import org.csstudio.swt.widgets.figures.ThermometerFigure.TemperatureUnit;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

/**
 * EditPart controller for the Thermometer widget. The controller mediates between
 * {@link ThermometerModel} and {@link ThermometerFigure}.
 * 
 * @author Xihui Chen
 * @author Takashi Nakamoto - added handler for "FillColor Alarm Sensitive" property
 */
public final class ThermometerEditPart extends AbstractMarkedWidgetEditPart {
	
	private ISeverity currentSeverity = null;


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

		// Change fill color when "FillColor Alarm Sensitive" property changes.
		IWidgetPropertyChangeHandler fillColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
				ThermometerFigure figure = (ThermometerFigure) refreshableFigure;
				boolean sensitive = (Boolean)newValue;
				if (sensitive && currentSeverity != null) {
					Device device = figure.getFillColor().getDevice();
					if (currentSeverity.isOK()) {
						figure.setFillColor(getWidgetModel().getFillColor());
					} else if (currentSeverity.isMajor()) {
						Color color = new Color(device, AlarmRepresentationScheme.getMajorColor());
						figure.setFillColor(color);
					} else if (currentSeverity.isMinor()) {
						Color color = new Color(device, AlarmRepresentationScheme.getMinorColor());
						figure.setFillColor(color);
					} else if (currentSeverity.isInvalid()) {
						Color color = new Color(device, AlarmRepresentationScheme.getInValidColor());
						figure.setFillColor(color);
					}
				} else {
					figure.setFillColor(getWidgetModel().getFillColor());
				}
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_FILLCOLOR_ALARM_SENSITIVE, fillColorAlarmSensitiveHandler);

		
		// Change fill color when alarm severity changes.
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {

				ThermometerFigure figure = (ThermometerFigure) refreshableFigure;
				ISeverity newSeverity = ((IValue)newValue).getSeverity();
				
				if (!getWidgetModel().isFillColorAlarmSensitive()) {
					currentSeverity = newSeverity;
					return false;
				}
				
				if (currentSeverity != null) {
					if (currentSeverity.isOK() && newSeverity.isOK())
						return false;
					if (currentSeverity.isMajor() && newSeverity.isMajor())
						return false;
					if (currentSeverity.isMinor() && newSeverity.isMinor())
						return false;
					if (currentSeverity.isInvalid() && newSeverity.isInvalid())
						return false;
				}
				
				Device device = figure.getFillColor().getDevice();
				if (newSeverity.isOK()) {
					figure.setFillColor(getWidgetModel().getFillColor());
				} else if (newSeverity.isMajor()) {
					Color color = new Color(device, AlarmRepresentationScheme.getMajorColor());
					figure.setFillColor(color);
				} else if (newSeverity.isMinor()) {
					Color color = new Color(device, AlarmRepresentationScheme.getMinorColor());
					figure.setFillColor(color);
				} else if (newSeverity.isInvalid()) {
					Color color = new Color(device, AlarmRepresentationScheme.getInValidColor());
					figure.setFillColor(color);
				}
				
				currentSeverity = newSeverity;
				
				return true;
			}
		};
		setPropertyChangeHandler(ThermometerModel.PROP_PVVALUE, valueHandler);	
	}

}
