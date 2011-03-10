package org.csstudio.archive;

import org.csstudio.data.values.ITimestamp;

/** Information for one Channel in archive.
 *  @see ArchiveServer#getNames(int, String)
 *  @author Kay Kasemir
 */
public class NameInfo
{
	private final String name;
	private final ITimestamp start, end;

	/** Constructor. */
	public NameInfo(String name, ITimestamp start, ITimestamp end)
	{
		this.name = name;
		this.start = start;
		this.end = end;
	}

	/** @return The channel name. */
	public String getName()
	{	return name;	}

	/** @return The time stamp of the first sample found in the archive. */
	public ITimestamp getStart()
	{	return start;	}

	/** @return The time stamp of the last sample found in the archive. */
	public ITimestamp getEnd()
	{	return end;	}

	@SuppressWarnings("nls")
    @Override public String toString()
	{
	    if (start != null  &&  end != null)
    		return String.format("'%s': %s - %s",
    				name, start.toString(), end.toString());
	    else if (end != null)
            return String.format("'%s': ? - %s",
                    name, end.toString());
	    else
            return String.format("'%s': ? - ?", name);
	}
}