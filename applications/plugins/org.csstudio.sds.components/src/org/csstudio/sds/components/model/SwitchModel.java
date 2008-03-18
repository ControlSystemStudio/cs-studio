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
import org.csstudio.sds.model.properties.ArrayOptionProperty;
import org.csstudio.sds.model.properties.IntegerProperty;

import org.csstudio.sds.components.common.SwitchPlugins;

/**
 * A switch widget model.
 * 
 * @author jbercic
 * 
 */
public final class SwitchModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.sds.components.Switch";
	
	/**
	 * The IDs of the properties.
	 */
	public static final String PROP_TRANSPARENT = "transparency";
	public static final String PROP_TYPE = "switch.type";
	public static final String PROP_STATE = "switch.state";
	public static final String PROP_ROTATE = "rotation";
	public static final String PROP_LINEWIDTH = "linewidth";
	
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
		if (SwitchPlugins.names.length>0) {
			addProperty(PROP_TYPE, new ArrayOptionProperty("Switch Type",WidgetPropertyCategory.Behaviour,SwitchPlugins.names,0));
		}
		addProperty(PROP_STATE, new IntegerProperty("Switch State",WidgetPropertyCategory.Display,0));
		addProperty(PROP_ROTATE, new IntegerProperty("Rotation",WidgetPropertyCategory.Display,0,0,360));
		addProperty(PROP_LINEWIDTH, new IntegerProperty("Line Width",WidgetPropertyCategory.Display,4));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createParameter(PROP_NAME)+"\n");
		buffer.append("Type:\t");
		buffer.append(createParameter(PROP_TYPE)+"\n");
		buffer.append("State:\t");
		buffer.append(createParameter(PROP_STATE));
		return buffer.toString();
	}

	public boolean getTransparent() {
		return (Boolean) getProperty(PROP_TRANSPARENT).getPropertyValue();
	}
	
	public int getType() {
		return (Integer) getProperty(PROP_TYPE).getPropertyValue();
	}
	
	public int getState() {
		return (Integer) getProperty(PROP_STATE).getPropertyValue();
	}
	
	public int getRotation() {
		return (Integer) getProperty(PROP_ROTATE).getPropertyValue();
	}
	
	public int getLineWidth() {
		return (Integer) getProperty(PROP_LINEWIDTH).getPropertyValue();
	}
}
