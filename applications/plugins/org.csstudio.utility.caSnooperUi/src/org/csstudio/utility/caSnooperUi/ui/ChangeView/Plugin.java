package org.csstudio.utility.caSnooperUi.ui.ChangeView;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main class for the activator
 * 
 * 
 */
public class Plugin extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */ 
	public static final String PLUGIN_ID = "org.csstudio.utility.caSnooper";

	/**
	 * The shared instance
	 */
	private static Plugin plugin;
	
	/**
	 * The constructor
	 */
	public Plugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}
	
	public void stop(BundleContext context)throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Plugin getDefault() {
		return plugin;
	}
}