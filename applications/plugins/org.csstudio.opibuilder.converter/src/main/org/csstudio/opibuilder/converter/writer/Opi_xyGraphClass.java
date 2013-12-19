/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter.writer;

import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.csstudio.opibuilder.converter.model.EdmBoolean;
import org.csstudio.opibuilder.converter.model.EdmColor;
import org.csstudio.opibuilder.converter.model.EdmInt;
import org.csstudio.opibuilder.converter.model.EdmString;
import org.csstudio.opibuilder.converter.model.Edm_xyGraphClass;

/**
 * XML conversion class for Edm_activeRectangleClass
 * 
 * @author Lei Hu, Xihui Chen
 */
public class Opi_xyGraphClass extends OpiWidget {

	private static Logger log = Logger
			.getLogger("org.csstudio.opibuilder.converter.writer.Opi_xyGraphClass");
	private static final String typeId = "xyGraph";
	private static final String name = "EDM xyGraph";
	private static final String version = "1.0";

	/**
	 * Converts the Edm_activeRectangleClass to OPI Rectangle widget XML.
	 */
	public Opi_xyGraphClass(Context con, Edm_xyGraphClass r) {
		super(con, r);
		setTypeId(typeId);
		setVersion(version);
		setName(name);

		new OpiBoolean(widgetContext, "show_toolbar", false);

		new OpiInt(widgetContext, "axis_count", 3); // axis count
		// Title
		if (r.getGraphTitle() != null)
			new OpiString(widgetContext, "title", r.getGraphTitle());
		if (r.getXLabel() != null)
			new OpiString(widgetContext, "axis_0_axis_title", r.getXLabel());
		if (r.getYLabel() != null)
			new OpiString(widgetContext, "axis_1_axis_title", r.getYLabel());
		if (r.getY2Label() != null)
			new OpiString(widgetContext, "axis_2_axis_title", r.getY2Label());

		new OpiBoolean(widgetContext, "axis_2_left_bottom_side", false);

		new OpiColor(widgetContext, "axis_0_axis_color", r.getFgColor(), r);
		new OpiColor(widgetContext, "axis_1_axis_color", r.getFgColor(), r);
		new OpiColor(widgetContext, "axis_2_axis_color", r.getFgColor(), r);

		new OpiColor(widgetContext, "background_color", con.getRootDisplay().getBgColor(), r);

		new OpiColor(widgetContext, "plot_area_background_color", r.getBgColor(), r);

		new OpiColor(widgetContext, "axis_0_grid_color", r.getGridColor(), r);
		new OpiColor(widgetContext, "axis_1_grid_color", r.getGridColor(), r);
		new OpiColor(widgetContext, "axis_2_grid_color", r.getGridColor(), r);

		if (r.isBorder()) {
			new OpiInt(widgetContext, "border_width", 1);
			new OpiInt(widgetContext, "border_style", 1);
		} else {
			new OpiInt(widgetContext, "border_width", 0);
			new OpiInt(widgetContext, "border_style", 0);
		}

		new OpiBoolean(widgetContext, "show_plot_area_border", r.isPlotAreaBorder());

		new OpiBoolean(widgetContext, "axis_0_visible", r.isShowXAxis());
		new OpiBoolean(widgetContext, "axis_1_visible", r.isShowYAxis());
		new OpiBoolean(widgetContext, "axis_2_visible", r.isShowY2Axis());
		
		new OpiDouble(widgetContext, "axis_0_minimum", r.getxMin());
		new OpiDouble(widgetContext, "axis_1_minimum", r.getyMin());
		new OpiDouble(widgetContext, "axis_2_minimum", r.getY2Min());
		
		new OpiDouble(widgetContext, "axis_0_maximum", r.getxMax());
		new OpiDouble(widgetContext, "axis_1_maximum", r.getyMax());
		new OpiDouble(widgetContext, "axis_2_maximum", r.getY2Max());
		
		new OpiBoolean(widgetContext, "axis_0_show_grid", r.isxShowMajorGrid());
		new OpiBoolean(widgetContext, "axis_1_show_grid", r.isyShowMajorGrid());
		new OpiBoolean(widgetContext, "axis_2_show_grid", r.isY2ShowMajorGrid());
		
		
		

		new OpiInt(widgetContext, "axis_0_time_format",
				(r.getXAxisStyle() != null && (r.getXAxisStyle().equals("time"))) ? 7 : 0);

		new OpiBoolean(widgetContext, "axis_1_logScale", r.getYAxisStyle() != null
				&& (r.getYAxisStyle().equals("log10")));

		new OpiBoolean(widgetContext, "axis_2_logScale", r.getY2AxisStyle() != null
				&& (r.getY2AxisStyle().equals("log10")));

		new OpiInt(widgetContext, "axis_1_time_format", 0);
		new OpiInt(widgetContext, "axis_2_time_format", 0);
		
		if (r.getTriggerPv() != null)
			new OpiString(widgetContext, "trigger_pv", r.getTriggerPv());
		

		// axis properties
		for (int i = 0; i < 2; i++) {
			new OpiBoolean(widgetContext, "axis_" + i + "_auto_scale",
					r.isAutoScaleBothDirections());
			new OpiDouble(widgetContext, "axis_" + i + "_auto_scale_threshold",
					r.getAutoScaleThreshPct() / 100);

		}
		if(r.getxAxisSrc()!=null && r.getxAxisSrc().equals("AutoScale"))
			new OpiBoolean(widgetContext, "axis_0_auto_scale",	true);

		if(r.getyAxisSrc()!=null && r.getyAxisSrc().equals("AutoScale"))
			new OpiBoolean(widgetContext, "axis_1_auto_scale",	true);

		if(r.getY2AxisSrc()!=null && r.getY2AxisSrc().equals("AutoScale"))
			new OpiBoolean(widgetContext, "axis_2_auto_scale",	true);
		
		
		// trace properties
		new OpiInt(widgetContext, "trace_count", r.getNumTraces()); 
			
		
		
		// PV X,Y
		if (r.getXPv().isExistInEDL()) {
			for (Entry<String, EdmString>  entry: r.getXPv().getEdmAttributesMap().entrySet()) {
				new OpiString(widgetContext, "trace_" + entry.getKey()+ "_x_pv", entry.getValue());
			}
			
		}
		for(int i=0; i<r.getNumTraces(); i++){			
			//give it a big buffer if it is waveform, edm will show all waveform values regardless nPts.
			new OpiInt(widgetContext, "trace_"+i+"_buffer_size", r.getnPts());
			new OpiInt(widgetContext, "trace_"+i+"_update_delay", r.getUpdateTimerMs());
			if(r.getPlotMode()==null && r.getnPts()<5){//assume it is a waveform
				new OpiBoolean(widgetContext, "trace_"+i+"_concatenate_data", false);
				new OpiInt(widgetContext, "trace_"+i+"_buffer_size", 2000);
			}
		}
		
		
		if (r.getYPv().isExistInEDL()) {
			for (Entry<String, EdmString> entry : r.getYPv().getEdmAttributesMap().entrySet()) {
				new OpiString(widgetContext, "trace_" + entry.getKey() + "_y_pv", entry.getValue());
			}
		}

		if (r.getPlotColor().isExistInEDL()) {
			for (Entry<String, EdmColor> entry : r.getPlotColor().getEdmAttributesMap().entrySet()) {
				new OpiColor(widgetContext, "trace_" + entry.getKey() + "_trace_color",
						entry.getValue(), r);
			}
		}
		
		if(r.getPlotStyle().isExistInEDL()){
			for (Entry<String, EdmString> entry : r.getPlotStyle().getEdmAttributesMap().entrySet()) {
				if(entry.getValue().get().equals("needle")){
					new OpiInt(widgetContext, "trace_"+entry.getKey() + "_trace_type", 3);
				}else if (entry.getValue().get().equals("point")){
					new OpiInt(widgetContext, "trace_"+entry.getKey() + "_trace_type", 2);
				}else if (entry.getValue().get().equals("single point")){
					new OpiInt(widgetContext, "trace_"+entry.getKey() + "_trace_type", 2);
					new OpiInt(widgetContext, "trace_"+entry.getKey() + "_buffer_size", 1);
				}else{
					new OpiInt(widgetContext, "trace_"+entry.getKey() + "_trace_type", 0);
				}
			}
		}
				
		if(r.getLineThickness().isExistInEDL()){
			for (Entry<String, EdmInt> entry : r.getLineThickness().getEdmAttributesMap().entrySet())
				new OpiInt(widgetContext, "trace_"+entry.getKey() + "_line_width", entry.getValue().get());				
		}
		
		if (r.getLineStyle().isExistInEDL()) {
			for (Entry<String, EdmString> entry : r.getLineStyle().getEdmAttributesMap().entrySet()) {
				if (entry.getValue().get().equals("dash")) {
					new OpiInt(widgetContext, "trace_" + entry.getKey() + "_trace_type", 1);
				}
			}
		}
		if(r.getPlotSymbolType().isExistInEDL()){
			for (Entry<String, EdmString> entry : r.getPlotSymbolType().getEdmAttributesMap().entrySet()) {
				int m = 0;
				if (entry.getValue().get().equals("circle")) {
					m=2;
				}else if (entry.getValue().get().equals("square")) {
					m=5;
				}else if (entry.getValue().get().equals("diamond")) {
					m=7;
				}				
				new OpiInt(widgetContext, "trace_" + entry.getKey() + "_point_style", m);				
			}
		}
		
		if(r.getOpMode().isExistInEDL()){
			for (Entry<String, EdmString> entry : r.getOpMode().getEdmAttributesMap().entrySet()) {
				//EDM will sort the data in this mode where BOY cannot, so plot it as points
				if (entry.getValue().get().equals("plot")) { 
					new OpiInt(widgetContext, "trace_" + entry.getKey() + "_trace_type", 2);
					if(!r.getPlotSymbolType().getEdmAttributesMap().containsKey(entry.getKey()))
						new OpiInt(widgetContext, "trace_" + entry.getKey() + "_point_style", 1);	
					new OpiInt(widgetContext, "trace_" + entry.getKey() + "_point_size", 2);	
				}
			}
		}
		
		if(r.getPlotUpdateMode().isExistInEDL()){
			for (Entry<String, EdmString> entry : r.getPlotUpdateMode().getEdmAttributesMap().entrySet()) {
				int m = 1;
				if (entry.getValue().get().equals("xOrY")) {
					m=0;
				}else if (entry.getValue().get().equals("x")) {
					m=2;
				}else if (entry.getValue().get().equals("y")) {
					m=3;
				}else if (entry.getValue().get().equals("trigger")) 
					m=4;				
				new OpiInt(widgetContext, "trace_" + entry.getKey() + "_update_mode", m);				
			}
		}
		
	
		if(r.getUseY2Axis().isExistInEDL()){
			for (Entry<String, EdmBoolean> entry : r.getUseY2Axis().getEdmAttributesMap().entrySet()) {
				if(entry.getValue().is())
					new OpiInt(widgetContext, "trace_"+entry.getKey()+"_y_axis_index", 2);
			}
		}
		
	
		
		log.debug("Edm_xyGraphClass written.");

	}

}
