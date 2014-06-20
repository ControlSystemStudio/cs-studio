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
import org.csstudio.opibuilder.converter.model.Edm_activeLineClass;

/**
 * XML conversion class for Edm_activeLineClass
 * 
 * @author Xihui Chen
 */
public class Opi_activeLineClass extends OpiWidget {

	private static Logger log = Logger
			.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeRectangleClass");
	private static final String typeId_polyline = "polyline";
	private static final String typeId_polygon = "polygon";
	private boolean isPolygon;
	private static final String name = "EDM Line";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_activeLineClass(Context con, Edm_activeLineClass r) {
		super(con, r);

		// set it as ployline or polygon
		if (r.isFill()) {
			setTypeId(typeId_polygon);
			isPolygon = true;
			new OpiColor(widgetContext, "line_color", r.getLineColor(), r);
			new OpiColor(widgetContext, "background_color",
					r.getFillColor(), r);
			
			// If a string property is not exist, it is null.
			if (r.getAlarmPv() != null) {
				// line color alarm rule.
				if(r.isLineAlarm())
					createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "line_color",
						"lineColorAlarmRule", true);
				if(r.isFillAlarm())
					createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "background_color",
						"backColorAlarmRule", true);
			}
			
		} else{
			setTypeId(typeId_polyline);
			new OpiColor(widgetContext, "background_color", r.getLineColor(), r);
			if (r.getAttribute("arrows").isExistInEDL()) {
				int a = 0;
				if (r.getArrows().equals("from"))
					a = 1;
				else if (r.getArrows().equals("to"))
					a = 2;
				else if (r.getArrows().equals("both"))
					a = 3;

				new OpiInt(widgetContext, "arrows", a);
			}
			
			// If a string property is not exist, it is null.
			if (r.getAlarmPv() != null) {
				if(r.isLineAlarm())
					createColorAlarmRule(r, convertPVName(r.getAlarmPv()), "background_color",
						"backColorAlarmRule", true);
			}
		}
		
		widgetContext.getElement().setAttribute("version", version);

		new OpiString(widgetContext, "name", name);

		new OpiPointsList(widgetContext, "points", r.getXPoints(), r.getYPoints());
			
		new OpiInt(widgetContext, "line_width", r.getLineWidth() == 0 ? 1 : r.getLineWidth());

		/*
		 * It is not clear when there is no border for EDM display.
		 * 
		 * Here it is assumed there is no border (border style == 0) when line
		 * style is not set.
		 * 
		 * The alternative would be to use lineWidth?
		 */
		int lineStyle = 0;
		if (r.getLineStyle().isExistInEDL()) {

			switch (r.getLineStyle().get()) {
			case EdmLineStyle.SOLID: {
				lineStyle = 0;
			}
				break;
			case EdmLineStyle.DASH: {
				lineStyle = 1;
			}
				break;
			}

		}
		new OpiInt(widgetContext, "line_style", lineStyle);

		log.debug("Edm_activeRectangleClass written.");

	}

	protected void setDefaultPropertyValue() {
		super.setDefaultPropertyValue();

		if (isPolygon)
			new OpiBoolean(widgetContext, "transparent", false);
		else {
			new OpiDouble(widgetContext, "fill_level", 0);
			new OpiBoolean(widgetContext, "fill_arrow", true);
		}
	}

}
