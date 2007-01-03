package org.csstudio.archive;

/** Info about one archive on a server.
 *  @see ArchiveServer#getArchiveInfos()
 *  @author Kay Kasemir
 */
public interface ArchiveInfo
{
	/** This key is used in various requests to the archive server.
     *  @return Returns the numeric key that identifies an archive.
     */
	public int getKey();

    /** @return Returns the name of the archive. */
	public String getName();

	/** @return Returns an arbitrary info string about the archive. */
	public String getDescription();
}