package org.csstudio.archive.channelarchiver;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.osgi.framework.BundleContext;

/** Plugin lifecycle handler.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Plugin extends org.eclipse.core.runtime.Plugin
{
    /** The plug-in ID */
    final static String ID = "org.csstudio.archive.channelarchiver";
    
    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

    /** Shared instance */
    private static Plugin plugin = null;
    
    /** Constructor */
    public Plugin()
    {
        plugin = this;
    }
    
    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
