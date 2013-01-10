package org.csstudio.channel.opiwidgets;

import java.util.List;

import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class PVTableByPropertyModel extends AbstractChannelWidgetWithNotificationModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.PVTableByProperty"; //$NON-NLS-1$
	
	public static final String ROW_PROPERTY = "row_property"; //$NON-NLS-1$	
	public static final String COLUMN_PROPERTY = "column_property"; //$NON-NLS-1$	
	public static final String COLUMN_TAGS = "column_tags"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(ROW_PROPERTY, "Row Property", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(COLUMN_PROPERTY, "Column Property", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(COLUMN_TAGS, "Column Tags", WidgetPropertyCategory.Basic, ""));
	}

	@Override
	public String getTypeID() {
		return ID;
	}
	
	public String getRowProperty() {
		return getCastedPropertyValue(ROW_PROPERTY);
	}
	
	public String getColumnProperty() {
		return getCastedPropertyValue(COLUMN_PROPERTY);
	}
	
	public List<String> getColumnTags() {
		return getListProperty(COLUMN_TAGS);
	}

}
