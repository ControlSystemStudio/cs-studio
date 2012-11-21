/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.vtype;

import java.util.List;

import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.VEnum;
import org.epics.util.time.Timestamp;

/** Archive-derived {@link VEnum} implementation
 *  @author Kay Kasemir
 */
public class ArchiveVEnum extends ArchiveVType implements VEnum
{
	final private List<String> labels;
	final private int index;

	public ArchiveVEnum(final Timestamp timestamp,
			final AlarmSeverity severity, final String status,
			final List<String> labels, final int index)
	{
		super(timestamp, severity, status);
		this.labels = labels;
		this.index = index;
	}
	
	@Override
	public List<String> getLabels()
	{
		return labels;
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

	@Override
	public String toString()
	{
		return VTypeHelper.toString(this);
	}
}
