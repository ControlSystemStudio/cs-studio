/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ArrayInt;
import org.epics.util.array.ListDouble;
import org.epics.util.array.ListInt;
import org.epics.util.time.Timestamp;

/** Archive-derived {@link VDoubleArray} implementation
 *  @author Kay Kasemir
 */
public class ArchiveVDoubleArray extends ArchiveVDisplayType implements VDoubleArray
{
	final private ListDouble data;

	public ArchiveVDoubleArray(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final Display display, final double... data)
	{
		this(timestamp, severity, status, display, new ArrayDouble(data));
	}
	
	public ArchiveVDoubleArray(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final Display display, final ListDouble data)
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
	public ListDouble getData()
	{
		return data;
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
		if (! (obj instanceof VDoubleArray))
			return false;
		final ListDouble other = ((VDoubleArray) obj).getData();
		return data.equals(other);
	}

	@Override
	public String toString()
	{
		return VTypeHelper.toString(this);
	}
}
