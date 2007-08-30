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
	 * The ID of the <i>transparent</i> property.
	 */
	public static final String PROP_TRANSPARENT = "transparent_background";
	/**
	 * The IDs of the <i>startangle</i> property.
	 */
	public static final String PROP_STARTANGLE = "start_angle";
	/**
	 * The IDs of the <i>angle</i> property.
	 */
	public static final String PROP_ANGLE = "angle";
	/**
	 * The IDs of the <i>linewidth</i> property.
	 */
	public static final String PROP_LINEWIDTH = "linewidth";
	/**
	 * The IDs of the <i>filled</i> property.
	 */
	public static final String PROP_FILLED = "filled";
	/**
	 * The IDs of the <i>fillcolor</i> property.
	 */
	public static final String PROP_FILLCOLOR = "color.fill";

	/**
	 * Constructor.
	 */
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

	/**
	 * Returns the transparent state of the background.
	 * @return true, if the background is transparent, false otherwise
	 */
	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	/**
	 * Returns the value for the start angle. 
	 * @return The value for the start angle
	 */
	public int getStartAngle() {
		return (Integer) getProperty(PROP_STARTANGLE).getPropertyValue();
	}
	
	/**
	 * Returns the value for the angle.
	 * @return The value for the angle
	 */
	public int getAngle() {
		return (Integer) getProperty(PROP_ANGLE).getPropertyValue();
	}
	
	/**
	 * Returns the width of the arc.
	 * @return The width of the arc
	 */
	public int getLineWidth() {
		return (Integer) getProperty(PROP_LINEWIDTH).getPropertyValue();
	}
	
	/**
	 * Returns the fill state of the arc.
	 * @return true, if the arc should be filled, false otherwise
	 */
	public boolean getFill() {
		return (Boolean) getProperty(PROP_FILLED).getPropertyValue();
	}
	
	/**
	 * Returns the fill color of the arc.
	 * @return The fill color of the arc
	 */
	public RGB getFillColor() {
		return (RGB) getProperty(PROP_FILLCOLOR).getPropertyValue();
	}
}
