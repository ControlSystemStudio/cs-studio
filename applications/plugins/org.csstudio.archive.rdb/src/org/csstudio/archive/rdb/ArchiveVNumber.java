/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.rdb;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.Display;
import org.epics.pvmanager.data.VNumber;
import org.epics.util.time.Timestamp;

/** Archive-derived {@link VNumber} implementation
 *  @author Kay Kasemir
 */
public class ArchiveVNumber extends ArchiveVDisplayType implements VNumber
{
	final private Number value;

	public ArchiveVNumber(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final Display display, final Number value)
	{
		super(timestamp, severity, status, display);
		this.value = value;
	}

	@Override
	public Number getValue()
	{
		return value;
	}
	
	@Override
	public String toString()
	{
		return VTypeHelper.toString(this);
	}
}
