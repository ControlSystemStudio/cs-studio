package org.csstudio.utility.managementactions;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * Controls this plug-in's lifecycle.
 */
public class Activator extends AbstractCssUiPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.managementActions";

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
		// do nothing
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}
}
