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
import org.csstudio.opibuilder.converter.model.Edm_xyGraphClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * @author Matevz
 */
public class Opi_xyGraphClass extends OpiWidget {

	private static Logger log = Logger.getLogger("org.csstudio.opibuilder.converter.writer.Opi_xyGraphClass");
	private static final String typeId = "xyGraph";
	private static final String name = "EDM xyGraph";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.  
	 */
	public Opi_xyGraphClass(Context con, Edm_xyGraphClass r) {
		super(con, r);
		setTypeId(typeId);

		context.getElement().setAttribute("version", version);		
		new OpiString(context, "name", name);

		new OpiInt(context, "axis_count", 3);  //axis count, Ä¬ÈÏÎª3¸ö
		//Title
		if(r.getAttribute("graphTitle").isInitialized())
			new OpiString(context, "title", r.getGraphTitle());
		if(r.getAttribute("xLabel").isInitialized())
			new OpiString(context, "axis_0_axis_title", r.getXLabel());
		if(r.getAttribute("yLabel").isInitialized())
			new OpiString(context, "axis_1_axis_title", r.getYLabel());
		if(r.getAttribute("y2Label").isInitialized())
			new OpiString(context, "axis_2_axis_title", r.getY2Label());
		//Ó³ÉäfgColor bgColor gridColor
		new OpiColor(context, "foreground_color", r.getFgColor());
		new OpiColor(context, "background_color", r.getBgColor());
		new OpiColor(context, "axis_0_grid_color", r.getGridColor());
		
		//borderÓ³Éä
		if(r.isBorder())
			{
			new OpiInt(context, "border_width", 1);
			new OpiInt(context, "border_style", 1);
			}
		else
			{
			new OpiInt(context, "border_width", 0);
			new OpiInt(context, "border_style", 0);
			}
		//plotAreaBorder
		if(r.isPlotAreaBorder())
			new OpiBoolean(context, "show_plot_area_border", r.isPlotAreaBorder());
		else
			new OpiBoolean(context, "show_plot_area_border", r.isPlotAreaBorder());
			
		//AxisÓ³Éä²Ù×÷
		if(r.getAttribute("showXAxis").isInitialized())	
			new OpiBoolean(context, "axis_0_visible", r.isShowXAxis());
		if(r.getAttribute("showYAxis").isInitialized())	
			new OpiBoolean(context, "axis_1_visible", r.isShowYAxis());
		if(r.getAttribute("showY2Axis").isInitialized())	
			new OpiBoolean(context, "axis_2_visible", r.isShowY2Axis());
		
		if(r.getAttribute("xAxisStyle").isInitialized() && (r.getXAxisStyle().equals("time")))
			new OpiInt(context, "axis_0_time_format", 1);				
		else 
			new OpiInt(context, "axis_0_time_format", 0);
		if(r.getAttribute("yAxisStyle").isInitialized() && (r.getYAxisStyle().equals("time")))
			new OpiInt(context, "axis_1_time_format", 1);				
		else 
			new OpiInt(context, "axis_1_time_format", 0);
		
		new OpiInt(context, "axis_1_time_format", 0);
		new OpiInt(context, "axis_2_time_format", 0);
		//trace properties
		new OpiInt(context, "trace_count",r.getNumTraces()); //¸ú×ÙÊýÁ¿
		
		//PV X,Y ×ª»»
		int x=0,y=0;
		for (x=0;x<r.getXPv().getLineCount();x++)
		{
		y=0;
		String[] getXpvs=r.getXPv().getLine(x).split(" ");//Ê¹ÓÃ¿Õ°××Ö·û·Ö¸î×Ö·û´®
		for(String Xstring:getXpvs){
			if(y==1) 	new OpiString(context, "trace_"+x+"_x_pv", Xstring);
				y++;
		}
		}
		for (x=0;x<r.getYPv().getLineCount();x++)
		{
		y=0;
		String[] getYpvs=r.getYPv().getLine(x).split(" ");//Ê¹ÓÃ¿Õ°××Ö·û·Ö¸î×Ö·û´®
		for(String Ystring:getYpvs){
			if(y==1) 	new OpiString(context, "trace_"+x+"_y_pv", Ystring);
				y++;
		}
		}
		
		if(r.getAttribute("triggerPv").isInitialized())
			new OpiString(context, "trigger_pv", r.getTriggerPv());
		log.debug("Edm_xyGraphClass written.");

	}

}


