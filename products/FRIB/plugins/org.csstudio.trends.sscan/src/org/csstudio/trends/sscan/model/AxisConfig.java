/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.model;

import java.io.PrintWriter;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Element;

/**
 * Information about configuration of an axis
 * 
 * @author Kay Kasemir
 */
public class AxisConfig {
	/** Model to which this axis belongs */
	private Model model = null;

	/** Visible? */
	private boolean visible;

	/** Name, axis label */
	private String name;

	/** Name, axis label fontData */
	private FontData fontData;

	/** Name, axis scale label fontData */
	private FontData scaleFontData;

	/** Color */
	private RGB rgb;

	/** Axis range */
	private double min, max;

	/** Auto-scale? */
	private boolean auto_scale;

	/** Logarithmic scale? */
	private boolean log_scale;
	
	/**
	 * Fire Event when Axis config changed ?
	 */
	private boolean fireEvent;

	
	// GRID LINE 
	
	private RGB gridLineColor;
	private boolean showGridLine;
	private boolean dashGridLine;
	
	
	// FORMAT
	private boolean timeFormatEnabled;
	private boolean autoFormat;
	private String format;
	

	public RGB getGridLineColor() {
		return gridLineColor;
	}

	public void setGridLineColor(RGB gridLineColor) {
		this.gridLineColor = gridLineColor;
	}

	public boolean isShowGridLine() {
		return showGridLine;
	}

	public void setShowGridLine(boolean showGridLine) {
		this.showGridLine = showGridLine;
	}

	public boolean isDashGridLine() {
		return dashGridLine;
	}

	public void setDashGridLine(boolean dashGridLine) {
		this.dashGridLine = dashGridLine;
	}

	public boolean isTimeFormatEnabled() {
		return timeFormatEnabled;
	}

	public void setTimeFormatEnabled(boolean timeFormatEnabled) {
		this.timeFormatEnabled = timeFormatEnabled;
	}

	public boolean isAutoFormat() {
		return autoFormat;
	}

	public void setAutoFormat(boolean autoFormat) {
		this.autoFormat = autoFormat;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public AxisConfig(final Boolean visible, final String name, final RGB rgb,
			final double min, final double max, final boolean auto_scale,
			final boolean log_scale) {
		this(true, name, null, null, new RGB(0, 0, 0), 0.0, 10.0, false, false,
				false, true, null, true, false, "");
	}

	/**
	 * Initialize
	 * 
	 * @param name
	 * @param font
	 * @param scaleFont
	 * @param rgb
	 * @param min
	 * @param max
	 * @param auto_scale
	 * @param log_scale
	 */
	public AxisConfig(final Boolean visible, final String name, FontData font,
			FontData scaleFont, final RGB rgb, final double min,
			final double max, final boolean auto_scale,
			final boolean log_scale, final boolean showGridLine,
			final boolean dashGridLine, final RGB gridLineColor, final boolean autoFormat, final boolean timeFormat, final String format) {
		this.visible = visible;
		this.name = name;
		this.fontData = font;
		this.scaleFontData = scaleFont;
		this.rgb = rgb;
		this.min = min;
		this.max = max;
		this.auto_scale = auto_scale;
		this.log_scale = log_scale;

		// GRID LINE
		this.showGridLine = showGridLine;
		this.dashGridLine = dashGridLine;
		this.gridLineColor = gridLineColor;
		
		//FORMAT
		this.autoFormat = autoFormat;
		this.timeFormatEnabled = timeFormat;
		this.format = format;
	}

	/**
	 * Initialize with defaults
	 * 
	 * @param name
	 */
	public AxisConfig(final String name) {
		this(true, name, new RGB(0, 0, 0), 0.0, 10.0, false, false);
	}

	/**
	 * @param model
	 *            Model to which this item belongs
	 */
	void setModel(final Model model) {
		this.model = model;
	}

	/** @return <code>true</code> if axis should be displayed */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            Should axis be displayed?
	 */
	public void setVisible(final boolean visible) {
		this.visible = visible;
		fireAxisChangeEvent();
	}

	/** @return Axis title */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            New axis title
	 */
	public void setName(final String name) {
		this.name = name;
		fireAxisChangeEvent();
	}

	public FontData getFontData() {
		return fontData;
	}

	public void setFontData(FontData fontData) {
		this.fontData = fontData;
	}

	public FontData getScaleFontData() {
		return scaleFontData;
	}

	public void setScaleFontData(FontData scaleFontData) {
		this.scaleFontData = scaleFontData;
	}

	/** @return Color */
	public RGB getColor() {
		return rgb;
	}

	
	
	/**
	 * @param color
	 *            New color
	 */
	public void setColor(final RGB color) {
		rgb = color;
		fireAxisChangeEvent();
		
		
	}

	/** @return Axis range minimum */
	public double getMin() {
		return min;
	}

	/** @return Axis range maximum */
	public double getMax() {
		return max;
	}

	/**
	 * @param limit
	 *            New axis range maximum
	 */
	public void setRange(final double min, final double max) {
		// Ignore empty range
		if (min == max)
			return;
		// Assert min is below max
		if (min < max) {
			this.min = min;
			this.max = max;
		} else {
			this.min = max;
			this.max = min;
		}
		fireAxisChangeEvent();
	}

	/** @return Auto-scale? */
	public boolean isAutoScale() {
		return auto_scale;
	}

	/**
	 * @param auto_scale
	 *            Should axis auto-scale?
	 */
	public void setAutoScale(final boolean auto_scale) {
		this.auto_scale = auto_scale;
		fireAxisChangeEvent();
	}

	/** @return Logarithmic scale? */
	public boolean isLogScale() {
		return log_scale;
	}

	/**
	 * @param log_scale
	 *            Use logarithmic scale?
	 */
	public void setLogScale(final boolean log_scale) {
		this.log_scale = log_scale;
		if(fireEvent)fireAxisChangeEvent();
	}

	/** Notify model about changes */
	private void fireAxisChangeEvent() {
		if (model != null)
			model.fireAxisChangedEvent(this);
	}

	/**
	 * Write XML formatted axis configuration
	 * 
	 * @param writer
	 *            PrintWriter
	 */
	public void write(final PrintWriter writer) {
		XMLWriter.start(writer, 2, Model.TAG_AXIS);
		writer.println();
		XMLWriter.XML(writer, 3, Model.TAG_NAME, name);

		if (fontData != null)
			XMLWriter.XML(writer, 3, Model.TAG_FONT, fontData);

		if (scaleFontData != null)
			XMLWriter.XML(writer, 3, Model.TAG_SCALE_FONT, scaleFontData);

		XMLWriter.start(writer, 3, Model.TAG_COLOR);
		writer.println();
		XMLWriter.XML(writer, 4, Model.TAG_RED, rgb.red);
		XMLWriter.XML(writer, 4, Model.TAG_GREEN, rgb.green);
		XMLWriter.XML(writer, 4, Model.TAG_BLUE, rgb.blue);
		XMLWriter.end(writer, 3, Model.TAG_COLOR);
		writer.println();
		XMLWriter.XML(writer, 3, Model.TAG_XMIN, min);
		XMLWriter.XML(writer, 3, Model.TAG_XMAX, max);
		XMLWriter.XML(writer, 3, Model.TAG_XLOG_SCALE,
				Boolean.toString(log_scale));
		XMLWriter.XML(writer, 3, Model.TAG_XAUTO_SCALE,
				Boolean.toString(auto_scale));
		XMLWriter.XML(writer, 3, Model.TAG_YMIN, min);
		XMLWriter.XML(writer, 3, Model.TAG_YMAX, max);
		XMLWriter.XML(writer, 3, Model.TAG_YLOG_SCALE,
				Boolean.toString(log_scale));
		XMLWriter.XML(writer, 3, Model.TAG_YAUTO_SCALE,
				Boolean.toString(auto_scale));
		XMLWriter.XML(writer, 3, Model.TAG_VISIBLE, Boolean.toString(visible));

		// GRID LINE
		XMLWriter.start(writer, 3, Model.TAG_GRID_LINE);
		writer.println();

		XMLWriter.XML(writer, 4, Model.TAG_SHOW_GRID_LINE, showGridLine);
		XMLWriter.XML(writer, 4, Model.TAG_DASH_GRID_LINE, dashGridLine);

		if (gridLineColor != null)
			Model.writeColor(writer, 4, Model.TAG_COLOR, gridLineColor);
		
		XMLWriter.end(writer, 3, Model.TAG_GRID_LINE);
		writer.println();
		
		//FORMAT 
		XMLWriter.start(writer, 3, Model.TAG_FORMAT);
		writer.println();

		XMLWriter.XML(writer, 4, Model.TAG_AUTO_FORMAT, autoFormat);
		XMLWriter.XML(writer, 4, Model.TAG_TIME_FORMAT, timeFormatEnabled);
		XMLWriter.XML(writer, 4, Model.TAG_FORMAT_PATTERN, format);

		XMLWriter.end(writer, 3, Model.TAG_FORMAT);
		writer.println();

	

		XMLWriter.end(writer, 2, Model.TAG_AXIS);

		writer.println();
	}

	/**
	 * Create Axis info from XML document
	 * 
	 * @param node
	 * @return AxisConfig
	 * @throws Exception
	 *             on error
	 */
	public static AxisConfig fromDocument(final Element node) throws Exception {
		final String name = DOMHelper.getSubelementString(node, Model.TAG_NAME);

		String fontInfo = DOMHelper.getSubelementString(node, Model.TAG_FONT);

		FontData fontData = null;
		if (fontInfo != null && !fontInfo.trim().equals("")) {
			fontData = new FontData(fontInfo);
		}

		fontInfo = DOMHelper.getSubelementString(node, Model.TAG_SCALE_FONT);

		FontData scaleFontData = null;
		if (fontInfo != null && !fontInfo.trim().equals("")) {
			scaleFontData = new FontData(fontInfo);
		}
		//TODO: seperate XMIN YMIN so this works for any axis
		final double min = DOMHelper.getSubelementDouble(node, Model.TAG_XMIN,
				0.0);
		final double max = DOMHelper.getSubelementDouble(node, Model.TAG_XMAX,
				10.0);
		final boolean auto_scale = DOMHelper.getSubelementBoolean(node,
				Model.TAG_XAUTO_SCALE, false);
		final boolean log_scale = DOMHelper.getSubelementBoolean(node,
				Model.TAG_XLOG_SCALE, false);
		final boolean visible = DOMHelper.getSubelementBoolean(node,
				Model.TAG_VISIBLE, true);

		RGB rgb = Model.loadColorFromDocument(node);
		if (rgb == null)
			rgb = new RGB(0, 0, 0);

		// GRID LINE
		Element gridNode = DOMHelper.findFirstElementNode(node.getFirstChild(), Model.TAG_GRID_LINE);
		System.out.println("GRID NODE " + gridNode.getNodeName());
		
		final boolean showGridLine = DOMHelper.getSubelementBoolean(gridNode,
				Model.TAG_SHOW_GRID_LINE, false);

		final boolean dashGridLine = DOMHelper.getSubelementBoolean(gridNode,
				Model.TAG_DASH_GRID_LINE, false);

		RGB rgbGridLine = Model.loadColorFromDocument(gridNode);
		
		// GRID LINE
				Element formatNode = DOMHelper.findFirstElementNode(node.getFirstChild(), Model.TAG_FORMAT);
				
				
				final boolean autoFormat = DOMHelper.getSubelementBoolean(formatNode,
						Model.TAG_AUTO_FORMAT, true);

				final boolean timeFormat = DOMHelper.getSubelementBoolean(formatNode,
						Model.TAG_TIME_FORMAT, false);
				
				final String format = DOMHelper.getSubelementString(formatNode,
						Model.TAG_FORMAT_PATTERN, "");


		return new AxisConfig(visible, name, fontData, scaleFontData, rgb, min,
				max, auto_scale, log_scale, showGridLine, dashGridLine,
				rgbGridLine, autoFormat, timeFormat, format);
	}

	/** @return String representation for debugging */
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "Axis '" + name + "', range " + min + " ... " + max + ", "
				+ rgb.toString();
	}

	/** @return Copied axis configuration. Not associated with a model */
	public AxisConfig copy() {
		return new AxisConfig(visible, name, rgb, min, max, auto_scale,
				log_scale);
	}

	public boolean isFireEvent() {
		return fireEvent;
	}

	public void setFireEvent(boolean fireEvent) {
		this.fireEvent = fireEvent;
	}


}
