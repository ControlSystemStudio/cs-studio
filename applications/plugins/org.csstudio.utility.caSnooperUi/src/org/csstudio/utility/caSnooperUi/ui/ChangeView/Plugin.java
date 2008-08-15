package org.csstudio.utility.caSnooperUi.ui.ChangeView;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main class for the activator
 * 
 * 
 */
public class Plugin extends AbstractCssUiPlugin {

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
	protected void doStart(BundleContext context){
		plugin = this;
	}
	
	protected void doStop(BundleContext context){
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

	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}

}