package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class PVTableByPropertyModel extends AbstractWidgetModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.PVTableByProperty"; //$NON-NLS-1$
	
	public static final String CHANNEL_QUERY = "channel_query"; //$NON-NLS-1$	
	public static final String ROW_PROPERTY = "row_property"; //$NON-NLS-1$	
	public static final String COLUMN_PROPERTY = "column_property"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(CHANNEL_QUERY, "Channel query", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(ROW_PROPERTY, "Row property", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(COLUMN_PROPERTY, "Column property", WidgetPropertyCategory.Basic, ""));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public String getChannelQuery() {
		return getCastedPropertyValue(CHANNEL_QUERY);
	}
	
	public String getRowProperty() {
		return getCastedPropertyValue(ROW_PROPERTY);
	}
	
	public String getColumnProperty() {
		return getCastedPropertyValue(COLUMN_PROPERTY);
	}

}
