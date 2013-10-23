/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmLineStyle;
import org.csstudio.opibuilder.converter.model.Edm_activeArcClass;

/**
 * XML conversion class for Edm_activeArcClass
 * @author Matevz
 */
public class Opi_activeArcClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeArcClass");
	private static final String typeId = "arc";
	private static final String name = "EDM arc";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeArcClass to OPI Rectangle widget XML.  
	 */
	public Opi_activeArcClass(Context con, Edm_activeArcClass r) {
		super(con, r);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);
		
		new OpiString(context, "name", name);
		new OpiColor(context, "foreground_color", r.getLineColor());
		
		if(r.getAttribute("fill").isInitialized())
			new OpiBoolean(context, "fill", r.isFill());
		
		if (r.getFillColor().isInitialized()) {
			new OpiColor(context, "background_color", r.getFillColor());
		}
		
		if(r.getAttribute("fillAlarm").isInitialized())
			new OpiBoolean(context, "backcolor_alarm_sensitive", r.isFillAlarm());
		
		
		if(r.getAttribute("lineAlarm").isInitialized())
			new OpiBoolean(context, "forecolor_alarm_sensitive", r.isLineAlarm());
	
		if(r.getAttribute("alarmPv").isInitialized())
			new OpiString(context, "pv_name", r.getAlarmPv());
		
		int line_width = 1;
		if(r.getAttribute("lineWidth").isInitialized() && (r.getLineWidth() != 0 || r.isFill()))
			line_width = r.getLineWidth();
		new OpiInt(context, "line_width", line_width);

		int lineStyle = 0;
		if (r.getLineStyle().isInitialized()) {

			switch (r.getLineStyle().get()) {
			case EdmLineStyle.SOLID: {
				lineStyle = 0;
			} break;
			case EdmLineStyle.DASH: {
				lineStyle = 1;
			} break;
			}

		}
		new OpiInt(context, "line_style", lineStyle);
		
		if(r.getAttribute("startAngle").isInitialized())
			new OpiDouble(context, "start_angle", r.getStartAngle());
		if(r.getAttribute("startAngle").isInitialized())
			new OpiDouble(context, "total_angle", r.getTotalAngle());
		log.debug("Edm_activeArcClass written.");

	}
	
	protected void setDefaultPropertyValue(){
		super.setDefaultPropertyValue();		
		new OpiBoolean(context, "transparent", true);
	}

}
