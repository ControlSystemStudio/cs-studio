package org.csstudio.archive.reader.fastarchiver;

import java.io.IOException;
import java.util.HashMap;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;
import org.csstudio.archive.reader.fastarchiver.exceptions.FADataNotAvailableException;

/**
 * The plugin.xml registers this factory for ArchiveReaders when the URL prefix
 * indicates a Fast Archiver URL (i.e. starts with "fads://")
 * 
 * @author FJohlinger
 */
public class FastArchiveReaderFactory implements ArchiveReaderFactory {

	private static HashMap<String, ArchiveReader> archivers = new HashMap<String, ArchiveReader>();
		
	/**
	 * {@inheritDoc}
	 * @throws FADataNotAvailableException 
	 * @throws IOException 
	 */
	@Override
	public ArchiveReader getArchiveReader(String url) throws IOException, FADataNotAvailableException {
		if (!archivers.containsKey(url)) 
			archivers.put(url, new FastArchiveReader(url));
		return archivers.get(url);
	}

}
