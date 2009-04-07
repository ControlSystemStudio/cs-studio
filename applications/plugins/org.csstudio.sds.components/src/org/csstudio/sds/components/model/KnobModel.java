package org.csstudio.sds.components.model;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a knob widget model.
 * @author Xihui Chen
 */
public class KnobModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the knob color property. */
	public static final String PROP_KNOB_COLOR = "bulb_color"; //$NON-NLS-1$	
	
	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect3D"; //$NON-NLS-1$
	
	
	/** The ID of the effect show value label property. */
	public static final String PROP_SHOW_VALUE_LABEL = "show_value_label"; //$NON-NLS-1$
	
	/** The ID of the thumb Color property. */
	public static final String PROP_THUMB_COLOR = "thumbColor"; //$NON-NLS-1$
	
	/** The ID of the Ramp Gradient. */
	public static final String PROP_RAMP_GRADIENT = "ramp_gradient"; //$NON-NLS-1$
	
	/** The default value of the default knob color property. */
	private static final RGB DEFAULT_KNOB_COLOR = new RGB(243,243,243);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 173;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 173;
	
	/** The default value of the thumb color property. */
	private static final RGB DEFAULT_THUMB_COLOR = new RGB(127, 127, 127);
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.Knob"; //$NON-NLS-1$	
	
	public KnobModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
		addProperty(PROP_KNOB_COLOR, new ColorProperty("Knob Color",
				WidgetPropertyCategory.Display,DEFAULT_KNOB_COLOR));	
		
		addProperty(PROP_EFFECT3D, new BooleanProperty("3D Effect", 
				WidgetPropertyCategory.Display, true));
		
		addProperty(PROP_SHOW_VALUE_LABEL, new BooleanProperty("Show Value Label", 
				WidgetPropertyCategory.Display, true));		
		
		addProperty(PROP_THUMB_COLOR, new ColorProperty("Thumb Color",
				WidgetPropertyCategory.Display,DEFAULT_THUMB_COLOR));
		
		addProperty(PROP_RAMP_GRADIENT, new BooleanProperty("Ramp Gradient", 
				WidgetPropertyCategory.Display, true));	
		
		setPropertyDescription(PROP_SHOW_MARKERS, "Show Ramp");
	}	

	@Override
	public String getTypeID() {
		return ID;
	}		

	/**
	 * @return the knob color
	 */
	public RGB getKnobColor() {
		return (RGB) getProperty(PROP_KNOB_COLOR).getPropertyValue();
	}	
	
	/**
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isEffect3D() {
		return (Boolean) getProperty(PROP_EFFECT3D).getPropertyValue();
	}

	/**
	 * @return true if the widget would be painted with 3D effect, false otherwise
	 */
	public boolean isShowValueLabel() {
		return (Boolean) getProperty(PROP_SHOW_VALUE_LABEL).getPropertyValue();
	}
	
	/**
	 * Gets the RGB for thumb.
	 * @return The thumb color
	 */
	public RGB getThumbColor() {
		return (RGB) getProperty(PROP_THUMB_COLOR).getPropertyValue();
	}
}
