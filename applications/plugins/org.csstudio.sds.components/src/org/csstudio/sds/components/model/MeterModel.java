package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.DoubleProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * The meter model.
 * 
 * @author jbercic
 * 
 */
public final class MeterModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.sds.components.Meter";

	/**
	 * Identifiers for the properties
	 */
	public static final String PROP_ANGLE = "angle";
	public static final String PROP_INNANGLE = "angle.inner";
	public static final String PROP_NEEDLECOLOR = "color.needle";
	public static final String PROP_RADIUS = "radius.visible";
	public static final String PROP_SCALERADIUS = "radius.scale";
	public static final String PROP_MINSTEP = "step.minor";
	public static final String PROP_MAJSTEP = "step.major";
	public static final String PROP_MINVAL = "value.minimum";
	public static final String PROP_MAXVAL = "value.maximum";
	public static final String PROP_VALUE = "value";
	public static final String PROP_SCALECOLOR = "color.scale";
	public static final String PROP_SCALEWIDTH = "width.scale";
	public static final String PROP_TEXTRADIUS = "radius.text";
	public static final String PROP_TRANSPARENT = "transparency";
	public static final String PROP_MCOLOR = "color.m";
	public static final String PROP_LOLOCOLOR = "color.lolo";
	public static final String PROP_LOCOLOR = "color.lo";
	public static final String PROP_HICOLOR = "color.hi";
	public static final String PROP_HIHICOLOR = "color.hihi";
	public static final String PROP_MBOUND = "bound.m";
	public static final String PROP_LOLOBOUND = "bound.lolo";
	public static final String PROP_LOBOUND = "bound.lo";
	public static final String PROP_HIBOUND = "bound.hi";
	public static final String PROP_HIHIBOUND = "bound.hihi";
	public static final String PROP_VALFONT = "font.values";
	public static final String PROP_CHANFONT = "font.channel";
	
	/**
	 * The ID of the precision property.
	 */
	public static final String PROP_PRECISION = "precision"; //$NON-NLS-1$
	
	/**
	 * Constructor.
	 */
	public MeterModel() {
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
		addProperty(PROP_INNANGLE,new IntegerProperty("Inner Angle",WidgetPropertyCategory.Display,80,1,359));
		addProperty(PROP_NEEDLECOLOR,new ColorProperty("Needle Color",WidgetPropertyCategory.Display,new RGB(0,255,0)));
		addProperty(PROP_RADIUS,new DoubleProperty("Visible Radius",WidgetPropertyCategory.Display,0.15,0.0,1.0));
		addProperty(PROP_TEXTRADIUS,new DoubleProperty("Scale Text Radius",WidgetPropertyCategory.Display,0.25,0.0,1.0));
		addProperty(PROP_TRANSPARENT,new BooleanProperty("Transparent Background",WidgetPropertyCategory.Display,false));
		
		addProperty(PROP_SCALERADIUS,new DoubleProperty("Scale Radius",WidgetPropertyCategory.Display,0.25,0.0,1.0));
		addProperty(PROP_MINSTEP,new DoubleProperty("Minor Scale Step",WidgetPropertyCategory.Behaviour,1.0));
		addProperty(PROP_MAJSTEP,new DoubleProperty("Major Scale Step",WidgetPropertyCategory.Behaviour,5.0));
		addProperty(PROP_SCALECOLOR,new ColorProperty("Scale Color",WidgetPropertyCategory.Display,new RGB(0,0,0)));
		addProperty(PROP_SCALEWIDTH,new IntegerProperty("Scale Width",WidgetPropertyCategory.Display,1));
		
		addProperty(PROP_MINVAL,new DoubleProperty("Minimum Value",WidgetPropertyCategory.Behaviour,0.0));
		addProperty(PROP_MAXVAL,new DoubleProperty("Maximum Value",WidgetPropertyCategory.Behaviour,10.0));
		addProperty(PROP_VALUE,new DoubleProperty("Value",WidgetPropertyCategory.Behaviour,0.0));
		
		//background colors
		addProperty(PROP_MCOLOR,new ColorProperty("Color M",WidgetPropertyCategory.Display,new RGB(0,255,0)));
		addProperty(PROP_LOLOCOLOR,new ColorProperty("Color LOLO",WidgetPropertyCategory.Display,new RGB(255,0,0)));
		addProperty(PROP_LOCOLOR,new ColorProperty("Color LO",WidgetPropertyCategory.Display,new RGB(255,81,81)));
		addProperty(PROP_HICOLOR,new ColorProperty("Color HI",WidgetPropertyCategory.Display,new RGB(255,81,81)));
		addProperty(PROP_HIHICOLOR,new ColorProperty("Color HIHI",WidgetPropertyCategory.Display,new RGB(255,0,0)));
		
		//level boundaries
		addProperty(PROP_MBOUND,new DoubleProperty("Boundary M",WidgetPropertyCategory.Behaviour,6.0));
		addProperty(PROP_LOLOBOUND,new DoubleProperty("Boundary LOLO",WidgetPropertyCategory.Behaviour,2.0));
		addProperty(PROP_LOBOUND,new DoubleProperty("Boundary LO",WidgetPropertyCategory.Behaviour,4.0));
		addProperty(PROP_HIBOUND,new DoubleProperty("Boundary HI",WidgetPropertyCategory.Behaviour,8.0));
		addProperty(PROP_HIHIBOUND,new DoubleProperty("Boundary HIHI",WidgetPropertyCategory.Behaviour,10.0));
		
		//font properties
		addProperty(PROP_VALFONT, new FontProperty("Values Font",WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE)));
		addProperty(PROP_CHANFONT, new FontProperty("Channel Font",WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE)));
		// precision
		addProperty(PROP_PRECISION, new IntegerProperty("Decimal places",
				WidgetPropertyCategory.Behaviour, 2, 0, 5));
	}

	public int getAngle() {
		return (Integer) getProperty(PROP_ANGLE).getPropertyValue();
	}
	
	public int getInnerAngle() {
		return (Integer) getProperty(PROP_INNANGLE).getPropertyValue();
	}
	
	public RGB getNeedleColor() {
		return (RGB) getProperty(PROP_NEEDLECOLOR).getPropertyValue();
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
	
	public FontData getValuesFont() {
		return (FontData) getProperty(PROP_VALFONT).getPropertyValue();
	}
	
	public FontData getChannelFont() {
		return (FontData) getProperty(PROP_CHANFONT).getPropertyValue();
	}

	@Override
	public String getDoubleTestProperty() {
		return PROP_VALUE;
	}
	
	/**
	 * Return the precision.
	 * 
	 * @return The precision.
	 */
	public int getPrecision() {
		return (Integer) getProperty(PROP_PRECISION).getPropertyValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}
}
