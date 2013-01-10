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

/**
 * The model delegate for widgets have PV Name property.
 * @author Xihui Chen
 *
 */
public class PVWidgetModelDelegate implements IPVWidgetModel{	
	

	AbstractWidgetModel model;

	public PVWidgetModelDelegate(AbstractWidgetModel model) {
		this.model = model;
	}
	
	public void configureBaseProperties() {
		model.addPVProperty(new StringProperty(PROP_PVNAME, "PV Name", WidgetPropertyCategory.Basic,
				""), new PVValueProperty(PROP_PVVALUE, null));
		
		model.addProperty(new BooleanProperty(PROP_BORDER_ALARMSENSITIVE, 
				"Alarm Sensitive", WidgetPropertyCategory.Border, true));
		model.addProperty(new BooleanProperty(PROP_FORECOLOR_ALARMSENSITIVE, 
				"ForeColor Alarm Sensitive", WidgetPropertyCategory.Display, false));
		model.addProperty(new BooleanProperty(PROP_BACKCOLOR_ALARMSENSITIVE, 
				"BackColor Alarm Sensitive", WidgetPropertyCategory.Display, false));
		
		model.setTooltip("$(" + PROP_PVNAME + ")\n" + "$(" + PROP_PVVALUE + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	public boolean isBorderAlarmSensitve(){
		if(model.getProperty(PROP_BORDER_ALARMSENSITIVE) == null)
			return false;
		return (Boolean)model.getCastedPropertyValue(PROP_BORDER_ALARMSENSITIVE);
	}
	
	public boolean isForeColorAlarmSensitve(){
		if(model.getProperty(PROP_FORECOLOR_ALARMSENSITIVE) == null)
			return false;
		return (Boolean)model.getCastedPropertyValue(PROP_FORECOLOR_ALARMSENSITIVE);
	}
	
	public boolean isBackColorAlarmSensitve(){
		if(model.getProperty(PROP_BACKCOLOR_ALARMSENSITIVE) == null)
			return false;
		return (Boolean)model.getCastedPropertyValue(PROP_BACKCOLOR_ALARMSENSITIVE);
	}
	
	public String getPVName(){
		return (String)model.getCastedPropertyValue(PROP_PVNAME);
	}

}
