package org.csstudio.utility.pv;

import org.apache.log4j.Logger;
import org.csstudio.platform.AbstractCssPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.osgi.framework.BundleContext;

/** Plugin-activator for the PV.
 *  @author Kay Kasemir
 */
public class Plugin extends AbstractCssPlugin
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
    
	@Override
    public String getPluginId()
	{   return ID; }

    /** @see AbstractCssPlugin */
    @SuppressWarnings("nls")
    @Override
    protected void doStart(BundleContext context) throws Exception
    { /* NOP */ }

    /** @see AbstractCssPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        plugin = null;
    }
    
    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
