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
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.AlarmRepresentationScheme;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.widgets.model.ProgressBarModel;
import org.csstudio.opibuilder.widgets.model.ScaledSliderModel;
import org.csstudio.swt.widgets.figures.ProgressBarFigure;
import org.csstudio.swt.widgets.figures.ScaledSliderFigure;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;

/**
 * EditPart controller for the scaled slider widget. The controller mediates between
 * {@link ScaledSliderModel} and {@link ScaledSliderFigure}.
 * 
 * @author Xihui Chen
 * 
 */
public final class ProgressBarEditPart extends AbstractMarkedWidgetEditPart {

	private ISeverity currentSeverity = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final ProgressBarModel model = getWidgetModel();

		ProgressBarFigure bar = new ProgressBarFigure();
		
		initializeCommonFigureProperties(bar, model);		
		bar.setFillColor(model.getFillColor());
		bar.setEffect3D(model.isEffect3D());	
		bar.setFillBackgroundColor(model.getFillbackgroundColor());
		bar.setHorizontal(model.isHorizontal());
		bar.setShowLabel(model.isShowLabel());
		bar.setOrigin(model.getOrigin());
		bar.setOriginIgnored(model.isOriginIgnored());
		bar.setIndicatorMode(model.isIndicatorMode());
		return bar;

	}

	@Override
	public ProgressBarModel getWidgetModel() {
		return (ProgressBarModel)getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		registerCommonPropertyChangeHandlers();
		
		IWidgetPropertyChangeHandler originHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((ProgressBarFigure)figure).setOrigin((Double)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_ORIGIN, originHandler);
		
	IWidgetPropertyChangeHandler originIgnoredHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((ProgressBarFigure)figure).setOriginIgnored((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_ORIGIN_IGNORED, originIgnoredHandler);
		
		//fillColor
		IWidgetPropertyChangeHandler fillColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ProgressBarFigure slider = (ProgressBarFigure) refreshableFigure;
				slider.setFillColor(((OPIColor) newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_FILL_COLOR, fillColorHandler);	
		
		//fillBackgroundColor
		IWidgetPropertyChangeHandler fillBackColorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ProgressBarFigure slider = (ProgressBarFigure) refreshableFigure;
				slider.setFillBackgroundColor(((OPIColor) newValue).getSWTColor());
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_FILLBACKGROUND_COLOR, fillBackColorHandler);	
		
	
		
		//effect 3D
		IWidgetPropertyChangeHandler effect3DHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ProgressBarFigure slider = (ProgressBarFigure) refreshableFigure;
				slider.setEffect3D((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_EFFECT3D, effect3DHandler);	
		
		//effect 3D
		IWidgetPropertyChangeHandler showLabelHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ProgressBarFigure slider = (ProgressBarFigure) refreshableFigure;
				slider.setShowLabel((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_SHOW_LABEL, showLabelHandler);	
		
		IWidgetPropertyChangeHandler indicatorHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ProgressBarFigure slider = (ProgressBarFigure) refreshableFigure;
				slider.setIndicatorMode((Boolean) newValue);
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_INDICATOR_MODE, indicatorHandler);	
		
		//horizontal
		IWidgetPropertyChangeHandler horizontalHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ProgressBarFigure slider = (ProgressBarFigure) refreshableFigure;
				slider.setHorizontal((Boolean) newValue);
				ProgressBarModel model = (ProgressBarModel)getModel();
				
				if((Boolean) newValue) //from vertical to horizontal
					model.setLocation(model.getLocation().x - model.getSize().height/2 + model.getSize().width/2,
						model.getLocation().y + model.getSize().height/2 - model.getSize().width/2);
				else  //from horizontal to vertical
					model.setLocation(model.getLocation().x + model.getSize().width/2 - model.getSize().height/2,
						model.getLocation().y - model.getSize().width/2 + model.getSize().height/2);					
				
				model.setSize(model.getSize().height, model.getSize().width);
				
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_HORIZONTAL, horizontalHandler);	
		
		
		//enabled. WidgetBaseEditPart will force the widget as disabled in edit model,
		//which is not the case for the scaled slider		
		IWidgetPropertyChangeHandler enableHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {
				ProgressBarFigure slider = (ProgressBarFigure) refreshableFigure;
				slider.setEnabled((Boolean) newValue);				
				return false;
			}
		};
		setPropertyChangeHandler(ProgressBarModel.PROP_ENABLED, enableHandler);	

		// Change fill color whne "FillColor Alarm Sensitive" property changes.
		IWidgetPropertyChangeHandler fillColorAlarmSensitiveHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(Object oldValue, Object newValue, IFigure refreshableFigure) {
				ProgressBarFigure figure = (ProgressBarFigure) refreshableFigure;
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
		
		// Change fill color whne alarm severity changes.
		IWidgetPropertyChangeHandler valueHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue,
					final IFigure refreshableFigure) {

				ProgressBarFigure figure = (ProgressBarFigure) refreshableFigure;
				
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
		setPropertyChangeHandler(ProgressBarModel.PROP_PVVALUE, valueHandler);	
	}
}
