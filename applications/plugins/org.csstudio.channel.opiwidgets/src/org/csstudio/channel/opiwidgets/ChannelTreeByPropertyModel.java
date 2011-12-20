package org.csstudio.channel.opiwidgets;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class ChannelTreeByPropertyModel extends AbstractChannelWidgetModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.ChannelTreeByProperty"; //$NON-NLS-1$
	
	public static final String CONFIGURABLE = "configurable"; //$NON-NLS-1$	
	public static final String TREE_PROPERTIES = "tree_properties"; //$NON-NLS-1$	
	public static final String SELECTION_PV_NAME = "selection_pv_name"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new BooleanProperty(CONFIGURABLE, "Configurable", WidgetPropertyCategory.Behavior, false));
		addProperty(new StringProperty(TREE_PROPERTIES, "Tree properties", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(SELECTION_PV_NAME, "Selection PV Name", WidgetPropertyCategory.Basic, ""));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public List<String> getTreeProperties() {
		String list = getCastedPropertyValue(TREE_PROPERTIES);
		String[] tokens = list.split(",");
		List<String> properties = new ArrayList<String>();
		for (String token : tokens) {
			properties.add(token.trim());
		}
		return properties;
	}
	
	public String getSelectionPvName() {
		return getCastedPropertyValue(SELECTION_PV_NAME);
	}
	
	public boolean getConfigurable() {
		return getCastedPropertyValue(CONFIGURABLE);
	}

}
