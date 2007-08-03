package org.csstudio.sds.cosywidgets.models;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.IntegerProperty;

import org.csstudio.sds.cosywidgets.common.SwitchPlugins;

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
	public static final String ID = "cosywidgets.switch";
	
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
			addProperty(PROP_TYPE, new OptionProperty("Switch Type",WidgetPropertyCategory.Behaviour,SwitchPlugins.names,0));
		}
		addProperty(PROP_STATE, new IntegerProperty("Switch State",WidgetPropertyCategory.Display,0));
		addProperty(PROP_ROTATE, new IntegerProperty("Rotation",WidgetPropertyCategory.Display,0,0,360));
		addProperty(PROP_LINEWIDTH, new IntegerProperty("Line Width",WidgetPropertyCategory.Display,4));
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
