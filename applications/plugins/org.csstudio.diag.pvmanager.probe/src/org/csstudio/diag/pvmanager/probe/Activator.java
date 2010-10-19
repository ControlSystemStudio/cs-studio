package org.csstudio.diag.pvmanager.probe;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.pvmanager.probe";

	// The shared instance
	private static Activator plugin;
	
	 /** Lazily initialised Log4j Logger */
    private static Logger log = null;
    
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}
	
	/** @see AbstractCssUiPlugin */
    @Override
    public String getPluginId()
    {   return PLUGIN_ID;  }
    
    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStart(BundleContext context) throws Exception
    { /* NOP */ }

    /** @see AbstractCssUiPlugin */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {   plugin = null;   }

	/** @return The shared instance. */
	public static Activator getDefault()
    {	return plugin;	}
    
    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
