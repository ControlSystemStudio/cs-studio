package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * This class defines a common widget model for any widget 
 * which has a scale. 
 * @author Xihui Chen
 */
public abstract class AbstractScaledWidgetModel extends AbstractWidgetModel {
	
	
	/** The ID of the <i>transparent</i> property. */
	public static final String PROP_TRANSPARENT = "transparency";	
	
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
	
	/**
	 * The ID of the pv name property.
	 */
	public static final String PROP_PV_NAME= "pv_name"; //$NON-NLS-1$
	
	/**
	 * The ID of the pv value property.
	 */
	public static final String PROP_PV_VALUE= "pv_value"; //$NON-NLS-1$

	@Override
	protected void configureProperties() {
		
		addPVProperty(new StringProperty(PROP_PV_NAME, "PV Name", WidgetPropertyCategory.Behavior,
				true, ""), new PVValueProperty(PROP_PV_VALUE, null));
		
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent Background",
				WidgetPropertyCategory.Display, true, true));
		
		
		addProperty(new DoubleProperty(PROP_MIN, "Minimum", 
				WidgetPropertyCategory.Behavior, true, DEFAULT_MIN));
		
		addProperty(new DoubleProperty(PROP_MAX, "Maximum", 
				WidgetPropertyCategory.Behavior, true, DEFAULT_MAX));			
		
		addProperty(new DoubleProperty(PROP_MAJOR_TICK_STEP_HINT, "Major Tick Step Hint", 
				WidgetPropertyCategory.Display, true, DEFAULT_MAJOR_TICK_STEP_HINT, 1, 1000));			
		
		addProperty(new BooleanProperty(PROP_SHOW_MINOR_TICKS, "Show Minor Ticks", 
				WidgetPropertyCategory.Display, true, true));		
		
		addProperty(new BooleanProperty(PROP_SHOW_SCALE, "Show Scale", 
				WidgetPropertyCategory.Display, true, true));		
		
		addProperty(new BooleanProperty(PROP_LOG_SCALE, "Log Scale", 
				WidgetPropertyCategory.Display, true,false));
		
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
