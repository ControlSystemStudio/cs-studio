package org.csstudio.archive.archiveRecordInterface;

import org.csstudio.archive.ArchiveInfo;

/** DESY Implementation of org.csstudio.archive.ArchiveInfo
 *  @see ArchivesRequest
 *  @author Albert Kagarmanov
 */
public class ArchiveInfoImpl implements ArchiveInfo
{
	private int key;
	private String name, description;
	
	/** Constructor. */
	public ArchiveInfoImpl(int key, String name, String description)
	{
		this.key = key;
		this.name = name;
		this.description = description;
	}

	/* @see org.csstudio.archive.ArchiveInfo */
	public int getKey()
	{
		return key;
	}

    /* @see org.csstudio.archive.ArchiveInfo */
	public String getName()
	{
		return name;
	}

    /* @see org.csstudio.archive.ArchiveInfo */
	public String getDescription()
	{
		return description;
	}
}