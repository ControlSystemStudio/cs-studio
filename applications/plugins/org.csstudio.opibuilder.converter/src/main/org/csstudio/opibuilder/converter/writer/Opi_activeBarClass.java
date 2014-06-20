/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.Edm_activeBarClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Lei Hu, Xihui Chen
 */
public class Opi_activeBarClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeBarClass");
	private static final String typeId = "progressbar";
	private static final String name = "EDM progressbar";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeBarClass(Context con, Edm_activeBarClass r) {
		super(con, r);
		setTypeId(typeId);
		setVersion(version);
		setName(name);
		new OpiBoolean(widgetContext, "effect_3d", false);
		new OpiBoolean(widgetContext, "indicator_mode", false);
		new OpiBoolean(widgetContext, "show_scale", r.isShowScale());
		new OpiBoolean(widgetContext, "transparent_background", false);
		new OpiBoolean(widgetContext, "show_markers", false);
		new OpiBoolean(widgetContext, "show_label", false);
		new OpiBoolean(widgetContext, "forecolor_alarm_sensitive", false);
		new OpiBoolean(widgetContext, "border_alarm_sensitive", r.isFgAlarm());
		
		new OpiInt(widgetContext, "border_width", r.isBorder()?1:0);
		new OpiInt(widgetContext, "border_style", r.isBorder()?1:0);
		new OpiColor(widgetContext, "border_color", r.getFgColor(), r);
		
		if(r.getIndicatorPv() != null)
			new OpiString(widgetContext, "pv_name", convertPVName(r.getIndicatorPv()));
		
		new OpiColor(widgetContext, "fill_color", r.getIndicatorColor(), r);
		new OpiColor(widgetContext, "color_fillbackground", r.getBgColor(), r);
		new OpiBoolean(widgetContext, "fillcolor_alarm_sensitive", r.isIndicatorAlarm());	
		
		new OpiBoolean(widgetContext, "horizontal",
					r.getOrientation()==null || !r.getOrientation().equals("vertical"));
		
		new OpiBoolean(widgetContext, "limits_from_pv", r.isLimitsFromDb());
		new OpiBoolean(widgetContext, "origin_ignored", !r.getAttribute("origin").isExistInEDL());
		new OpiDouble(widgetContext, "origin", r.getOrigin());
		new OpiDouble(widgetContext, "minimum", r.getMin());
		new OpiDouble(widgetContext, "maximum", r.getMax());
		
		

		log.debug("Edm_activeBarClass written.");

	}

}

