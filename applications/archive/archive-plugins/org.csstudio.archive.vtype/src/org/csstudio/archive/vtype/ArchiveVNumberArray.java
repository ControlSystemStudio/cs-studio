/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.time.Instant;
import java.util.List;

import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.ArrayDimensionDisplay;
import org.diirt.vtype.Display;
import org.diirt.vtype.VNumberArray;
import org.diirt.vtype.ValueUtil;
import org.diirt.util.array.ArrayDouble;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ArrayLong;
import org.diirt.util.array.ListInt;
import org.diirt.util.array.ListNumber;

/** Archive-derived {@link VNumberArray} implementation
 *  @author Kay Kasemir
 */
public class ArchiveVNumberArray extends ArchiveVDisplayType implements VNumberArray
{
    final private ListNumber data;

    /** Initialize from doubles
     *  @param timestamp
     *  @param severity
     *  @param status
     *  @param display
     *  @param data
     */
    public ArchiveVNumberArray(final Instant timestamp,
            final AlarmSeverity severity, final String status,
            final Display display, final double... data)
    {
        this(timestamp, severity, status, display, new ArrayDouble(data));
    }

    /** Initialize from longs
     *  @param timestamp
     *  @param severity
     *  @param status
     *  @param display
     *  @param data
     */
    public ArchiveVNumberArray(final Instant timestamp,
            final AlarmSeverity severity, final String status,
            final Display display, final long... data)
    {
        this(timestamp, severity, status, display, new ArrayLong(data));
    }

    /** Initialize from integers
     *  @param timestamp
     *  @param severity
     *  @param status
     *  @param display
     *  @param data
     */
    public ArchiveVNumberArray(final Instant timestamp,
            final AlarmSeverity severity, final String status,
            final Display display, final int... data)
    {
        this(timestamp, severity, status, display, new ArrayInt(data));
    }

    /** Initialize from {@link ListNumber}
     *  @param timestamp
     *  @param severity
     *  @param status
     *  @param display
     *  @param data
     */
    public ArchiveVNumberArray(final Instant timestamp,
            final AlarmSeverity severity, final String status,
            final Display display, final ListNumber data)
    {
        super(timestamp, severity, status, display);
        this.data = data;
    }

    @Override
    public ListInt getSizes()
    {
        return new ArrayInt(data.size());
    }

    @Override
    public ListNumber getData()
    {
        return data;
    }

    @Override
    public List<ArrayDimensionDisplay> getDimensionDisplay()
    {
        return ValueUtil.defaultArrayDisplay(this);
    }

    @Override
    public int hashCode()
    {
        return data.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (! (obj instanceof VNumberArray))
            return false;
        final ListNumber other = ((VNumberArray) obj).getData();
        return data.equals(other);
    }

    @Override
    public String toString()
    {
        return VTypeHelper.toString(this);
    }
}
