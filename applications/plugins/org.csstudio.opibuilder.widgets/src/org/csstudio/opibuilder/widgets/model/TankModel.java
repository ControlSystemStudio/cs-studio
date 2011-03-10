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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a tank widget model.
 * @author Xihui Chen
 */
public class TankModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$	
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_EFFECT3D = "effect_3d"; //$NON-NLS-1$
	
	/**
	 * The ID of the fillbackground-Color property.
	 */
	public static final String PROP_FILLBACKGROUND_COLOR = "color_fillbackground";
	
	
	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_FILL_COLOR = new RGB(0,0,255);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 180;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 150;
	
	/**
	 * The default value of the fillbackground color property. 
	 */
	private static final RGB DEFAULT_FILLBACKGROUND_COLOR = new RGB(192, 192, 192);
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.tank"; //$NON-NLS-1$	
	
	public TankModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(new ColorProperty(PROP_FILL_COLOR, "Fill Color",
				WidgetPropertyCategory.Display, DEFAULT_FILL_COLOR));	
		
		addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect", 
				WidgetPropertyCategory.Display, true));	

		
		addProperty(new ColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground",
				WidgetPropertyCategory.Display, DEFAULT_FILLBACKGROUND_COLOR));
		
		setPropertyValue(PROP_LO_COLOR, new RGB(255, 128, 0));
		setPropertyValue(PROP_HI_COLOR, new RGB(255, 128, 0));		
		setPropertyVisible(PROP_VALUE_LABEL_FORMAT, false);
	}
	


	@Override
	public String getTypeID() {
		return ID;
	}		

	/**
	 * @return the fill color
	 */
	public Color getFillColor() {
		return getSWTColorFromColorProperty(PROP_FILL_COLOR);
	}	
	
	/**
	 * @return true if the bulb should be shown, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}
	
	
	/**
	 * Gets the RGB for fillbackground.
	 * @return The fillbackground color
	 */
	public Color getFillbackgroundColor() {
		return getSWTColorFromColorProperty(PROP_FILLBACKGROUND_COLOR);
	}
	
}
