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
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a scaled slider widget model.
 * @author Xihui Chen
 */
public class ProgressBarModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$
	
	/** The ID of the fillcolor alarm sensitive property. */
	public static final String PROP_FILLCOLOR_ALARM_SENSITIVE = "fillcolor_alarm_sensitive"; //$NON-NLS-1$	
	
	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect_3d"; //$NON-NLS-1$
	
	/** The ID of the horizontal property. */
	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	
	/** The ID of the fillbackground-Color property. */
	public static final String PROP_FILLBACKGROUND_COLOR = "color_fillbackground"; //$NON-NLS-1$
	
	public static final String PROP_SHOW_LABEL = "show_label"; //$NON-NLS-1$
	
	public static final String PROP_ORIGIN = "origin"; //$NON-NLS-1$

	public static final String PROP_ORIGIN_IGNORED= "origin_ignored"; //$NON-NLS-1$
	
	public static final String PROP_INDICATOR_MODE= "indicator_mode"; //$NON-NLS-1$
	
	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_FILL_COLOR = new RGB(0,0,255);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 80;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 200;
	
	/** The default value of the fillbackground color property. */
	private static final RGB DEFAULT_FILLBACKGROUND_COLOR = new RGB(200, 200, 200);
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.progressbar"; //$NON-NLS-1$	
	
	public ProgressBarModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
	
		addProperty(new ColorProperty(PROP_FILL_COLOR, "Fill Color",
				WidgetPropertyCategory.Display,DEFAULT_FILL_COLOR));
		
		addProperty(new BooleanProperty(PROP_FILLCOLOR_ALARM_SENSITIVE, "FillColor Alarm Sensitive", 
				WidgetPropertyCategory.Display, false));	
		
		addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(new BooleanProperty(PROP_SHOW_LABEL, "Show Label", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(new BooleanProperty(PROP_HORIZONTAL, "Horizontal", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(new ColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground",
				WidgetPropertyCategory.Display,DEFAULT_FILLBACKGROUND_COLOR));
		
		addProperty(new DoubleProperty(PROP_ORIGIN, "Origin", WidgetPropertyCategory.Behavior,
				0));
			
		addProperty(new BooleanProperty(PROP_ORIGIN_IGNORED, "Origin Ignored", 
				WidgetPropertyCategory.Behavior , true));
		
		addProperty(new BooleanProperty(PROP_INDICATOR_MODE, "Indicator Mode", 
				WidgetPropertyCategory.Display, false));
	
		setPropertyValue(PROP_LO_COLOR, new OPIColor(255, 128, 0));
		setPropertyValue(PROP_HI_COLOR, new OPIColor(255, 128, 0));
		
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
	 * @return true if the fill color is sensitive to alarm
	 */
	public boolean isFillColorAlarmSensitive() {
		return (Boolean) getProperty(PROP_FILLCOLOR_ALARM_SENSITIVE).getPropertyValue();
	}
	
	/**
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}
	
	public boolean isShowLabel() {
		return (Boolean) getProperty(PROP_SHOW_LABEL).getPropertyValue();
	}
	/**
	 * @return true if the widget is in horizontal orientation, false otherwise
	 */
	public boolean isHorizontal() {
		return (Boolean) getProperty(PROP_HORIZONTAL).getPropertyValue();
	}
	
	/**
	 * Gets the RGB for fillbackground.
	 * @return The fillbackground color
	 */
	public Color getFillbackgroundColor() {
		return getSWTColorFromColorProperty(PROP_FILLBACKGROUND_COLOR);
	}

	public double getOrigin() {
		return (Double)getPropertyValue(PROP_ORIGIN);
	}
	
	public boolean isOriginIgnored(){
		return (Boolean)getPropertyValue(PROP_ORIGIN_IGNORED);
	}
	
	public boolean isIndicatorMode(){
		return (Boolean)getPropertyValue(PROP_INDICATOR_MODE);
	}
	
	@Override
	public void rotate90(boolean clockwise) {
		setPropertyValue(PROP_HORIZONTAL, !isHorizontal());
	}
	
	@Override
	public void rotate90(boolean clockwise, Point center) {
		super.rotate90(clockwise, center);
		setPropertyValue(PROP_HORIZONTAL, !isHorizontal());
		super.rotate90(true);
	}
	
}
