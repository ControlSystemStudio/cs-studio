package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;


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
	private static final String DEFAULT_FILL_COLOR = "#ff0000";
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 200;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 100;
	
	/**
	 * The default value of the fillbackground color property. 
	 */
	private static final String DEFAULT_FILLBACKGROUND_COLOR = "#D2D2D2";
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.Thermometer"; //$NON-NLS-1$	
	
	public ThermometerModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setColor(AbstractWidgetModel.PROP_COLOR_FOREGROUND,"#000000");
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addColorProperty(PROP_FILL_COLOR, "Fill Color",
				WidgetPropertyCategory.Display,DEFAULT_FILL_COLOR);	
		
		addProperty(PROP_SHOW_BULB, new BooleanProperty("Show Bulb", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(PROP_FAHRENHEIT, new BooleanProperty("Fahrenheit?", 
				WidgetPropertyCategory.Display, false));	
		
		addColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground",
				WidgetPropertyCategory.Display,DEFAULT_FILLBACKGROUND_COLOR);
		
		addProperty(PROP_EFFECT3D, new BooleanProperty("3D Effect", 
				WidgetPropertyCategory.Display, true));	
		
		setColor(PROP_LO_COLOR, "#FF8000");
		setColor(PROP_HI_COLOR, "#FF8000");
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createTooltipParameter(PROP_ALIASES)+"\n");
		buffer.append("Value:\t");
		buffer.append(createTooltipParameter(PROP_VALUE)+"\n");
		return buffer.toString();
	}

	@Override
	public String getTypeID() {
		return ID;
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
}
