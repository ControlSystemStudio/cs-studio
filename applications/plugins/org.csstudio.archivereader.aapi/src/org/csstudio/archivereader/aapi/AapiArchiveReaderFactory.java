package org.csstudio.archivereader.aapi;

import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.ArchiveReaderFactory;

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
