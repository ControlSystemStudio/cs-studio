/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import java.text.NumberFormat;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.util.time.Timestamp;

/** Base of archive-derived {@link VType} implementations that include {@link Display}
 *  @author Kay Kasemir
 */
public class ArchiveVDisplayType extends ArchiveVType implements Display
{
	final private Display display;
	
	public ArchiveVDisplayType(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final Display display)
	{
		super(timestamp, severity, status);
		this.display = display;
	}

	@Override
	public Double getLowerDisplayLimit()
	{
		return display.getLowerDisplayLimit();
	}

	@Override
	public Double getLowerCtrlLimit()
	{
		return display.getLowerCtrlLimit();
	}

	@Override
	public Double getLowerAlarmLimit()
	{
		return display.getLowerWarningLimit();
	}

	@Override
	public Double getLowerWarningLimit()
	{
		return display.getLowerWarningLimit();
	}

	@Override
	public String getUnits()
	{
		return display.getUnits();
	}

	@Override
	public NumberFormat getFormat()
	{
		return display.getFormat();
	}

	@Override
	public Double getUpperWarningLimit()
	{
		return display.getUpperWarningLimit();
	}

	@Override
	public Double getUpperAlarmLimit()
	{
		return display.getUpperAlarmLimit();
	}

	@Override
	public Double getUpperCtrlLimit()
	{
		return display.getUpperCtrlLimit();
	}

	@Override
	public Double getUpperDisplayLimit()
	{
		return display.getUpperDisplayLimit();
	}
}
