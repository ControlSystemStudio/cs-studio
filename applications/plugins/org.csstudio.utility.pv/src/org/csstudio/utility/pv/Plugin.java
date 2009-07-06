package org.csstudio.utility.pv;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;

/** Plugin-activator for the PV.
 *  @author Kay Kasemir
 */
public class Plugin extends org.eclipse.core.runtime.Plugin
{
	public final static String ID = "org.csstudio.utility.pv"; //$NON-NLS-1$
    
    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

    /** The singleton instance */
	private static Plugin plugin;

    /** Constructor */
    public Plugin()
    {
        plugin = this;
    }
    
    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
