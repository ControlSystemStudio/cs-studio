package org.csstudio.archive.reader.fastarchiver;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;


/** The plugin.xml registers this factory for ArchiveReaders when the
 *  URL prefix indicates a Fast Archiver URL
 *  @author Friederike Johlinger
 */
public class FastArchiverArchiveReaderFactory implements ArchiveReaderFactory{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {
		return new FastArchiveReader(url);
	}

}
