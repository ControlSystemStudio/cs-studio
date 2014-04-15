package org.csstudio.archive.reader.appliance;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/**
 * Provides instance of the appliance archive reader.
 * 
 * @author Miha Novak <miha.novak@cosylab.com>
 */
public class ApplianceArchiveReaderFactory implements ArchiveReaderFactory{

	/* (non-Javadoc)
	 * @see org.csstudio.archive.reader.ArchiveReaderFactory#getArchiveReader(java.lang.String)
	 */
	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {
		return new ApplianceArchiveReader(url, Activator.getDefault().isUseStatistics());
	}
}