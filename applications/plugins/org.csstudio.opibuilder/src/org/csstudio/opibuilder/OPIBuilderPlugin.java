package org.csstudio.opibuilder;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OPIBuilderPlugin extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.opibuilder";
	
	/**
	 * The ID of the widget extension point.
	 */
	public static final String EXTPOINT_WIDGET = PLUGIN_ID + ".widget"; //$NON-NLS-1$

	// The shared instance
	private static OPIBuilderPlugin plugin;
	
	private static Logger log;
	
	/**
	 * The constructor
	 */
	public OPIBuilderPlugin() {
		plugin = this;
	}

	

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static OPIBuilderPlugin getDefault() {
		return plugin;
	}



	@Override
	protected void doStart(BundleContext context) throws Exception {
	
	}



	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}


	/**
	 * @return the central CSS logger.
	 */
	public static Logger getLogger(){
		if(log == null)
			log = CentralLogger.getInstance().getLogger(plugin);
		return log;
	}

	@Override
	public String getPluginId() {
		// TODO Auto-generated method stub
		return null;
	}

}
