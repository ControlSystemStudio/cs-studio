package org.csstudio.archive.rdb;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.utility.rdb.StringID;

/** Severity with name, ID and ISeverity interface.
 *  <p>
 *  Tries to match the severity name to the basic OK, MINOR, MAJOR levels.
 *  Everything else is considered INVALID.
 *  @author Kay Kasemir
 */
public class Severity extends StringID implements ISeverity
{
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
	public boolean isOK()
	{
		return level == Level.OK;
	}

	/** {@inheritDoc} */
	public boolean isMinor()
	{
		return level == Level.MINOR;
	}

	/** {@inheritDoc} */
	public boolean isMajor()
	{
		return level == Level.MAJOR;
	}

	/** {@inheritDoc} */
	public boolean isInvalid()
	{
		return level == Level.INVALID;
	}

	/** {@inheritDoc} */
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
