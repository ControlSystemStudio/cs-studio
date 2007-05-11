package org.csstudio.archive;

import org.csstudio.platform.data.ITimestamp;

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
	
	@Override public String toString()
	{
		return String.format("'%s': %s - %s", //$NON-NLS-1$
				name, start.toString(), end.toString());
	}
}