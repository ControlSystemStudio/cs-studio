package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public class TextIndicatorModel extends LabelModel {

	public static final String PROP_PVNAME= "pv_name"; //$NON-NLS-1$
	
	public static final String PROP_PVVALUE= "pv_value"; //$NON-NLS-1$
	
	
	public TextIndicatorModel() {
		setPropertyValue(PROP_TEXT, "");
	}
	
	
	@Override
	public String getTypeID() {
		return "org.csstudio.opibuilder.widgets.TextIndicator"; //$NON-NLS-1$;
	}
	
	
	@Override
	protected void configureProperties() {
		super.configureProperties();
		setPropertyVisible(PROP_TEXT, false);
		addPVProperty(new StringProperty(PROP_PVNAME, "PV Name",WidgetPropertyCategory.Behavior,
				true, ""), new PVValueProperty(PROP_PVVALUE, null));
		
	}
	
	
}
