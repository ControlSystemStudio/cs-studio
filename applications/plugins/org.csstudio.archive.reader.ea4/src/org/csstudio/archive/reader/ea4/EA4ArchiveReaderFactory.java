package org.csstudio.archive.reader.ea4;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/** The plugin.xml registers this factory for ArchiveReaders when the
 *  URL prefix indicates a Channel Archiver URL
 *  @author Kay Kasemir
 */
public class EA4ArchiveReaderFactory implements ArchiveReaderFactory {
    /** {@inheritDoc}*/
    @Override
    public ArchiveReader getArchiveReader(final String url) throws Exception {
        return new EA4ArchiveReader(url);
    }
}
