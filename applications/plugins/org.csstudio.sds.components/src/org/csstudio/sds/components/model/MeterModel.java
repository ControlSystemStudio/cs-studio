/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.util.ColorAndFontUtil;

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
		addIntegerProperty(PROP_ANGLE, "Display Angle", WidgetPropertyCategory.Display, 90, 1, 359);
		addIntegerProperty(PROP_INNANGLE, "Inner Angle", WidgetPropertyCategory.Display, 80, 1, 359);
		addColorProperty(PROP_NEEDLECOLOR, "Needle Color", WidgetPropertyCategory.Display, "#00ff00");
		addDoubleProperty(PROP_RADIUS, "Visible Radius", WidgetPropertyCategory.Display, 0.15, 0.0, 1.0);
		addDoubleProperty(PROP_TEXTRADIUS, "Scale Text Radius", WidgetPropertyCategory.Display, 0.25, 0.0, 1.0);
		addBooleanProperty(PROP_TRANSPARENT, "Transparent Background", WidgetPropertyCategory.Display, false);

		addDoubleProperty(PROP_SCALERADIUS, "Scale Radius", WidgetPropertyCategory.Display, 0.25, 0.0, 1.0);
		addDoubleProperty(PROP_MINSTEP, "Minor Scale Step", WidgetPropertyCategory.Behaviour, 1.0);
		addDoubleProperty(PROP_MAJSTEP, "Major Scale Step", WidgetPropertyCategory.Behaviour, 5.0);
		addColorProperty(PROP_SCALECOLOR, "Scale Color", WidgetPropertyCategory.Display, "#000000");
		addIntegerProperty(PROP_SCALEWIDTH, "Scale Width", WidgetPropertyCategory.Display, 1);

		addDoubleProperty(PROP_MINVAL, "Minimum Value", WidgetPropertyCategory.Behaviour, 0.0);
		addDoubleProperty(PROP_MAXVAL, "Maximum Value", WidgetPropertyCategory.Behaviour, 10.0);
		addDoubleProperty(PROP_VALUE, "Value", WidgetPropertyCategory.Behaviour, 0.0);

		// background colors
		addColorProperty(PROP_MCOLOR, "Color M", WidgetPropertyCategory.Display, "#00ff00");
		addColorProperty(PROP_LOLOCOLOR, "Color LOLO", WidgetPropertyCategory.Display, "#ff0000");
		addColorProperty(PROP_LOCOLOR, "Color LO", WidgetPropertyCategory.Display, "#ff5151");
		addColorProperty(PROP_HICOLOR, "Color HI", WidgetPropertyCategory.Display, "#ff5151");
		addColorProperty(PROP_HIHICOLOR, "Color HIHI", WidgetPropertyCategory.Display, "#ff0000");

		// level boundaries
		addDoubleProperty(PROP_MBOUND, "Boundary M", WidgetPropertyCategory.Behaviour, 6.0);
		addDoubleProperty(PROP_LOLOBOUND, "Boundary LOLO", WidgetPropertyCategory.Behaviour, 2.0);
		addDoubleProperty(PROP_LOBOUND, "Boundary LO", WidgetPropertyCategory.Behaviour, 4.0);
		addDoubleProperty(PROP_HIBOUND, "Boundary HI", WidgetPropertyCategory.Behaviour, 8.0);
		addDoubleProperty(PROP_HIHIBOUND, "Boundary HIHI", WidgetPropertyCategory.Behaviour, 10.0);

		// font properties
		addFontProperty(PROP_VALFONT, "Values Font", WidgetPropertyCategory.Display, ColorAndFontUtil.toFontString("Arial", 8));
		addFontProperty(PROP_CHANFONT, "Channel Font", WidgetPropertyCategory.Display, ColorAndFontUtil.toFontString("Arial", 8));
		// precision
		addIntegerProperty(PROP_PRECISION, "Decimal places", WidgetPropertyCategory.Behaviour, 2, 0, 5);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
		buffer.append("Maximum:\t");
		buffer.append(createTooltipParameter(PROP_MAXVAL) + "\n");
		buffer.append("Minimum:\t");
		buffer.append(createTooltipParameter(PROP_MINVAL) + "\n");
		buffer.append("Value:\t");
		buffer.append(createTooltipParameter(PROP_VALUE) + "\n");
		buffer.append("Level HIHI:\t");
		buffer.append(createTooltipParameter(PROP_HIHIBOUND) + "\n");
		buffer.append("Level HI:\t");
		buffer.append(createTooltipParameter(PROP_HIBOUND) + "\n");
		buffer.append("Level LO:\t");
		buffer.append(createTooltipParameter(PROP_LOBOUND) + "\n");
		buffer.append("Level LOLO:\t");
		buffer.append(createTooltipParameter(PROP_LOLOBOUND));
		return buffer.toString();
	}

	public int getAngle() {
		return getIntegerProperty(PROP_ANGLE).getPropertyValue();
	}

	public int getInnerAngle() {
		return getIntegerProperty(PROP_INNANGLE).getPropertyValue();
	}

	public double getVisibleRadius() {
		return getDoubleProperty(PROP_RADIUS).getPropertyValue();
	}

	public double getScaleRadius() {
		return getDoubleProperty(PROP_SCALERADIUS).getPropertyValue();
	}

	public double getMinorStep() {
		return getDoubleProperty(PROP_MINSTEP).getPropertyValue();
	}

	public double getMajorStep() {
		return getDoubleProperty(PROP_MAJSTEP).getPropertyValue();
	}

	public double getMaxValue() {
		return getDoubleProperty(PROP_MAXVAL).getPropertyValue();
	}

	public double getMinValue() {
		return getDoubleProperty(PROP_MINVAL).getPropertyValue();
	}

	public double getValue() {
		return getDoubleProperty(PROP_VALUE).getPropertyValue();
	}

	public int getScaleWidth() {
		return getIntegerProperty(PROP_SCALEWIDTH).getPropertyValue();
	}

	public double getTextRadius() {
		return getDoubleProperty(PROP_TEXTRADIUS).getPropertyValue();
	}

	public boolean getTransparent() {
		return getBooleanProperty(PROP_TRANSPARENT).getPropertyValue();
	}

	public double getMBound() {
		return getDoubleProperty(PROP_MBOUND).getPropertyValue();
	}

	public double getLOLOBound() {
		return getDoubleProperty(PROP_LOLOBOUND).getPropertyValue();
	}

	public double getLOBound() {
		return getDoubleProperty(PROP_LOBOUND).getPropertyValue();
	}

	public double getHIBound() {
		return getDoubleProperty(PROP_HIBOUND).getPropertyValue();
	}

	public double getHIHIBound() {
		return getDoubleProperty(PROP_HIHIBOUND).getPropertyValue();
	}

	/**
	 * Return the precision.
	 * 
	 * @return The precision.
	 */
	public int getPrecision() {
		return getIntegerProperty(PROP_PRECISION).getPropertyValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}
}
