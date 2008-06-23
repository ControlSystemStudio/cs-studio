package org.csstudio.archive.rdb;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class RDBPlugin extends Plugin
{
    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

    /** The shared instance */
    private static RDBPlugin plugin;
    
    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        plugin = this;
    }

    /** {@inheritDoc} */
    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

    /** @return the shared instance */
    public static RDBPlugin getDefault()
    {
        return plugin;
    }
    
    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
