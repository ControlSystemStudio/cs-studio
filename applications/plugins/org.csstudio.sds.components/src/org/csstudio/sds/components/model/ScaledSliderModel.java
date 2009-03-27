package org.csstudio.sds.components.model;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a scaled slider widget model.
 * @author Xihui Chen
 */
public class ScaledSliderModel extends AbstractScaledWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$	
	
	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$
	
	/** The ID of the horizontal property. */
	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	
	/** The ID of the fillbackground-Color property. */
	public static final String PROP_FILLBACKGROUND_COLOR = "fillbackgroundColor"; //$NON-NLS-1$
	
	/** The ID of the thumb Color property. */
	public static final String PROP_THUMB_COLOR = "thumbColor"; //$NON-NLS-1$
	
	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_FILL_COLOR = new RGB(0,0,255);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 200;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 100;
	
	/** The default value of the fillbackground color property. */
	private static final RGB DEFAULT_FILLBACKGROUND_COLOR = new RGB(200, 200, 200);
	
	/** The default value of the thumb color property. */
	private static final RGB DEFAULT_THUMB_COLOR = new RGB(172, 172, 172);
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.ScaledSlider"; //$NON-NLS-1$	
	
	public ScaledSliderModel() {
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
		
		addProperty(PROP_HORIZONTAL, new BooleanProperty("Horizontal", 
				WidgetPropertyCategory.Display, false));	
		
		addProperty(PROP_FILLBACKGROUND_COLOR, new ColorProperty("Color Fillbackground",
				WidgetPropertyCategory.Display,DEFAULT_FILLBACKGROUND_COLOR));
		
		addProperty(PROP_THUMB_COLOR, new ColorProperty("Thumb Color",
				WidgetPropertyCategory.Display,DEFAULT_THUMB_COLOR));		
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
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
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
	public RGB getFillbackgroundColor() {
		return (RGB) getProperty(PROP_FILLBACKGROUND_COLOR).getPropertyValue();
	}
	
	/**
	 * Gets the RGB for thumb.
	 * @return The thumb color
	 */
	public RGB getThumbColor() {
		return (RGB) getProperty(PROP_THUMB_COLOR).getPropertyValue();
	}
}
