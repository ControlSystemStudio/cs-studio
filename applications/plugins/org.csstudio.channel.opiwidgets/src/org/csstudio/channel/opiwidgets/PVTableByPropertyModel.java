package org.csstudio.channel.opiwidgets;

import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class PVTableByPropertyModel extends AbstractChannelWidgetModel {
	
	public final String ID = "org.csstudio.channel.opiwidgets.PVTableByProperty"; //$NON-NLS-1$
	
	public static final String ROW_PROPERTY = "row_property"; //$NON-NLS-1$	
	public static final String COLUMN_PROPERTY = "column_property"; //$NON-NLS-1$	
	public static final String SELECTION_PV_NAME = "selection_pv_name"; //$NON-NLS-1$	
	public static final String ROW_SELECTION_PV_NAME = "row_selection_pv_name"; //$NON-NLS-1$	
	
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(ROW_PROPERTY, "Row Property", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(COLUMN_PROPERTY, "Column Property", WidgetPropertyCategory.Basic, ""));
		addProperty(new StringProperty(ROW_SELECTION_PV_NAME, "Row Selection PV Name", WidgetPropertyCategory.Basic, ""));
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
	
	public String getRowSelectionPvName() {
		return getCastedPropertyValue(ROW_SELECTION_PV_NAME);
	}

}
