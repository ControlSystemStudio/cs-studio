package org.csstudio.sds.components.model;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a tank widget model.
 * @author Xihui Chen
 */
public class TankModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$	
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$
	
	/**
	 * The ID of the fillbackground-Color property.
	 */
	public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor";
	
	
	/** The default value of the default fill color property. */
	private static final String DEFAULT_FILL_COLOR = "#0000ff";
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 200;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 100;
	
	/**
	 * The default value of the fillbackground color property. 
	 */
	private static final String DEFAULT_FILLBACKGROUND_COLOR = "#C0C0C0";
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.Tank"; //$NON-NLS-1$	
	
	public TankModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor("#000000");
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(PROP_FILL_COLOR, new ColorProperty("Fill Color",
				WidgetPropertyCategory.Display,DEFAULT_FILL_COLOR));	
		
		addProperty(PROP_EFFECT3D, new BooleanProperty("3D Effect", 
				WidgetPropertyCategory.Display, true));	

		
		addProperty(PROP_FILLBACKGROUND_COLOR, new ColorProperty("Color Fillbackground",
				WidgetPropertyCategory.Display,DEFAULT_FILLBACKGROUND_COLOR));
		
		setPropertyValue(PROP_LO_COLOR, "#FF8000");
		setPropertyValue(PROP_HI_COLOR, "#FF8000");		
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
	 * @return the fill color
	 */
	public ColorProperty getFillColor() {
		return (ColorProperty) getProperty(PROP_FILL_COLOR);
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
	public ColorProperty getFillbackgroundColor() {
		return (ColorProperty) getProperty(PROP_FILLBACKGROUND_COLOR);
	}
	
}
