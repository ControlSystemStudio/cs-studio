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
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a XMeter widget model.
 * @author Xihui Chen
 */
public class MeterModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_NEEDLE_COLOR = "needle_color"; //$NON-NLS-1$	
	
	/** The ID of the Ramp Gradient. */
	public static final String PROP_RAMP_GRADIENT = "ramp_gradient"; //$NON-NLS-1$
	
	/**Show value label. */
	public static final String PROP_SHOW_VALUE_LABEL = "show_value_label"; //$NON-NLS-1$
	

	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_NEEDLE_COLOR = new RGB(255,0,0);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 85;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 200;
	
	public static final int MINIMUM_WIDTH = 100;

	

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.meter"; //$NON-NLS-1$	
	
	public MeterModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
		setBackgroundColor(new RGB(255, 255, 255));
		setBorderStyle(BorderStyle.RIDGED);
		setScaleOptions(true, true, true);
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(new ColorProperty(PROP_NEEDLE_COLOR, "Needle Color",
				WidgetPropertyCategory.Display, DEFAULT_NEEDLE_COLOR));	
		
		
		addProperty(new BooleanProperty(PROP_RAMP_GRADIENT, "Ramp Gradient", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(new BooleanProperty(PROP_SHOW_VALUE_LABEL, "Show Value Label", 
				WidgetPropertyCategory.Display, true));	
		
		setPropertyDescription(PROP_SHOW_MARKERS, "Show Ramp");		
		
		//Ramp cannot be transparent.
		setPropertyValue(PROP_TRANSPARENT, false);
		removeProperty(PROP_TRANSPARENT);
	}	

	@Override
	public String getTypeID() {
		return ID;
	}		

	/**
	 * @return the needle color
	 */
	public Color getNeedleColor() {
		return getSWTColorFromColorProperty(PROP_NEEDLE_COLOR);
	}	
	
	/**
	 * @return true if the widget is in horizontal orientation, false otherwise
	 */
	public boolean isRampGradient() {
		return (Boolean) getProperty(PROP_RAMP_GRADIENT).getPropertyValue();
	}
	
	@Override
	public boolean isTransparent() {
		return false;
	}
	
}
