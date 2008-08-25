package org.csstudio.config.authorizeid;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.config.authorizeid";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
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
		plugin = this;
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}

	@Override
	public String getPluginId() {
		// TODO Auto-generated method stub
		return null;
	}

}
