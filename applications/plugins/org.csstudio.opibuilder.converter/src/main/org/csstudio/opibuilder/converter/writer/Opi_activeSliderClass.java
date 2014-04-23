/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeSliderClass;

/**
 * XML conversion class for Edm_activeSliderClasss
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activeSliderClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeSliderClass");
	private static final String typeId = "scaledslider";
	private static final String name = "EDM Slider";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeSliderClasss to OPI Rectangle widget XML.  
	 */
	public Opi_activeSliderClass(Context con, Edm_activeSliderClass r) {
		super(con, r);
		setTypeId(typeId);
		setName(name);
		setVersion(version);
		
		if(r.getControlPv()!=null)
			new OpiString(widgetContext, "pv_name", convertPVName(r.getControlPv()));
		
		new OpiBoolean(widgetContext, "horizontal", true);
		new OpiBoolean(widgetContext, "show_markers", false);
		new OpiBoolean(widgetContext, "log_scale", false);
		new OpiColor(widgetContext, "thumb_color", r.getControlColor(), r);
		if(r.isControlAlarm() && r.getControlPv()!=null){
			createColorAlarmRule(r, convertPVName(r.getControlPv()), "fill_color", 
					"FillColorAlarmRule", true);
			createColorAlarmRule(r, r.getControlPv(), "color_fillbackground", 
					"FillbackgroundColorAlarmRule", true);
		}
		
		new OpiDouble(widgetContext, "page_increment", r.getIncrement());
		
		new OpiDouble(widgetContext, "maximum", r.getScaleMax());
		new OpiDouble(widgetContext, "minimum", r.getScaleMin());
		
		new OpiBoolean(widgetContext, "limits_from_pv", r.isLimitsFromDb());
		
		log.debug("Edm_activeSliderClass written.");

	}
	
	protected void setDefaultPropertyValue(){
		super.setDefaultPropertyValue();		
		new OpiBoolean(widgetContext, "transparent", true);
	}

}
