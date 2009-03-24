package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.components.internal.localization.Messages;


/**
 * This class defines a common widget model for any widget 
 * which has one scale and standard markers. 
 * Standard markers are comprised of LOLO, LO, HI, HIHI. 
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
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_SHOW_MINOR_TICKS = "showMinorTicks"; //$NON-NLS-1$
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_SHOW_MARKERS = "showMarkers"; //$NON-NLS-1$
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_SHOW_SCALE = "showScale"; //$NON-NLS-1$
	
	/** The ID of the log scale property. */
	public static final String PROP_LOG_SCALE = "logScale"; //$NON-NLS-1$	
	
	/** The ID of the lolo level property.*/
	public static final String PROP_LOLO_LEVEL = "loloLevel"; //$NON-NLS-1$
	
	/** The ID of the lo level property. */
	public static final String PROP_LO_LEVEL = "loLevel"; //$NON-NLS-1$
	
	/** The ID of the hi level property. */
	public static final String PROP_HI_LEVEL = "hiLevel"; //$NON-NLS-1$
	
	/** The ID of the hihi level property. */
	public static final String PROP_HIHI_LEVEL = "hihiLevel"; //$NON-NLS-1$		
	
	/** The default value of the value property. */
	private static final double DEFAULT_VALUE = 50;	
	
	/** The default value of the minimum property. */
	private static final double DEFAULT_MIN = 0;
	
	/** The default value of the maximum property. */
	private static final double DEFAULT_MAX = 100;	
	
	/** The default value of the levels property. */
	private static final double[] DEFAULT_LEVELS = new double[]{10, 20, 80, 90};	

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
		
		addProperty(PROP_SHOW_MINOR_TICKS, new BooleanProperty("Show Minor Ticks", 
				WidgetPropertyCategory.Display, true));		
		
		addProperty(PROP_SHOW_MARKERS, new BooleanProperty("Show Markers", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(PROP_SHOW_SCALE, new BooleanProperty("Show Scale", 
				WidgetPropertyCategory.Display, true));		
		
		addProperty(PROP_LOG_SCALE, new BooleanProperty("Log Scale", 
				WidgetPropertyCategory.Display, false));
		
		addProperty(PROP_LOLO_LEVEL, new DoubleProperty("Level LOLO", 
				WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[0]));
		addProperty(PROP_LO_LEVEL, new DoubleProperty("Level LO", 
				WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[1]));
		addProperty(PROP_HI_LEVEL, new DoubleProperty("Level HI", 
				WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[2]));
		addProperty(PROP_HIHI_LEVEL, new DoubleProperty("Level HIHI", 
				WidgetPropertyCategory.Behaviour,DEFAULT_LEVELS[3]));
		
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
	 * Gets the lolo level for this model.
	 * @return double
	 * 				The lolo level
	 */
	public double getLoloLevel() {
		return (Double) getProperty(PROP_LOLO_LEVEL).getPropertyValue();
	}
	
	/**
	 * Gets the lo level for this model.
	 * @return double
	 * 				The lo level
	 */
	public double getLoLevel() {
		return (Double) getProperty(PROP_LO_LEVEL).getPropertyValue();
	}
	
	/**
	 * Gets the hi level for this model.
	 * @return double
	 * 				The hi level
	 */
	public double getHiLevel() {
		return (Double) getProperty(PROP_HI_LEVEL).getPropertyValue();
	}
	
	/**
	 * Gets the minimum value for this model.
	 * @return double
	 * 				The minimum value
	 */
	public double getHihiLevel() {
		return (Double) getProperty(PROP_HIHI_LEVEL).getPropertyValue();
	}
	

	/**
	 * @return true if the minor ticks should be shown, false otherwise
	 */
	public boolean isShowMinorTicks() {
		return (Boolean) getProperty(PROP_SHOW_MINOR_TICKS).getPropertyValue();
	}
	
	/**
	 * @return true if the minor ticks should be shown, false otherwise
	 */
	public boolean isShowMarkers() {
		return (Boolean) getProperty(PROP_SHOW_MARKERS).getPropertyValue();
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
