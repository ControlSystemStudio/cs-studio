package org.csstudio.diag.rack;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin activator
 *  @author Kay Kasemir
 */
public class Activator extends AbstractUIPlugin
{
    /** Lazily initialized Log4j Logger */
    private static Logger log = null;
  
    private static Activator plugin = null;
    
    public Activator()
    {
        plugin = this;
    }
    
    @Override
    public void stop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null)    // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
    public static Activator getDefault()
    {
        return plugin;
    }
}
