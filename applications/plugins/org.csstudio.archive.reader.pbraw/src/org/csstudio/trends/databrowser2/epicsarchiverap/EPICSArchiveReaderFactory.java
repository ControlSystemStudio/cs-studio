package org.csstudio.trends.databrowser2.epicsarchiverap;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/*
 * @author Luofeng Li
 */
public class EPICSArchiveReaderFactory implements ArchiveReaderFactory {

	  /** Prefix used by this reader */
    final public static String PREFIX = "pbraw";
   
    final private static Map<String, EPICSArchiveReader> cache = new HashMap<String, EPICSArchiveReader>();
	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {
 
	    synchronized (cache)
        {
            ArchiveReader reader = cache.get(url);
            if (reader != null)
                return reader;
        }
	    String getRawDataURL="/data/getData.raw";
	    String searchingPVURL="/bpl/searchForPVsRegex?regex=";
	  
	    //url=parseURL(url);
	    final EPICSArchiveReader reader = new EPICSArchiveReader(url, getRawDataURL, searchingPVURL);
        // Cache the reader by URL
        synchronized (cache)
        {
            cache.put(url, reader);
        }
        return reader;

	
	}
	
	

	

}
