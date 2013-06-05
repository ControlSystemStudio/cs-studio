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
import org.csstudio.opibuilder.widgets.model.TankModel;
import org.csstudio.swt.widgets.figures.TankFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;

/**
 * EditPart controller for the tank widget. The controller mediates between
 * {@link TankModel} and {@link TankFigure}.
 * 
 * @author Xihui Chen
 * @author Takashi Nakamoto - support for "FillColor Alarm Sensitive" property
 */
public final class TankEditPart extends AbstractMarkedWidgetEditPart {
	
	private ISeverity currentSeverity = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		TankModel model = getWidgetModel();

		TankFigure tank = new TankFigure();
		
		initializeCommonFigureProperties(tank, model);		
		tank.setFillColor(model.getFillColor());
		tank.setEffect3D(model.isEffect3D());	
		tank.setFillBackgroundColor(model.getFillbackgroundColor());
		return tank;

	}

	@Override
	public TankModel getWidgetModel() {
		return (TankModel)getModel();
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
				TankFigure tank = (TankFigure) refreshableFigure;
				tank.setFillColor(((OPIColor) newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_FILL_COLOR, fillColorHandler);	
		
		//fillBackgroundColor
		IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				TankFigure tank = (TankFigure) refreshableFigure;
				tank.setFillBackgroundColor(((OPIColor) newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);	
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				TankFigure tank = (TankFigure) refreshableFigure;
				tank.setEffect3D((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(TankModel.PROP_EFFECT3D, effect3DHandler);	

		// Change fill color when "FillColor Alarm Sensitive" property changes.
		IWidgetPropertyChangeHandler fillColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
				TankFigure figure = (TankFigure) refreshableFigure;
				boolean sensitive = (Boolean)newValue;
				if (sensitive) {
					ISeverity severity = ((IValue)newValue).getSeverity();
					Device device = figure.getFillColor().getDevice();
					if (severity.isOK()) {
						figure.setFillColor(getWidgetModel().getFillColor());
					} else if (severity.isMajor()) {
						Color color = new Color(device, AlarmRepresentationScheme.getMajorColor());
						figure.setFillColor(color);
					} else if (severity.isMinor()) {
						Color color = new Color(device, AlarmRepresentationScheme.getMinorColor());
						figure.setFillColor(color);
					} else if (severity.isInvalid()) {
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

				TankFigure figure = (TankFigure) refreshableFigure;
				
				if (!getWidgetModel().isFillColorAlarmSensitive())
					return false;
				ISeverity newSeverity = ((IValue)newValue).getSeverity();
				
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
		setPropertyChangeHandler(TankModel.PROP_PVVALUE, valueHandler);	
	}

}
