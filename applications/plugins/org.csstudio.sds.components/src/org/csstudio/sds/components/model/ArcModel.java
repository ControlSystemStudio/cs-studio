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
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createParameter(PROP_ALIASES)+"\n");
		buffer.append("Start Angle:\t");
		buffer.append(createParameter(PROP_STARTANGLE)+"\n");
		buffer.append("Angle:\t");
		buffer.append(createParameter(PROP_ANGLE));
		return buffer.toString();
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
