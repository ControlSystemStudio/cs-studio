package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
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
	private static final int DEFAULT_HEIGHT = 85;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 200;
	

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.meter"; //$NON-NLS-1$	
	
	public XMeterModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
		setBackgroundColor(new RGB(255, 255, 255));
		setBorderStyle(BorderStyle.RIDGED);
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(new ColorProperty(PROP_NEEDLE_COLOR, "Needle Color",
				WidgetPropertyCategory.Display, true, DEFAULT_NEEDLE_COLOR));	
		
		
		addProperty(new BooleanProperty(PROP_RAMP_GRADIENT, "Ramp Gradient", 
				WidgetPropertyCategory.Display, true, true));	
		
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
