/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.Color;


/**The model for a rounded rectangle widget.
 * 
 * @author Xihui Chen
 *
 */
public class RoundedRectangleModel extends AbstractShapeModel {
	
	
	public final String ID = "org.csstudio.opibuilder.widgets.RoundedRectangle";

	public static final String PROP_CORNER_WIDTH = "corner_width"; //$NON-NLS-1$

	public static final String PROP_CORNER_HEIGHT = "corner_height"; //$NON-NLS-1$

	private static final int DEFAULT_CORNER_WIDTH = 16;
	
	private static final int DEFAULT_CORNER_HEIGHT = 16;
	
	/**
	 * True if the ellipse should be filled with gradient effect.
	 */
	public static final String PROP_GRADIENT = "gradient"; //$NON-NLS-1$
	
	/**
	 * The color on gradient start.
	 */
	public static final String PROP_BACKGROUND_GRADIENT_START_COLOR = "bg_gradient_color"; //$NON-NLS-1$	
		
	/**
	 * The color on foreground gradient start.
	 */
	public static final String PROP_FOREGROUND_GRADIENT_START_COLOR = "fg_gradient_color"; //$NON-NLS-1$	
		

	@Override
	public String getTypeID() {
		return ID;
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		addProperty(new IntegerProperty(PROP_CORNER_WIDTH, "Corner Width", 
				WidgetPropertyCategory.Display, DEFAULT_CORNER_WIDTH));
		addProperty(new IntegerProperty(PROP_CORNER_HEIGHT, "Corner Height", 
				WidgetPropertyCategory.Display, DEFAULT_CORNER_HEIGHT));
		addProperty(new ColorProperty(PROP_BACKGROUND_GRADIENT_START_COLOR, "Background Gradient Start Color",
				WidgetPropertyCategory.Display,  CustomMediaFactory.COLOR_WHITE));	
		addProperty(new ColorProperty(PROP_FOREGROUND_GRADIENT_START_COLOR, "Foreground Gradient Start Color",
				WidgetPropertyCategory.Display,  CustomMediaFactory.COLOR_WHITE));	
		addProperty(new BooleanProperty(PROP_GRADIENT, "Gradient", 
				WidgetPropertyCategory.Display, false));
	}
	
	/**
	 * @return the corner width
	 */
	public final int getCornerWidth(){
		return (Integer)getPropertyValue(PROP_CORNER_WIDTH);
	}
	

	/**
	 * @return the corner height
	 */
	public final int getCornerHeight(){
		return (Integer)getPropertyValue(PROP_CORNER_HEIGHT);
	}
	
	public boolean isGradient(){
		return (Boolean)getPropertyValue(PROP_GRADIENT);
	}

	public Color getBackgroundGradientStartColor(){
		return ((OPIColor)getPropertyValue(PROP_BACKGROUND_GRADIENT_START_COLOR)).getSWTColor();
	}
	
	public Color getForegroundGradientStartColor(){
		return ((OPIColor)getPropertyValue(PROP_FOREGROUND_GRADIENT_START_COLOR)).getSWTColor();
	}


}
