package org.csstudio.opibuilder.widgets.model;


import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.DoubleProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.OPIColor;
import org.eclipse.swt.graphics.RGB;


/**
 * This class defines a scaled slider widget model.
 * @author Xihui Chen
 */
public class ScaledSliderModel extends AbstractMarkedWidgetModel{	
	
	/** The ID of the fill color property. */
	public static final String PROP_FILL_COLOR = "fill_color"; //$NON-NLS-1$	
	
	/** The ID of the effect 3D property. */
	public static final String PROP_EFFECT3D = "effect_3d"; //$NON-NLS-1$
	
	/** The ID of the horizontal property. */
	public static final String PROP_HORIZONTAL = "horizontal"; //$NON-NLS-1$
	
	/** The ID of the fillbackground-Color property. */
	public static final String PROP_FILLBACKGROUND_COLOR = "color_fillbackground"; //$NON-NLS-1$
	
	/** The ID of the thumb Color property. */
	public static final String PROP_THUMB_COLOR = "thumb_color"; //$NON-NLS-1$
	
	/**
	 * The ID of the increment property.
	 */
	public static final String PROP_STEP_INCREMENT = "step_increment"; //$NON-NLS-1$
	
	/** The amount the scrollbar will move when the page up or page down areas are
	pressed.*/
	public static final String PROP_PAGE_INCREMENT = "page_increment"; //$NON-NLS-1$		
	
	
//	/**
//	 * The ID of the pv name property.
//	 */
//	public static final String PROP_CONTROL_PV= "control_pv"; //$NON-NLS-1$
//	
//	/**
//	 * The ID of the pv value property.
//	 */
//	public static final String PROP_CONTROL_PV_VALUE= "control_pv_value"; //$NON-NLS-1$
//	
	/** The default value of the default fill color property. */
	private static final RGB DEFAULT_FILL_COLOR = new RGB(0,0,255);
	
	/** The default value of the height property. */	
	private static final int DEFAULT_HEIGHT = 200;
	
	/** The default value of the width property. */
	private static final int DEFAULT_WIDTH = 120;
	
	/** The default value of the fillbackground color property. */
	private static final RGB DEFAULT_FILLBACKGROUND_COLOR = new RGB(200, 200, 200);
	
	/** The default value of the thumb color property. */
	private static final RGB DEFAULT_THUMB_COLOR = new RGB(172, 172, 172);
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.scaledslider"; //$NON-NLS-1$	
	
	public ScaledSliderModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setForegroundColor(new RGB(0,0,0));
	}	

	@Override
	protected void configureProperties() {
		super.configureProperties();		
//		addPVProperty(new StringProperty(PROP_CONTROL_PV, "Control PV", WidgetPropertyCategory.Basic,
//		""), new PVValueProperty(PROP_CONTROL_PV_VALUE, null));
		
		addProperty(new ColorProperty(PROP_FILL_COLOR, "Fill Color",
				WidgetPropertyCategory.Display,DEFAULT_FILL_COLOR));	
		
		addProperty(new BooleanProperty(PROP_EFFECT3D, "3D Effect", 
				WidgetPropertyCategory.Display, true));	
		
		addProperty(new BooleanProperty(PROP_HORIZONTAL, "Horizontal", 
				WidgetPropertyCategory.Display, false));	
		
		addProperty(new ColorProperty(PROP_FILLBACKGROUND_COLOR, "Color Fillbackground",
				WidgetPropertyCategory.Display,DEFAULT_FILLBACKGROUND_COLOR));
		
		addProperty(new ColorProperty(PROP_THUMB_COLOR, "Thumb Color",
				WidgetPropertyCategory.Display,DEFAULT_THUMB_COLOR));	
		
		addProperty(new DoubleProperty(PROP_STEP_INCREMENT, "Step_Increment",
				WidgetPropertyCategory.Behavior, 1.0));
		

		addProperty(new DoubleProperty(PROP_PAGE_INCREMENT, "Page_Increment",
				WidgetPropertyCategory.Behavior, 5));
		
		setPropertyValue(PROP_LO_COLOR, new OPIColor(255, 128, 0));
		setPropertyValue(PROP_HI_COLOR, new OPIColor(255, 128, 0));
		//setPropertyDescription(PROP_PVNAME, "Readback PV");
		
	}	

	@Override
	public String getTypeID() {
		return ID;
	}		

	/**
	 * @return the fill color
	 */
	public RGB getFillColor() {
		return getRGBFromColorProperty(PROP_FILL_COLOR);
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
		return getRGBFromColorProperty(PROP_FILLBACKGROUND_COLOR);
	}
	
	/**
	 * Gets the RGB for thumb.
	 * @return The thumb color
	 */
	public RGB getThumbColor() {
		return getRGBFromColorProperty(PROP_THUMB_COLOR);
	}
	
	/**
	 * Return the increment value.
	 * 
	 * @return The increment value.
	 */
	public double getStepIncrement() {
		return (Double) getProperty(PROP_STEP_INCREMENT).getPropertyValue();
	}
	
	public double getPageIncrement() {
		return (Double) getProperty(PROP_PAGE_INCREMENT).getPropertyValue();
	}
}
