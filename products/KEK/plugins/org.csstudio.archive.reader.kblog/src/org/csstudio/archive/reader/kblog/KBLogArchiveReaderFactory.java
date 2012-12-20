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
		// See the implementation of
		// org.csstudio.archive.reader.rdb.ArchiveReader.getArchiveReader
		// to see the detail of the background of this implementation. 
		
		final Activator instance = Activator.getInstance();
		if (instance == null)
			throw new Exception("KBLogArchiveReaderFacotry requires Plugin infrastructure");
		synchronized (instance)
		{
			final String kblogrdPath = KBLogPreferences.getPathToKBLogRD();
			final String relPathToSubarchiveList = KBLogPreferences.getRelPathToSubarchiveList();
			final String relPathToLCFDir = KBLogPreferences.getRelPathToLCFDir();
			final boolean reduceData = KBLogPreferences.getReduceData();
			return new KBLogArchiveReader(url, kblogrdPath, relPathToSubarchiveList, relPathToLCFDir, reduceData);
		}
	}
}
