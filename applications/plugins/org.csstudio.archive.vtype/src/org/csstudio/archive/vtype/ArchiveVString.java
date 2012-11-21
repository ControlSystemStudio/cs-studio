/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VString;
import org.epics.util.time.Timestamp;

/** Archive-derived {@link VString} implementation
 *  @author Kay Kasemir
 */
public class ArchiveVString extends ArchiveVType implements VString
{
	final private String value;

	public ArchiveVString(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final String value)
	{
		super(timestamp, severity, status);
		this.value = value;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return VTypeHelper.toString(this);
	}
}
