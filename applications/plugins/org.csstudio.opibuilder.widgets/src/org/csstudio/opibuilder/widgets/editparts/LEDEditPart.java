/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.LEDModel;
import org.csstudio.swt.widgets.figures.LEDFigure;
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
				if(!(Boolean)newValue){
					int width = Math.min(getWidgetModel().getWidth(), getWidgetModel().getHeight());
					getWidgetModel().setSize(width, width);
				}
				return true;
			}
		};
		setPropertyChangeHandler(LEDModel.PROP_SQUARE_LED, handler);	
		
		//force square size
		final IWidgetPropertyChangeHandler sizeHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				if(getWidgetModel().isSquareLED())
					return false;
				if(((Integer)newValue) < LEDModel.MINIMUM_SIZE)
					newValue = LEDModel.MINIMUM_SIZE;			
				getWidgetModel().setSize((Integer)newValue, (Integer)newValue);
				return false;
			}
		};		
		PropertyChangeListener sizeListener = new PropertyChangeListener() {
		
			public void propertyChange(PropertyChangeEvent evt) {
				sizeHandler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
			}
		};
		getWidgetModel().getProperty(AbstractWidgetModel.PROP_WIDTH).
			addPropertyChangeListener(sizeListener);
		getWidgetModel().getProperty(AbstractWidgetModel.PROP_HEIGHT).
			addPropertyChangeListener(sizeListener);
		
	}

}
