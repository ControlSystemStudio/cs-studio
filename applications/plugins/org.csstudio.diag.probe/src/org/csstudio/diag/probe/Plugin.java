package org.csstudio.diag.probe;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class Plugin extends AbstractCssUiPlugin
{
    /** The plug-in ID */
    public static final String ID = "org.csstudio.diag.probe"; //$NON-NLS-1$

    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

    /** The shared instance */
	private static Plugin plugin;
	
	/** Constructor. */
	public Plugin()
    {	plugin = this;	}
    
    /** @see AbstractCssUiPlugin */
    @Override
    public String getPluginId()
    {   return ID;  }
    
    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStart(BundleContext context) throws Exception
    { /* NOP */ }

    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {   plugin = null;   }

	/** @eturn The shared instance. */
	public static Plugin getDefault()
    {	return plugin;	}
    
    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
