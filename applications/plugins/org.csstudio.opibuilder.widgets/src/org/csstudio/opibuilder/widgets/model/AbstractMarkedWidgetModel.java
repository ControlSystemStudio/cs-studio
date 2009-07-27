package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a common widget model for any widget 
 * which has one scale and standard markers. 
 * Standard markers are comprised of LOLO, LO, HI, HIHI. 
 * @author Xihui Chen
 */
public abstract class AbstractMarkedWidgetModel extends AbstractScaledWidgetModel {
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_SHOW_MARKERS = "showMarkers"; //$NON-NLS-1$	

	/** The ID of the lolo level property.*/
	public static final String PROP_LOLO_LEVEL = "loloLevel"; //$NON-NLS-1$
	
	/** The ID of the lo level property. */
	public static final String PROP_LO_LEVEL = "loLevel"; //$NON-NLS-1$
	
	/** The ID of the hi level property. */
	public static final String PROP_HI_LEVEL = "hiLevel"; //$NON-NLS-1$
	
	/** The ID of the hihi level property. */
	public static final String PROP_HIHI_LEVEL = "hihiLevel"; //$NON-NLS-1$		
	
	
	/** The ID of the show lolo property.*/
	public static final String PROP_SHOW_LOLO = "showLOLO"; //$NON-NLS-1$
	
	/** The ID of the show lo property. */
	public static final String PROP_SHOW_LO = "showLO"; //$NON-NLS-1$
	
	/** The ID of the show hi property. */
	public static final String PROP_SHOW_HI = "showHI"; //$NON-NLS-1$
	
	/** The ID of the show hihi property. */
	public static final String PROP_SHOW_HIHI = "showHIHI"; //$NON-NLS-1$		
	
	/** The ID of the lolo color property.*/
	public static final String PROP_LOLO_COLOR = "loloColor"; //$NON-NLS-1$
	
	/** The ID of the lo color property. */
	public static final String PROP_LO_COLOR = "loColor"; //$NON-NLS-1$
	
	/** The ID of the hi color property. */
	public static final String PROP_HI_COLOR = "hiColor"; //$NON-NLS-1$
	
	/** The ID of the hihi color property. */
	public static final String PROP_HIHI_COLOR = "hihiColor"; //$NON-NLS-1$		

	
	/** The default value of the levels property. */
	private static final double[] DEFAULT_LEVELS = new double[]{10, 20, 80, 90};	
	
	/** The default color of the lolo color property. */
	private static final RGB DEFAULT_LOLO_COLOR = new RGB(255,0,0);
	/** The default color of the lo color property. */
	private static final RGB DEFAULT_LO_COLOR = new RGB(255, 255 ,0);
	/** The default color of the hi color property. */
	private static final RGB DEFAULT_HI_COLOR = new RGB(255, 255,0);
	/** The default color of the hihi color property. */
	private static final RGB DEFAULT_HIHI_COLOR = new RGB(255,0,0);
	

	@Override
	protected void configureProperties() {	
		
		super.configureProperties();
		addProperty(new BooleanProperty(PROP_SHOW_MARKERS, "Show Markers", 
				WidgetPropertyCategory.Display, true, true));			
		
		addProperty(new DoubleProperty(PROP_LOLO_LEVEL, "Level LOLO", 
				WidgetPropertyCategory.Behavior, true, DEFAULT_LEVELS[0]));
		addProperty(new DoubleProperty(PROP_LO_LEVEL, "Level LO", 
				WidgetPropertyCategory.Behavior, true, DEFAULT_LEVELS[1]));
		addProperty(new DoubleProperty(PROP_HI_LEVEL, "Level HI", 
				WidgetPropertyCategory.Behavior, true, DEFAULT_LEVELS[2]));
		addProperty(new DoubleProperty(PROP_HIHI_LEVEL, "Level HIHI", 
				WidgetPropertyCategory.Behavior, true, DEFAULT_LEVELS[3]));
		
		addProperty(new BooleanProperty(PROP_SHOW_LOLO, "Show LOLO", 
				WidgetPropertyCategory.Display, true, true));		
		addProperty(new BooleanProperty(PROP_SHOW_LO, "Show LO", 
				WidgetPropertyCategory.Display, true, true));	
		addProperty(new BooleanProperty(PROP_SHOW_HI, "Show HI", 
				WidgetPropertyCategory.Display, true, true));	
		addProperty(new BooleanProperty(PROP_SHOW_HIHI, "Show HIHI", 
				WidgetPropertyCategory.Display, true, true));
		
		addProperty(new ColorProperty(PROP_LOLO_COLOR, "Color LOLO ",
				WidgetPropertyCategory.Display, true, DEFAULT_LOLO_COLOR));
		addProperty(new ColorProperty(PROP_LO_COLOR, "Color LO",
				WidgetPropertyCategory.Display, true, DEFAULT_LO_COLOR));
		addProperty(new ColorProperty(PROP_HI_COLOR, "Color HI",
				WidgetPropertyCategory.Display, true, DEFAULT_HI_COLOR));
		addProperty(new ColorProperty(PROP_HIHI_COLOR, "Color HIHI",
				WidgetPropertyCategory.Display, true, DEFAULT_HIHI_COLOR));		
		
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
	 * Gets the hihi level of this model.
	 * @return double
	 * 				The hihi level 
	 */
	public double getHihiLevel() {
		return (Double) getProperty(PROP_HIHI_LEVEL).getPropertyValue();
	}
	
	
	/**
	 * @return the lolo color
	 */
	public RGB getLoloColor() {
		return (RGB) getProperty(PROP_LOLO_COLOR).getPropertyValue();
	}	
	
	/**
	 * @return the lo color
	 */
	public RGB getLoColor() {
		return (RGB) getProperty(PROP_LO_COLOR).getPropertyValue();
	}	
	/**
	 * @return the hi color
	 */
	public RGB getHiColor() {
		return (RGB) getProperty(PROP_HI_COLOR).getPropertyValue();
	}	
	/**
	 * @return the hihi color
	 */
	public RGB getHihiColor() {
		return (RGB) getProperty(PROP_HIHI_COLOR).getPropertyValue();
	}	

	
	

	
	/**
	 * @return true if the minor ticks should be shown, false otherwise
	 */
	public boolean isShowMarkers() {
		return (Boolean) getProperty(PROP_SHOW_MARKERS).getPropertyValue();
	}

	/**
	 * @return true if the lolo marker should be shown, false otherwise
	 */
	public boolean isShowLolo() {
		return (Boolean) getProperty(PROP_SHOW_LOLO).getPropertyValue();
	}
	
	/**
	 * @return true if the lo marker should be shown, false otherwise
	 */
	public boolean isShowLo() {
		return (Boolean) getProperty(PROP_SHOW_LO).getPropertyValue();
	}
	
	/**
	 * @return true if the hi marker should be shown, false otherwise
	 */
	public boolean isShowHi() {
		return (Boolean) getProperty(PROP_SHOW_HI).getPropertyValue();
	}
	
	/**
	 * @return true if the hihi marker should be shown, false otherwise
	 */
	public boolean isShowHihi() {
		return (Boolean) getProperty(PROP_SHOW_HIHI).getPropertyValue();
	}
	
	
}
