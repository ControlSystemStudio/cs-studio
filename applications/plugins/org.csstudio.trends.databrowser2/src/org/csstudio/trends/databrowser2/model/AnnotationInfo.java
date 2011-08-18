/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.model;

import java.io.PrintWriter;

import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.data.values.ITimestamp;
import org.csstudio.trends.databrowser2.ui.Plot;

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
}
