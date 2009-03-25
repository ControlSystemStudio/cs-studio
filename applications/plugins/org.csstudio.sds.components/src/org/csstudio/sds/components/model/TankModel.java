package org.csstudio.sds.components.model;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a tank widget model.
 * @author Xihui Chen
 */
public class TankModel extends AbstractScaledWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$	
	
	/** The ID of the show minor ticks property. */
	public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$
	
	/**
	 * The ID of the fillbackground-Color property.
	 */
	public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor";
	
	
	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_FILL_COLOR = new RGB(0,0,255);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 200;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 100;
	
	/**
	 * The default value of the fillbackground color property. 
	 */
	private static final RGB DEFAULT_FILLBACKGROUND_COLOR = new RGB(192, 192, 192);
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.Tank"; //$NON-NLS-1$	
	
	public TankModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
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
	public RGB getFillColor() {
		return (RGB) getProperty(PROP_FILL_COLOR).getPropertyValue();
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
	public RGB getFillbackgroundColor() {
		return (RGB) getProperty(PROP_FILLBACKGROUND_COLOR).getPropertyValue();
	}
	
}
