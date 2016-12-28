/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.util.Objects;

import org.csstudio.swt.rtplot.data.PlotDataItem;
import org.eclipse.swt.graphics.Point;

/** Annotation that's displayed on a YAxis
 *  @param <XTYPE> Data type used for the {@link PlotDataItem}
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Annotation<XTYPE extends Comparable<XTYPE>>
{
    final protected boolean internal;
    final protected Trace<XTYPE> trace;

    /** The position and value, i.e. x/y in value space */
    protected volatile XTYPE position;
    protected volatile double value;

    /** Offset of annotation from the referenced position */
    protected volatile Point offset;

    /** The (multi-line) text to display */
    protected String text;

    /** Constructor */
    public Annotation(final Trace<XTYPE> trace, final XTYPE position, final double value, final Point offset, final String text)
    {
        this(false, trace, position, value, offset, text);
    }

    /** Constructor */
    public Annotation(final boolean internal, final Trace<XTYPE> trace, final XTYPE position, final double value, final Point offset, final String text)
    {
        this.internal = internal;
        this.trace = Objects.requireNonNull(trace);
        this.position = Objects.requireNonNull(position);
        this.value = value;
        this.offset = Objects.requireNonNull(offset);
        this.text = Objects.requireNonNull(text);
    }

    /** @return Internal annotation, not created by user? */
    public boolean isInternal()
    {
        return internal;
    }

    /** @return Trace for which this annotation shows values */
    public Trace<XTYPE> getTrace()
    {
        return trace;
    }

    /** @return Position (x-axis value, time) of this annotation */
    public XTYPE getPosition()
    {
        return position;
    }

    /** @return Offset of annotation from the referenced position */
    public Point getOffset()
    {
        return offset;
    }

    /** @return Value (on the Y axis) of this annotation */
    public double getValue()
    {
        return value;
    }

    /** @return Text (label) of this annotation */
    public String getText()
    {
        return text;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "Annotation for " + trace + " @ " + position + " / " + value + " : " + text;
    }
}
