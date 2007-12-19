package org.csstudio.sns.product;
import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/** Plugin lifecycle.
 *  @author Kay Kasemir
 */
public class PluginActivator extends AbstractUIPlugin
{
    final public static String ID = "org.csstudio.sns.product"; //$NON-NLS-1$

    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

    private static PluginActivator plugin;
    
    public PluginActivator()
    {
        plugin = this;
    }

    /** {@inheritDoc} */
    @Override
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
    }
    
    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
