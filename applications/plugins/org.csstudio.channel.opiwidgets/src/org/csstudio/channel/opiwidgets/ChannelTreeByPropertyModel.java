package org.csstudio.channel.opiwidgets;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class ChannelTreeByPropertyModel extends AbstractChannelWidgetWithNotificationModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.ChannelTreeByProperty"; //$NON-NLS-1$
	
	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$	
	public static final String TREE_PROPERTIES = "tree_properties"; //$NON-NLS-1$	
	public static final String SHOW_CHANNEL_NAMES = "show_channel_names"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(CONFIGURABLE, "Configurable", WidgetPropertyCategory.Behavior, false));
		addProperty(new StringProperty(TREE_PROPERTIES, "Tree Properties", WidgetPropertyCategory.Basic, ""));
		addProperty(new BooleanProperty(SHOW_CHANNEL_NAMES, "Show Channel Names", WidgetPropertyCategory.Basic, true));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public List<String> getTreeProperties() {
		return getListProperty(TREE_PROPERTIES);
	}
	
	@Override
	protected String defaultSelectionExpression() {
		if (isShowChannelNames()) {
			return "#(Channel Name)";
		} else {
			return "#(" + getTreeProperties().get(getTreeProperties().size() - 1) + ")";
		}
	}
	
	public boolean getConfigurable() {
		return getCastedPropertyValue(CONFIGURABLE);
	}
	
	public boolean isShowChannelNames() {
		return getCastedPropertyValue(SHOW_CHANNEL_NAMES);
	}

}
