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
import org.epics.pvmanager.data.Statistics;
import org.epics.pvmanager.data.VStatistics;
import org.epics.util.time.Timestamp;

/** Archive-derived {@link VStatistics} implementation
 *  @author Kay Kasemir
 */
public class ArchiveVStatistics extends ArchiveVDisplayType implements VStatistics
{
	final private double average;
	final private double min;
	final private double max;
	final private double stddev;
	final private int count;

	public ArchiveVStatistics(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final Display display,
			final double average, final double min, final double max, final double stddev, final int count)
	{
		super(timestamp, severity, status, display);
		this.average = average;
		this.min = min;
		this.max = max;
		this.stddev = stddev;
		this.count = count;
	}

	public ArchiveVStatistics(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final Display display,
			final Statistics stats)
	{
		this(timestamp, severity, status, display,
			 stats.getAverage(),
			 stats.getMin(), stats.getMax(),
			 stats.getStdDev(), stats.getNSamples());
	}
	
	@Override
	public Double getAverage()
	{
		return average;
	}

	@Override
	public Double getStdDev()
	{
		return stddev;
	}

	@Override
	public Double getMin()
	{
		return min;
	}

	@Override
	public Double getMax()
	{
		return max;
	}

	@Override
	public Integer getNSamples()
	{
		return count;
	}

	@Override
	public String toString()
	{
		return VTypeHelper.toString(this);
	}
}
