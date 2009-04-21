package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.components.internal.localization.Messages;

/**
 * This class defines a common widget model for any widget 
 * which has a scale. 
 * @author Xihui Chen
 */
public abstract class AbstractScaledWidgetModel extends AbstractWidgetModel {
	
	
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparency";	
	
	/** The ID of the value property. */
	public static final String PROP_VALUE = "value"; //$NON-NLS-1$
	
	/** The ID of the minimum property. */
	public static final String PROP_MIN = "minimum"; //$NON-NLS-1$		
	
	/** The ID of the maximum property. */
	public static final String PROP_MAX = "maximum"; //$NON-NLS-1$		
	
	/** The ID of the major tick step hint property. */
	public static final String PROP_MAJOR_TICK_STEP_HINT = "majorTickStepHint"; //$NON-NLS-1$		
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_SHOW_MINOR_TICKS = "showMinorTicks"; //$NON-NLS-1$
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_SHOW_SCALE = "showScale"; //$NON-NLS-1$
	
	/** The ID of the log scale property. */
	public static final String PROP_LOG_SCALE = "logScale"; //$NON-NLS-1$	
	
	/** The default value of the value property. */
	private static final double DEFAULT_VALUE = 50;	
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = 0;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = 100;	
	
		/** The default value of the major tick step hint property. */
	private static final double DEFAULT_MAJOR_TICK_STEP_HINT = 50;	

	@Override
	protected void configureProperties() {
		
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background",
				WidgetPropertyCategory.Display,true));
		
		addProperty(PROP_VALUE, new DoubleProperty(Messages.FillLevelProperty,
				WidgetPropertyCategory.Behaviour, DEFAULT_VALUE));
		
		addProperty(PROP_MIN, new DoubleProperty("Minimum", 
				WidgetPropertyCategory.Behaviour,DEFAULT_MIN));
		
		addProperty(PROP_MAX, new DoubleProperty("Maximum", 
				WidgetPropertyCategory.Behaviour,DEFAULT_MAX));			
		
		addProperty(PROP_MAJOR_TICK_STEP_HINT, new DoubleProperty("Major Tick Step Hint", 
				WidgetPropertyCategory.Display,DEFAULT_MAJOR_TICK_STEP_HINT));			
		
		addProperty(PROP_SHOW_MINOR_TICKS, new BooleanProperty("Show Minor Ticks", 
				WidgetPropertyCategory.Display, true));		
		
		addProperty(PROP_SHOW_SCALE, new BooleanProperty("Show Scale", 
				WidgetPropertyCategory.Display, true));		
		
		addProperty(PROP_LOG_SCALE, new BooleanProperty("Log Scale", 
				WidgetPropertyCategory.Display, false));
		
	}
	
	/**
	 * @return the value
	 */
	public Double getValue() {
		return (Double) getProperty(PROP_VALUE).getPropertyValue();
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
	public Double getMajorTickStepHint() {
		return (Double) getProperty(PROP_MAJOR_TICK_STEP_HINT).getPropertyValue();
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
	
}
