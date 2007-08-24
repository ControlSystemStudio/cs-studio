package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.ColorProperty;
import org.eclipse.swt.graphics.RGB;

/**
 * An arc widget model.
 * 
 * @author jbercic
 * 
 */
public final class ArcModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.sds.components.Arc";
	
	/**
	 * The IDs of the properties.
	 */
	public static final String PROP_TRANSPARENT = "transparent_background";
	public static final String PROP_STARTANGLE = "start_angle";
	public static final String PROP_ANGLE = "angle";
	public static final String PROP_LINEWIDTH = "linewidth";
	public static final String PROP_FILLED = "filled";
	public static final String PROP_FILLCOLOR = "color.fill";

	public ArcModel () {
		setWidth(50);
		setHeight(50);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_TRANSPARENT, new BooleanProperty("Transparent Background",WidgetPropertyCategory.Display,true));
		addProperty(PROP_STARTANGLE, new IntegerProperty("Start Angle",WidgetPropertyCategory.Display,0,0,360));
		addProperty(PROP_ANGLE, new IntegerProperty("Angle",WidgetPropertyCategory.Display,90,0,360));
		addProperty(PROP_LINEWIDTH, new IntegerProperty("Line Width",WidgetPropertyCategory.Display,1));
		addProperty(PROP_FILLED, new BooleanProperty("Filled",WidgetPropertyCategory.Display,false));
		addProperty(PROP_FILLCOLOR, new ColorProperty("Fill Color",WidgetPropertyCategory.Display,new RGB(255,0,0)));
	}

	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	public int getStartAngle() {
		return (Integer) getProperty(PROP_STARTANGLE).getPropertyValue();
	}
	
	public int getAngle() {
		return (Integer) getProperty(PROP_ANGLE).getPropertyValue();
	}
	
	public int getLineWidth() {
		return (Integer) getProperty(PROP_LINEWIDTH).getPropertyValue();
	}
	
	public boolean getFill() {
		return (Boolean) getProperty(PROP_FILLED).getPropertyValue();
	}
	
	public RGB getFillColor() {
		return (RGB) getProperty(PROP_FILLCOLOR).getPropertyValue();
	}
}
