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
import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.trends.databrowser2.ui.Plot;
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
	final private ITimestamp timestamp;
	final private double value;
	final private int axis;
	final private String title;

	public AnnotationInfo(final ITimestamp timestamp, final double value, final int axis,
			final String title)
    {
		this.timestamp = timestamp;
		this.value = value;
		this.axis = axis;
		this.title = title;
    }

	/** @return Time stamp */
	public ITimestamp getTimestamp()
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
     */
	public void write(final PrintWriter writer)
    {
        XMLWriter.start(writer, 2, Model.TAG_ANNOTATION);
        writer.println();
        XMLWriter.XML(writer, 3, Model.TAG_TIME, timestamp);
        XMLWriter.XML(writer, 3, Model.TAG_VALUE, value);

        XMLWriter.XML(writer, 3, Model.TAG_NAME, title);
        XMLWriter.XML(writer, 3, Model.TAG_AXIS, axis);
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
        final String timetext = DOMHelper.getSubelementString(node, Model.TAG_TIME, TimestampFactory.now().toString());
        final Calendar calendar = AbsoluteTimeParser.parse(timetext);
        final ITimestamp timestamp = TimestampFactory.fromCalendar(calendar);
        final double value = DOMHelper.getSubelementDouble(node, Model.TAG_VALUE, 0.0);
        final int axis = DOMHelper.getSubelementInt(node, Model.TAG_AXIS, 0);
		final String title = DOMHelper.getSubelementString(node, Model.TAG_NAME, "Annotation"); //$NON-NLS-1$

        return new AnnotationInfo(timestamp, value, axis, title);
    }
}
