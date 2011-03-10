/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;

import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.PVValueProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;

/**The abstract widget model for all PV related widgets. 
 * @author Xihui Chen
 *
 */
public abstract class AbstractPVWidgetModel extends AbstractWidgetModel {


	/**
	 * If this is true, an corresponding alarm border will show up when the alarm status
	 * of input PV is not normal. 
	 * The default Major alarm border is red bold line border. Minor alarm border
	 * is orange bold line border. Invalid alarm border is pink bold line border.
	 * The alarm colors can be redefined in color macro file.  
	 */
	public static final String PROP_BORDER_ALARMSENSITIVE= "border_alarm_sensitive"; //$NON-NLS-1$
	
	/**
	 * If this is true, the foreground color will change depends on the alarm status of input PV.
	 * The default major color is red, minor color is orange and invalid color is pink.
	 * The alarm colors can be redefined in color macro file.   
	 */
	public static final String PROP_FORECOLOR_ALARMSENSITIVE= "forecolor_alarm_sensitive"; //$NON-NLS-1$

	/**
	 * If this is true, the background color will change depends on the alarm status of input PV.
	 * The default major color is red, minor color is orange and invalid color is pink.
	 * The alarm colors can be redefined in color macro file.   
	 */
	public static final String PROP_BACKCOLOR_ALARMSENSITIVE= "backcolor_alarm_sensitive"; //$NON-NLS-1$
	/**
	 * The property which hold the value of input PV.
	 */
	public static final String PROP_PVVALUE= "pv_value"; //$NON-NLS-1$
	
	/**
	 * The name of the input PV.
	 */
	public static final String PROP_PVNAME= "pv_name"; //$NON-NLS-1$
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();		
		addPVProperty(new StringProperty(PROP_PVNAME, "PV Name", WidgetPropertyCategory.Basic,
				""), new PVValueProperty(PROP_PVVALUE, null));
		
		addProperty(new BooleanProperty(PROP_BORDER_ALARMSENSITIVE, 
				"Alarm Sensitive", WidgetPropertyCategory.Border, true));
		addProperty(new BooleanProperty(PROP_FORECOLOR_ALARMSENSITIVE, 
				"ForeColor Alarm Sensitive", WidgetPropertyCategory.Display, false));
		addProperty(new BooleanProperty(PROP_BACKCOLOR_ALARMSENSITIVE, 
				"BackColor Alarm Sensitive", WidgetPropertyCategory.Display, false));
		
		setTooltip("$(" + PROP_PVNAME + ")\n" + "$(" + PROP_PVVALUE + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public boolean isBorderAlarmSensitve(){
		if(getProperty(PROP_BORDER_ALARMSENSITIVE) == null)
			return false;
		return (Boolean)getCastedPropertyValue(PROP_BORDER_ALARMSENSITIVE);
	}
	
	public boolean isForeColorAlarmSensitve(){
		if(getProperty(PROP_FORECOLOR_ALARMSENSITIVE) == null)
			return false;
		return (Boolean)getCastedPropertyValue(PROP_FORECOLOR_ALARMSENSITIVE);
	}
	
	public boolean isBackColorAlarmSensitve(){
		if(getProperty(PROP_BACKCOLOR_ALARMSENSITIVE) == null)
			return false;
		return (Boolean)getCastedPropertyValue(PROP_BACKCOLOR_ALARMSENSITIVE);
	}
	
	public String getPVName(){
		return (String)getCastedPropertyValue(PROP_PVNAME);
	}
}
