/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader;

import org.csstudio.data.values.ISeverity;

/** Severity with name and ISeverity interface.
 *  <p>
 *  Tries to match the severity name to the basic OK, MINOR, MAJOR levels.
 *  Everything else is considered INVALID.
 *  @author Kay Kasemir
 */
public class Severity implements ISeverity
{
	private static final long serialVersionUID = 1L;

	/** The basic severity levels */
	public enum Level
	{
		OK,
		MINOR,
		MAJOR,
		INVALID
	}

	/** Name of this severity */
	final private String name;

	/** Level of this severity */
	final private Level level;

	/** Constructor
	 *  @param id
	 *  @param name
	 */
	public Severity(String name)
	{   // Store name as given...
	    this.name = name;
	    // ... but use UPPERCASE version to determine Level
	    name = name.toUpperCase();
		if (name.length() == 0  ||  name.indexOf(Level.OK.name()) >= 0)
			level = Level.OK;
		else if (name.indexOf(Level.MINOR.name()) >= 0)
				level = Level.MINOR;
		else if (name.indexOf(Level.MAJOR.name()) >= 0)
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

    /** @see ISeverity#toString() */
	@Override
    final public String toString()
    {
        return name;
    }
}
