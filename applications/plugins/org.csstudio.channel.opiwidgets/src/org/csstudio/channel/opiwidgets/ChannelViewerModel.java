/**
 * 
 */
package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**
 * @author shroffk
 * 
 */
public class ChannelViewerModel extends AbstractChannelWidgetModel {

	public final String ID = "org.csstudio.channel.opiwidgets.ChannelViewer"; //$NON-NLS-1$

	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(CONFIGURABLE, "Configurable", WidgetPropertyCategory.Behavior, false));

	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public boolean isConfigurable() {
		return getCastedPropertyValue(CONFIGURABLE);
	}

}
