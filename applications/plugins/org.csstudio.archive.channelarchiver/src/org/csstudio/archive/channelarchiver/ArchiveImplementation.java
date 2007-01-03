package org.csstudio.archive.channelarchiver;

import org.csstudio.archive.IArchiveImplementation;

/** IArchiveImplementation for the ChannelArchiver.
 *  @author Jan Hatje
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public class ArchiveImplementation implements IArchiveImplementation
{
    // @see IArchiveImplementation
	public ArchiveServer getServerInstance(String url) throws Exception
    {   return new org.csstudio.archive.channelarchiver.ArchiveServer(url);	 }

    // @see IArchiveImplementation
	public String[] getURLList()
    {   return null;	}
}
