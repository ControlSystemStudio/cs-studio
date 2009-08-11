package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a gauge widget model.
 * @author Xihui Chen
 */
public class GaugeModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_NEEDLE_COLOR = "needle_color"; //$NON-NLS-1$	
	
	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$

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
	public static final String ID = "org.csstudio.opibuilder.widgets.gauge"; //$NON-NLS-1$	
	
	public GaugeModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,255,0));
		setBackgroundColor(new RGB(64,128,128));
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(new ColorProperty(PROP_NEEDLE_COLOR, "Needle Color",
				WidgetPropertyCategory.Display, true, DEFAULT_NEEDLE_COLOR));	
		
		addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect", 
				WidgetPropertyCategory.Display, true, true));	
		
		addProperty(new BooleanProperty(PROP_RAMP_GRADIENT, "Ramp Gradient", 
				WidgetPropertyCategory.Display, true, true));	
		
		setPropertyDescription(PROP_SHOW_MARKERS, "Show Ramp");
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
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}
	
	/**
	 * @return true if the ramp is gradient, false otherwise
	 */
	public boolean isRampGradient() {
		return (Boolean) getProperty(PROP_RAMP_GRADIENT).getPropertyValue();
	}
	
	
}
