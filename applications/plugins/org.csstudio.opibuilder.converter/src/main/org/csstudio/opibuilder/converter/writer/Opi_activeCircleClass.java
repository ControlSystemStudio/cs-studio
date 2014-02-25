/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmLineStyle;
import org.csstudio.opibuilder.converter.model.Edm_activeCircleClass;

/**
 * XML conversion class for Edm_activeCircleClasss
 * @author Matevz
 */
public class Opi_activeCircleClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeCircleClass");
	private static final String typeId = "Ellipse";
	private static final String name = "EDM Ellipse";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeCircleClasss to OPI Rectangle widget XML.  
	 */
	public Opi_activeCircleClass(Context con, Edm_activeCircleClass r) {
		super(con, r);
		setTypeId(typeId);

		widgetContext.getElement().setAttribute("version", version);
		
		new OpiString(widgetContext, "name", name);
		new OpiColor(widgetContext, "line_color", r.getLineColor(), r);
		
		new OpiBoolean(widgetContext, "transparent", !r.isFill());
		
		if (r.getFillColor().isExistInEDL()) {
			new OpiColor(widgetContext, "background_color",
					r.isFillAlarm()? new EdmColor(null, 0, 255,0): r.getFillColor(), r);
		}
		
		if (r.getAlarmPv() != null) {
			// line color alarm rule.
			if (r.isLineAlarm())
				createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "line_color",
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
		

		log.debug("Edm_activeCircleClass written.");

	}
	
	protected void setDefaultPropertyValue(){
		super.setDefaultPropertyValue();		
		new OpiBoolean(widgetContext, "transparent", true);
	}

}
