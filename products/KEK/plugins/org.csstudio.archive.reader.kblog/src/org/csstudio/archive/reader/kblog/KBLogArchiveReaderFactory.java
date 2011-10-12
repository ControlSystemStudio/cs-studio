package org.csstudio.archive.reader.kblog;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/**
 * The plugin.xml registers this factory for ArchiveReaders when the URL
 * prefex indicates a KBLog URL
 * 
 * @author Takashi Nakamoto
 */
public class KBLogArchiveReaderFactory implements ArchiveReaderFactory {

	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {
		final Activator instance = Activator.getInstance();
		if (instance == null)
			throw new Exception("KBLogArchiveReaderFacotry requires Plugin infrastructure");
		synchronized (instance)
		{
			// TODO read preferences and pass them to the constructor of KBLogArchiveReader
			return new KBLogArchiveReader(url);
		}
	}
}
