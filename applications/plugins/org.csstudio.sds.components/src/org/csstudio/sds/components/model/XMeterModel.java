package org.csstudio.sds.components.model;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a XMeter widget model.
 * @author Xihui Chen
 */
public class XMeterModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_NEEDLE_COLOR = "needle_color"; //$NON-NLS-1$	
	
	/** The ID of the Ramp Gradient. */
	public static final String PROP_RAMP_GRADIENT = "ramp_gradient"; //$NON-NLS-1$

	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_NEEDLE_COLOR = new RGB(255,0,0);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 200;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 200;
	

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.XMeter"; //$NON-NLS-1$	
	
	public XMeterModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(PROP_NEEDLE_COLOR, new ColorProperty("Needle Color",
				WidgetPropertyCategory.Display,DEFAULT_NEEDLE_COLOR));	
		
		
		addProperty(PROP_RAMP_GRADIENT, new BooleanProperty("Ramp Gradient", 
				WidgetPropertyCategory.Display, true));	
		
		setPropertyDescription(PROP_SHOW_MARKERS, "Show Ramp");		
		setPropertyValue(PROP_TRANSPARENT, false);
	}	

	@Override
	public String getTypeID() {
		return ID;
	}		

	/**
	 * @return the needle color
	 */
	public RGB getNeedleColor() {
		return (RGB) getProperty(PROP_NEEDLE_COLOR).getPropertyValue();
	}	
	
	/**
	 * @return true if the widget is in horizontal orientation, false otherwise
	 */
	public boolean isRampGradient() {
		return (Boolean) getProperty(PROP_RAMP_GRADIENT).getPropertyValue();
	}
	
	
}
