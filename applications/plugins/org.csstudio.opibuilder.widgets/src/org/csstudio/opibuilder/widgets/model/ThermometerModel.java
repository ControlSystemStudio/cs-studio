package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a thermometer widget model.
 * @author Xihui Chen
 */
public class ThermometerModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$	
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_SHOW_BULB = "showBulb"; //$NON-NLS-1$
	
	/** The ID of the fahrenheit property. */	
	public static final String PROP_FAHRENHEIT = "fahrenheit"; //$NON-NLS-1$
	
	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$
	
	/**
	 * The ID of the fillbackground-Color property.
	 */
	public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor";
	
	
	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_FILL_COLOR = new RGB(255,0,0);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 200;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 100;
	
	/**
	 * The default value of the fillbackground color property. 
	 */
	private static final RGB DEFAULT_FILLBACKGROUND_COLOR = new RGB(210,210,210);
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.thermometer"; //$NON-NLS-1$	
	
	public ThermometerModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(new ColorProperty(PROP_FILL_COLOR, "Fill Color",
				WidgetPropertyCategory.Display, true, DEFAULT_FILL_COLOR));	
		
		addProperty(new BooleanProperty(PROP_SHOW_BULB, "Show Bulb", 
				WidgetPropertyCategory.Display, true, true));	
		
		addProperty(new BooleanProperty(PROP_FAHRENHEIT, "Fahrenheit?", 
				WidgetPropertyCategory.Display, true, false));	
		
		addProperty(new ColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground",
				WidgetPropertyCategory.Display, true, DEFAULT_FILLBACKGROUND_COLOR));
		
		addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect", 
				WidgetPropertyCategory.Display, true, true));	
		
		setPropertyValue(PROP_LO_COLOR, new RGB(255, 128, 0));
		setPropertyValue(PROP_HI_COLOR, new RGB(255, 128, 0));
		
	}
	
	

	@Override
	public String getTypeID() {
		return ID;
	}		

	/**
	 * @return the fill color
	 */
	public RGB getFillColor() {
		return (RGB) getProperty(PROP_FILL_COLOR).getPropertyValue();
	}	
	
	/**
	 * @return true if the bulb should be shown, false otherwise
	 */
	public boolean isShowBulb() {
		return (Boolean) getProperty(PROP_SHOW_BULB).getPropertyValue();
	}

	/**
	 * @return true if unit is in fahrenheit, false otherwise
	 */
	public boolean isFahrenheit() {
		return (Boolean) getProperty(PROP_FAHRENHEIT).getPropertyValue();	
	}	

	/**
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}
	
	/**
	 * Gets the RGB for fillbackground.
	 * @return The fillbackground color
	 */
	public RGB getFillbackgroundColor() {
		return (RGB) getProperty(PROP_FILLBACKGROUND_COLOR).getPropertyValue();
	}
	
}
