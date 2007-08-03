package org.csstudio.sds.cosywidgets.models;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.eclipse.swt.graphics.RGB;

/**
 * The meter model.
 * 
 * @author jbercic
 * 
 */
public final class ModifiedMeterModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "cosywidgets.modifiedmeter";

	/**
	 * Identifiers for the properties
	 */
	public static final String PROP_ANGLE = "prop.angle";
	public static final String PROP_INNANGLE = "prop.inner_angle";
	public static final String PROP_POINTERCOLOR = "prop.pointer_color";
	public static final String PROP_RADIUS = "prop.visible_radius";
	public static final String PROP_SCALERADIUS = "prop.scale_radius";
	public static final String PROP_MINSTEP = "prop.minor_step";
	public static final String PROP_MAJSTEP = "prop.major_step";
	public static final String PROP_MINVAL = "prop.minimum_value";
	public static final String PROP_MAXVAL = "prop.maximum_value";
	public static final String PROP_VALUE = "prop.value";
	public static final String PROP_SCALECOLOR = "prop.scale_color";
	public static final String PROP_SCALEWIDTH = "prop.scale_width";
	public static final String PROP_TEXTRADIUS = "prop.text_width";
	public static final String PROP_TRANSPARENT = "prop.transparency";
	public static final String PROP_MCOLOR = "prop.m_color";
	public static final String PROP_LOLOCOLOR = "prop.lolo_color";
	public static final String PROP_LOCOLOR = "prop.lo_color";
	public static final String PROP_HICOLOR = "prop.hi_color";
	public static final String PROP_HIHICOLOR = "prop.hihi_color";
	public static final String PROP_MBOUND = "prop.m_bound";
	public static final String PROP_LOLOBOUND = "prop.lolo_bound";
	public static final String PROP_LOBOUND = "prop.lo_bound";
	public static final String PROP_HIBOUND = "prop.hi_bound";
	public static final String PROP_HIHIBOUND = "prop.hihi_bound";
	
	public ModifiedMeterModel() {
		super();
		setWidth(90);
		setHeight(90);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		// add the necessary properties for the meter
		addProperty(PROP_ANGLE,new IntegerProperty("Display Angle",WidgetPropertyCategory.Display,90,1,359));
		//TODO: ta je odvisen od PROP_ANGLE
		addProperty(PROP_INNANGLE,new IntegerProperty("Inner Angle",WidgetPropertyCategory.Display,80,1,359));
		addProperty(PROP_POINTERCOLOR,new ColorProperty("Pointer Color",WidgetPropertyCategory.Display,new RGB(0,255,0)));
		addProperty(PROP_RADIUS,new DoubleProperty("Visible Radius",WidgetPropertyCategory.Display,0.15,0.0,1.0));
		addProperty(PROP_SCALERADIUS,new DoubleProperty("Scale Radius",WidgetPropertyCategory.Display,0.25,0.0,1.0));
		addProperty(PROP_MINSTEP,new DoubleProperty("Minor Scale Step",WidgetPropertyCategory.Behaviour,1.0));
		addProperty(PROP_MAJSTEP,new DoubleProperty("Major Scale Step",WidgetPropertyCategory.Behaviour,5.0));
		addProperty(PROP_MINVAL,new DoubleProperty("Minimum Value",WidgetPropertyCategory.Behaviour,0.0));
		addProperty(PROP_MAXVAL,new DoubleProperty("Maximum Value",WidgetPropertyCategory.Behaviour,10.0));
		//TODO: ta odvisen od min in max value
		addProperty(PROP_VALUE,new DoubleProperty("Value",WidgetPropertyCategory.Behaviour,0.0));
		addProperty(PROP_SCALECOLOR,new ColorProperty("Scale Color",WidgetPropertyCategory.Display,new RGB(0,0,0)));
		addProperty(PROP_SCALEWIDTH,new IntegerProperty("Scale Width",WidgetPropertyCategory.Display,1));
		addProperty(PROP_TEXTRADIUS,new DoubleProperty("Scale Text Radius",WidgetPropertyCategory.Display,0.25,0.0,1.0));
		addProperty(PROP_TRANSPARENT,new BooleanProperty("Transparent Background",WidgetPropertyCategory.Display,false));
		
		//background colors
		addProperty(PROP_MCOLOR,new ColorProperty("M Color",WidgetPropertyCategory.Display,new RGB(0,255,0)));
		addProperty(PROP_LOLOCOLOR,new ColorProperty("LOLO Color",WidgetPropertyCategory.Display,new RGB(255,0,0)));
		addProperty(PROP_LOCOLOR,new ColorProperty("LO Color",WidgetPropertyCategory.Display,new RGB(255,81,81)));
		addProperty(PROP_HICOLOR,new ColorProperty("HI Color",WidgetPropertyCategory.Display,new RGB(255,81,81)));
		addProperty(PROP_HIHICOLOR,new ColorProperty("HIHI Color",WidgetPropertyCategory.Display,new RGB(255,0,0)));
		
		//level boundaries
		addProperty(PROP_MBOUND,new DoubleProperty("M Boundary",WidgetPropertyCategory.Behaviour,6.0));
		addProperty(PROP_LOLOBOUND,new DoubleProperty("LOLO Boundary",WidgetPropertyCategory.Behaviour,2.0));
		addProperty(PROP_LOBOUND,new DoubleProperty("LO Boundary",WidgetPropertyCategory.Behaviour,4.0));
		addProperty(PROP_HIBOUND,new DoubleProperty("HI Boundary",WidgetPropertyCategory.Behaviour,8.0));
		addProperty(PROP_HIHIBOUND,new DoubleProperty("HIHI Boundary",WidgetPropertyCategory.Behaviour,10.0));
	}

	public int getAngle() {
		return (Integer) getProperty(PROP_ANGLE).getPropertyValue();
	}
	
	public int getInnerAngle() {
		return (Integer) getProperty(PROP_INNANGLE).getPropertyValue();
	}
	
	public RGB getPointerColor() {
		return (RGB) getProperty(PROP_POINTERCOLOR).getPropertyValue();
	}
	
	public double getVisibleRadius() {
		return (Double) getProperty(PROP_RADIUS).getPropertyValue();
	}
	
	public double getScaleRadius() {
		return (Double) getProperty(PROP_SCALERADIUS).getPropertyValue();
	}
	
	public double getMinorStep() {
		return (Double) getProperty(PROP_MINSTEP).getPropertyValue();
	}
	
	public double getMajorStep() {
		return (Double) getProperty(PROP_MAJSTEP).getPropertyValue();
	}
	
	public double getMaxValue() {
		return (Double) getProperty(PROP_MAXVAL).getPropertyValue();
	}
	
	public double getMinValue() {
		return (Double) getProperty(PROP_MINVAL).getPropertyValue();
	}
	
	public double getValue() {
		return (Double) getProperty(PROP_VALUE).getPropertyValue();
	}
	
	public RGB getScaleColor() {
		return (RGB) getProperty(PROP_SCALECOLOR).getPropertyValue();
	}
	
	public int getScaleWidth() {
		return (Integer) getProperty(PROP_SCALEWIDTH).getPropertyValue();
	}
	
	public double getTextRadius() {
		return (Double) getProperty(PROP_TEXTRADIUS).getPropertyValue();
	}
	
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	public RGB getMColor() {
		return (RGB) getProperty(PROP_MCOLOR).getPropertyValue();
	}
	
	public RGB getLOLOColor() {
		return (RGB) getProperty(PROP_LOLOCOLOR).getPropertyValue();
	}
	
	public RGB getLOColor() {
		return (RGB) getProperty(PROP_LOCOLOR).getPropertyValue();
	}
	
	public RGB getHIColor() {
		return (RGB) getProperty(PROP_HICOLOR).getPropertyValue();
	}
	
	public RGB getHIHIColor() {
		return (RGB) getProperty(PROP_HIHICOLOR).getPropertyValue();
	}
	
	public double getMBound() {
		return (Double) getProperty(PROP_MBOUND).getPropertyValue();
	}
	
	public double getLOLOBound() {
		return (Double) getProperty(PROP_LOLOBOUND).getPropertyValue();
	}
	
	public double getLOBound() {
		return (Double) getProperty(PROP_LOBOUND).getPropertyValue();
	}
	
	public double getHIBound() {
		return (Double) getProperty(PROP_HIBOUND).getPropertyValue();
	}
	
	public double getHIHIBound() {
		return (Double) getProperty(PROP_HIHIBOUND).getPropertyValue();
	}

	@Override
	public String getDoubleTestProperty() {
		return PROP_VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}
}
