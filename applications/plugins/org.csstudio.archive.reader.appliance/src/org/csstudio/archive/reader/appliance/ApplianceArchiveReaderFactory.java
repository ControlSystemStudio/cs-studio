package org.csstudio.archive.reader.appliance;

import java.util.concurrent.ConcurrentHashMap;
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
	final private static ConcurrentHashMap<String, ApplianceArchiveReader> cache = new ConcurrentHashMap<String, ApplianceArchiveReader>();

	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {

		ApplianceArchiveReader result = cache.get(url);
	    if( result == null ) {
	    	ApplianceArchiveReader reader = cache.putIfAbsent(url,new ApplianceArchiveReader(url, Activator.getDefault().isUseStatistics()));
	      if( reader != null ) {
	        result = reader;
	      }
	    }
	    return result;
	}
	
}
