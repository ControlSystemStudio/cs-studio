package org.csstudio.opibuilder.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

public abstract class AbstractPVWidgetModel extends AbstractWidgetModel {


	public static final String PROP_BORDER_ALARMSENSITIVE= "border.alarmsensitive"; //$NON-NLS-1$
	public static final String PROP_FORECOLOR_ALARMSENSITIVE= "foregroundcolor.alarmsensitive"; //$NON-NLS-1$
	public static final String PROP_BACKCOLOR_ALARMSENSITIVE= "backgroundcolor.alarmsensitive"; //$NON-NLS-1$
	/**
	 * The ID of the pv value property.
	 */
	public static final String PROP_PVVALUE= "pv.value"; //$NON-NLS-1$
	
	/**
	 * The ID of the pv name property.
	 */
	public static final String PROP_PVNAME= "pv.name"; //$NON-NLS-1$
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();		
		addPVProperty(new StringProperty(PROP_PVNAME, "PV Name", WidgetPropertyCategory.Basic,
				true, ""), new PVValueProperty(PROP_PVVALUE, null));
		
		addProperty(new BooleanProperty(PROP_BORDER_ALARMSENSITIVE, 
				"Alarm Sensitive", WidgetPropertyCategory.Border, true, false));
		addProperty(new BooleanProperty(PROP_FORECOLOR_ALARMSENSITIVE, 
				"ForeColor Alarm Sensitive", WidgetPropertyCategory.Display, true, false));
		addProperty(new BooleanProperty(PROP_BACKCOLOR_ALARMSENSITIVE, 
				"BackColor Alarm Sensitive", WidgetPropertyCategory.Display, true, false));
	}

	public boolean isBorderAlarmSensitve(){
		return (Boolean)getCastedPropertyValue(PROP_BORDER_ALARMSENSITIVE);
	}
	
	public boolean isForeColorAlarmSensitve(){
		return (Boolean)getCastedPropertyValue(PROP_FORECOLOR_ALARMSENSITIVE);
	}
	
	public boolean isBackColorAlarmSensitve(){
		return (Boolean)getCastedPropertyValue(PROP_BACKCOLOR_ALARMSENSITIVE);
	}
}
