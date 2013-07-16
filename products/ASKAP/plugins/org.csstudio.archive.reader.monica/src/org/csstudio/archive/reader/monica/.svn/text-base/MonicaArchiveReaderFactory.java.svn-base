package org.csstudio.archive.reader.monica;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

public class MonicaArchiveReaderFactory implements ArchiveReaderFactory {
	private static final Logger logger = Logger.getLogger(MonicaArchiveReaderFactory.class.getName());
	
	MonicaArchiveClient monicaIceClient = null;	
	
	public MonicaArchiveReaderFactory() {
		
	}

	@Override
	public ArchiveReader getArchiveReader(String url) throws Exception {
        final Activator instance = Activator.getInstance();
        if (instance == null)
            throw new Exception("RDBArchiveReaderFactory requires Plugin infrastructure");
        synchronized (instance)
        {
            final String adaptorName = Preferences.getAdaptorName();
            
            final Properties iceProperties = Preferences.getIceProperties();
           
            
            if (monicaIceClient!=null) {
            	monicaIceClient.close();
				logger.log(Level.INFO, "Disconnected to MoniCA server");
            }
            
        	try {
                monicaIceClient = new MonicaArchiveClient(adaptorName, iceProperties);
                logger.log(Level.INFO, "Connected to MoniCA server");
                
        	} catch (Exception e) {
                logger.log(Level.WARNING, "Failed to connected to MoniCA server", e);
        		throw new Exception("Failed to connected to MoniCA server", e);
        	}
            
			return new MonicaArchiveReader(url, adaptorName, monicaIceClient);
        }
	}

}
