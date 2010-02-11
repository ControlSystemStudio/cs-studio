package org.csstudio.archivereader.channelarchiver;

import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.ArchiveReaderFactory;

/** The plugin.xml registers this factory for ArchiveReaders when the
 *  URL prefix indicates a Channel Archiver URL
 *  @author Kay Kasemir
 */
public class ChannelArchiverArchiveReaderFactory implements
        ArchiveReaderFactory
{
    /** {@inheritDoc}*/
    public ArchiveReader getArchiveReader(final String url) throws Exception
    {
        return new ChannelArchiverReader(url);
    }
}
