/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.FontProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIFont;

/**
 * This class defines a common widget model for any widget 
 * which has a scale. 
 * @author Xihui Chen
 */
public abstract class AbstractScaledWidgetModel extends AbstractPVWidgetModel {
	
	
	/** True if the widget's background is transparent. */
	public static final String PROP_TRANSPARENT = "transparent_background";	
	
	/** Lower limit of the widget. */
	public static final String PROP_MIN = "minimum"; //$NON-NLS-1$		
	
	/** Higher limit of the widget. */
	public static final String PROP_MAX = "maximum"; //$NON-NLS-1$		
	
	/** The minimum distance (in pixels) between major ticks.*/
	public static final String PROP_MAJOR_TICK_STEP_HINT = "major_tick_step_hint"; //$NON-NLS-1$		
	
	/**Show minor ticks. */
	public static final String PROP_SHOW_MINOR_TICKS = "show_minor_ticks"; //$NON-NLS-1$
	
	/** Show scale. */
	public static final String PROP_SHOW_SCALE = "show_scale"; //$NON-NLS-1$
	
	/** True if the scale is log scale. */
	public static final String PROP_LOG_SCALE = "log_scale"; //$NON-NLS-1$	

	/** True if the scale is log scale. */
	public static final String PROP_SCALE_FONT = "scale_font"; //$NON-NLS-1$	
	
	/**
	 * The numeric format pattern for the scale.
	 */
	public static final String PROP_SCALE_FORMAT = "scale_format";

	/**
	 * The numeric format pattern for the value label.
	 */
	public static final String PROP_VALUE_LABEL_FORMAT = "value_label_format";
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = 0;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = 100;	
	
		/** The default value of the major tick step hint property. */
	private static final int DEFAULT_MAJOR_TICK_STEP_HINT = 50;	

	@Override
	protected void configureProperties() {
		
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent Background",
				WidgetPropertyCategory.Display, true));
		
		
		addProperty(new DoubleProperty(PROP_MIN, "Minimum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MIN));
		
		addProperty(new DoubleProperty(PROP_MAX, "Maximum", 
				WidgetPropertyCategory.Behavior, DEFAULT_MAX));			
		
		addProperty(new IntegerProperty(PROP_MAJOR_TICK_STEP_HINT, "Major Tick Step Hint", 
				WidgetPropertyCategory.Display, DEFAULT_MAJOR_TICK_STEP_HINT, 1, 1000));			
		
		addProperty(new BooleanProperty(PROP_SHOW_MINOR_TICKS, "Show Minor Ticks", 
				WidgetPropertyCategory.Display, true));		
		
		addProperty(new BooleanProperty(PROP_SHOW_SCALE, "Show Scale", 
				WidgetPropertyCategory.Display, true));		
		
		addProperty(new BooleanProperty(PROP_LOG_SCALE, "Log Scale", 
				WidgetPropertyCategory.Display, false));
		
		addProperty(new FontProperty(PROP_SCALE_FONT, "Scale Font", 
				WidgetPropertyCategory.Display,	MediaService.DEFAULT_FONT));
		
		addProperty(new StringProperty(PROP_SCALE_FORMAT, "Scale Format", 
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
		
		addProperty(new StringProperty(PROP_VALUE_LABEL_FORMAT, "Value Label Format", 
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
	}
	


	/**
	 * @return the minimum value
	 */
	public Double getMinimum() {
		return (Double) getProperty(PROP_MIN).getPropertyValue();
	}


	/**
	 * @return the maximum value
	 */
	public Double getMaximum() {
		return (Double) getProperty(PROP_MAX).getPropertyValue();
	}

	/**
	 * @return the major tick step hint value
	 */
	public Integer getMajorTickStepHint() {
		return (Integer) getProperty(PROP_MAJOR_TICK_STEP_HINT).getPropertyValue();
	}

	
	
	/**
	 * @return true if the minor ticks should be shown, false otherwise
	 */
	public boolean isShowMinorTicks() {
		return (Boolean) getProperty(PROP_SHOW_MINOR_TICKS).getPropertyValue();
	}
	
	/**
	 * @return true if the scale should be shown, false otherwise
	 */
	public boolean isShowScale() {
		return (Boolean) getProperty(PROP_SHOW_SCALE).getPropertyValue();
	}
	

	/**
	 * @return true if log scale enabled, false otherwise
	 */
	public boolean isLogScaleEnabled() {
		return (Boolean) getProperty(PROP_LOG_SCALE).getPropertyValue();
	}
	
	/**
	 * Returns, if this widget should have a transparent background.
	 * @return boolean
	 * 				True, if it should have a transparent background, false otherwise
	 */
	public boolean isTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	public OPIFont getScaleFont(){
		return (OPIFont)getCastedPropertyValue(PROP_SCALE_FONT);
	}
	
	public String getScaleFormat(){
		return (String)getPropertyValue(PROP_SCALE_FORMAT);
	}
	
	public String getValueLabelFormat(){
		return (String)getPropertyValue(PROP_VALUE_LABEL_FORMAT);
	}
	
}
