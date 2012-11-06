package org.csstudio.diag.pvfields;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.epics.pvmanager.CompositeDataSource;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.jca.JCADataSource;

/** Test settings
 * 
 *  <p>Need adjustments for use at other sites
 *  
 *  @author Kay Kasemir
 */
public class TestSetup
{
    private static final String ADDR_LIST = "127.0.0.1 160.91.228.17";
	final public static String CHANNEL_NAME = "DTL_LLRF:IOC1:Load";
    
    public static void setup() throws Exception
    {
    	// Logging
    	Logger logger = Logger.getLogger("");
    	logger.setLevel(Level.FINE);
    	for (Handler handler : logger.getHandlers())
    		handler.setLevel(Level.ALL);

    	Logger.getLogger("com.cosylab").setLevel(Level.SEVERE);
    	Logger.getLogger("org.csstudio.platform").setLevel(Level.SEVERE);
    	    	
    	// Channel Access settings
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", ADDR_LIST);
        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", ADDR_LIST);
        
        // Use Channel Access as data source
        final CompositeDataSource sources = new CompositeDataSource();
        sources.putDataSource("epics", new JCADataSource());
        sources.setDefaultDataSource("epics");
        PVManager.setDefaultDataSource(sources);
    }
}
