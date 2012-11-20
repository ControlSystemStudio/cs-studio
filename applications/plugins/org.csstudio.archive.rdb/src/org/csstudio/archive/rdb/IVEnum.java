/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.util.List;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.VEnum;
import org.epics.util.time.Timestamp;

/** {@link VEnum} implementation
 * 
 *  <p>VType system currently lacks one.
 *  @author Kay Kasemir
 */
public class IVEnum implements VEnum
{
	final private Timestamp timestamp;
	final private AlarmSeverity severity;
	final private String status;
	final private List<String> labels;
	final private int index;

	
	public IVEnum(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final List<String> labels, final int index)
	{
		this.timestamp = timestamp;
		this.severity = severity;
		this.status = status;
		this.labels = labels;
		this.index = index;
	}
	
	
	@Override
	public List<String> getLabels()
	{
		return labels;
	}

	@Override
	public AlarmSeverity getAlarmSeverity() 
	{
		return severity;
	}

	@Override
	public String getAlarmName()
	{
		return status;
	}

	@Override
	@Deprecated
	public AlarmStatus getAlarmStatus()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	@Override
	public Integer getTimeUserTag()
	{
		return 0;
	}

	@Override
	public boolean isTimeValid()
	{
		return true;
	}

	@Override
	public String getValue()
	{
		try
		{
			return labels.get(index);
		}
		catch (RuntimeException ex)
		{
			return "Enum <" + index + ">";
		}
	}

	@Override
	public int getIndex()
	{
		return index;
	}
}
