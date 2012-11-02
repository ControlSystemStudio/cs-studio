package org.csstudio.diag.pvfields;

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
    	// Channel Access settings
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", ADDR_LIST);
        System.setProperty("gov.aps.jca.jni.JNIContext.addr_list", ADDR_LIST);
        
        // Use Channel Access as data source
        PVManager.setDefaultDataSource(new JCADataSource());
    }
}
