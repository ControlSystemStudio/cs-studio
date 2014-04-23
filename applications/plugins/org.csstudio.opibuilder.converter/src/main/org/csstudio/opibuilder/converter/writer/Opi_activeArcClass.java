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
		setVersion(version);
		setName(name);
		
		new OpiColor(widgetContext, "foreground_color",r.getLineColor(), r);
		

		new OpiBoolean(widgetContext, "fill", r.isFill());
		
		new OpiColor(widgetContext, "background_color", r.getFillColor(), r);
			
			
		if (r.getAlarmPv() != null) {
			// line color alarm rule.
			if (r.isLineAlarm())
				createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "foreground_color",
						"lineColorAlarmRule", true);
			if (r.isFillAlarm())
				createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "background_color",
						"backColorAlarmRule", true);
		}
		
		int line_width = 1;
		if(r.getAttribute("lineWidth").isExistInEDL() && (r.getLineWidth() != 0 || r.isFill()))
			line_width = r.getLineWidth();
		new OpiInt(widgetContext, "line_width", line_width);

		int lineStyle = 0;
		if (r.getLineStyle().isExistInEDL()) {

			switch (r.getLineStyle().get()) {
			case EdmLineStyle.SOLID: {
				lineStyle = 0;
			} break;
			case EdmLineStyle.DASH: {
				lineStyle = 1;
			} break;
			}

		}
		new OpiInt(widgetContext, "line_style", lineStyle);
		
		
		new OpiDouble(widgetContext, "start_angle", r.getStartAngle());

		new OpiDouble(widgetContext, "total_angle", 
					r.getAttribute("totalAngle").isExistInEDL()?r.getTotalAngle():180);
		
		log.debug("Edm_activeArcClass written.");

	}
	
	protected void setDefaultPropertyValue(){
		super.setDefaultPropertyValue();		
		new OpiBoolean(widgetContext, "transparent", true);
	}

}
