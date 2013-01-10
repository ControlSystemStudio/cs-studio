package org.csstudio.archive.reader.aapi;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/** 
 * Factory class for archive reader extension point.
 * 
 * @author jhatje
 * @author $Author: jhatje $
 * @since 17.12.2010
 */
public class AapiArchiveReaderFactory implements ArchiveReaderFactory {

	/**
     * {@inheritDoc}
     */
	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {
		return new AapiArchiveReader(url);
	}
}
