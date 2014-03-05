/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.io.PrintWriter;
import java.util.Calendar;

import org.csstudio.apputil.time.AbsoluteTimeParser;
import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.archive.vtype.TimestampHelper;
import org.csstudio.swt.xygraph.figures.Annotation.CursorLineStyle;
import org.csstudio.trends.databrowser2.persistence.XYGraphSettings;
import org.csstudio.trends.databrowser2.ui.Plot;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.epics.util.time.Timestamp;
import org.w3c.dom.Element;

/** Information about a Plot Annotation
 *
 *  <p>In the current implementation the plot library actually
 *  tracks the currently active annotations.
 *
 *  <p>This class is used by the model to read initial annotations
 *  from the {@link Model}'s XML file,
 *  and to later read them back from the {@link Plot} for writing
 *  them to the XML file.
 *
 *  <p>The info keeps less detail than the actual annotation in the XYGraph
 *  to reduce dependence on the plotting library.
 *
 *  @author Kay Kasemir
 */
public class AnnotationInfo
{
	final private Timestamp timestamp;
	final private double value;
	final private int axis;
	final private String title;

	//ADD Laurent PHILIPPE
	final private CursorLineStyle cursorLineStyle;
	final private boolean showName;
	final private boolean showPosition;
	final private boolean showSampleInfo;
	/**
	 * Add because getTitleFont send a SWTERROR if the receiver is dispose.
	 * It is the case when you save the plt file after ask to close CSS.
	 */
	final private FontData FontData;
	final private RGB color;
	
	public FontData getFontData() {
		return FontData;
	}

	public RGB getColor() {
		return color;
	}
	
	public boolean isShowSampleInfo() {
		return showSampleInfo;
	}

	public boolean isShowName() {
		return showName;
	}

	public boolean isShowPosition() {
		return showPosition;
	}

	public AnnotationInfo(final Timestamp timestamp, final double value, final int axis,
			final String title, CursorLineStyle lineStyle, final boolean showName, final boolean showPosition,
			final boolean showSampleInfo, final FontData fontData, final RGB color)
    {

		this.timestamp = timestamp;
		this.value = value;
		this.axis = axis;
		this.title = title;
		this.cursorLineStyle = lineStyle;
		this.showName = showName;
		this.showSampleInfo = showSampleInfo;
		this.showPosition = showPosition;
		this.FontData = fontData;
		this.color = color;

    }

	public AnnotationInfo(final Timestamp timestamp, final double value, final int axis,
			final String title)
    {
		this(timestamp, value, axis, title, CursorLineStyle.NONE, false, false, false, null, null);
    }

	/** @return Time stamp */
	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	/** @return Title */
    public double getValue()
    {
        return value;
    }

    /** @return Title */
    public String getTitle()
    {
        return title;
    }

	/** @return Axis index */
    public int getAxis()
    {
        return axis;
    }

	@SuppressWarnings("nls")
    @Override
	public String toString()
	{
		return "Annotation for axis " + axis + ": '" + title + "' @ " + timestamp + ", " + value;
	}

    /** Write XML formatted annotation configuration
     *  @param writer PrintWriter
     *  
     *  @deprecated annotation data is stored in the {@link XYGraphSettings}
     */
	@Deprecated
	public void write(final PrintWriter writer)
    {
        XMLWriter.start(writer, 2, Model.TAG_ANNOTATION);
        writer.println();
        XMLWriter.XML(writer, 3, Model.TAG_TIME, timestamp);
        XMLWriter.XML(writer, 3, Model.TAG_VALUE, value);

        XMLWriter.XML(writer, 3, Model.TAG_NAME, title);
        XMLWriter.XML(writer, 3, Model.TAG_AXIS, axis);
        XMLWriter.XML(writer, 3, Model.TAG_ANNOTATION_CURSOR_LINE_STYLE, cursorLineStyle.name());
        XMLWriter.XML(writer, 3, Model.TAG_ANNOTATION_SHOW_NAME, showName);
        XMLWriter.XML(writer, 3, Model.TAG_ANNOTATION_SHOW_POSITION, showPosition);

        if(color != null)
	    	 Model.writeColor(writer, 3, Model.TAG_ANNOTATION_COLOR, color);


	     if(FontData != null)
	    	 XMLWriter.XML(writer, 3, Model.TAG_ANNOTATION_FONT, FontData);

        XMLWriter.end(writer, 2, Model.TAG_ANNOTATION);


        writer.println();
    }

    /** Create {@link AnnotationInfo} from XML document
     *  @param node XML node with item configuration
     *  @return PVItem
     *  @throws Exception on error
     */
	public static AnnotationInfo fromDocument(final Element node) throws Exception
    {
        final String timetext = DOMHelper.getSubelementString(node, Model.TAG_TIME, TimestampHelper.format(Timestamp.now()));
        final Calendar calendar = AbsoluteTimeParser.parse(timetext);
        final Timestamp timestamp = Timestamp.of(calendar.getTime());
        final double value = DOMHelper.getSubelementDouble(node, Model.TAG_VALUE, 0.0);
        final int axis = DOMHelper.getSubelementInt(node, Model.TAG_AXIS, 0);
		final String title = DOMHelper.getSubelementString(node, Model.TAG_NAME, "Annotation"); //$NON-NLS-1$
		final String lineStyle = DOMHelper.getSubelementString(node, Model.TAG_ANNOTATION_CURSOR_LINE_STYLE, CursorLineStyle.NONE.name());

		final boolean showName = DOMHelper.getSubelementBoolean(node, Model.TAG_ANNOTATION_SHOW_NAME, false);
		final boolean showPosition = DOMHelper.getSubelementBoolean(node, Model.TAG_ANNOTATION_SHOW_POSITION, false);

		final RGB Color = Model.loadColorFromDocument(node, Model.TAG_ANNOTATION_COLOR);
		String fontInfo = DOMHelper.getSubelementString(node, Model.TAG_ANNOTATION_FONT);

		FontData fontData = null;
		if (fontInfo != null && !fontInfo.trim().isEmpty())
			fontData = new FontData(fontInfo);

        return new AnnotationInfo(timestamp, value, axis, title, CursorLineStyle.valueOf(lineStyle), showName, showPosition, false, fontData, Color);
    }

	public CursorLineStyle getCursorLineStyle() {
		return cursorLineStyle;
	}
}
