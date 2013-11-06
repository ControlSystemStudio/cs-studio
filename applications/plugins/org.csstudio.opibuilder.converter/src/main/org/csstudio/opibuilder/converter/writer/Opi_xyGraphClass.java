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
import org.csstudio.opibuilder.converter.model.EdmColor;
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

		new OpiColor(widgetContext, "axis_0_axis_color", r.getFgColor());
		new OpiColor(widgetContext, "axis_1_axis_color", r.getFgColor());
		new OpiColor(widgetContext, "axis_2_axis_color", r.getFgColor());

		new OpiColor(widgetContext, "background_color", con.getRootDisplay().getBgColor());

		new OpiColor(widgetContext, "plot_area_background_color", r.getBgColor());

		new OpiColor(widgetContext, "axis_0_grid_color", r.getGridColor());
		new OpiColor(widgetContext, "axis_1_grid_color", r.getGridColor());
		new OpiColor(widgetContext, "axis_2_grid_color", r.getGridColor());

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

		new OpiInt(widgetContext, "axis_0_time_format",
				(r.getXAxisStyle() != null && (r.getXAxisStyle().equals("time"))) ? 7 : 0);

		new OpiBoolean(widgetContext, "axis_1_logScale", r.getYAxisStyle() != null
				&& (r.getYAxisStyle().equals("log10")));

		new OpiBoolean(widgetContext, "axis_2_logScale", r.getY2AxisStyle() != null
				&& (r.getY2AxisStyle().equals("log10")));

		new OpiInt(widgetContext, "axis_1_time_format", 0);
		new OpiInt(widgetContext, "axis_2_time_format", 0);

		// trace properties
		new OpiInt(widgetContext, "trace_count", r.getNumTraces()); // ��������

		// PV X,Y
		for (int i = 0; i < r.getXPv().getLineCount(); i++) {
			String[] pvName = r.getXPv().getLine(i).split("\\s+");// ʹ�ÿհ��ַ�ָ��ַ�
			new OpiString(widgetContext, "trace_" + pvName[0] + "_x_pv", pvName[1]);
		}
		for (int i = 0; i < r.getYPv().getLineCount(); i++) {
			String[] pvName = r.getYPv().getLine(i).split("\\s+");// ʹ�ÿհ��ַ�ָ��ַ�
			new OpiString(widgetContext, "trace_" + pvName[0] + "_y_pv", pvName[1]);
		}

		if (r.getPlotColor() != null) {
			for (Entry<String, EdmColor> entry : r.getPlotColor().getEdmColorMap().entrySet()) {
				new OpiColor(widgetContext, "trace_" + entry.getKey() + "_trace_color",
						entry.getValue());
			}
		}

		if (r.getTriggerPv() != null)
			new OpiString(widgetContext, "trigger_pv", r.getTriggerPv());
		log.debug("Edm_xyGraphClass written.");

	}

}
