package org.csstudio.archivereader.archiverecord;

import org.csstudio.archivereader.ArchiveReader;
import org.csstudio.archivereader.ArchiveReaderFactory;

public class ArchiveRecordReaderFactory implements ArchiveReaderFactory {

	public ArchiveReader getArchiveReader(final String url) throws Exception {
		return new ArchiveRecordReader(url);
	}

}
