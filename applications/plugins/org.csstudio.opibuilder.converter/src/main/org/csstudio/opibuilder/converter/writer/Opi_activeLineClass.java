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
 * @author Xihui Chen
 */
public class Opi_activeLineClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_activeRectangleClass");
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
		
		//set it as ployline or polygon
		if(r.getAttribute("fill").isInitialized() && r.isFill()){			
			setTypeId(typeId_polygon);
			isPolygon = true;
		}
		else
			setTypeId(typeId_polyline);

		context.getElement().setAttribute("version", version);

		new OpiString(context, "name", name);
		
		new OpiPointsList(context, "points", r.getXPoints(), r.getYPoints()); 


		new OpiColor(context, "background_color", r.getFillColor());
		
		if(r.getAttribute("fillAlarm").isInitialized())
			new OpiBoolean(context, "backcolor_alarm_sensitive", r.isFillAlarm());
		
		new OpiColor(context, "foreground_color", r.getLineColor());
		
		if(r.getAttribute("lineAlarm").isInitialized())
			new OpiBoolean(context, "forecolor_alarm_sensitive", r.isLineAlarm());
	
		if(r.getAttribute("alarmPv").isInitialized())
			new OpiString(context, "pv_name", r.getAlarmPv());
		
		if (r.getAttribute("lineWidth").isInitialized()) {
			new OpiInt(context, "line_width", r.getLineWidth() == 0? 1 : r.getLineWidth());
		}

		if(!isPolygon){
			if(r.getAttribute("arrows").isInitialized()){
				int a = 0;
				if(r.getArrows().equals("from"))
					a = 1;
				else if(r.getArrows().equals("to"))
					a = 2;
				else if(r.getArrows().equals("both"))
					a = 3;					
				
				new OpiInt(context, "arrows", a);
			}
		}
		
		
			
		
		/* It is not clear when there is no border for EDM display. 
		 * 
		 * Here it is assumed there is no border (border style == 0) when line style
		 * is not set. 
		 * 
		 * The alternative would be to use lineWidth? 
		 */
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

		log.debug("Edm_activeRectangleClass written.");

	}
	
	protected void setDefaultPropertyValue(){
		super.setDefaultPropertyValue();

		if(isPolygon)
			new OpiBoolean(context, "transparent", false);
		else{
			new OpiDouble(context, "fill_level", 100);
			new OpiBoolean(context, "fill_arrow", true);
		}
	}

}
