package org.csstudio.archive.channelarchiver;

import org.csstudio.archive.ArchiveInfo;

/** Implementation of org.csstudio.archive.ArchiveInfo
 *  @see ArchivesRequest
 *  @author Kay Kasemir
 */
public class ArchiveInfoImpl implements ArchiveInfo
{
	private final int key;
	private final String name, description;
	
	/** Constructor. */
	public ArchiveInfoImpl(int key, String name, String description)
	{
		this.key = key;
		this.name = name;
		this.description = description;
	}

	/* @see org.csstudio.archive.ArchiveInfo */
	public int getKey()
	{	return key;	}

    /* @see org.csstudio.archive.ArchiveInfo */
	public String getName()
	{	return name;	}

    /* @see org.csstudio.archive.ArchiveInfo */
	public String getDescription()
	{	return description;	}
}