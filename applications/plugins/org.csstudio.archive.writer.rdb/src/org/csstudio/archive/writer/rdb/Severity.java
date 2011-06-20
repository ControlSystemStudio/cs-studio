/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.writer.rdb;

import org.csstudio.data.values.ISeverity;
import org.csstudio.platform.utility.rdb.StringID;

/** Severity with name, ID and ISeverity interface.
 *  <p>
 *  Tries to match the severity name to the basic OK, MINOR, MAJOR levels.
 *  Everything else is considered INVALID.
 *  @author Kay Kasemir
 */
public class Severity extends StringID implements ISeverity
{
	private static final long serialVersionUID = 1L;

	/** The basic severity levels */
	private enum Level
	{
		OK,
		MINOR,
		MAJOR,
		INVALID
	}

	/** Level of this severity */
	final private Level level;

	/** Constructor */
	public Severity(final int id, final String name)
	{
		super(id, name);
		if (name.length() == 0  ||  name.equalsIgnoreCase(Level.OK.name()))
			level = Level.OK;
		else if (name.equalsIgnoreCase(Level.MINOR.name()))
				level = Level.MINOR;
		else if (name.equalsIgnoreCase(Level.MAJOR.name()))
			level = Level.MAJOR;
		else
			level = Level.INVALID;
	}

	/** {@inheritDoc} */
	@Override
    public boolean isOK()
	{
		return level == Level.OK;
	}

	/** {@inheritDoc} */
    @Override
	public boolean isMinor()
	{
		return level == Level.MINOR;
	}

	/** {@inheritDoc} */
    @Override
	public boolean isMajor()
	{
		return level == Level.MAJOR;
	}

	/** {@inheritDoc} */
    @Override
	public boolean isInvalid()
	{
		return level == Level.INVALID;
	}

	/** {@inheritDoc} */
    @Override
    public boolean hasValue()
	{
		return true;
	}

	@Override
    final public String toString()
    {
        return getName();
    }
}
