/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.AlarmStatus;
import org.epics.pvmanager.data.Time;
import org.epics.pvmanager.data.VType;
import org.epics.util.time.Timestamp;

/** Base of archive-derived {@link VType} implementations
 *  @author Kay Kasemir
 */
@SuppressWarnings("deprecation") // Must at this time support AlarmStatus
public class ArchiveVType implements Alarm, Time, VType
{
	final private Timestamp timestamp;
	final private AlarmSeverity severity;
	final private String status;
	
	public ArchiveVType(final Timestamp timestamp,
			final AlarmSeverity severity, final String status)
	{
		this.timestamp = timestamp;
		this.severity = severity;
		this.status = status;
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
		return timestamp.getSec() > 0;
	}
}
