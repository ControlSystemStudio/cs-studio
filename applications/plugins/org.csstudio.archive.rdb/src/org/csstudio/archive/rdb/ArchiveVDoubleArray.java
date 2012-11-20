/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.util.Collections;
import java.util.List;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VDoubleArray;
import org.epics.util.array.ArrayDouble;
import org.epics.util.array.ListDouble;
import org.epics.util.time.Timestamp;

/** Archive-derived {@link VDoubleArray} implementation
 *  @author Kay Kasemir
 */
public class ArchiveVDoubleArray extends ArchiveVDisplayType implements VDoubleArray
{
	final private double[] data;

	public ArchiveVDoubleArray(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final Display display, final double[] data)
	{
		super(timestamp, severity, status, display);
		this.data = data;
	}

	@Override
	public List<Integer> getSizes()
	{
        return Collections.singletonList(data.length);
	}

	@Override
	public double[] getArray()
	{
		return data;
	}

	@Override
	public ListDouble getData()
	{
		return new ArrayDouble(data);
	}

	@Override
	public String toString()
	{
		return VTypeHelper.toString(this);
	}
}
