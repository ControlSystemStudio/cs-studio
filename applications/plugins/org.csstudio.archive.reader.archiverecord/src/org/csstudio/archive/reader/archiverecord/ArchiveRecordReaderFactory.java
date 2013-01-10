package org.csstudio.archive.reader.archiverecord;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

public class ArchiveRecordReaderFactory implements ArchiveReaderFactory {

	public ArchiveReader getArchiveReader(final String url) throws Exception {
		return new ArchiveRecordReader(url);
	}

}
