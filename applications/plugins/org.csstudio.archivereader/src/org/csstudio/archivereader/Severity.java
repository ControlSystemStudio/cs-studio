package org.csstudio.archivereader;

import org.csstudio.platform.data.ISeverity;

/** Severity with name and ISeverity interface.
 *  <p>
 *  Tries to match the severity name to the basic OK, MINOR, MAJOR levels.
 *  Everything else is considered INVALID.
 *  @author Kay Kasemir
 */
public class Severity implements ISeverity
{
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

    /** @see ISeverity#toString() */
	@Override
    final public String toString()
    {
        return name;
    }
}
