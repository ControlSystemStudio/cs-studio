package org.csstudio.diag.interconnectionServer;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.diag.interconnectionServer";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@Override
	protected void doStart(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getPluginId() {
		// TODO Auto-generated method stub
		return PLUGIN_ID;
	}

}
