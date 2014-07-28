package org.csstudio.archive.reader.fastarchiver;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/**
 * The plugin.xml registers this factory for ArchiveReaders when the URL prefix
 * indicates a Fast Archiver URL (i.e. starts with "fads://")
 * 
 * @author Friederike Johlinger
 */
public class FastArchiveReaderFactory implements ArchiveReaderFactory {
	//private static int createCount = 0;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {
		//System.out.println("FastArchiveReader number "+createCount);
		//createCount ++;
		return new FastArchiveReader(url);
	}

}
