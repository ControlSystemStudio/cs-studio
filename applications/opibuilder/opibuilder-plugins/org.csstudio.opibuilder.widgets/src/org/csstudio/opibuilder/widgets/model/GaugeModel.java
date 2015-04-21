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
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a gauge widget model.
 * @author Xihui Chen
 */
public class GaugeModel extends AbstractMarkedWidgetModel{	
	
	/** Needle color. */
	public static final String PROP_NEEDLE_COLOR = "needle_color"; //$NON-NLS-1$	
	
	/** True if the widget is drawn with advanced graphics. In some platforms,
	 * advance graphics may not be available, in which case the widget will not be drawn 
	 * with advanced graphics even this is set to true.*/
	public static final String PROP_EFFECT3D = "effect_3d"; //$NON-NLS-1$

	/** True if the ramp is gradient. */
	public static final String PROP_RAMP_GRADIENT = "ramp_gradient"; //$NON-NLS-1$

	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_NEEDLE_COLOR = new RGB(255,0,0);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 138;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 138;
	
	public static final int MINIMUM_SIZE = 30;

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.gauge"; //$NON-NLS-1$	
	
	public GaugeModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(255,255,255));
		setBackgroundColor(new RGB(0,64,128));
		setScaleOptions(true, true, true);
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(new ColorProperty(PROP_NEEDLE_COLOR, "Needle Color",
				WidgetPropertyCategory.Display, DEFAULT_NEEDLE_COLOR));	
		
		addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(new BooleanProperty(PROP_RAMP_GRADIENT, "Ramp Gradient", 
				WidgetPropertyCategory.Display, true));	
		
		setPropertyDescription(PROP_SHOW_MARKERS, "Show Ramp");
	}	

	@Override
	public String getTypeID() {
		return ID;
	}		

	/**
	 * @return the needle color
	 */
	public RGB getNeedleColor() {
		return getRGBFromColorProperty(PROP_NEEDLE_COLOR);
	}	
	
	/**
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}
	
	/**
	 * @return true if the ramp is gradient, false otherwise
	 */
	public boolean isRampGradient() {
		return (Boolean) getProperty(PROP_RAMP_GRADIENT).getPropertyValue();
	}
	
	@Override
	public void scale(double widthRatio, double heightRatio) {
		super.scale(widthRatio, heightRatio);
	}
	
}
